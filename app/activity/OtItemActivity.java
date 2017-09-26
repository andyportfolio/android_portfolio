package kr.co.infonetworks.www.app.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;

import kr.co.infonetworks.www.app.R;
import kr.co.infonetworks.www.app.utility.Utils;

//Listner-4. implements ListViewMyResponseAdapter.PopupListner 를 상속받는다
public class OtItemActivity extends AppCompatActivity implements ListViewMyResponseAdapter.PopupListner{
    private static final String ARG_PARAM1 = "param1";
    private final String TAG = "OtItemActivity";

    private static OtItemActivity instance = null;

    private String parentRnum;
    private String parent_fragment;

    private boolean parentRefreshFlag;   //저장을 하면 true로 변경
    private int parentPostion;           //호출한 Parent의 Position
    private int parentRefreshValue  ;    //답장횟수

    private Button replyBtn, closeBtn;

    private TextView txt_header, txt_rnum, txt_regdate, txt_title, txt_content, txt_region,
            txt_area, txt_floor, txt_room, txt_sprice, txt_dprice, txt_rprice, txt_company, txt_person, txt_tel, txt_mobile;

    private EditText et_replyContent;


    //Response용
    private TextView txt_regccode;
    private TextView txt_regcname;

    private String Str_result;

    private ListViewMyResponseAdapter listViewMyResponseAdapter;
    private ListView myresponselistView;

    private View view;
    private FrameLayout layout_MainFrame;

    //popup에서 사용할 text
    private PopupWindow popupWindow;
    private View popup;

    //popup에서 사용할 text
    private TextView txt_p_rnum;
    private TextView txt_p_regtime;
    private TextView txt_p_regcompany;
    private TextView txt_p_content;


    //replyconfirm용
    private TextView txt_p_origin_rnum;
    private TextView txt_p_regccode;
    private TextView txt_p_regcname;

    private ListViewOtReplyConfirmAdapter listViewOtReplyConfirmAdapter;
    private ListView otreplyconfirmlistView;

    private boolean parentReloadflag = false; //popup이 닫힌후 parent창이 reload되어야 하는지 판단

    private boolean fromNotifyflag = false; //Notify에서 호출되었다의 여부

    private OtItemActivity otItemActivity;

    public OtItemActivity() {
        // Required empty public constructor
        parentRefreshFlag=false; //초기화
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!Utils.isNetWork(this))  return;

        Intent intent       = getIntent();

        if ("notify".equals(intent.getStringExtra("from"))){
            parentRnum = Integer.toString(intent.getIntExtra("param1",0));
            parentPostion = intent.getIntExtra("param2", 0);    // 리스트뷰의 포지션
            parentRefreshValue = intent.getIntExtra("param3", 0);   //답변횟수

            fromNotifyflag =  true;

        }else{
            parentRnum = intent.getStringExtra("param1");
            parentPostion = intent.getIntExtra("param2", 0);    // 리스트뷰의 포지션
            parentRefreshValue = intent.getIntExtra("param3", 0);   //답변횟수

            fromNotifyflag =  false;
        }
        Log.d(TAG,"onCreate P1:"+parentRnum + "-P2:"+parent_fragment + "-P3:"+parentPostion + "-P4:"+parentRefreshValue);


        setContentView(R.layout.activity_ot_item);

