package com.example.chhua.scan_beacon;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import fr.arnaudguyon.xmltojsonlib.JsonToXml;
import fr.arnaudguyon.xmltojsonlib.XmlToJson;

import static com.example.chhua.scan_beacon.Tools.getDensity;


public class LoginActivity extends AppCompatActivity {

//    Button logoutButton;
//    ImageButton menuButton, settingButton, logoutButton;
//    TextClock clock;
    EditText loginUser, loginPassword;
    Button loginButton;
    ImageButton infoButton, facebookButton, googleButton;

    String targetID = this.getClass().getSimpleName();
    Tools tools = new Tools();

    byte[] ivByte = new byte[0];
    byte[] keyByte = new byte[0];

    Context mContext = this;

    ImageView logo;
    android.view.ViewGroup.LayoutParams params;
    LinearLayout llParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_login);

        Log.println(Log.INFO, targetID, "System onCreate");

        Pair<Integer, Integer> tempPixels = tools.getDisplayParameter(getApplicationContext());

        int wPixels = tempPixels.first;
        int hPixels = tempPixels.second;
//        int hPixels = tempPixels.second;
        System.out.println("wwwidthPixels : " + wPixels);

//        float screenDensity = tools.getDensity(mContext);
//        float wDP = tools.convertPixelToDp(wPixels, mContext);
//
//        System.out.println("widthDP : " + wDP);

        logo = (ImageView) findViewById(R.id.circleImageView3);
        params = logo.getLayoutParams();
        params.width = (wPixels * 3) / 5;
//        params.height = wPixels / 2;
        logo.setLayoutParams(params);


        loginUser = (EditText) findViewById(R.id.loginUser);
        params = loginUser.getLayoutParams();

        int width = (wPixels * 7) / 10;
        params.width = width;
        loginUser.setLayoutParams(params);

        int textSize = (hPixels * 4) / 100;
        float textSizeDp = tools.convertPixelToDp(textSize, mContext);
        loginUser.setTextSize(textSizeDp);

        loginPassword = (EditText) findViewById(R.id.loginPassword);
        params = loginPassword.getLayoutParams();
        params.width = width;
        loginPassword.setLayoutParams(params);

        loginPassword.setTextSize(textSizeDp);


        loginButton = (Button) findViewById(R.id.loginButton);
        params = loginButton.getLayoutParams();
        params.width = width;
        loginButton.setLayoutParams(params);

        loginButton.setTextSize(textSizeDp);

