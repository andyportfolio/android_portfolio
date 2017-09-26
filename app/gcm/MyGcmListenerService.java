/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kr.co.infonetworks.www.app.gcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

import kr.co.infonetworks.www.app.LoginActivity;
import kr.co.infonetworks.www.app.R;
import kr.co.infonetworks.www.app.activity.MyItemActivity;
import kr.co.infonetworks.www.app.activity.OtItemActivity;
import kr.co.infonetworks.www.app.base.MyApplication;

public class MyGcmListenerService extends GcmListenerService {

    private static final String TAG = "MyGcmListenerService";

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {

        int rnum = Integer.parseInt(data.getString("rnum"));
        int origin_rnum = Integer.parseInt(data.getString("origin_rnum"));
        String req_type = data.getString("req_type");

        String title = data.getString("title");
        String message = data.getString("message");
        String reginfo = data.getString("reginfo");

        String ccode = data.getString("ccode");

        Log.d(TAG, "From: " + from);

        Log.d(TAG, "rnum: " + String.valueOf(rnum));   //rnum

        Log.d(TAG, "title: " + title);      //[아파트-매매]아파트 매매건입니다
        Log.d(TAG, "message: " + message);  //내용입니다
        Log.d(TAG, "reginfo: " + reginfo);  //A업소/홍길동

        if (from.startsWith("/topics/")) {
            // message received from some topic.
        } else {
            // normal downstream message.
        }

        // [START_EXCLUDE]
        /**
         * Production applications would usually process the message here.
         * Eg: - Syncing with server.
         *     - Store message in local database.
         *     - Update UI.
         */

        /**
         * In some cases it may be useful to show a notification indicating to the user
         * that a message was received.
         */


        /**
         * 정책 : app을 설치한후 반드시 Login을 하여 shared 정보에 업소정보가 저장되어 있는 경우에만
         * Notification을 받을 수 있다
         * 1. shared 정보에 업소정보가 없으면 return한
         * 2. 업소정보가 있는 경우
         *    업소정보 = 노티발송 업소정보 일 경우에 보냈다는 노티를 보낸다
         *    업소정보 <> 노티발송 업소정보 일경우 노티를 보낸다
         */



        SharedPreferences app_preference  = PreferenceManager.getDefaultSharedPreferences(this);
        String str_cCode            = app_preference.getString("cCode", "NA");
        boolean notify_send_flag    = app_preference.getBoolean("notify_send_flag", true);
        boolean notify_receive_flag = app_preference.getBoolean("notify_receive_flag", true);

        if ("NA".equals(str_cCode)) {
            return;
        }else{

            int mode = chageSoundVibrateMode();

            //업소정보 = 노티 발송 업소
            if (str_cCode.equals(ccode) && notify_receive_flag){
                Log.d(TAG,"sendNotification_me start");
                sendNotification_me(title, message, reginfo);
                Log.d(TAG,"sendNotification_me end");
               // retrunSoundVibrateMode(mode);
            }

            if (!str_cCode.equals(ccode) && notify_send_flag){
                Log.d(TAG,"sendNotification start");
                sendNotification(rnum,origin_rnum,req_type, title, message, reginfo);
                Log.d(TAG,"sendNotification end");
               // retrunSoundVibrateMode(mode);
            }



        }

//        if 없소정보가 없으면 retrun 하라
//                else if 정보가 존재하는데  업소정보가 다르면 아래 알림을 띠어라
//        sendNotification(rnum,origin_rnum,req_type, title, message, reginfo);
//         else if 정보가 존재하는 업소정보가 같으면
//                 다른 방식의 알림을 띠어라
//                예를 들어 XXX를 등록하였습니다 , XXX 답장을 하였습니다
//
//                위와 같이 하여야 노티파이 로직을 단순화 할수 있다
//

        // [END_EXCLUDE]
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    private void sendNotification(int rnum,int origin_rnum,String req_type, String title, String message, String reginfo) {

        //Intent intent = new Intent(this, com.example.andy.second.activity.TestActivity.class);

        //2016-10-21 Login 이 되어 있다면 MainActivity 를 호출하고
        // Login 이 안되어 있다면 LoginActivity 을 호출한다

        MyApplication myapplication = (MyApplication) getApplication();

        Intent intent = null;

        //mainActivity가 running중 인경우 (Login 된경우)
        if (myapplication.getMainActivityRunning()){

            switch (req_type) {
            case "01":  //매수
                intent = new Intent(this, OtItemActivity.class);
                intent.putExtra("param1",rnum);
                break;
            case "02":  //답장
                intent = new Intent(this, MyItemActivity.class);;
                intent.putExtra("param1",origin_rnum);
                break;
            case "03":  //확인
                intent = new Intent(this, OtItemActivity.class);
                intent.putExtra("param1",origin_rnum);
                break;
            default:
                Log.d(TAG + "moved: ", "default");

                return; // 빠져 나간다
            }

        }else{
            intent = new Intent(this, LoginActivity.class);
        }

        intent.putExtra("from", "notify"); //Notify를 통해서 MainActivity 또는 LoginActivity 접근
        intent.putExtra("rnum", rnum);
        intent.putExtra("origin_rnum", origin_rnum);
        intent.putExtra("req_type", req_type);

        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        //PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
        //        PendingIntent.FLAG_ONE_SHOT);

        //http://raphaelh.tistory.com/31
        //알림 중복 방지 , 각각의 액션 수행
        PendingIntent pendingIntent = PendingIntent.getActivity(this, (int) (System.currentTimeMillis() / 1000), intent,
                PendingIntent.FLAG_UPDATE_CURRENT);


        //Make Notification Content
        //Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        Notification notification = new Notification();


        if("01".equals(req_type)){
            notification.sound = Uri.parse("android.resource://" + getPackageName() + "/" + kr.co.infonetworks.www.app.R.raw.notify);
        }else{
            notification.sound = Uri.parse("android.resource://" + getPackageName() + "/" + kr.co.infonetworks.www.app.R.raw.reply);
        }


//        if (vibrate) {
            notification.defaults|= Notification.DEFAULT_VIBRATE;
//        }

        notificationBuilder.setDefaults(notification.defaults);
        notificationBuilder.setSound(notification.sound);

        notificationBuilder.setSmallIcon(R.drawable.ic_mail_white_24dp) //메세지 아이콘
                .setTicker(title)                            //알람이 뜰때 잠깐표시되는 텍스트
                .setContentTitle(title)
                .setContentText(message + "\n" + reginfo)
                .setWhen(System.currentTimeMillis())
                //.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        //Notification Manager
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        //notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
        notificationManager.notify((int) (System.currentTimeMillis() / 1000), notificationBuilder.build());


    }

    private void sendNotification_me(String title, String message, String reginfo) {


         String tmp_title = "<송신> " + title;


        //Make Notification Content
        //Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        Notification notification = new Notification();

//        if (sound) {
            notification.sound = Uri.parse("android.resource://" + getPackageName() + "/" + kr.co.infonetworks.www.app.R.raw.send);
//        }

//        if (vibrate) {
            notification.defaults|= Notification.DEFAULT_VIBRATE;
//        }

        notificationBuilder.setDefaults(notification.defaults);
        notificationBuilder.setSound(notification.sound);

        notificationBuilder.setSmallIcon(R.drawable.ic_reply_white_24dp) //메세지 아이콘
                .setTicker(tmp_title)                            //알람이 뜰때 잠깐표시되는 텍스트
                .setContentTitle(tmp_title)
                .setContentText(message + "\n" + reginfo)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setContentIntent(PendingIntent.getActivity(this, 0, new Intent(), 0)); //클릭시 메세지 삭제


        //Notification Manager
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        //notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
        notificationManager.notify((int) (System.currentTimeMillis() / 1000), notificationBuilder.build());
    }

    private int chageSoundVibrateMode(){
        //소리로 전환하게 한다.
        int ret_val=0;
        //소리,진동, 무음 제어
        AudioManager clsAudioManager =  (AudioManager)getSystemService(Context.AUDIO_SERVICE);

        switch( clsAudioManager.getRingerMode( ) )
        {
            case AudioManager.RINGER_MODE_VIBRATE:
                // 진동 모드
                clsAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);  //벨
                ret_val = 1;
                break;
            case AudioManager.RINGER_MODE_NORMAL:
                // 소리 모드
                //clsAudioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);  //진동
                ret_val = 2;
                break;
            case AudioManager.RINGER_MODE_SILENT:
                // 무음 모드
                //clsAudioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);  //진동
                clsAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);  //벨
                ret_val = 3;
                break;
        }

        Log.d(TAG,"getRingerMode: " + ret_val);

        return ret_val;


    }

    private void retrunSoundVibrateMode(int mode){

        Log.d(TAG,"retrunSoundVibrateMode: " + mode);

        //소리,진동, 무음 제어
        AudioManager clsAudioManager =  (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        switch(mode)
        {
            case 1:
                Log.d(TAG,"RINGER_MODE_VIBRATE");
                // 진동 모드로 복귀
                clsAudioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);  //진동
                break;
            case 2:
                Log.d(TAG,"RINGER_MODE_NORMAL");
                //벨 모드로 복귀
                clsAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);  //벨
                break;
            case 3:
                Log.d(TAG,"RINGER_MODE_SILENT");
                // 무음 모드로 복귀
                clsAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);  //무음
                break;
        }
    }

}
