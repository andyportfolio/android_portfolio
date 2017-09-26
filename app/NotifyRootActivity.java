//package com.infonetworks.www.app;
//
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentManager;
//import android.support.v7.app.AppCompatActivity;
//import android.util.Log;
//
//import MyItemActivity;
//import OtItemActivity;
//
///**
// * A simple {@link Fragment} subclass.
// */
//public class NotifyRootActivity extends AppCompatActivity {
//    private static final String TAG = "NotifyRootActivity";
//    private static NotifyRootActivity instance = null;
//
//
//    private int rnum;
//    private int origin_rnum;
//    private String req_type;
//
//
//    public NotifyRootActivity() {
//        // Required empty public constructor
//    }
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_notifyroot);
//
//         /*
//        * Notifiy를 click 했을때 intent를 받아서 처리해야 한다
//        * */
//        Intent intent = getIntent();
//        rnum        = intent.getIntExtra("rnum", 0);
//        origin_rnum = intent.getIntExtra("origin_rnum", 0);
//        req_type    = intent.getStringExtra("req_type");    //01매수,02답장,03답장확인
//
//        //2016-10-21 update
//        FragmentManager mFragmentManager = getSupportFragmentManager();
//
//        switch (req_type) {
//
//            case "01": //매수
//                OtItemFragment otItemFragment = new OtItemFragment();
//
//                Bundle argsBundle = new Bundle();
//                argsBundle.putString("param1", String.valueOf(rnum));
//                argsBundle.putString("param2","fragment_notifyroot");
//                otItemFragment.setArguments(argsBundle);
//
//                mFragmentManager.beginTransaction().
//                        add(R.id.notifyroot_fragment, otItemFragment,"otItemFragment_notify").
//                        commit();
//                break;
//
//            case "02": //답장
//                MyItemActivity myItemFragment = new MyItemActivity();
//
//                Bundle argsBundle2 = new Bundle();
//                argsBundle2.putString("param1", String.valueOf(origin_rnum));
//                argsBundle2.putString("param2","fragment_notifyroot");
//                myItemFragment.setArguments(argsBundle2);
//                mFragmentManager.beginTransaction().
//                        add(R.id.notifyroot_fragment, myItemFragment,"myItemFragment_notify").
//                        commit();
//
//                break;
//            case "03": //확인
//                OtItemFragment otItemFragment2 = new OtItemFragment();
//
//                Bundle argsBundle3 = new Bundle();
//                argsBundle3.putString("param1",String.valueOf(origin_rnum));
//                argsBundle3.putString("param2","fragment_notifyroot");
//                otItemFragment2.setArguments(argsBundle3);
//
//                mFragmentManager.beginTransaction().
//                        add(R.id.notifyroot_fragment, otItemFragment2,"otItemFragment_notify").
//                        commit();
//
//                break;
//            default:
//                Log.d(TAG,"default");
//                break;
//        }
//
//
//    }
//
//
//}
