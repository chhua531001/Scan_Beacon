package com.example.chhua.scan_beacon;

import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

public class JavaScriptInterface {

    protected MainActivity parentActivity;
    protected WebView mWebView;

    String targetID = this.getClass().getSimpleName();
    
    public JavaScriptInterface(MainActivity _activity, WebView _webView)  {
        parentActivity = _activity;
        mWebView = _webView;
    }
    
    @JavascriptInterface
	public void loadURL(String url) {
		final String u = url;

		parentActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mWebView.loadUrl(u);
			}
		});
	}


    @JavascriptInterface
//    public void sdbis_notice(String json){
//        Log.println(Log.VERBOSE, targetID, "JavaScriptHandler.sdbis_notice is called : " + json);
//        this.parentActivity.javascriptCallFinished(json);
//    }
    public void esl_command(String json){
        Log.println(Log.VERBOSE, targetID, "JavaScriptHandler.esl_command is called : " + json);
        this.parentActivity.javascriptCallFinished(json);
    }



}
