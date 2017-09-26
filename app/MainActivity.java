package kr.co.infonetworks.www.app;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import kr.co.infonetworks.www.app.activity.MyItemActivity;
import kr.co.infonetworks.www.app.activity.NotifyConfigurationFragment;
import kr.co.infonetworks.www.app.activity.OtItemActivity;
import kr.co.infonetworks.www.app.db.ExecUploadImage;
import kr.co.infonetworks.www.app.utility.ImageCacheUtility;
import kr.co.infonetworks.www.app.utility.ImageUtility;
import kr.co.infonetworks.www.app.utility.Utils;
import kr.co.infonetworks.www.app.utility.jakewharton.DiskLruImageCache;
import kr.co.infonetworks.www.app.activity.RequestFragment;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONObject;

import java.io.File;
import java.util.concurrent.ExecutionException;

/**
 * Created by andy on 2016. 5. 28..
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_CAMERA = 2;
    private static String TAG = "MainActivity";

    private String str_from;
    private int rnum;
    private int origin_rnum;
    private String req_type;

    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;
    private Toolbar toolbar;
    private ActionBarDrawerToggle mDrawerToggle;
    private ImageButton imageButton;
    private Uri mImageCaptureUri;
    private Uri mCropImageUri;
    private int imageFlag = 0;

    private TextView tv_userinfo;
    private TextView tv_telinfo;

    private SharedPreferences app_preference;

    private String uNum;
    private String profile_path;

    private TabFragment tabFragment;
    private RequestFragment requestFragment;
    private NotifyConfigurationFragment notifyConfigurationFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!Utils.isNetWork(this))  return;

        setContentView(R.layout.activity_main);

        /*
        * Notifiy를 click 했을때 intent를 받아서 처리해야 한다
        * */
        Intent intent = getIntent();
        str_from    = intent.getStringExtra("from");
        rnum        = intent.getIntExtra("rnum", 0);
        origin_rnum = intent.getIntExtra("origin_rnum", 0);
        req_type    = intent.getStringExtra("req_type");    //01매수,02답장,03답장확인


        /**
         *Setup the DrawerLayout and NavigationView
         */

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        /**
         * Lets inflate the very first fragment
         * Here , we are inflating the TabFragment as the first Fragment
         */


        mFragmentManager = getSupportFragmentManager();

        Log.d(TAG + "str_from: ", str_from);


        //2016-10-24 update
        //기본 설정후 , Notify에 의해 MainActivity로 들어온 경우 NotifyActivity Intent를  호출한다
        tabFragment = TabFragment.newInstance();
        requestFragment = new RequestFragment();
        notifyConfigurationFragment= new NotifyConfigurationFragment();

        mFragmentManager.
                beginTransaction().
                add(R.id.containerView, tabFragment,"tabFragment").
                add(R.id.containerView, requestFragment,"requestFragment").
                add(R.id.containerView, notifyConfigurationFragment,"notifyConfigurationFragment").
                show(tabFragment).
                hide(requestFragment).
                hide(notifyConfigurationFragment).
                commit();


//        switch (str_from) {
//
//            case "login":
//                Log.d(TAG + "moved: ", "login");
//                mFragmentTransaction.replace(R.id.containerView, TabFragment.newInstance()).commit();
//                break;
//            case "gcm":
//                Log.d(TAG + "moved: ", "gcm");
//                mFragmentTransaction.replace(R.id.containerView, TabFragment.newInstance()).commit();
//                break;
//            case "notify":
//                Log.d(TAG + "moved: ", "notify");
//               // mFragmentTransaction.replace(R.id.containerView, NotifyRequestFragment.newInstance(rnum)).commit();
//                break;
//            default:
//                Log.d(TAG + "moved: ", "default");
//                mFragmentTransaction.replace(R.id.containerView, TabFragment.newInstance()).commit();
//                break;
//        }


