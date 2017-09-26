package kr.co.infonetworks.www.app.db;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import kr.co.infonetworks.www.app.R;

/**
 * Created by andy on 2016. 5. 28..
 */
public class ExecSelMyRequest_Notused extends AsyncTask<String, Void, String> {
    //String:시작시 필요한 데이터 타입(doInBackground의 input)
    //Void :진행상황을 표현하기 위한 데이터 타입
    //String:Return 데이터 타입(doInBackground의 output 이고 onPostExecute의 input

    private static String TAG = "ExecSelMyRequest_Notused";

    private Context context;

    private ProgressDialog pd;

    public ExecSelMyRequest_Notused(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.d(TAG, "step: ExecSelMyRequest_Notused");


        //progressive bar의 진행을 보여준다
        pd = ProgressDialog.show(context, context.getResources().getString(R.string.pd_title_selMyRequest), context.getResources().getString(R.string.pd_msg_selMyRequest));

    }

    @Override
    protected String doInBackground(String... params) {

        try {

            String from = params[0]; //어디서 부터 읽어올것인지의 기준점

            SharedPreferences app_preference = PreferenceManager.getDefaultSharedPreferences(context);
            String ccode = app_preference.getString("cCode", ""); //업소코드

            String loadcnt = app_preference.getString("loadcnt", "6"); //한번에 불러올 데이터의 사이즈, 없으면 10개씩 불러온다


            String app_link = context.getResources().getString(R.string.app_link); //대표주소
            String app_appSeleMyRequest = context.getResources().getString(R.string.url_appSelMyRequest); //URL

            //String link = new StringBuilder(app_link).append(app_appSeleRequest).toString();
            String link = app_link.concat(app_appSeleMyRequest);

            String data = URLEncoder.encode("ccode", "UTF-8") + "=" + URLEncoder.encode(ccode, "UTF-8");
            data += "&" + URLEncoder.encode("from", "UTF-8") + "=" + URLEncoder.encode(from, "UTF-8");
            data += "&" + URLEncoder.encode("loadcnt", "UTF-8") + "=" + URLEncoder.encode(loadcnt, "UTF-8");

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
