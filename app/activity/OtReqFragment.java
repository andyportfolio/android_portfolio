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
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import kr.co.infonetworks.www.app.R;
import kr.co.infonetworks.www.app.utility.Utils;

/**
 * A simple {@link Fragment} subclass.
 */
public class OtReqFragment extends Fragment  {
    private static OtReqFragment instance = null;
    private static String newOtReqFlag;

    private static int REQUEST_CODE = 1;

    private final String TAG = "OtReqFragment";
    private boolean lastItemVisibleFlag = false;        //화면에 리스트의 마지막 아이템이 보여지는지 체크

    private static boolean myRefreshFlag;        //화면에 리스트의 마지막 아이템이 보여지는지 체크
    private static int myPostion ;
    private static int myRefreshValue ;

    private View view;
    private TextView searchTime;
    private TextView totalCnt;
    private Button reloadBtn;


    private PullToRefreshListView otlistView;

    private ListViewOtAdapter listViewOtAdapter;
    private ArrayList<ListViewOtItem> listViewOtItems;

    private String disp_totalCnt;
    private String disp_currentDateandTime;
    private int sPostion = 0;       //불러와야 할 data의 위치

    public OtReqFragment() {
        // Required empty public constructor
    }


    public static OtReqFragment newInstance(boolean parentRefreshFlag,int parentPostion,int refreshValue) {
        if (instance == null) {
            newOtReqFlag = "Y";
            instance = new OtReqFragment();
        } else {
            newOtReqFlag = "N";
        }

        OtReqFragment.myRefreshFlag = parentRefreshFlag;
        OtReqFragment.myPostion = parentPostion;
        OtReqFragment.myRefreshValue = refreshValue;

        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView-start");



        //fragment 인 경우에는 inflater 를 이용하여 view를 생성해야 한다
        view = inflater.inflate(R.layout.fragment_otreq, container, false);

        if (!Utils.isNetWork(getContext())) return  view;

        searchTime = (TextView) view.findViewById(R.id.otlistView_Time);
        totalCnt = (TextView) view.findViewById(R.id.otlistView_Cnt);

        //사용법 : http://itmir.tistory.com/529
        otlistView = (PullToRefreshListView) view.findViewById(R.id.otlistView);
        otlistView.setMode(PullToRefreshBase.Mode.BOTH);


        listViewOtItems = new ArrayList<ListViewOtItem>();

        //adapter 생성
        //객체가 처음 생긴경우에만 adapter를 다시 생성한다
        if (listViewOtAdapter == null) {  //또는 //if ("Y".equals(newFlag) ) {
            //fragment인 경우 this -> getActivity()를 사용
            listViewOtAdapter = new ListViewOtAdapter();

        }

        otlistView.setAdapter(listViewOtAdapter);

        //다시읽어오기 newFlag== Y인 경우는 새로 MyReqFragment객체가 생성된것임으로 data를 select한다
        //객체가 생성된후에 다시 호출되면 (메뉴를 통해서 또는 세부item에서 return을 통해서) reload하지 않는다
        if ("Y".equals(newOtReqFlag)) {
            loadItemsFromDB("Init");
        } else {
            //기존에 생성된 객체를 다시 호출할 경우에는 시간, 건수를 화면에 다시 보여준다.
            totalCnt.setText(disp_totalCnt);
            searchTime.setText(disp_currentDateandTime);
        }


        //Set a listener to be invoked when the list should be refreshed.
        otlistView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {

                if (!Utils.isNetWork(getContext())) return;

                Log.d(TAG,"currentMode" + otlistView.getCurrentMode());

                if(otlistView.getCurrentMode().equals(PullToRefreshBase.Mode.PULL_FROM_START )){
                    //position 초기화
                    sPostion = 0;

                    //listview 초기화를 위한 방식
                    //http://stackoverflow.com/questions/7400475/how-to-remove-listview-all-items
                    listViewOtAdapter.clearData();
                    listViewOtAdapter.notifyDataSetChanged(); //data가 변경시 evnet 발생


                    //불러온 값이 있을경우에만 최상위로 이동한다
                    //http://stackoverflow.com/questions/2889793/android-listview-scrolling-to-top
                    if (loadItemsFromDB("Init") > 0) {
                        //otlistView.setSelectionAfterHeaderView();
                        otlistView.setVerticalScrollbarPosition(0);
                    }
                }else{
                    if (loadItemsFromDB("PulltoRefresh") > 0) {
                        listViewOtAdapter.notifyDataSetChanged();
                    }
                }


            }
        });

        //listitem click시
        otlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //your code

                ListViewOtItem item = (ListViewOtItem) parent.getItemAtPosition(position);

                String rnum = item.getRnum(); //key
                int responsecnt =  Integer.parseInt(item.getCnt().toString()); //현재 답장의 갯수

                Intent intent = new Intent(getActivity(),OtItemActivity.class);
                intent.putExtra("param1",rnum);
                intent.putExtra("param2",position);
                intent.putExtra("param3",responsecnt);

                startActivityForResult(intent,REQUEST_CODE);

            }
        });


        //최상위 view의 Floating ActionBar가 안보이면 보이게 한다
        FloatingActionButton fab = (FloatingActionButton) container.getRootView().findViewById(R.id.fab);

        if (!fab.isShown()) {
            fab.show();
        }

        Log.d(TAG, "onCreateView-end");

        return view;


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK){

            int myPostion       = data.getIntExtra("parentPostion",0);
            int myRefreshValue  = data.getIntExtra("parentRefreshValue",0);

            ListViewOtItem item = (ListViewOtItem) listViewOtAdapter.getItem(myPostion-1);
            item.setCnt(String.valueOf(myRefreshValue));
            listViewOtAdapter.notifyDataSetChanged(); //data가 변경시 evnet 발생
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public int loadItemsFromDB(String flag) {
        String from;
        int ret_cnt = 0;

        from = String.valueOf(sPostion);


        //AsyncTask 호출
        try {
            String Str_result = new ExecSelOtRequest(instance.getContext()).execute(from).get(); //data를 불러올 시작점을 넘겨준다
            ret_cnt = processResult(Str_result, flag);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }


        return ret_cnt;
    }

    private int processResult( String Str_Json,  String flag) {

        String tmp_title, tmp_cnt, tmp_date, tmp_display_data, tmp_rnum, tmp_regid, tmp_company;
        Boolean tmp_imageYN;
        StringBuilder builder = new StringBuilder();
        int ret_cnt = 0;

        try {
            //{"data":[{"type":"xxxx","category": "yyy"},{"type":"xxxx","category": "yyy"}],"sql":"Select...","cnt":89,"result":"Success"}
            JSONObject jsonObject = new JSONObject(Str_Json);

            String exeResult = jsonObject.getString("result"); //Success or Fail
            String exeCnt = jsonObject.getString("cnt"); //불러온 데이터의 갯수


            //처음 페이지가 open되거나 reload 버튼을 클릭했을때만 화면에 조회정보를 보여준다
            if ("Init".equals(flag)) {
                //전체 갯수 지정
                disp_totalCnt = builder.append("[").append(jsonObject.getString("total_cnt")).append(" cnt]").toString();
                totalCnt.setText(disp_totalCnt);
                builder.setLength(0);

                disp_currentDateandTime = "Query:" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(new Date());
                searchTime.setText(disp_currentDateandTime);
            }

            //Log.d(TAG, "jsonObject: " + jsonObject.toString());


            if (exeResult.equals("Success")) {

                JSONArray jsonArray = jsonObject.getJSONArray("data"); //[{"username":"a","token":"test"}]


                //req_type = 01 : request, 02: reply
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                    tmp_title = jsonObject1.getString("title");
                    tmp_cnt = jsonObject1.getString("rcnt");

                    tmp_rnum = jsonObject1.getString("rnum"); //key

                    tmp_display_data = builder.append("[").append(tmp_rnum).append("]")
                            .append(jsonObject1.getString("type")).append("-")
                            .append(jsonObject1.getString("category")).append("-")
                            .append(jsonObject1.getString("region")).toString();

                    builder.setLength(0); //stringbulder 초기화

                    tmp_date = jsonObject1.getString("reg_date");

                    tmp_regid = jsonObject1.getString("reg_id");

                    tmp_company =  builder.append(jsonObject1.getString("cname"))
                                        .append(" / ")
                                        .append(jsonObject1.getString("reg_name"))
                                        .toString();
                    builder.setLength(0); //stringbulder 초기화

                    //값이 존재하면(1) true, 없으면(0) false
                    tmp_imageYN = !"0".equals(jsonObject1.getString("imamgecnt"));

                    //String title, String cnt, String date, String display_data, String rnum ,Boolean imageYN
                    listViewOtAdapter.addItem(tmp_title, tmp_cnt, tmp_date, tmp_display_data, tmp_rnum, tmp_regid,tmp_company, tmp_imageYN);


                }

                //불러온 갯수 만큼이 다음에 읽어올 포지션이된다
                //처음에 0 이고 15개를 불러왔으면 다음에 불러올 포지션은 15이다
                //처음에 0 이고 5개만 불러왔으면 다음에 불러올 포지션은 5이다
                sPostion += Integer.parseInt(exeCnt);

                ret_cnt = Integer.parseInt(exeCnt);


            } else {
                //Data 가 없습니다
                Snackbar.make(view, kr.co.infonetworks.www.app.R.string.msg_appSelMyRequestFail, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();


            }




        } catch (Exception e) {
            e.printStackTrace();
        }


        return ret_cnt;
    }

    public class ExecSelOtRequest extends AsyncTask<String, Void, String> {
        //String:시작시 필요한 데이터 타입(doInBackground의 input)
        //Void :진행상황을 표현하기 위한 데이터 타입
        //String:Return 데이터 타입(doInBackground의 output 이고 onPostExecute의 input


        private Context context;

        private ProgressDialog pd;

        public ExecSelOtRequest(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(TAG, "step: ExecSelMyRequest_Notused");


            //progressive bar의 진행을 보여준다
            pd = ProgressDialog.show(context, context.getResources().getString(R.string.pd_title_selOtRequest), context.getResources().getString(R.string.pd_msg_selOtRequest));
        }

        @Override
        protected String doInBackground(String... params) {

            try {

                String from = params[0]; //어디서 부터 읽어올것인지의 기준점

                SharedPreferences app_preference = PreferenceManager.getDefaultSharedPreferences(context);
                String ccode = app_preference.getString("cCode", ""); //업소코드

                String loadcnt = app_preference.getString("loadcnt", "6"); //한번에 불러올 데이터의 사이즈, 없으면 10개씩 불러온다


                String app_link = context.getResources().getString(R.string.app_link); //대표주소
                String app_appSeleOtRequest = context.getResources().getString(R.string.url_appSelOtRequest); //URL

                //String link = new StringBuilder(app_link).append(app_appSeleRequest).toString();
                String link = app_link.concat(app_appSeleOtRequest);

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

//                Log.d(TAG, "return value json: " + sb.toString().trim());

                return sb.toString().trim();

            } catch (Exception e) {
                return new String("Exception: " + e.getMessage());
            }


        }

        @Override
        protected void onPostExecute(String result) {

            super.onPostExecute(result);
            otlistView.onRefreshComplete();
            Log.d(TAG, "otlistView.onRefreshComplete();");

            //progressive bar의 진행을 종료한다
            pd.dismiss();


        }
    }

}
