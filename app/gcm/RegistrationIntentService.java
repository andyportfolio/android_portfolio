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

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import kr.co.infonetworks.www.app.R;
import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import kr.co.infonetworks.www.app.MainActivity;

public class RegistrationIntentService extends IntentService {

    private static final String TAG = "RegIntentService";
    private static final String[] TOPICS = {"global"};

    private String Str_userid, Str_unum, Str_ccode;
    private SharedPreferences sharedPreferences;

    public RegistrationIntentService() {
        super(TAG);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        try {
            // [START register_for_gcm]
            // Initially this call goes out to the network to retrieve the token, subsequent calls
            // are local.
            // R.string.gcm_defaultSenderId (the Sender ID) is typically derived from google-services.json.
            // See https://developers.google.com/cloud-messaging/android/start for details on this file.
            // [START get_token]
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            // [END get_token]
            Log.i(TAG, "GCM Registration Token: " + token);

            // TODO: Implement this method to send any registration to your app's servers.
            sendRegistrationToServer(token);

            // Subscribe to topic channels
            subscribeTopics(token);

            // You should store a boolean that indicates whether the generated token has been
            // sent to your server. If the boolean is false, send the token to your server,
            // otherwise your server should have already received the token.
            sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, true).apply();
            // [END register_for_gcm]


        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
            sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false).apply();
        }
        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(QuickstartPreferences.REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    /**
     * Persist registration to third-party servers.
     *
     * Modify this method to associate the user's GCM registration token with any server-side account
     * maintained by your application.
     *
     * @param Str_token The new token.
     */
    private void sendRegistrationToServer(String Str_token) {
        // Add custom implementation, as needed.
        Str_unum = sharedPreferences.getString("uNum", "");
        Str_userid = sharedPreferences.getString("userId", "");
        Str_ccode = sharedPreferences.getString("cCode", "");

        Log.d(TAG, "Str_userid = " + Str_userid);

        //new NotUsed_ExecUpdToken(RegistrationIntentService.this).execute(Str_userid, token);
        String app_link = this.getResources().getString(R.string.app_link); //app 대표주소
        String app_updlink = this.getResources().getString(R.string.url_appTokenUpdate); //URL

        String link = app_link.concat(app_updlink);


        try {
            String data = URLEncoder.encode("userid", "UTF-8") + "=" + URLEncoder.encode(Str_userid, "UTF-8");
            data += "&" + URLEncoder.encode("unum", "UTF-8") + "=" + URLEncoder.encode(Str_unum, "UTF-8");
            data += "&" + URLEncoder.encode("token", "UTF-8") + "=" + URLEncoder.encode(Str_token, "UTF-8");
            data += "&" + URLEncoder.encode("ccode", "UTF-8") + "=" + URLEncoder.encode(Str_ccode, "UTF-8");


            Log.d(TAG, "step: NotUsed_ExecUpdToken doInBackground link=" + link);

            URL url = new URL(link);
            URLConnection conn = url.openConnection();

            conn.setDoOutput(true); //post
            //http전송을 위한 header값을 만든다
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            //post데이터를 생성하여 PHP를 호출한다
            wr.write(data);
            wr.flush();

            //PHP의 값을 가져온다
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            StringBuffer sb = new StringBuffer();
            String json = null;

            while ((json = reader.readLine()) != null) {
                sb.append(json + "\n");
            }

            Log.d(TAG, "NotUsed_ExecUpdToken return value json: " + sb.toString().trim());

            String Str_result = sb.toString().trim();


            //{"data":0,"sql":"update login set token = 'toketoektoek' where username='a'","result":"Success"}
            JSONObject jsonObject = new JSONObject(Str_result);

            String exeResult = jsonObject.getString("result"); //Success or Fail

            Log.d(TAG, "NotUsed_ExecUpdToken onPostExecute: " + exeResult);

            if (exeResult.equals("Success")) {

                //성공시 로컬 토큰값도 update 한다
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("localtoken", Str_token);
                editor.apply();



                Intent intent = new Intent(getApplicationContext(), MainActivity.class);

                intent.putExtra("from", "gcm");
                //intent.putExtra("token", Str_token);

                //Log.d(TAG,"updated token: " + tmp_username)
                Log.d(TAG, "call intentService.startActivity(intent)");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            } else {
                //실패시 로컬 토큰값도 clear 한다
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("localtoken", "X");
                editor.apply();

                Toast.makeText(getApplicationContext(), this.getResources().getString(R.string.msg_tokenUpdateFail), Toast.LENGTH_LONG).show();

            }
        } catch (Exception e) {
        }


    }


    /**
     * Subscribe to any GCM topics of interest, as defined by the TOPICS constant.
     *
     * @param token GCM token
     * @throws IOException if unable to reach the GCM PubSub service
     */
    // [START subscribe_topics]
    private void subscribeTopics(String token) throws IOException {
        GcmPubSub pubSub = GcmPubSub.getInstance(this);
        for (String topic : TOPICS) {
            pubSub.subscribe(token, "/topics/" + topic, null);
        }
    }
    // [END subscribe_topics]

}
