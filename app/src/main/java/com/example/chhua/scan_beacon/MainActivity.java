package com.example.chhua.scan_beacon;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.Result;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.dm7.barcodescanner.zxing.ZXingScannerView;


public class MainActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler{

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mBluetoothLeScanner;

    private boolean mScanning = false;

    private static final int cameraRequestCode = 50;
    private static final int RQS_ENABLE_BLUETOOTH = 51;
    private static final int RQS_ENABLE_ACCESS_FINE_LOCATION = 52;

//    Button scanButton, turnOnPermission;
    Button scanButton;
//    ListView listViewLE;

//    List<BluetoothDevice> listBluetoothDevice;
    List<BluetoothDevice> listBluetoothDevice = new ArrayList<>();
//    ListAdapter adapterLeScanResult;

    ArrayList<HashMap<String,String>> deviceInfo = new ArrayList<HashMap<String,String>>();
    ArrayList<HashMap<String,String>> requestParameter = new ArrayList<HashMap<String,String>>();
    ArrayList<HashMap<String,String>> backupRequestParameter = new ArrayList<HashMap<String,String>>();
//    ArrayList<HashMap<String,String>> idParameter = new ArrayList<HashMap<String,String>>();
//    ArrayList<HashMap<String,String>> backupIDParameter = new ArrayList<HashMap<String,String>>();

//    ArrayList<HashMap<String,String,String>> deviceInfo = new ArrayList<HashMap<String,String,String>>();

    private Handler mHandler, mHandler1;
    private static final long SCAN_PERIOD = 10000;
    private static final long RESCAN_PERIOD = 60000;

    String targetID = this.getClass().getSimpleName();
    Tools tools = new Tools();
    Context mContext = this;
//    MainActivity currentActivity = this;

    boolean btn4Scan = true;

    String DeviceID = "DeviceID";
    String ProductName = "ProductName";
    String UrlID = "UrlID";
    String RSSI = "RSSI";
    String RSSI1 = "rssi";
    String Eddystone = "beacon_id";

    LinearLayout title;

    Runnable r;

    WebView mWebView;

//    Button inquireBtn, toolsBtn;
    ImageButton messageBtn, inquireBtn, toolsBtn, settingBtn;
    TextView messageTxt, inquireTxt, toolsTxt, settingTxt;
    ImageButton detailBtn, commodityBtn, shelfBtn, storeBtn;
    TextView detailTxt, commodityTxt, shelfTxt, storeTxt;

    boolean btn4Scanner = true;
    private ZXingScannerView mScannerView = null;

    int previosuPage = 1;
    int currentPage = 1;

    boolean timerOn = true;
    boolean deviceReady = false;
    boolean firstTime = true;

//    boolean doWebView = false;

    int subFunction = 1;

    List<Integer> pageStack = new ArrayList<Integer>();

    Pair<Integer, Integer> tempPixels;
    int hPixels;

    View tempView;


    private GridView gridView;
    private int[] image = {
            R.drawable.cat, R.drawable.flower, R.drawable.hippo,
            R.drawable.monkey, R.drawable.mushroom, R.drawable.panda,
            R.drawable.rabbit, R.drawable.raccoon
    };
    private String[] imgText = {
            "cat", "flower", "hippo", "monkey", "mushroom", "panda", "rabbit", "raccoon"
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);

        Log.println(Log.INFO, targetID, "System onCreate");

        //隱藏ActionBar(App最上面的工具欄)
        android.support.v7.app.ActionBar m_myActionBar = getSupportActionBar();
        m_myActionBar.hide();

        File logFile = new File("sdcard/Log/log.file");

        if (logFile.exists())
        {
            logFile.delete();
        }

        String newstr = "http://Word#$#$% .Word 1234".replaceAll("[^A-Za-z0-9//.:]+", "");
        Log.println(Log.DEBUG, targetID, "newstr --> "+newstr);

        tempPixels = tools.getDisplayParameter(getApplicationContext());
        hPixels = tempPixels.second;
//        if (tempPixels.first <= tempPixels.second) {

        android.view.ViewGroup.MarginLayoutParams mParams;

        LinearLayout amLL = (LinearLayout) findViewById(R.id.am_ll);
        mParams = (ViewGroup.MarginLayoutParams) amLL.getLayoutParams();
        mParams.setMargins(0, (int) hPixels / 10, 0, 0);




        clearTopButtonBackground();

        title = (LinearLayout) findViewById(R.id.llBorder);

//        turnOnPermission = (Button) findViewById(R.id.turnPermission);
        scanButton = (Button) findViewById(R.id.scanButton);
//        scanButton.setOnClickListener(scanDevice);

//        clearTopButtonBackground();
//        messageBtn.setBackgroundColor(getResources().getColor(R.color.deepPurple));
//        messageTxt.setBackgroundColor(getResources().getColor(R.color.deepPurple));


//        //Check  ACCESS_FINE_LOCATION  Permission
//        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
//                +ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
//        if (permissionCheck != PackageManager.PERMISSION_GRANTED){
//            Log.println(Log.INFO, targetID, "PERMISSION_GRANTED11");
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
////                    || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
////                Toast.makeText(this, "被要求授權BLE位置資料", Toast.LENGTH_SHORT).show();
//                tools.toastNow(mContext, "被要求授權BLE位置資料", Color.WHITE);
//                Log.println(Log.INFO, targetID, "PERMISSION_GRANTED12");
//            }else{
//                if (Build.VERSION.SDK_INT >= 23) {
//                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, RQS_ENABLE_ACCESS_FINE_LOCATION);
//                    Log.println(Log.INFO, targetID, "PERMISSION_GRANTED13");
//                }
//            }
//        }else{
////            Toast.makeText(this,  "已經同意使用位置資訊訊息了", Toast.LENGTH_SHORT).show();
//            tools.toastNow(mContext, "已經同意使用位置資訊訊息了", Color.WHITE);
//        }


//        int permissionCheck = ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
//        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
//            // Should we show an explanation?
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
////                Toast.makeText(context, "Trebuie sa oferi acces la spatiul de stocare!", Toast.LENGTH_SHORT).show();
//                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 110);
////                ActivityCompat.requestPermissions(context, new String[]{android.Manifest.permission.RECORD_AUDIO}, 110);
//            } else {
////                Toast.makeText(context, "Descarc noi actualizari!", Toast.LENGTH_SHORT).show();
//                tools.toastNow(mContext, "Descarc noi actualizari!", Color.WHITE);
//            }
//        }



        //Check  ACCESS_FINE_LOCATION  Permission
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)+ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                +ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        Log.println(Log.INFO, targetID, "permissionCheck -->"+permissionCheck);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED){
            Log.println(Log.INFO, targetID, "PERMISSION_GRANTED11");
            Log.println(Log.INFO, targetID, "howRequestPermissionRationale -->"+ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION));
            //ACCESS_FINE_LOCATION在turn on時, 其ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION) = true
            //WRITE_EXTERNAL_STORAGE在turn on時, 其ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) = false
            if ((!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION))
                || (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE))
                    || (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) ) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, RQS_ENABLE_ACCESS_FINE_LOCATION);
//                    requestPermissions(new String[]{Manifest.permission.CAMERA}, cameraRequestCode);
                    Log.println(Log.INFO, targetID, "PERMISSION_GRANTED13");
                }
            }
        }else{
//            Toast.makeText(this,  "已經同意使用位置資訊訊息了", Toast.LENGTH_SHORT).show();
            tools.toastNow(mContext, "已經同意使用位置資訊訊息了", Color.WHITE);
            deviceReady = true;
        }







        // Check if BLE is supported on the device.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
//            Toast.makeText(this, "BLUETOOTH_LE 不支援這個裝置!", Toast.LENGTH_SHORT).show();
            tools.toastNow(mContext, "BLUETOOTH_LE 不支援這個裝置!", Color.WHITE);
            finish();
        }

        getBluetoothAdapterAndLeScanner();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
//            Toast.makeText(this, "bluetoothManager.getAdapter()==null", Toast.LENGTH_SHORT).show();
            tools.toastNow(mContext, "bluetoothManager.getAdapter()==null", Color.WHITE);
            finish();
            return;
        }

//        //Check  ACCESS_FINE_LOCATION  Permission
//        permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
//        if (permissionCheck != PackageManager.PERMISSION_GRANTED){
//            Log.println(Log.INFO, targetID, "PERMISSION_GRANTED21");
//            if (Build.VERSION.SDK_INT >= 23) {
//                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 53);
//                Log.println(Log.INFO, targetID, "PERMISSION_GRANTED23");
//            }
//        }
//        else{
////            Toast.makeText(this,  "已經同意使用位置資訊訊息了", Toast.LENGTH_SHORT).show();
//            tools.toastNow(mContext, "已經同意使用位置資訊訊息了", Color.WHITE);
//        }

