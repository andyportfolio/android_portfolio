package kr.co.infonetworks.www.app.activity;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import kr.co.infonetworks.www.app.R;
import kr.co.infonetworks.www.app.utility.Utils;


/**
 * A simple {@link Fragment} subclass.
 */
public class NotifyReqFragment extends Fragment  {
    private static NotifyReqFragment instance = null;
    private static String newNotifyFlag;

    private final String TAG = "NotifyReqFragment";

//    private static final String TYPENAME_REQUEST = "[매수]";
//    private static final String TYPENAME_RESPONSE = "[답장]";
//    private static final String TYPENAME_CONFIRM = "[확인]";
//    private static final String MEMBER_COMPANY = "회원업소";
//    private static final String SEARCH_TIME = "조회시간:";

    private static final String TYPENAME_REQUEST = "[Rq]";
    private static final String TYPENAME_RESPONSE = "[Re]";
    private static final String TYPENAME_CONFIRM = "[Ck]";
    private static final String MEMBER_COMPANY = "Agent";
    private static final String SEARCH_TIME = "Query:";

    private boolean lastItemVisibleFlag = false;        //화면에 리스트의 마지막 아이템이 보여지는지 체크

    private View view;
    private TextView searchTime;
    private TextView recive_cnt_txt, send_cnt_txt;
    private Button reloadBtn;
    //ListView otlistView;

    private ListView otlistView,mylistView;

    private ListViewOtNotifyAdapter listViewOtNotifyAdapter;
    private ListViewMyNotifyAdapter listViewMyNotifyAdapter;
    
    private ArrayList<ListViewNotifyItem> listViewNotifyItems;


    private String disp_receiveCnt_txt,disp_sendCnt_txt;

    private String disp_currentDateandTime;
    private int sPostion = 0;       //불러와야 할 data의 위치


    private static int REQUEST_CODE = 1;

    public NotifyReqFragment() {
        // Required empty public constructor
    }

