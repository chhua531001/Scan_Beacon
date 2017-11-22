package com.example.chhua.scan_beacon;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.net.ConnectivityManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class WifiTrunOnActivity extends AppCompatActivity {

    Context mContext = this;
    String targetID = this.getClass().getSimpleName();

    WifiManager wifi;
    Tools tools = new Tools();

    BroadcastReceiver connectionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction().equals(ConnectivityManager
                    .CONNECTIVITY_ACTION)) {

                boolean isConnected = ConnectivityManagerCompat.getNetworkInfoFromBroadcast
                        ((ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE),
                                intent).isConnected();

                if (isConnected) {
                    onConnectionEstablished();
                } else {
                    onConnectionAbsent();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);

//        boolean Wifi_status =  ApManager.configApState(MainActivity.this);

        //隱藏ActionBar(App最上面的工具欄)
        android.support.v7.app.ActionBar m_myActionBar = getSupportActionBar();
        m_myActionBar.hide();

        wifi = (WifiManager)mContext.getSystemService(Context.WIFI_SERVICE);


        Log.println(Log.DEBUG, targetID, "System onCreate");
    }


    public void onConnectionEstablished() {
        // do something

        Log.println(Log.DEBUG, targetID, "System onConnectionEstablished");
//        tools.toastNow(mContext, "WiFi已經正常連線", Color.WHITE);

        Intent intent = new Intent(this, LoginActivity.class);
//            intent.putExtra("notification", false);
        startActivity(intent);
//        startActivityForResult(intent,1);
        finish();
        overridePendingTransition(0, 0);
    }

    public void onConnectionAbsent() {
        // do something

        wifi.setWifiEnabled(true);

        Log.println(Log.DEBUG, targetID, "System onConnectionAbsent");
        tools.toastNow(mContext, "網路不存在, 自動開啟WiFi連線中...", Color.WHITE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(connectionReceiver, filter);

        Log.println(Log.DEBUG, targetID, "System onResume");

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.println(Log.DEBUG, targetID, "System onPause");
        try {
            unregisterReceiver(connectionReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
}