//        Log.println(Log.INFO, targetID, "WRITE_EXTERNAL_STORAGE");
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            Log.println(Log.INFO, targetID, "WRITE_EXTERNAL_STORAGE");
//            if(checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                Log.println(Log.INFO, targetID, "WRITE_EXTERNAL_STORAGE");
//                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 53);
//                Log.println(Log.INFO, targetID, "WRITE_EXTERNAL_STORAGE");
////                requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
//            }
//        }





//        scanButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                scanLeDevice(true);
//            }
//        });


//        listViewLE = (ListView)findViewById(R.id.lelist);
//
////        listBluetoothDevice = new ArrayList<>();
//        adapterLeScanResult = new SimpleAdapter(
//                this, deviceInfo, R.layout.style_listview, new String[] { DeviceID, ProductName, UrlID, RSSI }, new int[]{R.id.deviceID, R.id.PdName, R.id.urlID, R.id.rssiData});
//        listViewLE.setAdapter(adapterLeScanResult);
//        listViewLE.setOnItemClickListener(scanResultOnItemClickListener);

        mHandler = new Handler();
        mHandler1 = new Handler();

//        Log.println(Log.INFO, targetID, "Count Timer1");
//        mHandler.post(rescanRunnable);

    }

    final Runnable rescanRunnable = new Runnable() {
        public void run() {
            // TODO Auto-generated method stub
            // 需要背景作的事

            if (timerOn) {
                Log.println(Log.INFO, targetID, "Count Timer");
                mHandler.postDelayed(rescanRunnable, RESCAN_PERIOD);
                scanLeDevice(true);
            }
            else {
                Log.println(Log.INFO, targetID, "Count Timer Stop");
            }
        }
    };



    AdapterView.OnItemClickListener scanResultOnItemClickListener =
            new AdapterView.OnItemClickListener(){

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    final HashMap item = (HashMap) parent.getItemAtPosition(position);
                    String bleID = item.get(DeviceID).toString();
                    String url = item.get(UrlID).toString();
                    Log.println(Log.DEBUG, targetID, "BLE ID --> "+bleID);
                    Log.println(Log.DEBUG, targetID, "URL --> "+url);

                    mBluetoothLeScanner.stopScan(scanCallback);
//                    listViewLE.invalidateViews();
                    mScanning = false;

                    doWebView(url);

//                    setContentView(R.layout.webview_page);
//
//                    mWebView = (WebView) findViewById(R.id.webview);
//                    mWebView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.webColor));
////        mWebView.setBackgroundColor(Color.parseColor("#d9f1d8"));
//
//                    mWebView.getSettings().setJavaScriptEnabled(true);
//                    mWebView.setWebViewClient(mWebViewClient);
//                    mWebView.setWebChromeClient(new WebChromeClient());
//
//                    mWebView.loadUrl(url);
//                    mWebView.reload();

//                    final BluetoothDevice device =
//                            (BluetoothDevice) parent.getItemAtPosition(position);
//
//                    String msg = device.getAddress() + "\n"
//                            + device.getBluetoothClass().toString() + "\n"
//                            + getBTDevieType(device);
//
//                    new AlertDialog.Builder(MainActivity.this)
//                            .setTitle(device.getName())
//                            .setMessage(msg)
//                            .setPositiveButton("了解, 先不要連接", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//
//                                }
//                            })
//                            .setNeutralButton("連接", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    final Intent intent = new Intent(MainActivity.this,
//                                            WriteDataActivity.class);
//                                    intent.putExtra(ControlActivity.EXTRAS_DEVICE_NAME,
//                                            device.getName());
//                                    intent.putExtra(ControlActivity.EXTRAS_DEVICE_ADDRESS,
//                                            device.getAddress());
//
//                                    if (mScanning) {
//                                        mBluetoothLeScanner.stopScan(scanCallback);
//                                        mScanning = false;
//                                        scanButton.setEnabled(true);
//                                    }
//                                    startActivity(intent);
//
////                                    finish();  //後來自己加上的
//                                }
//                            })
//                            .show();

                }
            };

//    AdapterView.OnItemClickListener scanResultOnItemClickListener =
//            new AdapterView.OnItemClickListener(){
//
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    final BluetoothDevice device = (BluetoothDevice) parent.getItemAtPosition(position);
//
//                    String msg = device.getAddress() + "\n"
//                            + device.getBluetoothClass().toString() + "\n"
//                            + getBTDevieType(device);
//
//                    new AlertDialog.Builder(MainActivity.this)
//                            .setTitle(device.getName())
//                            .setMessage(msg)
//                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//
//                                }
//                            })
//                            .show();
//
//                }
//            };

    private String getBTDevieType(BluetoothDevice d){
        String type = "";

        switch (d.getType()){
            case BluetoothDevice.DEVICE_TYPE_CLASSIC:
                type = "DEVICE_TYPE_CLASSIC";
                break;
            case BluetoothDevice.DEVICE_TYPE_DUAL:
                type = "DEVICE_TYPE_DUAL";
                break;
            case BluetoothDevice.DEVICE_TYPE_LE:
                type = "DEVICE_TYPE_LE";
                break;
            case BluetoothDevice.DEVICE_TYPE_UNKNOWN:
                type = "DEVICE_TYPE_UNKNOWN";
                break;
            default:
                type = "unknown...";
        }

        return type;
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.println(Log.INFO, targetID, "System onResume");
        tools.appendLog("System onResume");

        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, RQS_ENABLE_BLUETOOTH);
            }
        }

        Log.println(Log.INFO, targetID, "System onReady = "+deviceReady);
        Log.println(Log.INFO, targetID, "System onfirstTime = "+firstTime);
        if(deviceReady && firstTime) {
            firstTime = false;
            Log.println(Log.INFO, targetID, "System onfirstTime = ");

            initMessagePage();

            currentPage = 1;
            pageStack.add(currentPage);

            Log.println(Log.DEBUG, targetID, "pageStack = "+pageStack.toString());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.println(Log.INFO, targetID, "System onPause");
//        if(mScannerView != null) {
////            mScannerView.removeAllViews(); //這是必須要作的, 否則會造成system reset
////            mScannerView.stopCamera();  // then stop the camera
//            mScannerView = null;
//            setContentView(R.layout.activity_main);  // and set the View again.
//        }
    }

    @Override
    protected void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.println(Log.INFO, targetID, "System onSaveInstanceState");

    }

    @Override
    protected void onRestoreInstanceState (Bundle savedInstanceState) {
        Log.println(Log.INFO, targetID, "System onRestoreInstanceState");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == RQS_ENABLE_BLUETOOTH && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }

        getBluetoothAdapterAndLeScanner();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
//            Toast.makeText(this, "bluetoothManager.getAdapter()==null", Toast.LENGTH_SHORT).show();
            tools.toastNow(mContext, "bluetoothManager.getAdapter()==null", Color.WHITE);
            finish();
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        Log.println(Log.INFO, targetID, "System onRequestPermissionsResult");
        switch (requestCode) {
            case RQS_ENABLE_ACCESS_FINE_LOCATION: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    Log.println(Log.INFO, targetID, "PERMISSION_GRANTED");
                    deviceReady = true;

                    if(!btn4Scan) {
                        scanButton.setText("掃描藍芽");
//                        scanButton.setOnClickListener(scanDevice);
                        btn4Scan = true;
                    }

//                    scanButton.setEnabled(true);
//                    turnOnPermission.setVisibility(View.INVISIBLE);
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.println(Log.INFO, targetID, "PERMISSION_CANCEL");
//                    Toast.makeText(MainActivity.this, "沒有啟用相機的授權, 無法使用QR-Code的Scan功能", Toast.LENGTH_SHORT).show();
                    tools.toastNow(mContext, "沒有啟用位置資訊訊息的授權, 無法使用的Scan功能", Color.WHITE);

                    if(btn4Scan) {
                        scanButton.setVisibility(View.VISIBLE);
                        scanButton.setText("請求啟用藍芽位置訊息與寫入儲存裝置的授權");
//                        scanButton.setOnClickListener(trunOnPermission);
                        btn4Scan = false;
                    }
//                    scanButton.setEnabled(false);
//                    turnOnPermission.setVisibility(View.VISIBLE);
                }
                break;

//                return;
            }

            case cameraRequestCode: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.println(Log.INFO, targetID, "PERMISSION_GRANTED");

                    if (!btn4Scanner) {
                        btn4Scanner = true;
                        scanButton.setText("掃描藍芽");
//                        scanButton.setText("QRScanner");
                    }
