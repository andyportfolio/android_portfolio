package kr.co.infonetworks.www.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;

import kr.co.infonetworks.www.app.gcm.GcmMainActivity;
import kr.co.infonetworks.www.app.utility.Utils;

public class LoginActivity extends AppCompatActivity {
    //상수 및 변수 선언
    private static final String TAG = "LoginActivity";

    //view
    private EditText userid, password;
    private CheckBox chkRemember;
    private Button loginBtn;

    private String Str_userid, Str_password, Str_check, Str_result, Str_localtoken;

    private SharedPreferences app_preference;
    private Intent intent;

    private String str_from="NA";
    private int rnum;
    private int origin_rnum;
    private String req_type;

    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // activity는 context를 상속받아서 만들었기때문에 paent = child를 하여 자동 형변환이 되어 할당될수 있다
        context = this;

        //객체 위젯 가져오기
        userid      = (EditText) findViewById(R.id.userid);
        password    = (EditText) findViewById(R.id.password);
        chkRemember = (CheckBox) findViewById(R.id.chkRemember);
        loginBtn    = (Button) findViewById(R.id.loginBtn);


        //preferece에서 값 가져오기
        //Activity,Service 와 같이 contextwarraper를 상속받은 곳에서는 getApplicationContext가
        //this 를 return하지만 view에서는 해당액티비티.this 또는 getApplicationContext를 쓴다
        app_preference  = PreferenceManager.getDefaultSharedPreferences(this);
        Str_userid      = app_preference.getString("userId", "");
        Str_password    = app_preference.getString("password", "");
        Str_check       = app_preference.getString("checked", "N");


        //id,password 기억하기
        if (Str_check.equals("Y")) {
            userid.setText(Str_userid);
            password.setText(Str_password);
            chkRemember.setChecked(true);
        }

        //userid에 값이 있는 경우에는 키보드가 안올라오게 막는다.
        if (userid.getText().toString().length() > 0) userid.setInputType(0);

        userid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userid.setInputType(1); //Click하면 키보드가 올라오게 한다
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Str_userid = userid.getText().toString();
                Str_password = password.getText().toString();

                if (!Utils.isNetWork(context))  return;