////        int height = (width / 7) + 20;
//        int height = wPixels / 7;
//        infoButton = (ImageButton) findViewById(R.id.infoButton1);
//        params = infoButton.getLayoutParams();
////        params.width = height;
//        params.height = height;
//        infoButton.setLayoutParams(params);
//
//        facebookButton = (ImageButton) findViewById(R.id.infoButton2);
//        params = facebookButton.getLayoutParams();
//        params.height = height;
//        facebookButton.setLayoutParams(params);
//
//        googleButton = (ImageButton) findViewById(R.id.infoButton3);
//        params = googleButton.getLayoutParams();
//        params.height = height;
//        googleButton.setLayoutParams(params);


        //隱藏ActionBar(App最上面的工具欄)
        android.support.v7.app.ActionBar m_myActionBar = getSupportActionBar();
        m_myActionBar.hide();

        //把螢幕固定為
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //使用SharedPreferences settings = getSharedPreferences("settingParameter", MODE_PRIVATE)去儲存程式一開始執行時需要的資料
        // MODE_PRIVATE = 0, MODE_WORLD_READABLE = 1,  MODE_WORLD_WRITEABLE = 2
        // Save Data
        //	    settings.edit()
        //	    .putString("discountStatus", String.valueOf(discountStatus))
        //        .commit();
        // Restore Data
        //         Boolean discountStatus = Boolean.valueOf(settings.getString("discountStatus", "不存在時的預設值"));
        // Clear SharedPreferences
        //settings.edit().clear().commit();
        //Clear All  SharedPreferences
        // haredPreferences.Editor.clear().commit();

        String ivAES = "c159eaca8ac6a235e4090467b3241706";
        String keyAES = "3a83960e2289c83dc8d119a12144f1edd39a5b2fe033c152";

        try {
            ivByte = Hex.decodeHex(ivAES.toCharArray());
        } catch (DecoderException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.println(Log.DEBUG, targetID, "ivByte = " + ivByte);
        Log.println(Log.DEBUG, targetID, "ivByte length = " + ivByte.length);

        try {
            keyByte = Hex.decodeHex(keyAES.toCharArray());
        } catch (DecoderException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.println(Log.DEBUG, targetID, "keyByte = " + keyByte);
        Log.println(Log.DEBUG, targetID, "keyByte length = " + keyByte.length);

        //AES  Decrypt Example
//        String ivAES =  "c159eaca8ac6a235e4090467b3241706";
//        String keyAES = "3a83960e2289c83dc8d119a12144f1edd39a5b2fe033c152";
//        String textString = "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";
//        String textString = "3VP0VIrkML13Xvmr0xFBaSmxfr0Ussksf2Ad/7oTbUnmdTUiCmKfRhLSKrRa5y1vXYZU285oTUD580RNBgyy4LhACj6ukWuUOImc1nYfMtzwxCdHz8InKAUj6/g8MyCN";

//        key='3a83960e2289c83dc8d119a12144f1edd39a5b2fe033c152'
//        iv='c159eaca8ac6a235e4090467b3241706'

//        byte[] xx2 = tools.hexToBin(ivAES);
//        Log.println(Log.DEBUG, targetID, "xx2 = "+xx2);
//        Log.println(Log.DEBUG, targetID, "xx2 length = "+xx2.length);

//        byte[] ivByte = new byte[0];
//        try {
//            ivByte = Hex.decodeHex(ivAES.toCharArray());
//        } catch (DecoderException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        Log.println(Log.DEBUG, targetID, "ivByte = "+ivByte);
//        Log.println(Log.DEBUG, targetID, "ivByte length = "+ivByte.length);

//        byte[] ivByte = new BigInteger(ivAES,16).toByteArray();
//        //You don't have to shift at all. The sign bit is the most significant (= leftmost) bit of your byte array. Since you know your numbers will always be positive,
//        // it is guaranteed to be 0. However, the array as a whole is right-aligned.
//        //So there are two cases: your left-most byte is 0x00 or not. If it is 0x00 you can safely drop it:
//        //If it is not 0, then you cannot drop it - but your array will already be in the representation you want, so you don't have to do anything.
//        if (ivByte[0] == 0) {
//            byte[] tmp = new byte[ivByte.length - 1];
//            System.arraycopy(ivByte, 1, tmp, 0, tmp.length);
//            ivByte = tmp;
//        }
//        Log.println(Log.DEBUG, targetID, "ivByte = "+ivByte);
//        Log.println(Log.DEBUG, targetID, "ivByte length = "+ivByte.length);

//        byte[] xx3 = tools.hexToBin(keyAES);
//        Log.println(Log.DEBUG, targetID, "xx3 = "+xx3);
//        Log.println(Log.DEBUG, targetID, "xx3 length = "+xx3.length);

//        byte[] keyByte = new byte[0];
//        try {
//            keyByte = Hex.decodeHex(keyAES.toCharArray());
//        } catch (DecoderException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        Log.println(Log.DEBUG, targetID, "keyByte = "+keyByte);
//        Log.println(Log.DEBUG, targetID, "keyByte length = "+keyByte.length);

//        byte[] keyByte = new BigInteger(keyAES,16).toByteArray();
//        //You don't have to shift at all. The sign bit is the most significant (= leftmost) bit of your byte array. Since you know your numbers will always be positive,
//        // it is guaranteed to be 0. However, the array as a whole is right-aligned.
//        //So there are two cases: your left-most byte is 0x00 or not. If it is 0x00 you can safely drop it:
//        //If it is not 0, then you cannot drop it - but your array will already be in the representation you want, so you don't have to do anything.
//        if (keyByte[0] == 0) {
//            byte[] tmp = new byte[keyByte.length - 1];
//            System.arraycopy(keyByte, 1, tmp, 0, tmp.length);
//            keyByte = tmp;
//        }
//        Log.println(Log.DEBUG, targetID, "keyByte = "+keyByte);
//        Log.println(Log.DEBUG, targetID, "keyByte length = "+keyByte.length);

//        logoutButton = (ImageButton) findViewById(R.id.logoutButton);
//        logoutButton.setVisibility(View.GONE);
//        clock = (TextClock) findViewById(R.id.textClock);
//        clock.setVisibility(View.GONE);
//        settingButton = (ImageButton) findViewById(R.id.settingButton);
//        settingButton.setVisibility(View.GONE);
//        menuButton = (ImageButton) findViewById(R.id.imageButton);
//        menuButton.setVisibility(View.GONE);

//        loginUser = (EditText) findViewById(R.id.loginUser);

        //XML to JSON Example
        String xmlString = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<library>\n" +
                "    <owner>John Doe</owner>\n" +
                "    <book id=\"007\">James Bond</book>\n" +
                "    <book id=\"000\">Book for the dummies</book>\n" +
                "</library>";  // some XML String previously created
        XmlToJson xmlToJson = new XmlToJson.Builder(xmlString).build();

        // convert to a Json String
        String jsonString = xmlToJson.toString();

        Log.println(Log.DEBUG, targetID, "xmlString = " + xmlString);
        Log.println(Log.DEBUG, targetID, "jsonString = " + jsonString);

        //JSON to XML Example
        JSONObject student1 = new JSONObject();
        try {
            student1.put("id", "3");
            student1.put("name", "NAME OF STUDENT");
            student1.put("year", "3rd");
            student1.put("curriculum", "Arts");
            student1.put("birthday", "5/5/1993");

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.println(Log.ERROR, targetID, "JSONException Error" + e);
        }

        JSONObject student2 = new JSONObject();
        try {
            student2.put("id", "2");
            student2.put("name", "NAME OF STUDENT2");
            student2.put("year", "4rd");
            student2.put("curriculum", "scicence");
            student2.put("birthday", "5/5/1993");

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.println(Log.ERROR, targetID, "JSONException Error" + e);
        }

        JSONArray jsonArray = new JSONArray();

        jsonArray.put(student1);
        jsonArray.put(student2);

        JSONObject studentsObj = new JSONObject();

        try {
            studentsObj.put("Students", jsonArray);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.println(Log.ERROR, targetID, "JSONException Error" + e);
        }

        String jsonStr = studentsObj.toString();

        System.out.println("jsonString: " + jsonStr);

        JsonToXml jsonToXml = new JsonToXml.Builder(jsonStr).build();

        String studentXml1String = jsonToXml.toString();
        Log.println(Log.DEBUG, targetID, "studentXml1String = " + studentXml1String);

//        String textString = "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";
        String textString = "3VP0VIrkML13Xvmr0xFBaSmxfr0Ussksf2Ad/7oTbUnmdTUiCmKfRhLSKrRa5y1vXYZU285oTUD580RNBgyy4LhACj6ukWuUOImc1nYfMtzwxCdHz8InKAUj6/g8MyCN";

//        byte[] test = Base64.decode(textString.getBytes(), Base64.DEFAULT);
//        Log.println(Log.DEBUG, targetID, "test = "+test);

        byte[] decrypByte = tools.DecryptAES(ivByte, keyByte, Base64.decode(textString.getBytes(), Base64.DEFAULT));
//        byte[] decrypByte = tools.DecryptAES(xx2, xx3, Base64.decode(textString.getBytes(), Base64.DEFAULT));
        Log.println(Log.DEBUG, targetID, "DecryptAES 1= " + decrypByte);
        String decrypString = new String(decrypByte);
        Log.println(Log.DEBUG, targetID, "DecryptAES = " + decrypString);

        //AES Encrypt Example
        byte[] encryptByte = tools.EncryptAES(ivByte, keyByte, decrypByte);
//        byte[] encryptByte = tools.EncryptAES(xx2, xx3, decrypByte);
        Log.println(Log.DEBUG, targetID, "aa1 = " + encryptByte);
        String encryptString = Base64.encodeToString(encryptByte, Base64.DEFAULT);
        Log.println(Log.DEBUG, targetID, "aa2 = " + encryptString);


        OkHttpPostHandler locationTask = new OkHttpPostHandler();
        String url = "https://service.misa.com.tw/api/user_login.php";
//        locationTask.execute(url, textString);

        //以下的方式可以拿到async task執行後回傳的result
        String asyncResult = "";
        try {
            asyncResult = locationTask.execute(url, textString).get();
            Log.println(Log.DEBUG, targetID, "asyncResult = " + asyncResult);

        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.println(Log.ERROR, targetID, "InterruptedException Error" + e);
        } catch (ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.println(Log.ERROR, targetID, "ExecutionException Error" + e);
        }

        Log.println(Log.INFO, targetID, "After Async Task");
        byte[] resultDecrypByte = tools.DecryptAES(ivByte, keyByte, Base64.decode(asyncResult.getBytes(), Base64.DEFAULT));
//        byte[] decrypByte = tools.DecryptAES(xx2, xx3, Base64.decode(textString.getBytes(), Base64.DEFAULT));
        Log.println(Log.DEBUG, targetID, "DecryptAES 1= " + resultDecrypByte);
        String resultDecrypString = new String(resultDecrypByte);
        Log.println(Log.DEBUG, targetID, "DecryptAES = " + resultDecrypString);

//
//        String ivAES = "1234567890abcdef";
//        String keyAES = "12345678901234567890123456789012";
//        String textString = "小黑人的Android教室 !";
//
//        //將IvAES、KeyAES、TextAES轉成byte[]型態帶入EncryptAES進行加密，再將回傳值轉成字串
//
//        byte[] TextByte = tools.EncryptAES(ivAES.getBytes(), keyAES.getBytes(), textString.getBytes());
//        String TEXT = Base64.encodeToString(TextByte, Base64.DEFAULT);
////加密字串結果為 : xq/WqrKuXIqLxw1BM4GJoAqPQp6Zh+vqLykVAj2GHFY=
//        Log.println(Log.DEBUG, targetID, "EncryptAES = "+TEXT);
//
//        //將IvAES、KeyAES、Text(加密文字:xq/WqrKuXIqLxw1BM4GJoAqPQp6Zh+vqLykVAj2GHFY=)轉成byte[]型態帶入DecryptAES進行解密，再將回傳值轉成字串
//        byte[] test = Base64.decode(TEXT.getBytes(), Base64.DEFAULT);
////        Log.println(Log.DEBUG, targetID, "test = "+test);
//        TextByte = tools.DecryptAES(ivAES.getBytes(), keyAES.getBytes(), Base64.decode(TEXT.getBytes(), Base64.DEFAULT));
//        TextByte = tools.DecryptAES(ivAES.getBytes(), keyAES.getBytes(), test);
//        TEXT = new String(TextByte);
////解密字串結果為 : 小黑人的Android教室 !
//        Log.println(Log.DEBUG, targetID, "DecryptAES = "+TEXT);
//
    }

    @Override
    protected void onResume() {
        Log.println(Log.INFO, targetID, "System onResume");
        super.onResume();

    }

    @Override
    protected void onPause() {
        Log.println(Log.INFO, targetID, "System onPause");
        super.onPause();

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
    public void onBackPressed() {
        Log.println(Log.INFO, targetID, "System onBackPressed");
        super.onBackPressed();
        // your code.
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.println(Log.INFO, targetID, "System onMonitor Rotate");

    }

    @Override
    public void onStop(){
        Log.println(Log.INFO, targetID, "System onStop");
        super.onStop();

    }

    @Override
    public void onDestroy(){
        Log.println(Log.INFO, targetID, "System onDestory");
        super.onDestroy();

    }

    public void loginClick(View v) {
        Log.println(Log.INFO, targetID, "Login Button Click");

        Intent intent = new Intent(this, MainActivity.class);
//            intent.putExtra("notification", false);
        startActivity(intent);
//        startActivityForResult(intent,1);
        finish();
        overridePendingTransition(0, 0);

    }

    public void forgotPasswordClick(View v) {
        Log.println(Log.INFO, targetID, "forgotPassword Button Click");

    }
//
//    public void transportClick(View v) {
//        Log.println(Log.INFO, targetID, "alarm Button Click");
//
//        Intent i = new Intent(this, TransportPageActivity.class);
//        startActivity(i);
//        finish();
//        overridePendingTransition(0, 0);
//
//    }
//
//    public void recordClick(View v) {
//        Log.println(Log.INFO, targetID, "alarm Button Click");
//
//        Intent i = new Intent(this, RecordPageActivity.class);
//        startActivity(i);
//        finish();
//        overridePendingTransition(0, 0);
//
//    }
//
//    public void positionClick(View v) {
//        Log.println(Log.INFO, targetID, "alarm Button Click");
//
//        Intent i = new Intent(this, PositionPageActivity.class);
//        startActivity(i);
//        finish();
//        overridePendingTransition(0, 0);
//
//    }

//    public class OkHttpPostHandler extends AsyncTask<String, Void, String> {
//
//        OkHttpClient client = new OkHttpClient();
//
//        @Override
//        protected String doInBackground(String...params) {
////             //方法 一:
////            Request.Builder builder = new Request.Builder();
////            builder.url(params[0]);
////            builder.addHeader("Accept-Language", "zh-TW");
////            Request request = builder.build();
//
////             //方法 三:
////            HttpUrl url = new HttpUrl.Builder()
////                    .scheme("https")
////                    .host("maps.googleapis.com")
////                    .addPathSegment("maps")
////                    .addPathSegment("api")
////                    .addPathSegment("geocode")
////                    .addPathSegment("json")
////                    .addQueryParameter("latlng", "22.6259594,120.3208026")
////                    .addQueryParameter("language", "zh-TW")
////                    .build();
////
////            Log.d("My App", "url = "+url);
////
////            Request request = new Request.Builder()
////                    //給予想要解析網頁的網址, 要使用中文時, 請在querry中加入&language=zh-TW
//////                .url("https://maps.googleapis.com/maps/api/geocode/json?latlng=22.6259594,120.3208026&language=zh-TW")
////                    .url(url)
////                    .build();
//
//            //方法 二:
//
//            FormBody.Builder formBuilder = new FormBody.Builder()
//                    .add("data_pack", params[1]);
//            RequestBody body = formBuilder.build();
//            Log.println(Log.DEBUG, targetID, "params[0] = "+params[0]);
//            Log.println(Log.DEBUG, targetID, "params[1] = "+params[1]);
//            Log.println(Log.DEBUG, targetID, "body = "+body.toString().toString());
//            Request request = new Request.Builder()
//                    .url(params[0])
//                    .post(body)
//                    .addHeader("Content-type", "text/x-markdown; charset=utf-8")
//                    .build();
//
//            try {
//                Response response = client.newCall(request).execute();
//                String result = response.body().string();
////                        Log.d("OKHTTP", json);
//                Log.println(Log.DEBUG, targetID, "Result = "+result);
//                return result;
//            }catch (Exception e){
//                e.printStackTrace();
//                Log.println(Log.ERROR, targetID, "Error");
//            }
//            return null;
//        }
//
//        @Override
//        //  若是使用String asyncResult = locationTask.execute(url, textString).get();的方式去拿到回傳的async data,
//        // 以下的onPostExecute就可以不需要作了
//        protected void onPostExecute(String result) {
//            super.onPostExecute(result);
//
//            byte[] decrypByte = tools.DecryptAES(ivByte, keyByte, Base64.decode(result.getBytes(), Base64.DEFAULT));
////        byte[] decrypByte = tools.DecryptAES(xx2, xx3, Base64.decode(textString.getBytes(), Base64.DEFAULT));
//            Log.println(Log.DEBUG, targetID, "DecryptAES 1= "+decrypByte);
//            String decrypString = new String(decrypByte);
//            Log.println(Log.DEBUG, targetID, "DecryptAES = "+decrypString);
//        }
//    }

}