//                    scanButton.setEnabled(true);
//                    turnOnPermission.setVisibility(View.INVISIBLE);

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.println(Log.INFO, targetID, "PERMISSION_CANCEL");
//                    Toast.makeText(MainActivity.this, "沒有啟用相機的授權, 無法使用QR-Code的Scan功能", Toast.LENGTH_SHORT).show();
                    tools.toastNow(mContext, "沒有啟用相機的授權, 無法使用QR-Code的Scan功能", Color.WHITE);
//                    scanButton.setEnabled(false);
//                    turnOnPermission.setVisibility(View.VISIBLE);
                    if (btn4Scanner) {
                        btn4Scanner = false;
                        scanButton.setText("重新請求相機使用授權");
                    }
                }
                break;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.println(Log.INFO, targetID, "System onMonitor Rotate");
    }

    public void onBackPressed() {
        // your code.
        Log.println(Log.INFO, targetID, "System onBackPressed");

        Log.println(Log.INFO, targetID, "pageStack before  -> "+pageStack.toString());
        int totalCount = pageStack.size();
        Log.println(Log.INFO, targetID, "pageStack Count -> "+totalCount);
        if(totalCount > 1) {
            pageStack.remove(totalCount - 1);
            totalCount = pageStack.size();
            Log.println(Log.INFO, targetID, "pageStack after  -> " + pageStack.toString());
            currentPage = pageStack.get(totalCount - 1);


            switch (currentPage) {
                case 1:
//                setContentView(R.layout.activity_main1);
//                previosuPage = tempPage;
                    initMessagePage();
                    break;
                case 2:
                    initInquirePage();
                    break;
                case 3:
                    initToolsPage();
                    break;
            }
        }
//        setContentView(R.layout.activity_main1);
//        if(mScannerView != null) {
////            mScannerView.removeAllViews(); //這是必須要作的, 否則會造成system reset
////            mScannerView.stopCamera();  // then stop the camera
//            mScannerView = null;
//            setContentView(R.layout.activity_main);  // and set the View again.
//        }
//        else {
//            super.onBackPressed();
//        }
    }

    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here

        Log.println(Log.DEBUG, targetID, "rawResult->"+rawResult.getText());
        Log.println(Log.DEBUG, targetID, "BarcodeFormat->"+rawResult.getBarcodeFormat().toString());

//        Log.e("handler", rawResult.getText()); // Prints scan results
//        Log.e("handler", rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode)

        // show the scanner result into dialog box.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Scan Result");
        builder.setMessage(rawResult.getText());
        AlertDialog alert1 = builder.create();
        alert1.show();
        Log.println(Log.INFO, targetID, "System onPause1");
        mScannerView.stopCamera();
//        Log.println(Log.INFO, targetID, "System onPause2");

//        mScannerView.stopCamera();
//        mScannerView = null;

        String URL_REGEX = "^((http?)://)[vorder]+(\\.[net]+)+([/?].*)?$";
        Pattern p = Pattern.compile(URL_REGEX);
        Matcher m = p.matcher(rawResult.getText().toString());//replace with string to compare
        if(m.find()) {
            System.out.println("String contains URL");
            doWebView(rawResult.getText().toString());
        }
        else {

            boolean errorCode = true;
//            URL_REGEX = "^((https?|ftp)://|(www|ftp)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?$";
            if(rawResult.getText().toString().length() < 21) {
                URL_REGEX = "^[a-z0-9-]";
                p = Pattern.compile(URL_REGEX);
                m = p.matcher(rawResult.getText().toString());//replace with string to compare
                if (m.find()) {
                    System.out.println("String contains Barcode");
                    String pdUrl = "http://vorder.net/demo/cq.php?pb="+rawResult.getText().toString();
                    doWebView(pdUrl);
                    errorCode = false;
                }
            }

            if(errorCode) {
                tools.toastNow(mContext, "QR-Code或是Bar-Code的資料不是正確的網址", Color.WHITE);
                mScannerView.setAutoFocus(true);
                mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
                mScannerView.startCamera();
            }
        }




//        if(rawResult.getText().toString().contains("https://vorder.net/")) {
//
////            mScannerView.stopCamera();
//            mScannerView = null;
//            doWebView(rawResult.getText().toString());
//
//        }
//        else {
//            tools.toastNow(mContext, "QR-Code的資料不是正確的網址", Color.WHITE);
////            setContentView(R.layout.activity_main);  // and set the View again.
//
//
//            mScannerView.setAutoFocus(true);
//            mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
//            mScannerView.startCamera();         // Start camera
//        }

