import Flutter
import UIKit

/// Flutter plugin for handling universal links and custom URL schemes on iOS.
@objc public class UniLinksPlugin: NSObject, FlutterPlugin, FlutterStreamHandler {
    
    private static let kMessagesChannel = "uni_links/messages"
    private static let kEventsChannel = "uni_links/events"
    
    private var eventSink: FlutterEventSink?
    private var initialLink: String?
    private var latestLink: String?
    
    public static func register(with registrar: FlutterPluginRegistrar) {
        let instance = UniLinksPlugin()
        
        let methodChannel = FlutterMethodChannel(
            name: kMessagesChannel,
            binaryMessenger: registrar.messenger()
        )
        registrar.addMethodCallDelegate(instance, channel: methodChannel)
        
        let eventChannel = FlutterEventChannel(
            name: kEventsChannel,
            binaryMessenger: registrar.messenger()
        )
        eventChannel.setStreamHandler(instance)
        
        registrar.addApplicationDelegate(instance)
    }
    
    public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
        switch call.method {
        case "getInitialLink":
            result(initialLink)
        case "getLatestLink":
            result(latestLink)
        default:
            result(FlutterMethodNotImplemented)
        }
    }
    
    public func onListen(withArguments arguments: Any?, eventSink events: @escaping FlutterEventSink) -> FlutterError? {
        self.eventSink = events
        return nil
    }
    
    public func onCancel(withArguments arguments: Any?) -> FlutterError? {
        self.eventSink = nil
        return nil
    }
    
    private func setLatestLink(_ link: String?) {
        latestLink = link
        if let eventSink = eventSink {
            eventSink(link)
        }
    }
}

// MARK: - UIApplicationDelegate
extension UniLinksPlugin: UIApplicationDelegate {
    
    public func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?
    ) -> Bool {
        if let url = launchOptions?[.url] as? URL {
            let link = url.absoluteString
            initialLink = link
            latestLink = link
        }
        return true
    }
    
    public func application(
        _ app: UIApplication,
        open url: URL,
        options: [UIApplication.OpenURLOptionsKey: Any] = [:]
    ) -> Bool {
        setLatestLink(url.absoluteString)
        return true
    }
    
    public func application(
        _ application: UIApplication,
        continue userActivity: NSUserActivity,
        restorationHandler: @escaping ([UIUserActivityRestoring]?) -> Void
    ) -> Bool {
        if userActivity.activityType == NSUserActivityTypeBrowsingWeb,
           let webpageURL = userActivity.webpageURL {
            let link = webpageURL.absoluteString
            setLatestLink(link)
            if eventSink == nil {
                initialLink = link
            }
            return true
        }
        return false
    }
} 