    public static NotifyReqFragment newInstance(boolean parentRefreshFlag) {
        if (instance == null) {
            newNotifyFlag = "Y";
            instance = new NotifyReqFragment();
        } else {
            newNotifyFlag = "N";
        }

        if (parentRefreshFlag) newNotifyFlag = "Y"; //child fragment에서 값이 추가되었음으로 reload한다.

        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //fragment 인 경우에는 inflater 를 이용하여 view를 생성해야 한다
        view = inflater.inflate(R.layout.fragment_notifyreq, container, false);

        if (!Utils.isNetWork(getContext())) return  view;

        searchTime = (TextView) view.findViewById(R.id.notifylistView_Time);


        reloadBtn = (Button) view.findViewById(R.id.btn_reload);

        recive_cnt_txt = (TextView) view.findViewById(R.id.recive_cnt_txt);
        otlistView = (ListView) view.findViewById(R.id.notifyOtlistView);

        send_cnt_txt = (TextView) view.findViewById(R.id.send_cnt_txt);
        mylistView = (ListView) view.findViewById(R.id.notifyMylistView);
        

        listViewNotifyItems = new ArrayList<ListViewNotifyItem>();

        //adapter 생성
        //객체가 처음 생긴경우에만 adapter를 다시 생성한다
        if (listViewOtNotifyAdapter == null) {  //또는 //if ("Y".equals(newFlag) ) {
            //fragment인 경우 this -> getActivity()를 사용
            listViewOtNotifyAdapter = new ListViewOtNotifyAdapter(getActivity());
        }

        if (listViewMyNotifyAdapter == null) {  //또는 //if ("Y".equals(newFlag) ) {
            //fragment인 경우 this -> getActivity()를 사용
            listViewMyNotifyAdapter = new ListViewMyNotifyAdapter(getActivity());
        }

        otlistView.setAdapter(listViewOtNotifyAdapter);
        mylistView.setAdapter(listViewMyNotifyAdapter);

        //다시읽어오기 newFlag == Y인 경우는 새로 NotifyReqFragment객체가 생성된것임으로 data를 select한다
        //객체가 생성된후에 다시 호출되면 (메뉴를 통해서 또는 세부item에서 return을 통해서) reload하지 않는다
        if ("Y".equals(newNotifyFlag)) {
            loadItemsFromDB("Init");
        } else {
            //기존에 생성된 객체를 다시 호출할 경우에는 시간, 건수를 화면에 다시 보여준다.
            recive_cnt_txt.setText(disp_receiveCnt_txt);
            send_cnt_txt.setText(disp_sendCnt_txt);

            searchTime.setText(disp_currentDateandTime);

            //listview의 높이설정
            setListViewHeightBasedOnChildren(otlistView);
            setListViewHeightBasedOnChildren(mylistView);


        }

        //reload event 등록
        reloadBtn.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (!Utils.isNetWork(getContext())) return;

                //listview 초기화를 위한 방식
                //http://stackoverflow.com/questions/7400475/how-to-remove-listview-all-items
                listViewOtNotifyAdapter.clearData();
                listViewOtNotifyAdapter.notifyDataSetChanged(); //data가 변경시 evnet 발생

                listViewMyNotifyAdapter.clearData();
                listViewMyNotifyAdapter.notifyDataSetChanged(); //data가 변경시 evnet 발생

                //불러온 값이 있을경우에만 최상위로 이동한다
                //http://stackoverflow.com/questions/2889793/android-listview-scrolling-to-top
                if (loadItemsFromDB("Init") > 0) {
                    otlistView.setVerticalScrollbarPosition(0);
                    mylistView.setVerticalScrollbarPosition(0);
                }

            }
        });


        //수신 listitem click시
        otlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {


                //your code

                ListViewNotifyItem item = (ListViewNotifyItem) parent.getItemAtPosition(position);

                String rnum = item.getRnum(); //key
                String origin_rnum = item.getOrigin_rnum();

                String req_type = item.getReq_type();


                switch (req_type) {
                    case "01": // 매수
                        Intent intent = new Intent(getActivity(),OtItemActivity.class);
                        intent.putExtra("param1",rnum);
                        startActivityForResult(intent,REQUEST_CODE);

                        break;
                    case "02": //담장
                        Intent intent2 = new Intent(getActivity(),MyItemActivity.class);
                        intent2.putExtra("param1",origin_rnum);
                        startActivityForResult(intent2,REQUEST_CODE);

                        break;
                    case "03": //답장확인
                        Intent intent3 = new Intent(getActivity(),OtItemActivity.class);
                        intent3.putExtra("param1",origin_rnum);
                        startActivityForResult(intent3,REQUEST_CODE);

                        break;

                }
            }
        });


        //listitem click시
        mylistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //your code

                ListViewNotifyItem item = (ListViewNotifyItem) parent.getItemAtPosition(position);
                String rnum = item.getRnum(); //key

                String orgin_rnum = item.getOrigin_rnum();

                String req_type = item.getReq_type();


                switch (req_type) {
                    case "01": // 매수
                        Intent intent = new Intent(getActivity(),MyItemActivity.class);
                        intent.putExtra("param1",rnum);
                        startActivityForResult(intent,REQUEST_CODE);


                        break;
                    case "02": //담장
                        Intent intent2 = new Intent(getActivity(),OtItemActivity.class);
                        intent2.putExtra("param1",orgin_rnum);
                        startActivityForResult(intent2,REQUEST_CODE);


                        break;
                    case "03": //답장확인
                        Intent intent3 = new Intent(getActivity(),MyItemActivity.class);
                        intent3.putExtra("param1",orgin_rnum);
                        startActivityForResult(intent3,REQUEST_CODE);

                        break;

                }
            }
        });



        //최상위 view의 Floating ActionBar가 안보이면 보이게 한다
        FloatingActionButton fab = (FloatingActionButton) container.getRootView().findViewById(R.id.fab);

        if (!fab.isShown()) {
            fab.show();
        }

        return view;


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK){
            reloadBtn.callOnClick(); //reload 수행
        }

        super.onActivityResult(requestCode, resultCode, data);
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

        Log.d(TAG,"Param height" + params.height );


    }

    public int loadItemsFromDB(String flag) {
        String from;
        int ret_cnt = 0;

        from = String.valueOf(sPostion);


        //AsyncTask 호출
        try {
            String Str_result = new ExecSelTodayNotify(instance.getContext()).execute(from).get(); //data를 불러올 시작점을 넘겨준다
            ret_cnt = processResult(Str_result, flag);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }


        return ret_cnt;
    }

    private int processResult( String Str_Json, String flag) {
        String  tmp_rnum = null, tmp_reqtype= null, tmp_date= null, tmp_title= null, tmp_refer_rnum= null,
                tmp_origin_rnum= null,tmp_type= null,tmp_category= null, tmp_content= null, tmp_ccode= null,tmp_cname= null,
                tmp_receive_code= null,tmp_receive_name= null,tmp_reqtype_name=null;

        String  tmp_reg_name = null, tmp_display_data = null;

        int recive_cnt = 0,send_cnt = 0;
        int ret_cnt = 0;

        try {
            //{"data":[{"type":"xxxx","category": "yyy"},{"type":"xxxx","category": "yyy"}],"sql":"Select...","cnt":89,"result":"Success"}
            JSONObject jsonObject = new JSONObject(Str_Json);

            String exeResult = jsonObject.getString("result"); //Success or Fail
            String exeCnt = jsonObject.getString("cnt"); //불러온 데이터의 갯수
            ret_cnt = Integer.parseInt(exeCnt);


            listViewMyNotifyAdapter.clearData(); //reload를 위해 기존 data clear
            listViewOtNotifyAdapter.clearData(); //reload를 위해 기존 data clear

            if (exeResult.equals("Success")) {

                SharedPreferences app_preference = PreferenceManager.getDefaultSharedPreferences(this.getContext());
                String ccode = app_preference.getString("cCode", ""); //로그인 업소코드


                JSONArray jsonArray = jsonObject.getJSONArray("data"); //[{"username":"a","token":"test"}]

                //req_type = 01 : request, 02: reply
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                    tmp_rnum = jsonObject1.getString("rnum");
                    tmp_reqtype = jsonObject1.getString("req_type"); //01request, 02reply, 03reply회신

                    tmp_date = jsonObject1.getString("reg_date");
                    tmp_title = jsonObject1.getString("title");

                    tmp_refer_rnum = jsonObject1.getString("refer_rnum");
                    tmp_origin_rnum = jsonObject1.getString("origin_rnum");

                    tmp_type = jsonObject1.getString("type"); //아파트, 연립주택 -1222 매수,급매 구분
                    tmp_category= jsonObject1.getString("category"); //전세, 매매

                    tmp_content = jsonObject1.getString("content");
                    tmp_ccode = jsonObject1.getString("ccode");
                    tmp_cname = jsonObject1.getString("cname");

                    tmp_receive_code = jsonObject1.getString("receive_ccode"); //수신처
                    tmp_receive_name = jsonObject1.getString("receive_cname"); //수신처명

                    //알림 메세지는 심플하게 보여준다

                    if (ccode.equals(tmp_ccode.toString())) { //송신

                        send_cnt++;

                        switch (tmp_reqtype) {
                            case "01": // 매수
                                tmp_reg_name = ">> " + NotifyReqFragment.MEMBER_COMPANY;
                                tmp_display_data = "("+tmp_type+"-"+ tmp_category+")"+tmp_title;
                                tmp_reqtype_name = NotifyReqFragment.TYPENAME_REQUEST;
                                break;
                            case "02": //담장
                                tmp_reg_name = ">> " + tmp_receive_name;
                                tmp_display_data = tmp_content;
                                tmp_reqtype_name = NotifyReqFragment.TYPENAME_RESPONSE;
                                break;
                            case "03": //답장확인
                                tmp_reg_name = ">> " + tmp_receive_name;
                                tmp_display_data = tmp_content;
                                tmp_reqtype_name = NotifyReqFragment.TYPENAME_CONFIRM;
                                break;

                        }
                    listViewMyNotifyAdapter.addItem(tmp_rnum, tmp_refer_rnum, tmp_origin_rnum, tmp_date, tmp_reg_name,tmp_reqtype,tmp_reqtype_name, tmp_display_data);

                    }else if (ccode.equals(tmp_receive_code.toString())){ //수신 (담장 확인)
                        recive_cnt++;

                        switch (tmp_reqtype) {
                            case "02": //담장
                                tmp_reg_name = "<< " + tmp_cname;
                                tmp_display_data = tmp_content;
                                tmp_reqtype_name = NotifyReqFragment.TYPENAME_RESPONSE;
                                break;
                            case "03": //답장확인
                                tmp_reg_name = "<< " + tmp_cname;
                                tmp_display_data = tmp_content;
                                tmp_reqtype_name = NotifyReqFragment.TYPENAME_CONFIRM;
                                break;
                        }

                        listViewOtNotifyAdapter.addItem(tmp_rnum, tmp_refer_rnum, tmp_origin_rnum, tmp_date, tmp_reg_name,tmp_reqtype,tmp_reqtype_name, tmp_display_data);
                    }else  if (!ccode.equals(tmp_receive_code.toString())
                            && "01".equals(tmp_reqtype.toString())) { //수신 (매수)
                        recive_cnt++;

                        tmp_reg_name = "<< " + tmp_cname;
                        tmp_display_data = "("+tmp_type+"-"+ tmp_category+")"+tmp_title;
                        tmp_reqtype_name = NotifyReqFragment.TYPENAME_REQUEST;
                        listViewOtNotifyAdapter.addItem(tmp_rnum, tmp_refer_rnum, tmp_origin_rnum, tmp_date,tmp_reg_name, tmp_reqtype,tmp_reqtype_name, tmp_display_data);
                    }


                }



            } else {
                //Data 가 없습니다
                Snackbar.make(view, kr.co.infonetworks.www.app.R.string.msg_appSelNotifyRequestFail, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();


            }


            StringBuilder builder = new StringBuilder();;
            //[수신:00/송신:00]
            disp_receiveCnt_txt = builder.append("Rec[").append(String.valueOf(recive_cnt)).append(" cnt]").toString();
            recive_cnt_txt.setText(disp_receiveCnt_txt.toString());
            builder.setLength(0);

            disp_sendCnt_txt = builder.append("Send[").append(String.valueOf(send_cnt)).append(" cnt]").toString();
            send_cnt_txt.setText(disp_sendCnt_txt.toString());
            builder.setLength(0);

            disp_currentDateandTime = NotifyReqFragment.SEARCH_TIME + new SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault()).
                    format(new Date());
            searchTime.setText(disp_currentDateandTime);

            //listview의 높이설정
            setListViewHeightBasedOnChildren(otlistView);
            setListViewHeightBasedOnChildren(mylistView);



        } catch (Exception e) {
            e.printStackTrace();
        }


        return ret_cnt;
    }

    public class ExecSelTodayNotify extends AsyncTask<String, Void, String> {
        //String:시작시 필요한 데이터 타입(doInBackground의 input)
        //Void :진행상황을 표현하기 위한 데이터 타입
        //String:Return 데이터 타입(doInBackground의 output 이고 onPostExecute의 input


        private Context context;

        private ProgressDialog pd;

        public ExecSelTodayNotify(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(TAG, "step: ExecSelNotify");


            //progressive bar의 진행을 보여준다
            pd = ProgressDialog.show(context, context.getResources().getString(R.string.pd_title_selNotifyRequest),
                    context.getResources().getString(R.string.pd_msg_selNotifyRequest));

        }

        @Override
        protected String doInBackground(String... params) {

            try {

                String from = params[0]; //어디서 부터 읽어올것인지의 기준점


                String app_link = context.getResources().getString(R.string.app_link); //대표주소
                String app_appSeleNotifyRequest = context.getResources().getString(R.string.url_appSelNotifyRequest); //URL

                String link = app_link.concat(app_appSeleNotifyRequest);

                String data = ""; //paramter가 없음

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
