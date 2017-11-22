package com.example.chhua.scan_beacon;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.widget.Toast;

/**
 * Created by user on 2017/10/19.
 */

public class NetworkStateListener extends BroadcastReceiver {

    Tools tools = new Tools();
    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("Receive Change.");
        WifiManager wifi = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();

        if (activeNetInfo != null
                && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
//            Toast.makeText(context, "Wifi Connected!", Toast.LENGTH_SHORT).show();
//            tools.toastNow(context, "Wifi Connected___!", Color.WHITE);
            tools.toastNow(context, "WiFi已經正常連線", Color.WHITE);
            System.out.println("Receive Change Connected.");
        } else {
//            Toast.makeText(context, "Wifi Not Connected!", Toast.LENGTH_SHORT).show();
//            tools.toastNow(context, "Wifi Not Connected!", Color.WHITE);
            tools.toastNow(context, "網路不存在, 自動開啟WiFi連線中...", Color.WHITE);
            System.out.println("Receive Change Not Connected.");
            wifi.setWifiEnabled(true);
//            tools.delayMS(10000);
        }
    }
}
