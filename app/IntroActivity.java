package kr.co.infonetworks.www.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;

/**
 * Created by andy on 2016. 11. 24..
 * http://zedd.tistory.com/1 , keyword = android 인트로화면
 */

public class IntroActivity extends Activity {
    private final static String TAG = "IntroActivity";
    private Handler h;//핸들러 선언


    private static final Uri URI = Uri.parse("content://com.google.android.gsf.gservices");
    private static final String ID_KEY = "android_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //인트로화면이므로 타이틀바를 없앤다
        setContentView(R.layout.activity_intro);


// TODO: 2016. 11. 24.
     //   version check를 넣느다

        h= new Handler(); //딜래이를 주기 위해 핸들러 생성
        h.postDelayed(mrun, 2000); // 딜레이 ( 런어블 객체는 mrun, 시간 2초)
    }

    Runnable mrun = new Runnable(){
        @Override
        public void run(){
            Intent i = new Intent(IntroActivity.this, LoginActivity.class); //인텐트 생성(현 액티비티, 새로 실행할 액티비티)
            startActivity(i);
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            //overridePendingTransition 이란 함수를 이용하여 fade in,out 효과를줌. 순서가 중요
        }
    };
    //인트로 중에 뒤로가기를 누를 경우 핸들러를 끊어버려 아무일 없게 만드는 부분
    //미 설정시 인트로 중 뒤로가기를 누르면 인트로 후에 홈화면이 나옴.
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        h.removeCallbacks(mrun);
    }

    //http://javaexpert.tistory.com/574
    //app version check
    public static String getVersionName(Context context) {
    Log.d(TAG, "here in getVersionName");
    try {
        PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.d(TAG, "err NameNotFoundException -> " + e.toString());
            return null;
        }
    }


    static String getAndroidId(Context context) {
        Log.d(TAG, "here in getAndroidId");
        String[] params = { ID_KEY };
        Cursor c = context.getContentResolver()
                .query(URI, null, null, params, null);

        if (!c.moveToFirst() || c.getColumnCount() < 2)
            return null;

        try {
            Log.d(TAG, "getAndroidId() : " + Long.toHexString(Long.parseLong(c.getString(1))));
            return Long.toHexString(Long.parseLong(c.getString(1)));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    //https://code.google.com/archive/p/android-market-api/
//    public static String getMarketVersionName(Context context) {
//        Log.i(TAG, "here in getMarketVersionName");
//
//        try {
//            MarketSession session = new MarketSession();
//            session.login(email, pass);
//            //Log.i(TAG, "session : "+session.getAuthSubToken());
//            session.getContext().setAndroidId(getAndroidId(context));
//            String query = "패키지명";
//
//            AppsRequest appsRequest = AppsRequest.newBuilder()
//                    .setQuery(query)
//                    .setStartIndex(0).setEntriesCount(1)
//                    .setOrderType(AppsRequest.OrderType.NEWEST)
//                    .setWithExtendedInfo(false)
//                    .build();
//
//            session.append(appsRequest, new Callback<appsresponse>() {
//
//                @Override
//                public void onResult(ResponseContext context, AppsResponse response) {
//                    //Log.i(TAG, "getAppCount() : " + response.getAppCount());
//                    for (int i=0; i<response.getappcount(); -="" getmarketversionname="" ?err="" log.i(tag,="" {="" e)="" (exception="" catch="" }="" session.flush();="" });="" marketversion="response.getApp(i).getVersion();" response.tostring());="" +="" ?="" :="" ?context.tostring()="" response.getapp(i).getversion());="" ?getversion()="" response.getapp(i).getversioncode());="" ?getversioncode()="" response.getapp(i).gettitle());="" ?gettitle()="" i++)=""> " + e.toString());
//            }
//
//            return marketVersion;
//        }

}