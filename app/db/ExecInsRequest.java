package kr.co.infonetworks.www.app.db;

import android.app.IntentService;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
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
public class ExecInsRequest extends AsyncTask<String, Void, String> {
    //String:시작시 필요한 데이터 타입(doInBackground의 input)
    //Void :진행상황을 표현하기 위한 데이터 타입
    //String:Return 데이터 타입(doInBackground의 output 이고 onPostExecute의 input


    private static String TAG = "ExecInsRequest";

    private IntentService intentService;

    private Intent intent;

    private ProgressDialog pd;

    private Context context;

    public ExecInsRequest(Context context) {
        this.context = context;
        //Fragment에서 호출해서 activity가 아니라 context를 넘겼다
        //getActivity를 해서 넘겨도 될것 같음
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        Log.d(TAG, "step: ExecInsRequest onPreExecute");
        //progressive bar의 진행을 보여준다
        pd = ProgressDialog.show(context, context.getResources().getString(R.string.pd_title_request), context.getResources().getString(R.string.pd_msg_request));

    }

    @Override
    protected String doInBackground(String... params) {
        String sType, sCategory, sTitle, sContent, str_cCode, str_cName, str_uNum, str_uName;

        try {
            sType = params[0];  //아파트
            sCategory = params[1];  //매매
            sTitle = params[2];  //제목
            sContent = params[3];  //내용

            str_cCode = params[4];  //업소코드
            str_cName = params[5];  //업소명
            str_uNum = params[6];  //사용자번호
            str_uName = params[7];  //사용자 이름


            String app_link = context.getResources().getString(R.string.app_link);
            String app_insRequest = context.getResources().getString(R.string.insRequestlink);

            //String link = new StringBuilder(app_link).append(app_insRequest).toString();
            String link = app_link.concat(app_insRequest);


            String data = URLEncoder.encode("category", "UTF-8") + "=" + URLEncoder.encode(sCategory, "UTF-8");
            data += "&" + URLEncoder.encode("type", "UTF-8") + "=" + URLEncoder.encode(sType, "UTF-8");
            data += "&" + URLEncoder.encode("title", "UTF-8") + "=" + URLEncoder.encode(sTitle, "UTF-8");
            data += "&" + URLEncoder.encode("content", "UTF-8") + "=" + URLEncoder.encode(sContent, "UTF-8");
            data += "&" + URLEncoder.encode("req_type", "UTF-8") + "=" + URLEncoder.encode("01", "UTF-8"); //신규등록
            data += "&" + URLEncoder.encode("cCode", "UTF-8") + "=" + URLEncoder.encode(str_cCode, "UTF-8");
            data += "&" + URLEncoder.encode("cName", "UTF-8") + "=" + URLEncoder.encode(str_cName, "UTF-8");
            data += "&" + URLEncoder.encode("uNum", "UTF-8") + "=" + URLEncoder.encode(str_uNum, "UTF-8");
            data += "&" + URLEncoder.encode("uName", "UTF-8") + "=" + URLEncoder.encode(str_uName, "UTF-8");

            Log.d(TAG, "step: link=" + link);
            Log.d(TAG, "step: data=" + data);


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

//        try{
//            //{"data":number,"'status'":"Success"}
//            JSONObject jsonObject = new JSONObject(result);
//
//            String exeResult = jsonObject.getString("status"); //Success or Fail
//
//            Log.d(TAG,"NotUsed_ExecUpdToken onPostExecute: " + exeResult);
//
//            if (exeResult.equals("Success")){
//                Toast.makeText(context,"sucess",Toast.LENGTH_LONG).show();
//
//
//            }else{
//                Toast.makeText(context,"fail",Toast.LENGTH_LONG).show();
//
//            }
//
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