//        //If you would like to resume scanning, call this method below:
//        mScannerView.resumeCameraPreview(this);
//        mScannerView.startCamera();         // Start camera

    }

    public void messageClick(View v) {
        Log.println(Log.INFO, targetID, "Message Button Click");

        if(currentPage != 1) {

            initMessagePage();

            currentPage = 1;
            pageStack.add(currentPage);

            Log.println(Log.DEBUG, targetID, "pageStack = "+pageStack.toString());
//            setContentView(R.layout.activity_main1);
//
//            if (mScannerView != null) {
//                Log.println(Log.INFO, targetID, "Timer stopCamera");
//                mScannerView.stopCamera();
//                mScannerView = null;
//            }
//
//            clearTopButtonBackground();
//            messageBtn.setBackgroundColor(getResources().getColor(R.color.deepPurple));
//            messageTxt.setBackgroundColor(getResources().getColor(R.color.deepPurple));
//
//
//            timerOn = true;
//            mHandler.post(rescanRunnable);
//            previosuPage = currentPage;
//            currentPage = 1;
//            pageStack.add(currentPage);
        }

    }

    public void inquireClick(View v) {
        Log.println(Log.INFO, targetID, "Inquire Button Click");

        if(currentPage != 2) {
//            setContentView(R.layout.inquire_page);
//
//            clearAllButtonBackground();
//            inquireBtn.setBackgroundColor(getResources().getColor(R.color.deepPurple));
//            inquireTxt.setBackgroundColor(getResources().getColor(R.color.deepPurple));
//
//            ViewGroup scanArea = (ViewGroup) findViewById(R.id.scanArea);
//
//            mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
////            setContentView(mScannerView);
//
//            mScannerView.setAutoFocus(true);
//            mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
//            mScannerView.startCamera();         // Start camera
//
//            scanArea.addView(mScannerView);
//
//
//            timerOn = false;
//            mBluetoothLeScanner.stopScan(scanCallback);
//            mScanning = false;
            initInquirePage();

            currentPage = 2;
            pageStack.add(currentPage);
            Log.println(Log.DEBUG, targetID, "pageStack = "+pageStack.toString());
        }

    }

    public void toolsClick(View v) {
        Log.println(Log.INFO, targetID, "Tools Button Click");

        if(currentPage != 3) {

            initToolsPage();

            currentPage = 3;
            pageStack.add(currentPage);
            Log.println(Log.DEBUG, targetID, "pageStack = "+pageStack.toString());
        }

    }

    public void settingClick(View v) {
        Log.println(Log.INFO, targetID, "Setting Button Click");

        clearTopButtonBackground();
        settingBtn.setBackgroundColor(getResources().getColor(R.color.deepPurple));
        settingTxt.setBackgroundColor(getResources().getColor(R.color.deepPurple));

        currentPage = 4;
    }

    public void detailClick(View v) {
        Log.println(Log.INFO, targetID, "Detail Button Click");

        if(subFunction != 1) {
            initInquirePage();
        }

//        clearAllButtonBackground();
//        detailBtn.setBackgroundColor(Color.BLUE);

    }

    public void commodityClick(View v) {
        Log.println(Log.INFO, targetID, "Commodity Button Click");

//        setContentView(R.layout.inquire_search_page);
//        subFunction = 2;

        if(subFunction != 2) {
            initInquireCommodityPage();
        }

//        clearAllButtonBackground();
//        commodityBtn.setBackgroundColor(getResources().getColor(R.color.deepPurple));
//        commodityTxt.setBackgroundColor(getResources().getColor(R.color.deepPurple));

//        clearAllButtonBackground();
//        commodityBtn.setBackgroundColor(Color.BLUE);

    }

    public void shelfClick(View v) {
        Log.println(Log.INFO, targetID, "Shelf Button Click");

        clearAllButtonBackground();
        shelfBtn.setBackgroundColor(getResources().getColor(R.color.deepPurple));
        shelfTxt.setBackgroundColor(getResources().getColor(R.color.deepPurple));

//        clearAllButtonBackground();
//        shelfBtn.setBackgroundColor(Color.BLUE);

    }

    public void storeClick(View v) {
        Log.println(Log.INFO, targetID, "Store Button Click");

        clearAllButtonBackground();
        storeBtn.setBackgroundColor(getResources().getColor(R.color.deepPurple));
        storeTxt.setBackgroundColor(getResources().getColor(R.color.deepPurple));

//        clearAllButtonBackground();
//        storeBtn.setBackgroundColor(Color.BLUE);

    }




    private void getBluetoothAdapterAndLeScanner(){
        // Get BluetoothAdapter and BluetoothLeScanner.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();

        mScanning = false;
    }

    /*
    to call startScan (ScanCallback callback),
    Requires BLUETOOTH_ADMIN permission.
    Must hold ACCESS_COARSE_LOCATION or ACCESS_FINE_LOCATION permission to get results.
     */
    private void scanLeDevice(final boolean enable) {
        Log.println(Log.INFO, targetID, "scanLeDevice");
//        tools.appendLog("scanLeDevice");
        if (enable  && !mScanning) {
            title.setVisibility(View.GONE);
            if(!deviceInfo.isEmpty()) {
                Log.println(Log.INFO, targetID, "deviceInfo.isNotEmpty");
//                tools.appendLog("deviceInfo.isNotEmpty");
                deviceInfo.clear();
//                listViewLE.invalidateViews();
            }
//            listBluetoothDevice.clear();
//            listViewLE.invalidateViews();

//            r = new Runnable (){
//                @Override
//                public void run() {
//                    mBluetoothLeScanner.stopScan(scanCallback);
//                    listViewLE.invalidateViews();
//
//                    tools.toastNow(mContext, "掃描藍芽裝置結束", Color.WHITE);
////                    tools.appendLog("scan finish");
//
////                    Toast.makeText(MainActivity.this,
////                            "Scan timeout",
////                            Toast.LENGTH_LONG).show();
//
//                    mScanning = false;
//                }
//            };
//
//            mHandler.postDelayed(r, SCAN_PERIOD);


            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable (){
                @Override
                public void run() {

                    //因為在更換到WebView時, 會stopScan, 因此;在此多判斷是否早已經stopScan了
                    if(mScanning) {
                        mBluetoothLeScanner.stopScan(scanCallback);
//                        listViewLE.invalidateViews();
                        tools.toastNow(mContext, "掃描藍芽裝置結束", Color.WHITE);
//                    tools.appendLog("scan finish");

//                    Toast.makeText(MainActivity.this,
//                            "Scan timeout",
//                            Toast.LENGTH_LONG).show();

                        mScanning = false;

                        if(!requestParameter.isEmpty()) {

//                            Log.println(Log.DEBUG, targetID, "IDParameter --> "+idParameter.toString());
//                            Log.println(Log.DEBUG, targetID, "backupIDParameter --> "+backupIDParameter.toString());
//                            int count = idParameter.size();
//                            int count1 = backupIDParameter.size();

                            Log.println(Log.DEBUG, targetID, "equalItems requestParameter --> "+requestParameter.toString());
                            Log.println(Log.DEBUG, targetID, "equalItems backupRequestParameter --> "+backupRequestParameter.toString());

                            int count = requestParameter.size();
                            int count1 = backupRequestParameter.size();
                            Boolean equalItems = true;
                            if(count == count1) {
                                for (int i = 0; i < count; i++) {
                                    String beaconID = requestParameter.get(i).get(Eddystone);
                                    if (!backupRequestParameter.toString().contains(beaconID)) {
                                        equalItems = false;
                                        backupRequestParameter = requestParameter;
                                        Log.println(Log.INFO, targetID, "IDParameter Not Equal2");
                                        break;
                                    }
                                }
                            }
                            else {
                                equalItems = false;
//                                backupIDParameter = idParameter;
                                backupRequestParameter = requestParameter;
                                Log.println(Log.INFO, targetID, "IDParameter Not Equal1");
                            }

                            Log.println(Log.DEBUG, targetID, "equalItems --> "+equalItems);

//                            Log.println(Log.DEBUG, targetID, "IDParameter Equal --> "+equalItems);
//                            Log.println(Log.DEBUG, targetID, "IDParameter Size --> "+count);
//                            Log.println(Log.DEBUG, targetID, "requestParameterx --> "+requestParameter.toString());
//                            Log.println(Log.DEBUG, targetID, "IDParameter --> "+idParameter.toString());
//                            Log.println(Log.DEBUG, targetID, "backupIDParameter --> "+backupIDParameter.toString());
                            if(!equalItems) {
                                Log.println(Log.INFO, targetID, "equalItems send Post");
                                String url = "http://vorder.net/demo/url_test.php";
                                JSONArray test = tools.HashMapArray2JSONArray(requestParameter);
                                Log.println(Log.DEBUG, targetID, "equalItems postResult -->"+test);
                                String postResult = tools.sendPostCoomad(url, test.toString());

                                Log.println(Log.DEBUG, targetID, "equalItems postResult1 -->"+postResult);


//                            JSONArray test = tools.HashMapArray2JSONArray(requestParameter);
//                            Log.println(Log.DEBUG, targetID, "deviceInfo1 test--> " + test.toString());
//
//                            OkHttpPostHandler locationTask = new OkHttpPostHandler();
//                            String url = "http://vorder.net/demo/url_test.php";
//                            //以下的方式可以拿到async task執行後回傳的result
//                            String asyncResult = "";
//                            try {
////                        asyncResult = locationTask.execute(url, eddyStone).get();
//                                asyncResult = locationTask.execute(url, test.toString()).get();
//                                Log.println(Log.DEBUG, targetID, "asyncResult = " + asyncResult);
//
//                            } catch (InterruptedException e) {
//                                // TODO Auto-generated catch block
//                                e.printStackTrace();
//                                Log.println(Log.ERROR, targetID, "InterruptedException Error" + e);
//                            } catch (ExecutionException e) {
//                                // TODO Auto-generated catch block
//                                e.printStackTrace();
//                                Log.println(Log.ERROR, targetID, "ExecutionException Error" + e);
//                            }
//
//                            Log.println(Log.INFO, targetID, "asyncResult Data : " + asyncResult);
//                            HashMap<String, String> asyncResult2JSON = tools.jsonObjectConvertToHashMap(asyncResult);


                                mWebView.loadUrl("javascript: display(" + postResult + ");");
                            }
//                            backupRequestParameter = new ArrayList<HashMap<String,String>>();
//                            idParameter = new ArrayList<HashMap<String,String>>();
                        }
                        else {
                            Log.println(Log.INFO, targetID, "requestParameter is Empty");
                        }

                        requestParameter = new ArrayList<HashMap<String, String>>();
                    }
                }
            }, SCAN_PERIOD);

//            //scan specified devices only with ScanFilter
//            ScanFilter scanFilter =
//                    new ScanFilter.Builder()
//                            .setServiceUuid(BluetoothLeService.ParcelUuid_GENUINO101_ledService)
//                            .build();
//            List<ScanFilter> scanFilters = new ArrayList<ScanFilter>();
//            scanFilters.add(scanFilter);
//
//            ScanSettings scanSettings =
//                    new ScanSettings.Builder().build();
//
//            mBluetoothLeScanner.startScan(scanFilters, scanSettings, scanCallback);

//            tools.appendLog("startScan");
            mBluetoothLeScanner.startScan(scanCallback);
//            tools.appendLog("set mScanning=true");
            mScanning = true;
//            scanButton.setEnabled(false);
            Log.println(Log.DEBUG, targetID, "scanLeDevice mScanning xx1-->"+mScanning);
        } else {
//            tools.appendLog("stopScan");
            mBluetoothLeScanner.stopScan(scanCallback);
//            tools.appendLog("set mScanning=false");
            mScanning = false;
//            scanButton.setEnabled(true);
            Log.println(Log.DEBUG, targetID, "scanLeDevice mScanning xx2-->"+mScanning);
        }

        Log.println(Log.DEBUG, targetID, "scanLeDevice mScanning -->"+mScanning);
    }

    private ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);

            String uuid = "468e";
            String resultString = result.toString();
            System.out.println("result => " + resultString);
//            tools.appendLog("result => " + resultString);
            int rssiData = result.getRssi();
            System.out.println("rssiData => " + rssiData);
