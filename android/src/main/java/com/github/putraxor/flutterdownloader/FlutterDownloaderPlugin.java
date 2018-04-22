package com.github.putraxor.flutterdownloader;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.PluginRegistry.Registrar;

import static android.content.Context.DOWNLOAD_SERVICE;

/**
 * FlutterDownloaderPlugin
 */
public class FlutterDownloaderPlugin implements MethodCallHandler {
    static DownloadManager dm;
    static private String writePerm = Manifest.permission.WRITE_EXTERNAL_STORAGE;

    /**
     * Plugin registration.
     */
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public static void registerWith(Registrar registrar) {
        dm = (DownloadManager) registrar.context().getSystemService(DOWNLOAD_SERVICE);
        final MethodChannel channel = new MethodChannel(registrar.messenger(), "flutter_downloader");
        channel.setMethodCallHandler(new FlutterDownloaderPlugin());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            registrar.activity().requestPermissions(new String[]{writePerm}, 11111);
        }
    }

    @Override
    public void onMethodCall(MethodCall call, Result result) {
        if (call.method.equals("download")) {
            download(call, result);
        } else {
            result.notImplemented();
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void download(MethodCall call, Result result) {
        try {
            String uri = call.argument("url");
            String fileName = call.argument("fileName");
            DownloadManager.Request r = new DownloadManager.Request(Uri.parse(uri));
            r.setVisibleInDownloadsUi(true);
            r.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
            r.allowScanningByMediaScanner();
            r.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
            dm.enqueue(r);
            result.success("Download started");
        } catch (Exception e) {
            result.error("Download failed", e.getMessage(), e);
        }
    }
}
