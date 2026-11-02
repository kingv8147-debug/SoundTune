package com.soundtune.app;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.ValueCallback;
import android.webkit.JavascriptInterface;
import android.media.audiofx.Equalizer;
import android.media.audiofx.AudioEffect;
import android.os.Build;
import android.content.Intent;
import android.net.Uri;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends Activity {
    private WebView webView;
    private ValueCallback<Uri[]> filePathCallback;
    private static final int FILE_CHOOSER_REQUEST = 4102;
    private Equalizer systemEq;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window w = getWindow();
        w.setStatusBarColor(android.graphics.Color.rgb(8,11,16));
        w.setNavigationBarColor(android.graphics.Color.rgb(8,11,16));
        w.setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        webView = new WebView(this);
        webView.setBackgroundColor(android.graphics.Color.rgb(8,11,16));
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setMediaPlaybackRequiresUserGesture(true);
        webView.setWebViewClient(new WebViewClient());
        webView.addJavascriptInterface(new SoundBridge(), "SoundTuneAndroid");
        webView.setWebChromeClient(new WebChromeClient() {
            @Override public boolean onShowFileChooser(WebView view, ValueCallback<Uri[]> callback, FileChooserParams params) {
                if (filePathCallback != null) filePathCallback.onReceiveValue(null);
                filePathCallback = callback;
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("audio/*");
                try { startActivityForResult(Intent.createChooser(intent, "Choose audio"), FILE_CHOOSER_REQUEST); }
                catch (Exception e) { filePathCallback = null; return false; }
                return true;
            }
        });
        setContentView(webView);
        webView.loadUrl("file:///android_asset/index.html");
    }

    private class SoundBridge {
        @JavascriptInterface public String enableSystemEqualizer() {
            try {
                if (systemEq == null) {
                    systemEq = new Equalizer(0, 0); // Output-mix session: supported only on some Android builds.
                    systemEq.setEnabled(true);
                }
                return "AVAILABLE:" + systemEq.getNumberOfBands();
            } catch (Throwable e) {
                return "UNAVAILABLE:" + e.getClass().getSimpleName();
            }
        }
        @JavascriptInterface public String setSystemBand(int index, int db) {
            try {
                if (systemEq == null) {
                    String state = enableSystemEqualizer();
                    if (!state.startsWith("AVAILABLE")) return state;
                }
                short count = systemEq.getNumberOfBands();
                if (count <= 0) return "UNAVAILABLE";
                int targetHz = new int[]{60,120,250,500,1000,2000,4000,8000,12000,16000}[Math.max(0, Math.min(9,index))];
                short best = 0; int delta = Integer.MAX_VALUE;
                for (short b=0; b<count; b++) {
                    int hz = systemEq.getCenterFreq(b) / 1000;
                    int d = Math.abs(hz-targetHz);
                    if (d < delta) { delta=d; best=b; }
                }
                short[] range = systemEq.getBandLevelRange();
                int level = Math.max(range[0], Math.min(range[1], db * 100));
                systemEq.setBandLevel(best, (short)level);
                return "OK";
            } catch (Throwable e) { return "UNAVAILABLE:" + e.getClass().getSimpleName(); }
        }
        @JavascriptInterface public void disableSystemEqualizer() {
            try { if (systemEq != null) systemEq.setEnabled(false); } catch (Throwable ignored) {}
        }
        @JavascriptInterface public void release() {
            try { if (systemEq != null) { systemEq.release(); systemEq = null; } } catch (Throwable ignored) {}
        }
        @JavascriptInterface public String getPlatform() { return "Android " + Build.VERSION.RELEASE; }
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_CHOOSER_REQUEST && filePathCallback != null) {
            Uri[] result = (resultCode == RESULT_OK && data != null && data.getData() != null) ? new Uri[]{data.getData()} : null;
            filePathCallback.onReceiveValue(result);
            filePathCallback = null;
        }
    }

    @Override public void onBackPressed() {
        if (webView != null && webView.canGoBack()) webView.goBack(); else super.onBackPressed();
    }

    @Override protected void onDestroy() {
        try { if (systemEq != null) { systemEq.release(); systemEq = null; } } catch (Throwable ignored) {}
        if (webView != null) { webView.destroy(); webView = null; }
        super.onDestroy();
    }
}