//        if ("login".equals(str_from)) {
//            //frame 영역을 TabFragment로 교체
//            mFragmentTransaction.replace(R.id.containerView, TabFragment.newInstance()).commit();
//        }else{
//            mFragmentTransaction.replace(R.id.containerView, ThreeFragment.newInstance()).commit();
//
//        }

        /**
         * Setup click events on the Navigation View Items.
         */

        //http://ljs93kr.tistory.com/16
        //navigation view Header View 관리
        mNavigationView = (NavigationView) findViewById(R.id.navigation);
        mNavigationView.setNavigationItemSelectedListener(this);


        mNavigationView.getMenu().getItem(0).setChecked(true); //first item selected

        //profile수정을 위한 image view를 가져온다
        //View headerView = mNavigationView.inflateHeaderView(R.layout.nav_header_main_0814);

        View headerView = mNavigationView.getHeaderView(0);


        //정보성 텍스트 뷰의 값을 지정한다
        //

        tv_userinfo = (TextView) headerView.findViewById(R.id.tv_userinfo);
        tv_telinfo = (TextView) headerView.findViewById(R.id.tv_telinfo);


        app_preference = PreferenceManager.getDefaultSharedPreferences(this);
        String str_userinfo = app_preference.getString("uName", "");
        str_userinfo = str_userinfo.concat("/");
        str_userinfo = str_userinfo.concat(app_preference.getString("cName", ""));

        String str_telinfo = app_preference.getString("tel", "");
        str_telinfo = str_telinfo.concat("/");
        str_telinfo = str_telinfo.concat(app_preference.getString("mobile", ""));

        tv_userinfo.setText(str_userinfo);
        tv_telinfo.setText(str_telinfo);

        profile_path = app_preference.getString("profile_path", "");

        //사용자 unique key => jpg file의 이름을 만들때 사용한다
        uNum = app_preference.getString("uNum", "0");

        imageButton = (ImageButton) headerView.findViewById(R.id.user_profile_photo);

        //저장된  profile이 있는 경우 호출하여 화면에 표시
        if (!"".equals(profile_path)) {
            Bitmap roundBitmap = ImageUtility.resizeImage(imageButton, profile_path);
            imageButton.setImageBitmap(roundBitmap);
        }


        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(),"click",Toast.LENGTH_LONG).show();

                DialogInterface.OnClickListener cameraListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        doTakePhotoAction();
                    }
                };

                DialogInterface.OnClickListener albumListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        doTakeAlbumAction();
                    }
                };

                DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                };

                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("사용할 이미지 선택")
                        .setPositiveButton("사진촬영", cameraListener)
                        .setNeutralButton("앨범선택", albumListener)
                        .setNegativeButton("취소", cancelListener)
                        .show();
            }
        });

        /**
         * Setup Drawer Toggle of the Toolbar
         */

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); //Enable Toolbar
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mDrawerToggle
                = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar,
                kr.co.infonetworks.www.app.R.string.drawer_open,
                kr.co.infonetworks.www.app.R.string.drawer_close);

        mDrawerLayout.addDrawerListener(mDrawerToggle);
        //Navigation Drawer와 화면과의 동기화를 위해서 onPostCreate,onConfigurationChanged 에
        //관련 로직을 추가한다


