package kr.co.infonetworks.www.app.db;

import android.app.IntentService;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import kr.co.infonetworks.www.app.R;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by andy on 2016. 8. 20..
 */
public class ExecUploadImage extends AsyncTask<String, Void, String> {

    private static String TAG = "ExecUploadImage";

    private IntentService intentService;

    private Intent intent;

    private ProgressDialog pd;

    private Context context;

    public ExecUploadImage(Context context) {
        this.context = context;
        //Fragment에서 호출해서 activity가 아니라 context를 넘겼다
        //getActivity를 해서 넘겨도 될것 같음
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        Log.d(TAG, "step: onPreExecute");
        //progressive bar의 진행을 보여준다
        pd = ProgressDialog.show(context, context.getResources().getString(R.string.pd_title_profile), context.getResources().getString(R.string.pd_msg_profile));

    }

    @Override
    protected String doInBackground(String... params) {
        String selectedFilePath;
        String uNum;

        String ret_val = "{'msg':'No Messgae','result':'Fail'}";
        String ret_msg;

        String app_link = context.getResources().getString(R.string.app_link);
        String app_imageRequest = context.getResources().getString(R.string.uploadImagelink);
        String link = app_link.concat(app_imageRequest);


        selectedFilePath = params[0];  // /storage/emulated/0/tmp_1471956847836.jpg
        uNum = params[1];               //uNum

        Log.d(TAG, "selectedFilePath: " + selectedFilePath);
        Log.d(TAG, "uNum: " + uNum);

        //selectedFilePath = "/exteranl/images/media/148311";

        int serverResponseCode = 0;

        HttpURLConnection connection;
        DataOutputStream dataOutputStream;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";


        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File selectedFile = new File(selectedFilePath);


        String[] parts = selectedFilePath.split("/");
        final String fileName = parts[parts.length - 1];

        if (!selectedFile.isFile()) {
            ret_msg = "Source File Does not Exist: " + selectedFilePath;
            ret_val = "{'msg':'" + ret_msg + "','result':'Fail'}";

        } else {
            try {
                FileInputStream fileInputStream = new FileInputStream(selectedFile);

                //URL설정
                URL url = new URL(link); //server URL : http://xxx/xxx.php

                //새로운 접속을 오픈한다
                connection = (HttpURLConnection) url.openConnection();

                //읽기,쓰기 모두 가능하게 설정
                connection.setDoInput(true);//Allow Inputs
                connection.setDoOutput(true);//Allow Outputs

                //캐시를 사용하지 않게 설정
                connection.setUseCaches(false);//Don't use a cached Copy

                //POST타입으로 설정
                connection.setRequestMethod("POST");

                //헤더 설정
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("ENCTYPE", "multipart/form-data");
                connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                connection.setRequestProperty("uploaded_file", selectedFilePath);

                //creating new dataoutputstream
                //전송할 인자 생성
                dataOutputStream = new DataOutputStream(connection.getOutputStream());

                //text 전송값 생성
                //http://www.androidpub.com/664496
                dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
                dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"" + "uNum" + "\"" + lineEnd);
                dataOutputStream.writeBytes(lineEnd);
                dataOutputStream.writeBytes(uNum);
                dataOutputStream.writeBytes(lineEnd);


                //writing bytes to data outputstream
                // "--" + 지정한 boundary값 + "\r\n"
                dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);

                //name = \서버에서 사용할 변수명 , filename = 실제파일이름
                //$_POST["이 곳에 들어갈 변수명"];
                dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"fileToUpload\";filename=\""
                        + selectedFilePath + "\"" + lineEnd);

                dataOutputStream.writeBytes(lineEnd);




                //버퍼사이즈를 설정하여 버퍼에 할당
                //returns no. o bytes present in fileInputStream
                bytesAvailable = fileInputStream.available();
                //selecting the buffer size as minimum of available bytes or 1 MB
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                //setting the buffer as byte array of size of bufferSize
                buffer = new byte[bufferSize];

                //스트림에 작성
                //reads bytes from FileInputStream(from 0th index of buffer to buffersize)
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                //loop repeats till bytesRead = -1, i.e., no bytes are left to read
                while (bytesRead > 0) {
                    //write the bytes read from inputstream - Upload file
                    dataOutputStream.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }

                dataOutputStream.writeBytes(lineEnd);
                dataOutputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                serverResponseCode = connection.getResponseCode();
                String serverResponseMessage = connection.getResponseMessage();


                Log.d(TAG, "Server Response is: " + serverResponseMessage + ": " + serverResponseCode);
                Log.d(TAG, "connection.getContent: " + connection.getContent().toString());

                //response code of 200 indicates the server status OK
                if (serverResponseCode == 200) {

                    //서버로 부터 리턴값 받기
                    //http://andriod77.blogspot.kr/2014/11/android-multipart-post-android.html
                    BufferedReader rd = null;

                    rd = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
                    String line = null;

                    while ((line = rd.readLine()) != null) {
                        Log.d(TAG, "line:" + line);
                        break;
                    }

                    if ("Success".equals(line)) {
                        ret_msg = "File upload completed: " + selectedFilePath;
                        ret_val = "{'msg':'" + ret_msg + "','result':'Success'}";
                    } else {
                        ret_msg = "File upload Fail: " + selectedFilePath;
                        ret_val = "{'msg':'" + ret_msg + "','result':'Fail'}";
                    }

                } else {
                    //andy add
                    ret_msg = "File upload fail server responsecode: " + String.valueOf(serverResponseCode);
                    ret_val = "{'msg':'" + ret_msg + "','result':'Fail'}";
                }

                //closing the input and output streams
                fileInputStream.close();
                dataOutputStream.flush();
                dataOutputStream.close();


            } catch (FileNotFoundException e) {
                e.printStackTrace();
                ret_msg = "File Not Found: " + selectedFilePath;
                ret_val = "{'msg':'" + ret_msg + "','result':'Fail'}";

            } catch (MalformedURLException e) {
                e.printStackTrace();
                ret_msg = "URL error!: " + link;
                ret_val = "{'msg':'" + ret_msg + "','result':'Fail'}";

            } catch (IOException e) {
                e.printStackTrace();
                ret_msg = "Cannot Read-Write File!: " + selectedFilePath;
                ret_val = "{'msg':'" + ret_msg + "','result':'Fail'}";
            }

        }

        return ret_val;

    }


    @Override
    protected void onPostExecute(String result) {
        Log.d(TAG, "step-onPostExecute:" + result);
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