                //validation check
                if (Str_userid.equals("") || Str_password.equals("")) {
                    //snack bar를 이용해도 좋을것 같다
                    Snackbar.make(v, kr.co.infonetworks.www.app.R.string.msg_loginChkMsg, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                    //Toast.makeText(getApplicationContext(), R.string.loginChkMsg, Toast.LENGTH_LONG).show();
                } else {

                    //Toast.makeText(getApplicationContext(),Str_uname+Str_password,Toast.LENGTH_LONG).show();

                    try {
                        //AsyncTask 호출 (Login Button을 click해서 Loging )
                        Str_result = new ExecLogin().execute(Str_userid, Str_password).get();

                        if ("Y".equals(app_preference.getString("tmp_notifyflag", "N"))){
                            processResult(v, Str_result,"notify");
                        }else{
                            processResult(v, Str_result,"login");
                        }


                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }


                }


            }
        });

        /*
        * Notifiy를 click 했을때 intent를 받아서 처리해야 한다
        * */
        Intent  notifyIntent = getIntent();
        str_from    = notifyIntent.getStringExtra("from");
        rnum        = notifyIntent.getIntExtra("rnum", 0);
        origin_rnum = notifyIntent.getIntExtra("origin_rnum", 0);
        req_type    = notifyIntent.getStringExtra("req_type");    //01매수,02답장,03답장확인


        if ("notify".equals(str_from)) {
            LayoutInflater inflater = this.getLayoutInflater();
            View view = inflater.inflate(R.layout.activity_login , null );

            //validation check
            if (Str_userid.equals("") || Str_password.equals("")) {
                //정상적으로 로그인 한후에는 해당 번호로 자동이동을 해야 함으로 임시로 저장함
                SharedPreferences.Editor editor = app_preference.edit();
                editor.putString("tmp_notifyflag", "Y"); //notify로 들어옴
                editor.putInt("tmp_rnum", rnum);
                editor.putInt("tmp_origin_rnum", origin_rnum);
                editor.putString("tmp_req_type", req_type);
                editor.apply();

                Snackbar.make(view, kr.co.infonetworks.www.app.R.string.msg_notify_loginChkMsg, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                //Toast.makeText(getApplicationContext(), R.string.loginChkMsg, Toast.LENGTH_LONG).show();
            } else {

                //Toast.makeText(getApplicationContext(),Str_uname+Str_password,Toast.LENGTH_LONG).show();

                try {
                    //AsyncTask 호출 (nofity를 click하여 로그인 수행)
                    Str_result = new ExecLogin().execute(Str_userid, Str_password).get();
                    processResult(view, Str_result,"notify");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }


            }

        }


    }


    private void processResult(View view, String Str_Json,String str_from) {

        String tmp_status = null, tmp_cCode = null, tmp_cName = null, tmp_userId = null,
                tmp_uNum = null, tmp_uName = null, tmp_oAuth = null, tmp_tel = null,
                tmp_mobile = null, tmp_loginTime = null, tmp_machine = null, tmp_dbtoken = null;


        try {
            //{"data":[{"username":"a","token":"test"}],"sql":"Select...","cnt":1,"result":"Success"}
            JSONObject jsonObject = new JSONObject(Str_Json);

            String exeResult = jsonObject.getString("result"); //Success or Fail
            String exeCnt = jsonObject.getString("cnt"); //0 없는사용자 1 사용불가(신청상태인 사람) 2 사용가능

            //Log.d("jsonObject: ", jsonObject.toString());


            if (exeResult.equals("Success")) {

                if (exeCnt.equals("2")) {
                    JSONArray jsonArray = jsonObject.getJSONArray("data"); //[{"username":"a","token":"test"}]

                    //1회만 looping
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                        tmp_status = jsonObject1.getString("status"); //Y: 사용가능 , N: 신청상태(Y,D 될수있음) , D:삭제
                        tmp_cCode = jsonObject1.getString("cCode"); //company code
                        tmp_cName = jsonObject1.getString("cName"); //company name
                        tmp_userId = jsonObject1.getString("userId"); //userid
                        tmp_uNum = jsonObject1.getString("uNum"); //serial number (Key)
                        tmp_uName = jsonObject1.getString("uName"); //user name
                        tmp_oAuth = jsonObject1.getString("oAuth"); //authorization
                        tmp_tel = jsonObject1.getString("tel"); //tel
                        tmp_mobile = jsonObject1.getString("mobile"); //mobile
                        tmp_loginTime = jsonObject1.getString("loginTime"); //login time
                        tmp_machine = jsonObject1.getString("machine"); //login machine
                        tmp_dbtoken = jsonObject1.getString("token"); // token


                    }


                    //SharedPreference에 Session 과 같은 의미로 저장해야 하는 값을 저장한다
                    SharedPreferences.Editor editor = app_preference.edit();
                    editor.putString("status", tmp_status);
                    editor.putString("cCode", tmp_cCode);
                    editor.putString("cName", tmp_cName);
                    editor.putString("userId", tmp_userId);
                    editor.putString("uNum", tmp_uNum);
                    editor.putString("uName", tmp_uName);
                    editor.putString("oAuth", tmp_oAuth);
                    editor.putString("tel", tmp_tel);
                    editor.putString("mobile", tmp_mobile);
                    editor.putString("loginTime", tmp_loginTime);
                    editor.putString("machine", tmp_machine);
                    editor.putString("dbtoken", tmp_dbtoken);


                    //checkbox 가 checked userid,password를 저장한다
                    if (chkRemember.isChecked()) {
                        //editor.putString("userid", Str_userid);
                        editor.putString("password", Str_password);
                        editor.putString("checked", "Y");
                    }

                    editor.apply();


                    //token값이 없을때(NA) GcmMainActivity에 가서 userid로 gcmInfo에 token을 저장한다
                    //또는 로컬에 저장된 값이 없을때 (앱을 삭제하면 토큰값이 달라진다)
                    //있으면 Main Activity로 간다
                    Str_localtoken = app_preference.getString("localtoken", "X"); //로컬에 저장된 토근을 가져(앱 삭제시 토큰 재생성을 위한 flag)

                    //db에 token이 없으면 NA 를 전송
                    if ("NA".equals(tmp_dbtoken) || "X".equals(Str_localtoken)) {

                        //GCM get token 수행
                        intent = new Intent(this, GcmMainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY); //stack에 저장하지 않음
                        startActivity(intent);

                        //intent.putExtra("userid", tmp_userId);
                        Log.d(TAG, "go to GcmMainActivity with userid : " + tmp_userId);

//                        intent = new Intent(this, com.example.andy.second.MainActivity.class);
//                        intent.putExtra("from", "login"); //login을 통해서 MainActivity 접근


                    } else {

                        //login or notify를 통해서 접근한 login인지를 확인
                        if ("login".equals(str_from.toString())) {
                            intent = new Intent(this, MainActivity.class);
                            intent.putExtra("from", "login"); //login을 통해서 MainActivity 접근
                            intent.putExtra("rnum", 0);
                            intent.putExtra("origin_rnum", 0);
                            intent.putExtra("req_type", "00");

                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                                    | intent.FLAG_ACTIVITY_SINGLE_TOP); //
                            startActivity(intent);
                            Log.d(TAG, "go to MainActivity with userid: " + tmp_userId);


                        }else if ("notify".equals(str_from.toString())) {
                            intent = new Intent(this, MainActivity.class);

                            intent.putExtra("from", "notify"); //Notify를 통해서 MainActivity
                            intent.putExtra("rnum", rnum);
                            intent.putExtra("origin_rnum", origin_rnum);
                            intent.putExtra("req_type", req_type);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                                    | intent.FLAG_ACTIVITY_SINGLE_TOP); //
                            startActivity(intent);
                            Log.d(TAG, "go to MainActivity with userid: " + tmp_userId);

                        }else {
                            Log.d(TAG, "No from " + str_from);
                        }
                    }

                    //Login 성공후 temp Notify관련 정보 clear
                    SharedPreferences.Editor editor2 = app_preference.edit();
                    editor2.putString("tmp_notifyflag", "N"); //notify로 들어옴
                    editor2.putInt("tmp_rnum", 0);
                    editor2.putInt("tmp_origin_rnum", 0);
                    editor2.putString("tmp_req_type", "00");
                    editor2.apply();


                } else {
                    //사용불가 사용자
                    Snackbar.make(view, kr.co.infonetworks.www.app.R.string.msg_loginPending, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                }

            } else {
                //없는 사용자
                Snackbar.make(view, kr.co.infonetworks.www.app.R.string.msg_loginFail, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

            }


        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    private class ExecLogin extends AsyncTask<String, Void, String> {
        //String:시작시 필요한 데이터 타입(doInBackground의 input)
        //Void :진행상황을 표현하기 위한 데이터 타입
        //String:Return 데이터 타입(doInBackground의 output 이고 onPostExecute의 input


        private Intent intent;
        private ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(TAG, "step: onPreExecute" + getLocalClassName());

            //progressive bar의 진행을 보여준다
            pd = ProgressDialog.show(LoginActivity.this, getResources().getString(R.string.pd_title_login), getResources().getString(R.string.pd_msg_login));
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                String userid = params[0];
                String password = params[1];

                String app_link =  getResources().getString(R.string.app_link); //app 대표주소
                String app_login = getResources().getString(R.string.url_appLogin); //URL

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
            //progressive bar의 진행을 종료한다
            pd.dismiss();


        }
    }
}
