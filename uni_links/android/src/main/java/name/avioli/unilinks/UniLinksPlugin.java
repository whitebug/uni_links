package name.avioli.unilinks;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.PluginRegistry;

/**
 * Flutter plugin for handling deep links and app links.
 * Supports Android v2 embedding.
 */
public class UniLinksPlugin
        implements FlutterPlugin,
                MethodChannel.MethodCallHandler,
                EventChannel.StreamHandler,
                ActivityAware {

    private static final String MESSAGES_CHANNEL = "uni_links/messages";
    private static final String EVENTS_CHANNEL = "uni_links/events";

    @Nullable
    private BroadcastReceiver changeReceiver;

    @Nullable
    private String initialLink;
    
    @Nullable
    private String latestLink;
    
    @Nullable
    private Context context;
    
    private boolean initialIntent = true;

    private void handleIntent(@NonNull Context context, @NonNull Intent intent) {
        String action = intent.getAction();
        String dataString = intent.getDataString();

        if (Intent.ACTION_VIEW.equals(action) && dataString != null) {
            if (initialIntent) {
                initialLink = dataString;
                initialIntent = false;
            }
            latestLink = dataString;
            if (changeReceiver != null) {
                changeReceiver.onReceive(context, intent);
            }
        }
    }

    @NonNull
    private BroadcastReceiver createChangeReceiver(@NonNull final EventChannel.EventSink events) {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(@NonNull Context context, @NonNull Intent intent) {
                String dataString = intent.getDataString();

                if (dataString == null) {
                    events.error("UNAVAILABLE", "Link unavailable", null);
                } else {
                    events.success(dataString);
                }
            }
        };
    }

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        this.context = flutterPluginBinding.getApplicationContext();
        register(flutterPluginBinding.getBinaryMessenger(), this);
    }

    private static void register(@NonNull BinaryMessenger messenger, @NonNull UniLinksPlugin plugin) {
        final MethodChannel methodChannel = new MethodChannel(messenger, MESSAGES_CHANNEL);
        methodChannel.setMethodCallHandler(plugin);

        final EventChannel eventChannel = new EventChannel(messenger, EVENTS_CHANNEL);
        eventChannel.setStreamHandler(plugin);
    }

    /**
     * Plugin registration method for Flutter embedding v1.
     * This method is called by the Flutter engine when the plugin is registered.
     */
    public static void registerWith(@NonNull PluginRegistry.Registrar registrar) {
        UniLinksPlugin plugin = new UniLinksPlugin();
        plugin.context = registrar.context();
        register(registrar.messenger(), plugin);
    }



    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        this.context = null;
    }

    @Override
    public void onListen(@Nullable Object o, @NonNull EventChannel.EventSink eventSink) {
        changeReceiver = createChangeReceiver(eventSink);
    }

    @Override
    public void onCancel(@Nullable Object o) {
        changeReceiver = null;
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull MethodChannel.Result result) {
        switch (call.method) {
            case "getInitialLink":
                result.success(initialLink);
                break;
            case "getLatestLink":
                result.success(latestLink);
                break;
            default:
                result.notImplemented();
                break;
        }
    }



    @Override
    public void onAttachedToActivity(@NonNull ActivityPluginBinding activityPluginBinding) {
        if (context != null) {
            this.handleIntent(this.context, activityPluginBinding.getActivity().getIntent());
        }
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {}

    @Override
    public void onReattachedToActivityForConfigChanges(
            @NonNull ActivityPluginBinding activityPluginBinding) {
        if (context != null) {
            this.handleIntent(this.context, activityPluginBinding.getActivity().getIntent());
        }
    }

    @Override
    public void onDetachedFromActivity() {}
}
