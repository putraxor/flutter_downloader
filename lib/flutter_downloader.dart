import 'dart:async';

import 'package:flutter/services.dart';

class FlutterDownloader {
  static const MethodChannel _channel =
      const MethodChannel('flutter_downloader');

  static Future<String> download(String url, String fileName) async {
    final String result = await _channel
        .invokeMethod('download', {'url': url, 'fileName': fileName});
    return result;
  }
}
