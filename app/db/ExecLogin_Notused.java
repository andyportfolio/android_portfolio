package kr.co.infonetworks.www.app.db;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import kr.co.infonetworks.www.app.R;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * Created by andy on 2016. 5. 28..
 */
public class ExecLogin_Notused extends AsyncTask<String, Void, String> {
    //String:시작시 필요한 데이터 타입(doInBackground의 input)
    //Void :진행상황을 표현하기 위한 데이터 타입
    //String:Return 데이터 타입(doInBackground의 output 이고 onPostExecute의 input

    private static String TAG = "ExecLogin_Notused";

    private Activity activity;

    private Intent intent;

    private ProgressDialog pd;

    public ExecLogin_Notused(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.d(TAG, "step: onPreExecute" + activity.getLocalClassName());


        //progressive bar의 진행을 보여준다
        pd = ProgressDialog.show(activity, activity.getResources().getString(R.string.pd_title_login), activity.getResources().getString(R.string.pd_msg_login));
    }

    @Override
    protected String doInBackground(String... params) {

        try {
            String userid = params[0];
            String password = params[1];

            String app_link = activity.getResources().getString(R.string.app_link); //app 대표주소
            String app_login = activity.getResources().getString(R.string.url_appLogin); //URL

            //String link = new StringBuilder(app_link).append(app_login).toString();
            String link = app_link.concat(app_login);

            String data = URLEncoder.encode("userId", "UTF-8") + "=" + URLEncoder.encode(userid, "UTF-8");
            data += "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");


            Log.d(TAG, "step: doInBackground link=" + link);

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

            Log.d(TAG, "return value json: " + sb.toString().trim());

            return sb.toString().trim();

        } catch (Exception e) {
            return new String("Exception: " + e.getMessage());
        }


    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

//        String tmp_username=null, tmp_token = null;
//
//        try{
//            //{"data":[{"username":"a","token":"test"}],"sql":"SELECT * FROM login WHERE username='a' AND password='1'","cnt":1,"result":"Success"}
//            JSONObject jsonObject = new JSONObject(result);
//
//            String exeResult = jsonObject.getString("result"); //Success or Fail
//
//            if (exeResult.equals("Success")){
//                JSONArray jsonArray = jsonObject.getJSONArray("data"); //[{"username":"a","token":"test"}]
//
//                //1회만 looping
//                for(int i=0; i <jsonArray.length(); i++){
//                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
//
//                    tmp_username = jsonObject1.getString("username");
//                    tmp_token= jsonObject1.getString("token");
//
//                }
//
//
//                intent = new Intent(activity,com.example.andy.second.gcm.GcmMainActivity.class);
//                intent.putExtra("username",tmp_username);
//                intent.putExtra("token",tmp_token);
//
//                Log.d(TAG,"go to username: " + tmp_username);
//
//                activity.startActivity(intent);
//
//            }else{
//                Toast.makeText(activity.getApplication(),activity.getResources().getString(R.string.loginFail),Toast.LENGTH_LONG).show();
//                intent = new Intent(activity,LoginActivity.class);
//                activity.startActivity(intent);
//
//            }
//
//
//        }
//        catch (Exception e){
//
//        }


        //progressive bar의 진행을 종료한다
        pd.dismiss();


    }
}