//        fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        //memorycache 생성
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        ImageCacheUtility.newInstance().createMemmoryCache(maxMemory);


        //diskcache 생성
        //위치 storage/emulated/0/Android/data/com.example.andy.second/cache/andy
        int DISK_CACHE_SIZE = 1024 * 1024 * 10; // 10MB
        DiskLruImageCache.newInstance().createDiskCache(this, "profile", DISK_CACHE_SIZE);


        if("notify".equals(str_from.toString())) {

            Intent notify_intent = null;
            switch (req_type) {

                case "01":
                    notify_intent = new Intent(this, OtItemActivity.class);
                    notify_intent.putExtra("param1",rnum);
                    break;
                case "02":
                    notify_intent = new Intent(this, MyItemActivity.class);;
                    notify_intent.putExtra("param1",origin_rnum);
                    break;
                case "03":
                    notify_intent = new Intent(this, OtItemActivity.class);
                    notify_intent.putExtra("param1",origin_rnum);
                    break;
                default:
                    return;
            }
            notify_intent.putExtra("from","notify");
            startActivity(notify_intent);

        }


    }



    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        //imagebutton view의 실제 사이즈를 알기위해서 사용
        //일반적으로 화면이 표시된 후에 작업을 해야 할 경우에 이 부분에서 실행한다
        //onCreate는 화면이 표시되기전이라 View의 크기를 불러오거나 프레임 애니메이션의 시작이 불가능하다
        //하지만 화면이 focusing될때 마다 실행되고 특정 API 이상에서만 동작한다
    }


    //back를 눌렀을때
    @Override
    public void onBackPressed() {

        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            //2017.07.17 수정 -- 뒤로 가는것을 막는다
            //super.onBackPressed();
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        //http://charlie0301.blogspot.kr/2013/09/android-side-menu-navigation-drawer.html
        //ActionBar와 NavigationDrawer와 연동 시 status의 동기화
        // (ActionBar 상에서의 indicator를 업데이트 하기 위해)
        // syncState()를 onPostCreate()에서 호출해 주는 것이 중요함.

        //화면이 맨처음에 열릴거나 , 수평/수직이 전환될때 호출된다
        //Log.d(TAG + "onPostCreate", "mDrawerToggle.syncState()");
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //Log.d(TAG + "onConfigurationChanged", "mDrawerToggle.onConfigurationChanged(newConfig)");
        //설정값들이 바뀌엇 Activity가 재시작될때
        //http://arabiannight.tistory.com/entry/318
        //Android 아이스크림 샌드위치(4.0) 이상 버전 화면 전환시 Activity 재생성 방지
        //
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //Actionbar가 보여질때 item을 추가한다 menu를 화면에 보여준다
        //메뉴를 보이지 않게 한다
        //getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case kr.co.infonetworks.www.app.R.id.action_settings:
                Toast.makeText(this, "action setting click", Toast.LENGTH_LONG).show();
                // User chose the "Settings" item, show the app settings UI...
                return true;

            case kr.co.infonetworks.www.app.R.id.action_logout:
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                Toast.makeText(this, "action logout click", Toast.LENGTH_LONG).show();


                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                //intent.putExtra("userid", Str_userid);
                //intent.putExtra("token", Str_token);

                //Log.d(TAG,"updated token: " + tmp_username)
                Log.d(TAG, "call intentService.startActivity(intent)");

                //http://202psj.tistory.com/532
                //http://mydevromance.tistory.com/15
                // 로그아웃을 눌렀을때 모든 스택의 내용을 지우고 로그인 페이지가 나오게 한다
                // 로그인 - gcm-main-....등등 의 순으로 스택에 쌓인다
                // 로그인 페이지만만 남겨 놓고 스택위에 있는 액티비티는 종료한다
                // 내것이 맨위에 있으면 재 사용한다
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
                        | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);


                return true;


            case kr.co.infonetworks.www.app.R.id.action_search:
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                Toast.makeText(this, "action_search click", Toast.LENGTH_LONG).show();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                Toast.makeText(this, "call super super ", Toast.LENGTH_LONG).show();
                return super.onOptionsItemSelected(item);

        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {


        switch (item.getItemId()){
            case kr.co.infonetworks.www.app.R.id.nav_first_layout:
                mFragmentManager.
                        beginTransaction().
                        show(tabFragment).
                        hide(requestFragment).
                        hide(notifyConfigurationFragment).
                        commit();

                break;
            case kr.co.infonetworks.www.app.R.id.nav_second_layout:
                mFragmentManager.
                        beginTransaction().
                        hide(tabFragment).
                        show(requestFragment).
                        hide(notifyConfigurationFragment).
                        commit();

                break;
            case kr.co.infonetworks.www.app.R.id.nav_notification:
                mFragmentManager.
                        beginTransaction().
                        hide(tabFragment).
                        hide(requestFragment).
                        show(notifyConfigurationFragment).
                        commit();

                break;
            case kr.co.infonetworks.www.app.R.id.nav_logout:
                //memory cache , disk cache를 삭제한다
                ImageCacheUtility.newInstance().clearCache();
                DiskLruImageCache.newInstance().clearCache();


                //UIL 삭제
                ImageLoader.getInstance().clearMemoryCache();
                ImageLoader.getInstance().clearDiskCache();

                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
                        | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;    //logout 인 경우 바로  return한다

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawer.closeDrawer(GravityCompat.START);

        return true;

//        if (item.getItemId() == R.id.nav_first_layout) {
//            mFragmentManager.
//                    beginTransaction().
//                    show(tabFragment).
//                    hide(requestFragment).
//                    commit();
//
////            mFragmentTransaction = mFragmentManager.beginTransaction();
////            //mFragmentTransaction.replace(R.id.containerView,new TabFragment()).commit();
////            mFragmentTransaction.replace(R.id.containerView, TabFragment.newInstance()).commit();
//        }
//
//        if (item.getItemId() == R.id.nav_second_layout) {
////            mFragmentTransaction = mFragmentManager.beginTransaction();
////            mFragmentTransaction.replace(R.id.containerView, new RequestFragment()).commit();
//            mFragmentManager.
//                    beginTransaction().
//                    hide(tabFragment).
//                    show(requestFragment).
//                    commit();
//
//        }



//        if (item.getItemId() == R.id.nav_logout) {
//
//            //memory cache , disk cache를 삭제한다
//            ImageCacheUtility.newInstance().clearCache();
//            DiskLruImageCache.newInstance().clearCache();
//
//
//            //UIL 삭제
//            ImageLoader.getInstance().clearMemoryCache();
//            ImageLoader.getInstance().clearDiskCache();
//
//
//
//            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
//                    | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(intent);
//            return true;    //logout 인 경우 바로  return한다
//
//        }
//
//
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
//        drawer.closeDrawer(GravityCompat.START);
//
//        return true;

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //UIL 삭제
        ImageLoader.getInstance().clearMemoryCache();
        ImageLoader.getInstance().clearDiskCache();
    }

    //------------------------------------------------
    // profile 처리
    //------------------------------------------------
    ////http://arabiannight.tistory.com/entry/안드로이드Android-사진-갤러리-Image-Crop-후-MMS-전송-하기
    //   --> 안드로이드 -> 카메라 에 있음
    //http://theeye.pe.kr/archives/1285
    //camera profile

    /**
     * 카메라에서 이미지 가져오기
     */
    public void doTakePhotoAction() {

        imageFlag = 0;

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // 임시로 사용할 파일의 경로를 생성
        String url = "tmp_" + String.valueOf(System.currentTimeMillis()) + ".JPEG";
        mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), url));

        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);

        // 특정기기에서 사진을 저장못하는 문제가 있어 다음을 주석처리 합니다.
        //intent.putExtra("return-data", true);
        startActivityForResult(intent, PICK_FROM_CAMERA);

    }


    /**
     * 앨범에서 이미지 가져오기
     */
    public void doTakeAlbumAction() {

        imageFlag = 1;

        // 앨범 호출
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }


    //여기서 부터 다시 해라
    //사진찍는데서는 오류가 발생한다

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK)
            return;

        //앨범일경우 1->0->2, 사진일경우 0->2 순으로 실행됨
        //앨범일 경우 content://media/external/images/media/148364
        //사진일 경우 file:///storage/emulated/0/tmp_1471956847836.jpg
        //파일 처리를 위해서는 content -> file 형식으로 바꾸어서 처리해야 함
        switch (requestCode) {
            //1
            case PICK_FROM_ALBUM: {
                Log.d(TAG, "1-PICK_FROM_ALBUM");

                mImageCaptureUri = data.getData();
                Log.d(TAG, "getPath:" + mImageCaptureUri.getPath().toString());

                // 이후의 처리가 카메라 부분과 같아 break 없이 진행 ==> 아래로 진행됨
            }
            //0
            case PICK_FROM_CAMERA: {
                Log.d(TAG, "0-PICK_FROM_CAMERA");

                // 이미지를 가져온 이후의 리사이즈할 이미지 크기를 결정합니다.
                // 이후에 이미지 크롭 어플리케이션을 호출하게 됩니다.

                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(mImageCaptureUri, "image/*");

                // Crop한 이미지를 저장할 Path
                intent.putExtra("output", mImageCaptureUri);

                // Return Data를 사용하면 번들 용량 제한으로 크기가 큰 이미지는
                // 넘겨 줄 수 없다.
                //intent.putExtra("return-data", true);
                startActivityForResult(intent, CROP_FROM_CAMERA);
                //사진을 자르는 액티비티 수행 ==> case CROP_FROM_CAMERA 를 수행하게 됨

                break;
            }
            //2
            case CROP_FROM_CAMERA: {
                Log.d(TAG, "2-CROP_FROM_CAMERA");

                // Crop 된 이미지를 넘겨 받습니다.
                Log.d(TAG, "mImageCaptureUri = " + mImageCaptureUri);


                String full_path = null;
                Bitmap photo = null;
                String selectedFilePath = null;

                //앨범 크롭을 한경우에는 content://media/external/images/media/148364
                //content -> file형태로 전환해야 함
                if (imageFlag == 1) {
                    selectedFilePath = getPathFromUri(mImageCaptureUri);
                }

                //카메라에서 크롭을 한경우에는 file:///storage/emulated/0/tmp_1471956847836.JPEG
                if (imageFlag == 0) {
                    selectedFilePath = mImageCaptureUri.getPath();
                }


//                //resize 수행
//                // Get the dimensions of the View
//                int targetW = imageButton.getWidth();
//                int targetH = imageButton.getHeight();
//
//                // Get the dimensions of the bitmap
//                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
//                bmOptions.inJustDecodeBounds = true;
//                BitmapFactory.decodeFile(selectedFilePath, bmOptions);
//                int photoW = bmOptions.outWidth;
//                int photoH = bmOptions.outHeight;
//
//                // Determine how much to scale down the image
//                int scaleFactor = Math.min(photoW/targetW, photoH/targetH);
//
//                Log.d(TAG,"scaleFactor: " + String.valueOf(scaleFactor)); //4 가 return됨
//                // Decode the image file into a Bitmap sized to fill the View
//                bmOptions.inJustDecodeBounds = false;
//                bmOptions.inSampleSize = scaleFactor;
//
//
//                photo = BitmapFactory.decodeFile(selectedFilePath, bmOptions);
//
//                int w = photo.getWidth(), h = photo.getHeight();
//
//                //Log.d(TAG, "width" + String.valueOf(w) + "--" + "height" + String.valueOf(h));
//                int radius = w > h ? h : w; // set the smallest edge as radius.
//                Bitmap roundBitmap = getCroppedBitmap(photo, radius);


                Bitmap roundBitmap1 = ImageUtility.resizeImage(imageButton, selectedFilePath);
                imageButton.setImageBitmap(roundBitmap1);

                //Bitmap Memory 관리
                //http://ccdev.tistory.com/m/2
//                if (photo != null) {
//                    photo.recycle();
//                    photo = null;
//                }
//
//                if (resized != null) {
//                    resized.recycle();
//                    resized = null;
//                }
//
//                if (roundBitmap != null) {
//                    roundBitmap.recycle();
//                    roundBitmap = null;
//                }


                Log.d(TAG, "selectedFilePath: " + selectedFilePath);

                try {
                    //AsyncTask 호출
                    String Str_imageResult = new ExecUploadImage(this).execute(selectedFilePath, uNum).get();
                    processResult(Str_imageResult, selectedFilePath);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }


                //사진을 crop했기때문에 temp file 을 삭제해야 한다
//                File file = new File(selectedFilePath);
//                if (file.exists()) {
//                    file.delete();
//                }


            }
        }
    }

    public int pxToDp(int px) {
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        int dp = Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return dp;
    }
    /**
     * content -> file path 형태로 전달한다
     *
     * @return String
     */
    private String getPathFromUri(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToNext();
        String path = cursor.getString(cursor.getColumnIndex("_data"));
        cursor.close();

        return path;
    }


    private void processResult(String Str_Json, String selectedFilePath) {

        try {
            //"{'msg':'xxxxx','result':'Success'}"
            JSONObject jsonObject = new JSONObject(Str_Json);

            String exeResult = jsonObject.getString("result"); //Success or Fail

            Log.d("jsonObject: ", jsonObject.toString());


            if (exeResult.equals("Success")) {
                Toast.makeText(this, "성공", Toast.LENGTH_LONG).show();

                //file path를 SharedPreference에 저장한다
                SharedPreferences.Editor editor = app_preference.edit();
                editor.putString("profile_path", selectedFilePath);
                editor.apply();


            } else {
                Toast.makeText(this, "실패", Toast.LENGTH_LONG).show();

                //없는 사용자
                //                Snackbar.make(view, R.string.msg_loginFail, Snackbar.LENGTH_LONG)
                //                        .setAction("Action", null).show();
                //                //Toast.makeText(this, this.getResources().getString(R.string.loginFail), Toast.LENGTH_LONG).show();
                //intent = new Intent(this, LoginActivity.class);
                //startActivity(intent);

            }


        } catch (Exception e) {
            e.printStackTrace();
        }


    }




}
