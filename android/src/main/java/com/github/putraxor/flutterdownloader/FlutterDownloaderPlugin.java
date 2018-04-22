package com.github.putraxor.flutterdownloader;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;

import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.PluginRegistry;
import io.flutter.plugin.common.PluginRegistry.Registrar;

import static android.content.Context.DOWNLOAD_SERVICE;

/**
 * FlutterDownloaderPlugin
 */
public class FlutterDownloaderPlugin implements MethodCallHandler {
    private DownloadManager dm;
    private final String WRITE_PERM = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private final int WRITE_PERM_REQ = 12321;
    private Registrar registrar;
    private String uri, fileName;

    /**
     * Constructor
     *
     * @param registrar
     */
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private FlutterDownloaderPlugin(Registrar registrar) {
        this.dm = (DownloadManager) registrar.activeContext().getSystemService(DOWNLOAD_SERVICE);
        this.registrar = registrar;
        registrar.addRequestPermissionsResultListener(new PluginRegistry.RequestPermissionsResultListener() {
            @Override
            public boolean onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
                boolean granted = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (requestCode == WRITE_PERM_REQ && granted) {
                    executeDownload();
                }
                return granted;
            }
        });
    }


    /**
     * Plugin registration.
     */

    public static void registerWith(Registrar registrar) {
        final MethodChannel channel = new MethodChannel(registrar.messenger(), "flutter_downloader");
        channel.setMethodCallHandler(new FlutterDownloaderPlugin(registrar));
    }

    /**
     * On method call
     *
     * @param call
     * @param result
     */
    @Override
    public void onMethodCall(MethodCall call, Result result) {
        if (call.method.equals("download")) {
            download(call, result);
        } else {
            result.notImplemented();
        }
    }

    /**
     * Create native downloader
     *
     * @param call
     * @param result
     */
    private void download(MethodCall call, Result result) {
        uri = call.argument("url");
        fileName = call.argument("fileName");
        boolean granted = ActivityCompat.checkSelfPermission(registrar.activity(), WRITE_PERM) == PackageManager.PERMISSION_GRANTED;
        if (granted) {
            try {
                executeDownload();
                result.success("Download started");
            } catch (Exception e) {
                result.error("Download failed", e.getMessage(), e);
            }
        } else {
            ActivityCompat.requestPermissions(registrar.activity(), new String[]{WRITE_PERM}, WRITE_PERM_REQ);
        }

    }

    /**
     * Execute download manager
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void executeDownload() {
        DownloadManager.Request r = new DownloadManager.Request(Uri.parse(uri));
        r.setTitle("Download");
        r.setDescription("Processing download request...");
        r.setVisibleInDownloadsUi(true);
        r.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
        r.allowScanningByMediaScanner();
        r.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        dm.enqueue(r);
    }
}
