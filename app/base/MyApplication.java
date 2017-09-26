package kr.co.infonetworks.www.app.base;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

/**
 * Created by andy on 2016. 7. 14..
 */
public class MyApplication extends android.app.Application {
    private static final String TAG = "MyApplication";
    private boolean mainActivityRunning = false;

    @Override
    public void onCreate() {
        super.onCreate();

        /** 나눔 폰트를 기본으로 설정한다 */
        FontOverride.setDefaultFont(this, "monospace", "fonts/NanumGothic.ttf");


        //Stric Mode설정
        //http://javaexpert.tistory.com/483
        if (Constants.Config.DEVELOPER_MODE && Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().penaltyDropBox().build());

            //2016-10-29 MyItemActivity에서 닫기시 프로그램 종료로 인하여 막음
            //http://stackoverflow.com/questions/5956132/android-strictmode-instancecountviolation?newreg=c1793cdf984048b88e2bff3aa4a410ee
            //StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyDeath().penaltyLog().penaltyDropBox().build());
        }

        //전역적 어플리케이션 정보접근을 위해서 context사용
        initImageLoader(getApplicationContext());


        //activity의 상태를 파악하기 위해서 설정
        //http://dev.re.kr/74
        registerActivityLifecycleCallbacks(mActivityLifecycleCallbacks);


    }

    //MyGcmListnerService.java에서 호출
    public boolean getMainActivityRunning(){
        return mainActivityRunning;
    }

    public static void initImageLoader(Context context){
        //Android Universal Image Loader
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.writeDebugLogs(); // Remove for release app

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build());

    }

    private ActivityLifecycleCallbacks mActivityLifecycleCallbacks =
            new ActivityLifecycleCallbacks() {
                @Override
                public void onActivityCreated(Activity activity, Bundle savedInstanceState){

                    if (activity.getClass().getName().contains("MainActivity")) {
                        mainActivityRunning = true;
                        Log.d(TAG,"onActivityCreated getName: " + activity.getClass().getName());
                    }


                }

                @Override
                public void onActivityStarted(Activity activity) {
//                   Log.d(TAG,"onActivityStarted :" + activity.getClass().getName());

                }

                @Override
                public void onActivityResumed(Activity activity) {
//                    Log.d(TAG,"onActivityResumed :" + activity.getClass().getName());

                }

                @Override
                public void onActivityPaused(Activity activity) {
//                    Log.d(TAG,"onActivityPaused :" + activity.getClass().getName());

                }

                @Override
                public void onActivityStopped(Activity activity) {
//                    Log.d(TAG,"onActivityStopped :" + activity.getClass().getName());

                }

                @Override
                public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
//                    Log.d(TAG,"onActivitySaveInstanceState :" + activity.getClass().getName());

                }

                @Override
                public void onActivityDestroyed(Activity activity) {

                    if (activity.getClass().getName().contains("MainActivity")) {
                        mainActivityRunning = false;
                        Log.d(TAG,"onActivityDestroyed :" + activity.getClass().getName());
                    }

                }
            };

}