//            tools.appendLog("rssiData => " + rssiData);
            SparseArray<byte[]> manufacturerSpecificData = result.getScanRecord().getManufacturerSpecificData();
            if(manufacturerSpecificData != null) {
                int key = manufacturerSpecificData.keyAt(0);
                System.out.println("key : " + key);
                if(key != 0) {
//            tools.appendLog("key : " + key);
                    byte[] broadcastData = manufacturerSpecificData.get(key);
//                System.out.println("broadcastData : " + broadcastData.toString());
//            appendLog("broadcastData : " + broadcastData.toString());

//
//            System.out.println("broadcastData[0] : " + broadcastData[0]);
//            System.out.println("broadcastData[1] : " + broadcastData[1]);
//
//            String xx0 = Integer.toHexString(broadcastData[0]);
//            String xx1 = broadcastData[1] < 0
//                    ? Integer.toHexString(broadcastData[1]+65536).substring(2) :    Integer.toHexString(broadcastData[1]);
//
//            System.out.println("broadcastData[0] : " + xx0);
//            System.out.println("broadcastData[1] : " + xx1);


                    //以下的兩個值就是uuid 468e
//            if(broadcastData[0] == 70 && broadcastData[1] == -114) {
//
//            //Copy byte[0] & byte[1]  to  uuidByte Array
//            byte[] uuidByte = Arrays.copyOfRange(broadcastData,0,2);
//            String uuidHexString = String.valueOf(Hex.encodeHex(uuidByte));

                    Log.println(Log.INFO, targetID, "not Empty");

                    //Copy byte[0] & byte[1]  然後轉換成HexString
                    String uuidHexString = String.valueOf(Hex.encodeHex(Arrays.copyOfRange(broadcastData, 0, 2)));
//            String uuidHexString = "468e";
//            uuidHexString = "02766f7264657203676738394d7a";
                    System.out.println("uuidHexString : " + uuidHexString);
//                tools.appendLog("uuidHexString : " + uuidHexString);
                    if (uuidHexString.equals(uuid)) {
                        System.out.println("--------------->95 : Got It");

                        String eddyStone = String.valueOf(Hex.encodeHex(Arrays.copyOfRange(broadcastData, 2, broadcastData.length)));
                        System.out.println("eddyStone : " + eddyStone);

//                    OkHttpPostHandler locationTask = new OkHttpPostHandler();
//                    String url = "http://vorder.net/demo/url_test.php";
////        locationTask.execute(url, textString);
//
//                    //以下的方式可以拿到async task執行後回傳的result
//                    String asyncResult = "";
//                    try {
//                        asyncResult = locationTask.execute(url, eddyStone).get();
//                        Log.println(Log.DEBUG, targetID, "asyncResult = " + asyncResult);
//
//                    } catch (InterruptedException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                        Log.println(Log.ERROR, targetID, "InterruptedException Error" + e);
//                    } catch (ExecutionException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                        Log.println(Log.ERROR, targetID, "ExecutionException Error" + e);
//                    }
//
//                    Log.println(Log.INFO, targetID, "asyncResult Data : "+asyncResult);
//                    HashMap<String, String> asyncResult2JSON = tools.jsonObjectConvertToHashMap(asyncResult);
//
//                    Log.println(Log.DEBUG, targetID, "asyncResult2JSON = " + asyncResult2JSON.toString());
//                    Log.println(Log.DEBUG, targetID, "asyncResult name = " + asyncResult2JSON.get("name"));
//                    Log.println(Log.DEBUG, targetID, "asyncResult url = " + asyncResult2JSON.get("url"));

                        String aaa = "02766f7264657203676738394d7a";
                        aaa = "02766f7264657203504b3576387a";

                        //轉換方法三:
                        String asciiString = convertHexByteArray2UrlString(broadcastData);
                        String asciiString1 = convertHexToString(aaa);
                        System.out.println("asciiString : " + asciiString);
                        System.out.println("asciiString1: " + asciiString1);


                        HashMap<String, String> item = new HashMap<String, String>();
                        item.put(DeviceID, result.getDevice().toString());
//                    item.put(ProductName, asyncResult2JSON.get("name"));
////                    item.put(UrlID, asciiString);
//                    item.put(UrlID, asyncResult2JSON.get("url"));
                        item.put(RSSI, String.valueOf(result.getRssi()) + " db");
                        item.put(Eddystone, eddyStone);
//                    item.put(Eddystone, "02766f7264657203504b3576387a");


//                deviceInfo.add(item);

                        if (title.getVisibility() == View.GONE) {
                            title.setVisibility(View.VISIBLE);
                        }

                        addBluetoothDevice(item);

                    }
                }
//                addBluetoothDevice(result.getDevice());
            }

            else {
                Log.println(Log.INFO, targetID, "broadcastData is Null");
//                tools.appendLog("broadcastData is Null");
            }

//            //以下是之前的作法
//            String hexByte = String.valueOf(Hex.encodeHex(broadcastData));
//            System.out.println("broadcastData : " + hexByte);
//
//            if(hexByte.contains(uuid)) {
//                System.out.println("--------------->95 : Got It");
//                String urlHexString = hexByte.replace(uuid, "");
//                System.out.println("urlString : " + urlHexString);
//
//                //轉換方法一:
//                String asciiString = convertHexToString(urlHexString);
//                System.out.println("asciiString : "+asciiString);
//
//                //轉換方法二:
//                String newAsciiString = convertHexString2UrlString(urlHexString);
//                System.out.println("newAsciiString : "+newAsciiString);
//
//                addBluetoothDevice(result.getDevice());
//
//            }
//
//
        }

//        @Override
//        public void onBatchScanResults(List<ScanResult> results) {
//            super.onBatchScanResults(results);
//            for(ScanResult result : results){
////                String aaa = result.toString();
////                System.out.println("result ======XXXX> " + aaa);
//                addBluetoothDevice(result.getDevice());
//            }
//        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
//            Toast.makeText(MainActivity.this, "onScanFailed: " + String.valueOf(errorCode), Toast.LENGTH_LONG).show();
            tools.toastNow(mContext, "掃描藍芽裝置失敗", Color.WHITE);
        }

        private void addBluetoothDevice(HashMap item) {
            Log.println(Log.DEBUG, targetID, "requestParameter 3 -->"+requestParameter);
            if (!deviceInfo.toString().contains(item.get(DeviceID).toString())) {
                Log.println(Log.INFO, targetID, "Add Item");
                deviceInfo.add(item);

                HashMap<String, String> requestItem = new HashMap<String, String>();
                HashMap<String, String> idRequestItem = new HashMap<String, String>();

                requestItem.put(Eddystone, (String) item.get(Eddystone));
                requestItem.put(RSSI1, ((String) item.get(RSSI)).replace(" db",""));
                requestParameter.add(requestItem);

//                idRequestItem.put(Eddystone, (String) item.get(Eddystone));
//                idParameter.add(idRequestItem);

                Log.println(Log.DEBUG, targetID, "requestParameter 2 -->"+requestParameter);


                requestItem = new HashMap<String, String>();

                requestItem.put(Eddystone, "02766f7264657203504b3576387a");
                requestItem.put(RSSI1, String.valueOf(-35));
                requestParameter.add(requestItem);

//                idRequestItem.put(Eddystone, (String) item.get(Eddystone));
//                idParameter.add(idRequestItem);

                Log.println(Log.DEBUG, targetID, "requestParameter 1 -->"+requestParameter);

//                listViewLE.invalidateViews();
            }
        }

