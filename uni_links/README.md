# Uni Links

[![pub package](https://img.shields.io/pub/v/uni_links.svg)](https://pub.dev/packages/uni_links)
[![likes](https://img.shields.io/pub/likes/uni_links)](https://pub.dev/packages/uni_links/score)
[![popularity](https://img.shields.io/pub/popularity/uni_links)](https://pub.dev/packages/uni_links/score)
[![pub points](https://img.shields.io/pub/points/uni_links)](https://pub.dev/packages/uni_links/score)
[![CI](https://github.com/avioli/uni_links/workflows/CI/badge.svg)](https://github.com/avioli/uni_links/actions)

A Flutter plugin for handling App/Deep Links (Android) and Universal Links and Custom URL schemes (iOS).

These links are simply web-browser-like-links that activate your app and may contain information that you can use to load specific section of the app or continue certain user activity from a website (or another app).

App Links and Universal Links are regular https links, thus if the app is not installed (or setup correctly) they'll load in the browser, allowing you to present a web-page for further action, eg. install the app.

## Features

- ✅ **Null Safety**: Full support for Dart null safety
- ✅ **Flutter 3.22+**: Compatible with latest Flutter SDK
- ✅ **Android v2 Embedding**: Modern Android plugin architecture
- ✅ **Swift iOS**: Modern Swift implementation for iOS
- ✅ **Web Support**: Federated plugin with web implementation
- ✅ **Comprehensive Testing**: Unit tests and integration tests
- ✅ **CI/CD**: Automated testing and building

## Installation

To use the plugin, add `uni_links` as a [dependency in your pubspec.yaml file](https://flutter.io/platform-plugins/).

```yaml
dependencies:
  uni_links: ^0.5.2
```

### Breaking Changes in 0.5.2

This version includes several important updates:

- **Flutter SDK**: Minimum version is now Flutter 3.22.0
- **Dart SDK**: Minimum version is now Dart 3.22.0
- **Android**: Updated to Android v2 embedding, minimum API 21 (Android 5.0)
- **iOS**: Updated to Swift implementation, minimum iOS 12.0
- **Null Safety**: Full null safety support

### Migration from 0.5.1

If you're upgrading from version 0.5.1, the API remains the same. The main changes are:

1. Update your `pubspec.yaml` to use Flutter 3.22+:
   ```yaml
   environment:
     sdk: ">=3.22.0 <4.0.0"
     flutter: ">=3.22.0"
   ```

2. Update your Android `minSdkVersion` to 21 or higher
3. Update your iOS deployment target to 12.0 or higher

## Configuration

### Android

Uni Links supports two types of Android links: "App Links" and "Deep Links".

- **App Links** only work with `https` scheme and require a specified host, plus a hosted file - `assetlinks.json`
- **Deep Links** can have any custom scheme and do not require a host, nor a hosted file

You need to declare at least one of the two intent filters in `android/app/src/main/AndroidManifest.xml`:

```xml
<manifest ...>
  <application ...>
    <activity ...>
      <!-- Deep Links -->
      <intent-filter>
        <action android:name="android.intent.action.VIEW" />
        <category android:name="android.intent.category.DEFAULT" />
        <category android:name="android.intent.category.BROWSABLE" />
        <data
          android:scheme="[YOUR_SCHEME]"
          android:host="[YOUR_HOST]" />
      </intent-filter>

      <!-- App Links -->
      <intent-filter android:autoVerify="true">
        <action android:name="android.intent.action.VIEW" />
        <category android:name="android.intent.category.DEFAULT" />
        <category android:name="android.intent.category.BROWSABLE" />
        <data
          android:scheme="https"
          android:host="[YOUR_HOST]" />
      </intent-filter>
    </activity>
  </application>
</manifest>
```

### iOS

There are two kinds of links in iOS: "Universal Links" and "Custom URL schemes".

- **Universal Links** only work with `https` scheme and require a specified host, entitlements and a hosted file - `apple-app-site-association`
- **Custom URL schemes** can have any custom scheme and there is no host specificity

#### Universal Links

Add or create a `com.apple.developer.associated-domains` entitlement in `ios/Runner/Runner.entitlements`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0">
<dict>
  <key>com.apple.developer.associated-domains</key>
  <array>
    <string>applinks:[YOUR_HOST]</string>
  </array>
</dict>
</plist>
```

#### Custom URL Schemes

Declare the scheme in `ios/Runner/Info.plist`:

```xml
<key>CFBundleURLTypes</key>
<array>
  <dict>
    <key>CFBundleTypeRole</key>
    <string>Editor</string>
    <key>CFBundleURLName</key>
    <string>[ANY_URL_NAME]</string>
    <key>CFBundleURLSchemes</key>
    <array>
      <string>[YOUR_SCHEME]</string>
    </array>
  </dict>
</array>
```

## Usage

### Initial Link

Returns the link that the app was started with, if any.

```dart
import 'package:uni_links/uni_links.dart';
import 'package:flutter/services.dart';

Future<void> initUniLinks() async {
  try {
    final initialLink = await getInitialLink();
    if (initialLink != null) {
      // Handle the initial link
      print('Initial link: $initialLink');
    }
  } on PlatformException {
    // Handle exception
  }
}
```

### Initial URI

Same as `getInitialLink`, but converted to a `Uri`.

```dart
try {
  final initialUri = await getInitialUri();
  if (initialUri != null) {
    // Handle the initial URI
    print('Initial URI: $initialUri');
  }
} on FormatException {
  // Handle invalid URI
}
```

### Link Stream

Listen for incoming links while the app is running.

```dart
StreamSubscription<String?>? _subscription;

Future<void> initUniLinks() async {
  // Check initial link first
  final initialLink = await getInitialLink();
  
  // Listen for incoming links
  _subscription = linkStream.listen((String? link) {
    if (link != null) {
      // Handle the incoming link
      print('Incoming link: $link');
    }
  }, onError: (err) {
    // Handle error
  });
}

@override
void dispose() {
  _subscription?.cancel();
  super.dispose();
}
```

### URI Stream

Same as `linkStream`, but transformed to emit `Uri` objects.

```dart
StreamSubscription<Uri?>? _subscription;

_subscription = uriLinkStream.listen((Uri? uri) {
  if (uri != null) {
    // Handle the incoming URI
    print('Incoming URI: $uri');
  }
}, onError: (err) {
  // Handle error
});
```

## Testing

### Android

Use `adb` to test deep links:

```bash
adb shell 'am start -W -a android.intent.action.VIEW -c android.intent.category.BROWSABLE -d "unilinks://example.com/path"'
```

### iOS

Use `xcrun` to test universal links:

```bash
xcrun simctl openurl booted "unilinks://example.com/path"
```

## Example

See the [example](example/) directory for a complete working example.

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for your changes
5. Run `flutter analyze` and `flutter test`
6. Submit a pull request

## License

BSD 2-clause