        //나의 view를 넘겨주기 위해서
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.activity_ot_item, null);


        //바탕화면의 불투명도를 0로 하여 투명하게 수정함
        layout_MainFrame = (FrameLayout) findViewById(R.id.mainframe);
        layout_MainFrame.getForeground().setAlpha(0);

        txt_header  = (TextView) findViewById(R.id.tv_header);
        txt_rnum    = (TextView) findViewById(R.id.tv_rnum);
        txt_regdate = (TextView) findViewById(R.id.tv_regdate);
        txt_title   = (TextView) findViewById(R.id.tv_title);
        txt_content = (TextView) findViewById(R.id.tv_content);
        txt_region  = (TextView) findViewById(R.id.tv_region);
        txt_area    = (TextView) findViewById(R.id.tv_area);
        txt_floor   = (TextView) findViewById(R.id.tv_floor);
        txt_room    = (TextView) findViewById(R.id.tv_room);

        txt_sprice  = (TextView) findViewById(R.id.tv_sprice);
        txt_dprice  = (TextView) findViewById(R.id.tv_dprice);
        txt_rprice  = (TextView) findViewById(R.id.tv_rprice);

        txt_company = (TextView) findViewById(R.id.tv_company);
        txt_person  = (TextView) findViewById(R.id.tv_person);
        txt_tel     = (TextView) findViewById(R.id.tv_tel);
        txt_mobile  = (TextView) findViewById(R.id.tv_mobile);


        et_replyContent = (EditText) findViewById(R.id.replyContent);

        //hidden data
        txt_regccode = (TextView) findViewById(R.id.tv_regccode);
        txt_regcname = (TextView) findViewById(R.id.tv_regcname);

        closeBtn = (Button) findViewById(R.id.closeBtn);
        replyBtn = (Button) findViewById(R.id.replyBtn);

        txt_header.setText("");

        txt_rnum.setText(parentRnum);

        txt_regdate.setText("");
        txt_title.setText("");
        txt_content.setText("");
        txt_region.setText("");
        txt_area.setText("");
        txt_floor.setText("");
        txt_room.setText("");
        txt_sprice.setText("");
        txt_dprice.setText("");
        txt_rprice.setText("");
        txt_company.setText("");
        txt_person.setText("");
        txt_tel.setText("");
        txt_mobile.setText("");

        et_replyContent.setText("");


        //당업소의 response history를 보여주기 위한  listview 생성
        //Listner-5. child에게 자신(this)를 넘겨서 레퍼런스를 넘겨서 , 자신의 함수를 호출하게 한다.
        listViewMyResponseAdapter = new ListViewMyResponseAdapter(this);

       myresponselistView = (ListView) findViewById(R.id.lv_responselist);
       myresponselistView.setAdapter(listViewMyResponseAdapter);


        loadData();


        replyBtn.setOnClickListener(new Button.OnClickListener(){
            /**
             * Called when a view has been clicked.
             *
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {

                if (!Utils.isNetWork(getApplicationContext()))  return;

                if ("".equals(et_replyContent.getText().toString())){
                    Snackbar.make(v, kr.co.infonetworks.www.app.R.string.msg_responseChkMsg,
                            Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }else{
                    InsResponse();
                }

            }
        });

        closeBtn.setOnClickListener(new Button.OnClickListener() {
            /**
             * Called when a view has been clicked.
             *
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {

                //일반 화면에서 호출시 return value가 존재
                if (!fromNotifyflag) {
                    Intent intent = getIntent();
                    intent.putExtra("parentRefreshValue", parentRefreshValue);
                    intent.putExtra("parentPostion", parentPostion);

                    if (parentRefreshFlag) { //parent가 refresh
                        setResult(RESULT_OK, intent);
                    } else {
                        setResult(RESULT_CANCELED, intent);
                    }
                }

                finish();



            }
        });



        //scroll 이동 http://tiann.tistory.com/13
        //10월7일 읽어봐라
//        http://m.blog.naver.com/netheson/40155169563
//        1. onCreate ~ onResume 에서는 UI 조작 명령이 동작하지 않는다.
//        2. 오직 UI Thread 만이 Android UI 를 조작할 수 있다.
//        3. View.post(Runnable) 을 이용해 등록한 명령은 UI 가 초기화 된 직후부터 동작한다.
        final ScrollView scrollview = ((ScrollView) view.findViewById(R.id.ot_scrollview));
        scrollview.post(new Runnable() {
            @Override
            public void run() {
                scrollview.fullScroll(ScrollView.FOCUS_UP);
            }
        });


    }

    @Override
    public void onBackPressed() {
        //super로 막은경우 back로 종료되지 않게 한다
        //super.onBackPressed();
    }

    public void loadData(){

        if (!Utils.isNetWork(this)) return;

        //Data 가 없습니다
        Log.d (TAG,"loadloadload");

        et_replyContent.setText(""); //답장 초기화

        //AsyncTask 호출
        try {
            Str_result = new OtItemActivity.ExecSelRequestDetail(this).execute(parentRnum).get();
            processResult(view, Str_result);


        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }

    private void InsResponse(){
        SharedPreferences app_preference;
        String sType, sRefer_rnum, sOrigin_rnum, sContent,
                sReceive_ccode, sReceive_cname, sCcode, sCname,sReg_id,sReg_name;


        try {

            //appRequestInsert.php 의 Type=02
            sType = "02";
            sRefer_rnum = txt_rnum.getText().toString();  //관련 refer 번호
            sOrigin_rnum = txt_rnum.getText().toString(); //최상위 rnum번호 (최상위번호 필요)
            sContent = et_replyContent.getText().toString();

            sReceive_ccode = txt_regccode.getText().toString(); //수신대상 업소
            sReceive_cname = txt_regcname.getText().toString(); //수신대상 업소명

            app_preference = PreferenceManager.getDefaultSharedPreferences(this);

            sCcode = app_preference.getString("cCode", "");
            sCname = app_preference.getString("cName", "");
            sReg_id = app_preference.getString("uNum", "");
            sReg_name = app_preference.getString("uName", "");


            String Str_result = new ExecInsResponse(this).execute(sType, sRefer_rnum, sOrigin_rnum,
                    sContent, sReceive_ccode, sReceive_cname, sCcode, sCname,sReg_id,sReg_name).get();
            processResult2(view,Str_result);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (Exception e) {
            //for JSONObject
        }

    }

    private void setListViewHeightBasedOnChildren(ListView listView) {

        //listview의 높이 구하는 방법 http://gpark.tistory.com/4
        // Get list adpter of listview;
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) return;

        int numberOfItems = listAdapter.getCount();

        // Get total height of all items.
        int totalItemsHeight = 0;
        for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
            View item = listAdapter.getView(itemPos, null, listView);
            item.measure(0, 0);
            totalItemsHeight += item.getMeasuredHeight();
        }

        // Get total height of all item dividers.
        int totalDividersHeight = listView.getDividerHeight() * (numberOfItems - 1);

        // Set list height.
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalItemsHeight + totalDividersHeight;
        listView.setLayoutParams(params);
        listView.requestLayout();


    }



    private void processResult(View view, String Str_Json) {

        String tmp_regdate = null, tmp_regcompany = null, tmp_content = null ,
                tmp_replycnt = null,tmp_reponse_rnum = null;

        try {
            //{"data":[{"rnum":"1",xxx},{"rnum":"2",yyyyy],"sql":"Select...","cnt":1,"result":"Success"}
            JSONObject jsonObject = new JSONObject(Str_Json);

            String exeResult = jsonObject.getString("result"); //Success or Fail

            Log.d(TAG, "jsonObject: " + jsonObject.toString());

            listViewMyResponseAdapter.clearData(); //reload를 위해 기존 data clear

            if (exeResult.equals("Success")) {

                JSONArray jsonArray = jsonObject.getJSONArray("data"); //[{"username":"a","token":"test"}]


                //req_type = 01 : request, 02: reply
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                    if ("01".equals(jsonObject1.getString("req_type"))) {

                        Log.d(TAG, "Request Data :  " + jsonObject1.toString());

                        txt_header.setText(jsonObject1.getString("type") + " - " + jsonObject1.getString("category"));
                        txt_rnum.setText(jsonObject1.getString("rnum"));
                        txt_regdate.setText(jsonObject1.getString("reg_date"));
                        txt_title.setText(jsonObject1.getString("title"));
                        txt_content.setText(jsonObject1.getString("content"));
                        txt_region.setText(jsonObject1.getString("region"));
                        txt_area.setText(jsonObject1.getString("area_from") + "-" + jsonObject1.getString("area_to"));
                        txt_floor.setText(jsonObject1.getString("floor_from") + "-" + jsonObject1.getString("floor_to"));
                        txt_room.setText(jsonObject1.getString("room_from") + "-" + jsonObject1.getString("room_to"));

                        txt_sprice.setText(jsonObject1.getString("sprice_from") + "-" + jsonObject1.getString("sprice_to"));
                        txt_dprice.setText(jsonObject1.getString("dprice_from") + "-" + jsonObject1.getString("dprice_to"));
                        txt_rprice.setText(jsonObject1.getString("rprice_from") + "-" + jsonObject1.getString("rprice_to"));

                        txt_company.setText(jsonObject1.getString("cname"));
                        txt_person.setText(jsonObject1.getString("reg_name"));
                        txt_tel.setText(jsonObject1.getString("tel1") + "-" + jsonObject1.getString("tel2") + "-" + jsonObject1.getString("tel3"));
                        txt_mobile.setText(jsonObject1.getString("mobile1") + "-" + jsonObject1.getString("mobile2") + "-" + jsonObject1.getString("mobile3"));

                        //지역 금액 등이  null로 화면에 나온다. 처리
                        //금액은 숫자 처리를 해야 한다 3자릿수 표시하기

                        //hidden data
                        txt_regccode.setText(jsonObject1.getString("ccode"));
                        txt_regcname.setText(jsonObject1.getString("cname"));


                    } else {

                       // Log.d(TAG, "Response Data : " + jsonObject1.getString("req_type") + "//" + jsonObject1.toString());

                        //Adapter를 통해서 Arraylist에 넣는다
                        tmp_regdate = "[" + jsonObject1.getString("rnum") + "]" + jsonObject1.getString("reg_date");
                        tmp_regcompany = jsonObject1.getString("reg_name") + "/" + jsonObject1.getString("cname");
                        tmp_content = jsonObject1.getString("content");

                        tmp_replycnt = jsonObject1.getString("replycnt");
                        tmp_reponse_rnum = jsonObject1.getString("rnum");

                        Log.d(TAG, "tmp_replycnt : " + tmp_replycnt);

                        listViewMyResponseAdapter.addItem(tmp_regdate,tmp_regcompany, tmp_content,tmp_replycnt,tmp_reponse_rnum);
                    }


                }


            } else {
                //Data 가 없습니다
                Snackbar.make(view, kr.co.infonetworks.www.app.R.string.msg_appSelMyRequestFailDetail, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();


            }

            //listview의 높이설정
            setListViewHeightBasedOnChildren(myresponselistView);


        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d(TAG, "processResult");

    }


    private class ExecSelRequestDetail extends AsyncTask<String, Void, String> {
        //String:시작시 필요한 데이터 타입(doInBackground의 input)
        //Void :진행상황을 표현하기 위한 데이터 타입
        //String:Return 데이터 타입(doInBackground의 output 이고 onPostExecute의 input


        private Context context;

        private ProgressDialog pd;

        public ExecSelRequestDetail(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(TAG, "step: ExecSelRequest");

            //progressive bar의 진행을 보여준다
            pd = ProgressDialog.show(context, context.getResources().getString(R.string.pd_title_selOtRequestDetail),
                    context.getResources().getString(R.string.pd_msg_selOtRequestDetail));
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                String rnum = params[0];
                SharedPreferences app_preference = PreferenceManager.getDefaultSharedPreferences(context);
                String ccode = app_preference.getString("cCode", "");

                String app_link = context.getResources().getString(R.string.app_link); //대표주소
                String app_appSeleRequest = context.getResources().getString(R.string.url_appSelOtRequestDetail); //URL

                //String link = new StringBuilder(app_link).append(app_appSeleRequest).toString();
                String link = app_link.concat(app_appSeleRequest);

                String data = URLEncoder.encode("rnum", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(rnum), "UTF-8");
                data += "&" + URLEncoder.encode("ccode", "UTF-8") + "=" + URLEncoder.encode(ccode, "UTF-8");

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

                //Log.d(TAG, "return value json: " + sb.toString().trim());

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

            Log.d(TAG, "notifyDataSetChanged");
            listViewMyResponseAdapter.notifyDataSetChanged(); //listview를 refresh

        }
    }


    private void processResult2(View view, String Str_result) {

        try {
            //{"data":number,"'status'":"Success"}
            JSONObject jsonObject = new JSONObject(Str_result);

            String exeResult = jsonObject.getString("status"); //Success or Fail

            if (exeResult.equals("Success")) {

                parentRefreshFlag=true; //답변을 보냈기때문에 닫기 버튼을 눌렀을때 parent 가 refresh되거나 변경되어야 함.
                parentRefreshValue ++;  //답장의 카운트를 증가 시킨다. -> Parent에 전달한다.

                //reload
                loadData();

            } else {

                //Data 저장시 오류 발생
                Snackbar.make(view, kr.co.infonetworks.www.app.R.string.msg_appMyReplyConfirmFail, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }


        } catch (Exception e) {
            e.printStackTrace();

        }

    }
    //insert replyconfirm
    private class ExecInsResponse extends AsyncTask<String, Void, String> {
        //String:시작시 필요한 데이터 타입(doInBackground의 input)
        //Void :진행상황을 표현하기 위한 데이터 타입
        //String:Return 데이터 타입(doInBackground의 output 이고 onPostExecute의 input

        private ProgressDialog pd;
        private Context context;

        public ExecInsResponse(Context context) {
            this.context = context;
            //Fragment에서 호출해서 activity가 아니라 context를 넘겼다
            //getActivity를 해서 넘겨도 될것 같음
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Log.d(TAG, "step: ExecInsRequest onPreExecute");
            //progressive bar의 진행을 보여준다
            pd = ProgressDialog.show(context,
                    context.getResources().getString(R.string.pd_title_insMyReplyConfirm),
                    context.getResources().getString(R.string.pd_msg_request));

        }

        @Override
        protected String doInBackground(String... params) {
            String sType, sRefer_rnum, sOrigin_rnum, sContent,
                    sReceive_ccode, sReceive_cname, sCcode, sCname,sReg_id,sReg_name;
            try {

                sType       = params[0];  //타입
                sRefer_rnum = params[1];  //관련 refer 번호
                sOrigin_rnum = params[2];  //최상위 rnum번호 (최상위번호 필요)
                sContent     = params[3];

                sReceive_ccode = params[4];  //수신대상 업소
                sReceive_cname = params[5];  //수신대상 업소명
                sCcode         = params[6];  //업소코드
                sCname         = params[7];  //업소명
                sReg_id        = params[8];  //사용자번호
                sReg_name      = params[9];  //사용자 이름



                String app_link = context.getResources().getString(R.string.app_link);
                String app_insRequest = context.getResources().getString(R.string.insRequestlink);

                //String link = new StringBuilder(app_link).append(app_insRequest).toString();
                String link = app_link.concat(app_insRequest);

                String data = URLEncoder.encode("req_type", "UTF-8") + "=" + URLEncoder.encode(sType, "UTF-8");
                data += "&" + URLEncoder.encode("refer_rnum", "UTF-8") + "=" + URLEncoder.encode(sRefer_rnum, "UTF-8");
                data += "&" + URLEncoder.encode("origin_rnum", "UTF-8") + "=" + URLEncoder.encode(sOrigin_rnum, "UTF-8");
                data += "&" + URLEncoder.encode("content", "UTF-8") + "=" + URLEncoder.encode(sContent, "UTF-8");
                data += "&" + URLEncoder.encode("receive_ccode", "UTF-8") + "=" + URLEncoder.encode(sReceive_ccode, "UTF-8");
                data += "&" + URLEncoder.encode("receive_cname", "UTF-8") + "=" + URLEncoder.encode(sReceive_cname, "UTF-8");
                data += "&" + URLEncoder.encode("cCode", "UTF-8") + "=" + URLEncoder.encode(sCcode, "UTF-8");
                data += "&" + URLEncoder.encode("cName", "UTF-8") + "=" + URLEncoder.encode(sCname, "UTF-8");
                data += "&" + URLEncoder.encode("uNum", "UTF-8") + "=" + URLEncoder.encode(sReg_id, "UTF-8");
                data += "&" + URLEncoder.encode("uName", "UTF-8") + "=" + URLEncoder.encode(sReg_name, "UTF-8");


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

            //progressive bar의 진행을 종료한다
            pd.dismiss();


        }
    }


    @Override
    public void OnloadPopupWindow(String str_rnum) {
        //PopupWindow 객체를 이용해 팝업창을 생성하기 위해서는 LayoutInflater 로
        //레이아웃 xml 을 객체화 한후 PopupWindow 인수로 넘기면 됩니다.
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        popup = layoutInflater.inflate(R.layout.ot_replyconfirm_popup, null);

        popupWindow = new PopupWindow(
                popup,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,true);


        popupWindow.setFocusable(true);


        Log.d(TAG,"My getwidht:" + popupWindow.getWidth());



        //배경화면을 불투명하게 함.
        layout_MainFrame = (FrameLayout) findViewById(R.id.mainframe);
        layout_MainFrame.getForeground().setAlpha(180);

        txt_p_rnum = (TextView) popup.findViewById(R.id.tv_rnum);
        txt_p_regtime = (TextView) popup.findViewById(R.id.tv_regtime);
        txt_p_regcompany = (TextView) popup.findViewById(R.id.tv_regcompany);
        txt_p_content = (TextView) popup.findViewById(R.id.tv_content);


        //hidden data
        txt_p_origin_rnum =  (TextView) popup.findViewById(R.id.tv_origin_rnum);
        txt_p_regccode = (TextView) popup.findViewById(R.id.tv_regccode);
        txt_p_regcname = (TextView) popup.findViewById(R.id.tv_regcname);


        //당업소의 response 에 대한 원래 요청업소 replyconfirm를 보여주기 위한  listview 생성
        listViewOtReplyConfirmAdapter = new ListViewOtReplyConfirmAdapter();


        otreplyconfirmlistView = (ListView) popup.findViewById(R.id.lv_replyconfirmlist);
        otreplyconfirmlistView.setAdapter(listViewOtReplyConfirmAdapter);

        //AsyncTask 호출
        loadPopupData(str_rnum);


        Button button = (Button) popup.findViewById(R.id.closeBtn);


        button.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  popupWindow.dismiss(); // PopupWindow의 해제
                  layout_MainFrame.getForeground().setAlpha(0); //배경화면을 투명하게 만듬
              }
          }
        );

        // 어떤 위치에 popupwindow를 호출할것인가를 표시
        //showAsDropDown 은 현재 view에서
        //showAtLocation 는 parent view를 기준으로 보여준다.
        //popupWindow.showAsDropDown(btn_replyBtn,-100,-1200);
        popupWindow.showAtLocation(txt_region, Gravity.CENTER,0,0);

        parentReloadflag = false; //popup이 open시 flag = false로 초기화
    }

    private void loadPopupData(String str_rnum){

        //AsyncTask 호출
        try {
            String Str_result = new ExecSelOtReplyConfirm(this).execute(str_rnum).get();
            processResult3(popup, Str_result);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void processResult3(View view, String Str_Json) {

        String tmp_reginfo = null, tmp_content = null ;

        listViewOtReplyConfirmAdapter.clearData(); //reload를 위해 기존 data clear

        try {
            //{"data":[{"rnum":"1",xxx},{"rnum":"2",yyyyy],"sql":"Select...","cnt":1,"result":"Success"}
            JSONObject jsonObject = new JSONObject(Str_Json);

            String exeResult = jsonObject.getString("result"); //Success or Fail

            Log.d(TAG, "jsonObject: " + jsonObject.toString());


            if (exeResult.equals("Success")) {

                JSONArray jsonArray = jsonObject.getJSONArray("data"); //[{"username":"a","token":"test"}]


                //req_type = 01 : request, 02: reply
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                    //response 경우
                    if ("02".equals(jsonObject1.getString("req_type"))) {

                        Log.d(TAG, "Request Data :  " + jsonObject1.toString());

                        txt_p_rnum.setText(jsonObject1.getString("rnum"));
                        txt_p_regtime.setText(jsonObject1.getString("reg_date"));
                        txt_p_regcompany.setText(jsonObject1.getString("reg_name") + "/" + jsonObject1.getString("cname"));
                        txt_p_content.setText(jsonObject1.getString("content"));

                        //hidden data
                        txt_p_origin_rnum.setText(jsonObject1.getString("origin_rnum"));
                        txt_p_regccode.setText(jsonObject1.getString("ccode")); //등록한 업소
                        txt_p_regcname.setText(jsonObject1.getString("cname")); //등록한 사람


                    } else {

                        Log.d(TAG, "Response Data : " + jsonObject1.getString("req_type") + "//" + jsonObject1.toString());

                        //Adapter를 통해서 Arraylist에 넣는다
                        tmp_reginfo = "["+ jsonObject1.getString("rnum") +"]"
                                + jsonObject1.getString("reg_date") + "/"
                                + jsonObject1.getString("reg_name");

                        tmp_content = jsonObject1.getString("content");

                        listViewOtReplyConfirmAdapter.addItem(tmp_reginfo, tmp_content);
                    }


                }


            } else {
                //Data 가 없습니다
                Snackbar.make(view, kr.co.infonetworks.www.app.R.string.msg_appSelMyRequestFailDetail, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();


            }

            //listview의 높이설정
            setListViewHeightBasedOnChildren(otreplyconfirmlistView);


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    //여기서 부터 고쳐라

    private class ExecSelOtReplyConfirm extends AsyncTask<String, Void, String> {
        //String:시작시 필요한 데이터 타입(doInBackground의 input)
        //Void :진행상황을 표현하기 위한 데이터 타입
        //String:Return 데이터 타입(doInBackground의 output 이고 onPostExecute의 input


        private Context context;

        private ProgressDialog pd;

        public ExecSelOtReplyConfirm(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(TAG, "step : ExecSelReplyConfirm");

            //progressive bar의 진행을 보여준다
            pd = ProgressDialog.show(context, context.getResources().getString(R.string.pd_title_selOtReplyConfirm),
                    context.getResources().getString(R.string.pd_msg_selOtReplyConfirm));
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                String rnum = params[0];

                String app_link = context.getResources().getString(R.string.app_link); //대표주소
                String app_appSeleRequest = context.getResources().getString(R.string.url_appSelOtReplyConfirm); //URL

                //String link = new StringBuilder(app_link).append(app_appSeleRequest).toString();
                String link = app_link.concat(app_appSeleRequest);

                String data = URLEncoder.encode("rnum", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(rnum), "UTF-8");

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

            listViewOtReplyConfirmAdapter.notifyDataSetChanged(); //listview를 refresh

        }
    }




}