//        private void addBluetoothDevice(BluetoothDevice device){
//            if(!listBluetoothDevice.contains(device)){
//                listBluetoothDevice.add(device);
//                listViewLE.invalidateViews();
//            }
//        }

    };

    //方法一:
    //%%%%%%%%%%%%%%%%%%%%%% HEX to ASCII %%%%%%%%%%%%%%%%%%%%%%
    public String convertHexToString(String hex){

        String ascii="";
        String str = "";

        // Convert hex string to "even" length
        int rmd,length;
        length=hex.length();
        rmd =length % 2;
        if(rmd==1)
            hex = "0"+hex;

        // split into two characters
        for( int i=0; i<hex.length()-1; i+=2 ){

            //split the hex into pairs
            String pair = hex.substring(i, (i + 2));
            if(i==0) {
                switch(pair) {
                    case "00":
                        str = "http://www.";
                        break;
                    case "01":
                        str = "https://www.";
                        break;
                    case "02":
                        str = "http://";
                        break;
                    case "03":
                        str = "https://";
                        break;
                }

            }
            else {
                //convert hex to decimal
                int dec = Integer.parseInt(pair, 16);
                str=CheckCode(dec);
                if(str.equals("n/a")) {
                    switch (pair) {
                        case "00":
                            str = ".com/";
                            break;
                        case "01":
                            str = ".org/";
                            break;
                        case "02":
                            str = ".edu/";
                            break;
                        case "03":
                            str = ".net/";
                            break;
                        case "04":
                            str = ".info/";
                            break;
                        case "05":
                            str = ".biz/";
                            break;
                        case "06":
                            str = ".gov/";
                            break;
                        case "07":
                            str = ".com";
                            break;
                        case "08":
                            str = ".org";
                            break;
                        case "09":
                            str = ".edu";
                            break;
                        case "0a":
                            str = ".net";
                            break;
                        case "0b":
                            str = ".info";
                            break;
                        case "0c":
                            str = ".biz";
                            break;
                        case "0d":
                            str = ".gov";
                            break;
                    }
                }
            }
//            ascii=ascii+" "+str;
            ascii=ascii+str;
        }
        return ascii;
    }

    public String CheckCode(int dec){
        String str;

        //convert the decimal to character
        str = Character.toString((char) dec);

        if(dec<32 || dec>126 && dec<161)
            str="n/a";
        return str;
    }

    //方法二:
    public String convertHexString2UrlString(String urlHexString) {

        byte[] urlByte = new byte[0];

        try {
            urlByte = Hex.decodeHex(urlHexString.toCharArray());
        } catch (DecoderException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
//        String urlByteString = new String(urlByte);
//        Log.println(Log.DEBUG, "XXXXX", "test length = "+urlByte.length);
//        Log.println(Log.DEBUG, "XXXXX", "testString = "+urlByteString);

        for (byte theByte : urlByte)
        {
            System.out.println("Single Byte : "+Integer.toHexString(theByte));
        }

        String urlString="";
        String str = "";

        for(int i=0; i<urlByte.length; i++) {
            if(i==0) {
                switch (urlByte[i]) {
                    case 0x00:
                        str = "http://www.";
                        break;
                    case 0x01:
                        str = "https://www.";
                        break;
                    case 0x02:
                        str = "http://";
                        break;
                    case 0x03:
                        str = "https://";
                        break;
                }
            }
            else {
                //Single byte to String
                str = new String(new byte[] { urlByte[i] });
                if(urlByte[i]<32 || urlByte[i]>126 && urlByte[i]<161) {
                    switch (urlByte[i]) {
                        case 0x00:
                            str = ".com/";
                            break;
                        case 0x01:
                            str = ".org/";
                            break;
                        case 0x02:
                            str = ".edu/";
                            break;
                        case 0x03:
                            str = ".net/";
                            break;
                        case 0x04:
                            str = ".info/";
                            break;
                        case 0x05:
                            str = ".biz/";
                            break;
                        case 0x06:
                            str = ".gov/";
                            break;
                        case 0x07:
                            str = ".com";
                            break;
                        case 0x08:
                            str = ".org";
                            break;
                        case 0x09:
                            str = ".edu";
                            break;
                        case 0x0a:
                            str = ".net";
                            break;
                        case 0x0b:
                            str = ".info";
                            break;
                        case 0x0c:
                            str = ".biz";
                            break;
                        case 0x0d:
                            str = ".gov";
                            break;
                    }
                }
            }
            urlString=urlString+str;
//            Log.println(Log.DEBUG, "XXXXX", "ascii = "+ascii);
        }
        return urlString;
    }

    public String convertHexByteArray2UrlString(byte[] broadcastData) {

        String urlString="";
        String str = "";

        for(int i=2; i<broadcastData.length; i++) {
            if(i==2) {
                System.out.println("Single Byte : "+Integer.toHexString(broadcastData[i]));
                switch (broadcastData[i]) {
                    case 0x00:
                        str = "http://www.";
                        break;
                    case 0x01:
                        str = "https://www.";
                        break;
                    case 0x02:
                        str = "http://";
                        break;
                    case 0x03:
                        str = "https://";
                        break;
                }
            }
            else {
                //Single byte to String
                str = new String(new byte[] { broadcastData[i] });
                if(broadcastData[i]<32 || broadcastData[i]>126 && broadcastData[i]<161) {
                    switch (broadcastData[i]) {
                        case 0x00:
                            str = ".com/";
                            break;
                        case 0x01:
                            str = ".org/";
                            break;
                        case 0x02:
                            str = ".edu/";
                            break;
                        case 0x03:
                            str = ".net/";
                            break;
                        case 0x04:
                            str = ".info/";
                            break;
                        case 0x05:
                            str = ".biz/";
                            break;
                        case 0x06:
                            str = ".gov/";
                            break;
                        case 0x07:
                            str = ".com";
                            break;
                        case 0x08:
                            str = ".org";
                            break;
                        case 0x09:
                            str = ".edu";
                            break;
                        case 0x0a:
                            str = ".net";
                            break;
                        case 0x0b:
                            str = ".info";
                            break;
                        case 0x0c:
                            str = ".biz";
                            break;
                        case 0x0d:
                            str = ".gov";
                            break;
                    }
                }
            }
            urlString=urlString+str;
//            Log.println(Log.DEBUG, "XXXXX", "ascii = "+ascii);
        }
        return urlString;
    }

//    View.OnClickListener scanDevice = new View.OnClickListener() {
//        @Override
//        public void onClick(final View v) {
//            scanLeDevice(true);
//        }
//    };
//
////    public void trunOnPermission(View v) {
//    View.OnClickListener trunOnPermission = new View.OnClickListener() {
//        @Override
//        public void onClick(final View v) {
//            Log.println(Log.INFO, targetID, "trunOnPermission Button Click");
//    //        setContentView(R.layout.activity_main);
//
//            //Check  ACCESS_FINE_LOCATION  Permission
//            int permissionCheck = ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION);
//            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
//                if (ActivityCompat.shouldShowRequestPermissionRationale(currentActivity, Manifest.permission.ACCESS_FINE_LOCATION)) {
////                    Toast.makeText(mContext, "被要求授權BLE位置資料", Toast.LENGTH_SHORT).show();
//                    tools.toastNow(mContext, "被要求授權BLE位置資料", Color.WHITE);
//                } else {
//                    if (Build.VERSION.SDK_INT >= 23) {
//                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, RQS_ENABLE_ACCESS_FINE_LOCATION);
//                    }
//                }
//            } else {
////                Toast.makeText(mContext, "已經同意使用位置資訊訊息了", Toast.LENGTH_SHORT).show();
//                tools.toastNow(mContext, "已經同意使用位置資訊訊息了", Color.WHITE);
//            }
//        }
//    };

    public void scan_permission(View v) {
        if(btn4Scan) {
            Log.println(Log.INFO, targetID, "Scan BLE Device");

            setContentView(R.layout.webview_page);

            mWebView = (WebView) findViewById(R.id.webview);
            mWebView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.webColor));
//        mWebView.setBackgroundColor(Color.parseColor("#d9f1d8"));

            mWebView.getSettings().setJavaScriptEnabled(true);
            mWebView.setWebViewClient(mWebViewClient);
            mWebView.setWebChromeClient(new WebChromeClient());
            mWebView.addJavascriptInterface(new JavaScriptInterface(this, mWebView), "BeaconsAppHandler");

            mWebView.loadUrl("http://vorder.net/demo/advertising_list.php");



//            tools.appendLog("Scan BLE Device");
//            scanLeDevice(true);
        }
        else {
            Log.println(Log.INFO, targetID, "trunOnPermission");

            //Check  ACCESS_FINE_LOCATION  Permission
            int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)+ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    +ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
            Log.println(Log.INFO, targetID, "permissionCheck -->"+permissionCheck);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED){
                Log.println(Log.INFO, targetID, "PERMISSION_GRANTED11");
                Log.println(Log.INFO, targetID, "howRequestPermissionRationale -->"+ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION));
                //ACCESS_FINE_LOCATION在turn on時, 其ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION) = true
                //WRITE_EXTERNAL_STORAGE在turn on時, 其ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) = false
                if ((!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION))
                        || (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE))
                        || (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) ) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, RQS_ENABLE_ACCESS_FINE_LOCATION);
//                    requestPermissions(new String[]{Manifest.permission.CAMERA}, cameraRequestCode);
                        Log.println(Log.INFO, targetID, "PERMISSION_GRANTED13");
                    }
                }



            //Check  ACCESS_FINE_LOCATION  Permission
