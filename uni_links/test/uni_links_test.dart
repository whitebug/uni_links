// Copyright 2017 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

import 'dart:async';

import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:uni_links/uni_links.dart';

void main() {
  TestWidgetsFlutterBinding.ensureInitialized();

  const MethodChannel channel = MethodChannel('uni_links/messages');

  final log = <MethodCall>[];

  setUp(() {
    log.clear();
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
        .setMockMethodCallHandler(channel, (MethodCall methodCall) async {
          log.add(methodCall);
          switch (methodCall.method) {
            case 'getInitialLink':
              return 'https://example.com/initial';
            case 'getLatestLink':
              return 'https://example.com/latest';
            default:
              return null;
          }
        });
  });

  tearDown(() {
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
        .setMockMethodCallHandler(channel, null);
  });

  group('UniLinks', () {
    test('getInitialLink returns the initial link', () async {
      final result = await getInitialLink();
      expect(result, equals('https://example.com/initial'));
      expect(log, hasLength(1));
      expect(log.first.method, equals('getInitialLink'));
    });

    test('getInitialUri returns the initial URI', () async {
      final result = await getInitialUri();
      expect(result, isA<Uri>());
      expect(result?.toString(), equals('https://example.com/initial'));
    });

    test('getInitialUri returns null when no initial link', () async {
      TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
          .setMockMethodCallHandler(channel, (MethodCall methodCall) async {
            log.add(methodCall);
            return null;
          });

      final result = await getInitialUri();
      expect(result, isNull);
    });

    test('getInitialUri throws FormatException for invalid URI', () async {
      TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
          .setMockMethodCallHandler(channel, (MethodCall methodCall) async {
            log.add(methodCall);
            return 'invalid://[uri';
          });

      expect(() => getInitialUri(), throwsA(isA<FormatException>()));
    });

    test('linkStream is available', () async {
      final stream = linkStream;
      expect(stream, isInstanceOf<Stream<String?>>());
    });

    test('uriLinkStream is available', () async {
      final stream = uriLinkStream;
      expect(stream, isInstanceOf<Stream<Uri?>>());
    });
  });
}
