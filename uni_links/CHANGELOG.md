# Changelog

## 0.5.2

### Breaking Changes

- **Flutter SDK**: Minimum version is now Flutter 3.22.0
- **Dart SDK**: Minimum version is now Dart 3.22.0
- **Android**: Updated to Android v2 embedding, minimum API 21 (Android 5.0)
- **iOS**: Updated to Swift implementation, minimum iOS 12.0

### New Features

- ✅ Full null safety support
- ✅ Modern Android v2 embedding implementation
- ✅ Swift-based iOS implementation
- ✅ Comprehensive unit tests
- ✅ GitHub Actions CI/CD pipeline
- ✅ Updated documentation with modern examples

### Improvements

- Updated Android build configuration to use Gradle 8.4 and AGP 8.4.0
- Updated iOS deployment target to iOS 12.0
- Added proper null safety annotations throughout the codebase
- Improved error handling and type safety
- Added comprehensive documentation with migration guide
- Updated example app with modern Flutter patterns

### Technical Changes

- **Android**: 
  - Updated to compileSdkVersion 34 and targetSdkVersion 34
  - Added proper null safety annotations (@Nullable, @NonNull)
  - Improved error handling in plugin methods
  - Updated to modern FlutterPlugin v2 embedding

- **iOS**:
  - Migrated from Objective-C to Swift
  - Updated to iOS 12.0 minimum deployment target
  - Improved type safety and error handling
  - Better integration with modern iOS APIs

- **Dependencies**:
  - Updated to Flutter 3.22.0 minimum
  - Updated cupertino_icons to ^1.0.8
  - Added flutter_lints ^4.0.0 for better code quality

### Migration Guide

If you're upgrading from version 0.5.1:

1. Update your `pubspec.yaml`:
   ```yaml
   environment:
     sdk: ">=3.22.0 <4.0.0"
     flutter: ">=3.22.0"
   ```

2. Update your Android `minSdkVersion` to 21 or higher in `android/app/build.gradle`

3. Update your iOS deployment target to 12.0 or higher in your Xcode project

The API remains the same, so no code changes are required in your application.

## 0.5.1

- Initial null safety support
- Bug fixes and improvements

## 0.5.0

- Migrated to null safety
- Updated to federated plugin architecture
- Added web support

## Previous versions

See the [GitHub releases](https://github.com/avioli/uni_links/releases) for older versions.