//            int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)+ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
//            Log.println(Log.INFO, targetID, "permissionCheck -->"+permissionCheck);
//            if (permissionCheck != PackageManager.PERMISSION_GRANTED){
//                Log.println(Log.INFO, targetID, "PERMISSION_GRANTED11");
//                Log.println(Log.INFO, targetID, "howRequestPermissionRationale -->"+ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION));
//                if ((!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION))
//                        || (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE))) {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE}, RQS_ENABLE_ACCESS_FINE_LOCATION);
//                        Log.println(Log.INFO, targetID, "PERMISSION_GRANTED13");
//                    }
//                }
//            Log.println(Log.INFO, targetID, "howRequestPermissionRationale -->"+ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE));
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE))  {
//                if (Build.VERSION.SDK_INT >= 23) {
//                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, RQS_ENABLE_ACCESS_FINE_LOCATION);
//                    Log.println(Log.INFO, targetID, "PERMISSION_GRANTED13");
//                }
//            }

//                    || (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE))){
////                Toast.makeText(this, "被要求授權BLE位置資料", Toast.LENGTH_SHORT).show();
//                tools.toastNow(mContext, "被要求授權BLE位置資料", Color.WHITE);
//                Log.println(Log.INFO, targetID, "PERMISSION_GRANTED12");
//            }else{
//                if (Build.VERSION.SDK_INT >= 23) {
//                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE}, RQS_ENABLE_ACCESS_FINE_LOCATION);
//                    Log.println(Log.INFO, targetID, "PERMISSION_GRANTED13");
//                }
//            }
            }else{
//            Toast.makeText(this,  "已經同意使用位置資訊訊息了", Toast.LENGTH_SHORT).show();
                tools.toastNow(mContext, "已經同意使用位置資訊訊息了", Color.WHITE);
                deviceReady = true;
            }
        }
    }

    WebViewClient mWebViewClient = new WebViewClient() {
//        @Override
//        public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
//
//            view.loadUrl(url);
//            return true;
//        }
        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed();
        }
    };

    public void previousClick(View v) {
        Log.println(Log.INFO, targetID, "previous Button Click");

        onBackPressed();
//        setContentView(R.layout.activity_main1);
//
//        title = (LinearLayout) findViewById(R.id.llBorder);
//
////        turnOnPermission = (Button) findViewById(R.id.turnPermission);
//        scanButton = (Button) findViewById(R.id.scanButton);
//        listViewLE = (ListView)findViewById(R.id.lelist);
//
////        listBluetoothDevice = new ArrayList<>();
//        adapterLeScanResult = new SimpleAdapter(
//                this, deviceInfo, R.layout.style_listview, new String[] { ProductName, UrlID, RSSI }, new int[]{R.id.deviceID, R.id.urlID, R.id.rssiData});
//        listViewLE.setAdapter(adapterLeScanResult);
//        listViewLE.setOnItemClickListener(scanResultOnItemClickListener);
//
//        mHandler = new Handler();
//
//        deviceInfo.clear();
//        listViewLE.invalidateViews();

//        getWindow().getDecorView().findViewById(android.R.id.content).invalidate();
//        Intent i = new Intent(this, MainActivity.class);
//        startActivity(i);
//        finish();
//        overridePendingTransition(0, 0);

    }

    public void javascriptCallFinished(final String json){

        Log.println(Log.INFO, targetID, "JavaScript Handler Finish");

    }

    public void clearTopButtonBackground() {

        messageBtn = (ImageButton) findViewById(R.id.messageButton);
        messageBtn.setBackgroundColor(0);
        inquireBtn = (ImageButton) findViewById(R.id.inquireButton);
        inquireBtn.setBackgroundColor(0);
        toolsBtn = (ImageButton) findViewById(R.id.toolsButton);
        toolsBtn.setBackgroundColor(0);
        settingBtn = (ImageButton) findViewById(R.id.settingButton);
        settingBtn.setBackgroundColor(0);

        android.view.ViewGroup.LayoutParams params;
//        android.view.ViewGroup.MarginLayoutParams mParams;

        //Layout的數值必須用Pixel來作為單位
        ConstraintLayout topCL = (ConstraintLayout) findViewById(R.id.top_cl);
        params = topCL.getLayoutParams();
//        params.width = wPixels / 2;
        params.height = hPixels / 10;
        topCL.setLayoutParams(params);

        LinearLayout topLL = (LinearLayout) findViewById(R.id.top_ll);
        params = topLL.getLayoutParams();
        params.height = hPixels / 10;
        topLL.setLayoutParams(params);


        //textSize必須用dp的數值來作為單位
        float textSize = (float) (hPixels / 50);
        Log.println(Log.DEBUG, targetID, "textSize_pixels --> "+textSize);
        float textSizeDP = tools.convertPixelToDp(textSize, mContext);
        Log.println(Log.DEBUG, targetID, "textSize_dps --> "+textSizeDP);

        messageTxt = (TextView) findViewById(R.id.messageText);
        messageTxt.setTextSize(textSizeDP);

        messageTxt = (TextView) findViewById(R.id.messageText);
        messageTxt.setBackgroundColor(0);
        messageTxt.setTextSize(textSizeDP);
        inquireTxt = (TextView) findViewById(R.id.inquireText);
        inquireTxt.setBackgroundColor(0);
        inquireTxt.setTextSize(textSizeDP);
        toolsTxt = (TextView) findViewById(R.id.toolsText);
        toolsTxt.setBackgroundColor(0);
        toolsTxt.setTextSize(textSizeDP);
        settingTxt = (TextView) findViewById(R.id.settingText);
        settingTxt.setBackgroundColor(0);
        settingTxt.setTextSize(textSizeDP);

    }

    public void clearBottomButtonBackground() {

        detailBtn = (ImageButton) findViewById(R.id.detailButton);
        detailBtn.setBackgroundColor(0);
        commodityBtn = (ImageButton) findViewById(R.id.commodityButton);
        commodityBtn.setBackgroundColor(0);
        shelfBtn = (ImageButton) findViewById(R.id.shelfButton);
        shelfBtn.setBackgroundColor(0);
        storeBtn = (ImageButton) findViewById(R.id.storeButton);
        storeBtn.setBackgroundColor(0);

        detailTxt = (TextView) findViewById(R.id.detailText);
        detailTxt.setBackgroundColor(0);
        commodityTxt = (TextView) findViewById(R.id.commodityText);
        commodityTxt.setBackgroundColor(0);
        shelfTxt = (TextView) findViewById(R.id.shelfText);
        storeTxt = (TextView) findViewById(R.id.storeText);
        storeTxt.setBackgroundColor(0);
    }

    public void clearAllButtonBackground() {

        messageBtn = (ImageButton) findViewById(R.id.messageButton);
        messageBtn.setBackgroundColor(0);
        inquireBtn = (ImageButton) findViewById(R.id.inquireButton);
        inquireBtn.setBackgroundColor(0);
        toolsBtn = (ImageButton) findViewById(R.id.toolsButton);
        toolsBtn.setBackgroundColor(0);
        settingBtn = (ImageButton) findViewById(R.id.settingButton);
        settingBtn.setBackgroundColor(0);

        detailBtn = (ImageButton) findViewById(R.id.detailButton);
        detailBtn.setBackgroundColor(0);
        commodityBtn = (ImageButton) findViewById(R.id.commodityButton);
        commodityBtn.setBackgroundColor(0);
        shelfBtn = (ImageButton) findViewById(R.id.shelfButton);
        shelfBtn.setBackgroundColor(0);
        storeBtn = (ImageButton) findViewById(R.id.storeButton);
        storeBtn.setBackgroundColor(0);

        android.view.ViewGroup.LayoutParams params;
//        android.view.ViewGroup.MarginLayoutParams mParams;

        //Layout的數值必須用Pixel來作為單位
        ConstraintLayout topCL = (ConstraintLayout) findViewById(R.id.top_cl);
        params = topCL.getLayoutParams();
//        params.width = wPixels / 2;
        params.height = hPixels / 10;
        topCL.setLayoutParams(params);

        ConstraintLayout bottomCL = (ConstraintLayout) findViewById(R.id.bottom_cl);
        params = bottomCL.getLayoutParams();
//        params.width = wPixels / 2;
        params.height = hPixels / 10;
        bottomCL.setLayoutParams(params);

        LinearLayout topLL = (LinearLayout) findViewById(R.id.top_ll);
        params = topLL.getLayoutParams();
//        params.width = wPixels / 2;
        params.height = hPixels / 10;
        topLL.setLayoutParams(params);

        LinearLayout bottomLL = (LinearLayout) findViewById(R.id.bottom_ll);
        params = bottomLL.getLayoutParams();
//        params.width = wPixels / 2;
        params.height = hPixels / 10;
        bottomLL.setLayoutParams(params);

//        LinearLayout amLL = (LinearLayout) findViewById(R.id.am_ll);
//        mParams = (ViewGroup.MarginLayoutParams) amLL.getLayoutParams();
//        mParams.setMargins(0, (int) hPixels / 10, 0, 0);

        //textSize必須用dp的數值來作為單位
        float textSize = (float) (hPixels / 50);
        Log.println(Log.DEBUG, targetID, "textSize_pixels --> "+textSize);
        float textSizeDP = tools.convertPixelToDp(textSize, mContext);
        Log.println(Log.DEBUG, targetID, "textSize_dps --> "+textSizeDP);

        messageTxt = (TextView) findViewById(R.id.messageText);
        messageTxt.setTextSize(textSizeDP);

        messageTxt = (TextView) findViewById(R.id.messageText);
        messageTxt.setBackgroundColor(0);
        messageTxt.setTextSize(textSizeDP);
        inquireTxt = (TextView) findViewById(R.id.inquireText);
        inquireTxt.setBackgroundColor(0);
        inquireTxt.setTextSize(textSizeDP);
        toolsTxt = (TextView) findViewById(R.id.toolsText);
        toolsTxt.setBackgroundColor(0);
        toolsTxt.setTextSize(textSizeDP);
        settingTxt = (TextView) findViewById(R.id.settingText);
        settingTxt.setBackgroundColor(0);
        settingTxt.setTextSize(textSizeDP);

        detailTxt = (TextView) findViewById(R.id.detailText);
        detailTxt.setBackgroundColor(0);
        detailTxt.setTextSize(textSizeDP);
        commodityTxt = (TextView) findViewById(R.id.commodityText);
        commodityTxt.setBackgroundColor(0);
        commodityTxt.setTextSize(textSizeDP);
        shelfTxt = (TextView) findViewById(R.id.shelfText);
        shelfTxt.setBackgroundColor(0);
        shelfTxt.setTextSize(textSizeDP);
        storeTxt = (TextView) findViewById(R.id.storeText);
        storeTxt.setBackgroundColor(0);
        storeTxt.setTextSize(textSizeDP);
    }

    public void doWebView(String url) {

        Log.println(Log.INFO, targetID, "doWebView");
//        Log.e("handler", url); // Prints scan results
        Log.println(Log.DEBUG, targetID, "URL->"+url);

        setContentView(R.layout.inquire_webview_page);


//        android.view.ViewGroup.LayoutParams params;
        android.view.ViewGroup.MarginLayoutParams mParams;

        mWebView = (WebView) findViewById(R.id.webview1);
        mParams = (ViewGroup.MarginLayoutParams) mWebView.getLayoutParams();
        mParams.setMargins(0, (int) hPixels / 10, 0, hPixels / 10);

        clearAllButtonBackground();
        inquireBtn.setBackgroundColor(getResources().getColor(R.color.deepPurple));
        inquireTxt.setBackgroundColor(getResources().getColor(R.color.deepPurple));

//        WebView mWebView = (WebView) findViewById(R.id.webview1);
        mWebView.setBackgroundColor(ContextCompat.getColor(this, R.color.webColor));
//        mWebView.setBackgroundColor(Color.parseColor("#d9f1d8"));
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(mWebViewClient);
        mWebView.setWebChromeClient(new WebChromeClient());

        mWebView.loadUrl(url);

        subFunction = 5;

//        doWebView = true;
//        mWebView.loadUrl("http://www.google.com.tw");
//        mWebView.loadUrl("https://goo.gl/TktWSt");
//        mWebView.loadUrl("https://www.facebook.com");
//        mWebView.loadUrl("https://www.google.com/");

//        mWebView.reload();
//        Button previousButton  = (Button) xxx.findViewById(R.id.previousButton);


    }

    public void initMessagePage() {
        setContentView(R.layout.message_page);

        android.view.ViewGroup.MarginLayoutParams mParams;

        mWebView = (WebView) findViewById(R.id.webview);
        mParams = (ViewGroup.MarginLayoutParams) mWebView.getLayoutParams();
        mParams.setMargins(0, (int) hPixels / 10, 0, 0);


//        mWebView = (WebView) findViewById(R.id.webview);
        mWebView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.webColor));
//        mWebView.setBackgroundColor(Color.parseColor("#d9f1d8"));

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(mWebViewClient);
        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.addJavascriptInterface(new JavaScriptInterface(this, mWebView), "BeaconsAppHandler");

        mWebView.loadUrl("http://vorder.net/demo/advertising_list.php");

        if (mScannerView != null) {
            Log.println(Log.INFO, targetID, "Timer stopCamera");
            mScannerView.stopCamera();
            mScannerView = null;
        }

        clearTopButtonBackground();
        messageBtn.setBackgroundColor(getResources().getColor(R.color.deepPurple));
        messageTxt.setBackgroundColor(getResources().getColor(R.color.deepPurple));

        deviceInfo = new ArrayList<HashMap<String,String>>();
        requestParameter = new ArrayList<HashMap<String,String>>();
        backupRequestParameter = new ArrayList<HashMap<String,String>>();
//        idParameter = new ArrayList<HashMap<String,String>>();
//        backupIDParameter = new ArrayList<HashMap<String,String>>();

        timerOn = true;
        mHandler.post(rescanRunnable);

//            tools.appendLog("Scan BLE Device");
//        scanLeDevice(true);
    }

    public void initInquirePage() {
        setContentView(R.layout.inquire_page);

//        android.view.ViewGroup.LayoutParams params;
        android.view.ViewGroup.MarginLayoutParams mParams;

        LinearLayout inquireLL = (LinearLayout) findViewById(R.id.inquire_ll);
        mParams = (ViewGroup.MarginLayoutParams) inquireLL.getLayoutParams();
        mParams.setMargins(0, (int) hPixels / 10, 0, hPixels / 10);

        clearAllButtonBackground();
        inquireBtn.setBackgroundColor(getResources().getColor(R.color.deepPurple));
        inquireTxt.setBackgroundColor(getResources().getColor(R.color.deepPurple));


        ViewGroup scanArea = (ViewGroup) findViewById(R.id.scanArea);

        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
//            setContentView(mScannerView);

        mScannerView.setAutoFocus(true);
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();         // Start camera

        scanArea.addView(mScannerView);


        timerOn = false;
        mBluetoothLeScanner.stopScan(scanCallback);
        mScanning = false;
//        doWebView = false;
        subFunction = 1;
    }

    public void initInquireCommodityPage() {
        setContentView(R.layout.inquire_search_page);

//        android.view.ViewGroup.LayoutParams params;
        android.view.ViewGroup.MarginLayoutParams mParams;

        LinearLayout inquireLL = (LinearLayout) findViewById(R.id.inquire_ll);
        mParams = (ViewGroup.MarginLayoutParams) inquireLL.getLayoutParams();
        mParams.setMargins(0, (int) hPixels / 10, 0, hPixels / 10);

        clearAllButtonBackground();
        inquireBtn.setBackgroundColor(getResources().getColor(R.color.deepPurple));
        inquireTxt.setBackgroundColor(getResources().getColor(R.color.deepPurple));


        ViewGroup scanArea = (ViewGroup) findViewById(R.id.scanArea);

        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
//            setContentView(mScannerView);

        mScannerView.setAutoFocus(true);
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();         // Start camera

        scanArea.addView(mScannerView);


//        timerOn = false;
//        mBluetoothLeScanner.stopScan(scanCallback);
//        mScanning = false;
//        doWebView = false;
        subFunction = 2;
    }

    public void initToolsPage() {

        setContentView(R.layout.tools_page);
        tempView = this.findViewById(android.R.id.content);

        android.view.ViewGroup.MarginLayoutParams mParams;

        gridView = (GridView)findViewById(R.id.main_page_gridview);
        mParams = (ViewGroup.MarginLayoutParams) gridView.getLayoutParams();
        mParams.setMargins(0, (int) hPixels / 10, 0, 0);

        clearTopButtonBackground();
        toolsBtn.setBackgroundColor(getResources().getColor(R.color.deepPurple));
        toolsTxt.setBackgroundColor(getResources().getColor(R.color.deepPurple));

        List<Map<String, Object>> items = new ArrayList<Map<String,Object>>();
        for (int i = 0; i < image.length; i++) {
            Map<String, Object> item = new HashMap<String, Object>();
            item.put("image", image[i]);
            item.put("text", imgText[i]);
            items.add(item);
        }
        SimpleAdapter adapter = new SimpleAdapter(this,
                items, R.layout.grid_item, new String[]{"image", "text"},
                new int[]{R.id.image, R.id.text});
        gridView = (GridView)findViewById(R.id.main_page_gridview);
        gridView.setNumColumns(4);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), "Your choice is " + imgText[position],
                        Toast.LENGTH_SHORT).show();

                switch(position) {
                    case 0:
                        inquireClick(tempView);
                        break;

                }
            }
        });



    }



}


