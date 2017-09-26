package kr.co.infonetworks.www.app.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import kr.co.infonetworks.www.app.R;


public class OtNotifyItemFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static OtNotifyItemFragment instance = null;
    private static String mParam1;
    private final String TAG = "OtNotifyItemFragment";

    private Button closeBtn;

    private TextView txt_header, txt_rnum, txt_regdate, txt_title, txt_content, txt_region,
            txt_area, txt_floor, txt_room, txt_sprice, txt_dprice, txt_rprice, txt_company, txt_person, txt_tel, txt_mobile;

    private String Str_result;

    private ListViewOtResponseAdapter listViewOtResponseAdapter;
    private ListView otresponselistView;

    private View view;

    public OtNotifyItemFragment() {
        // Required empty public constructor

    }

    public static OtNotifyItemFragment newInstance(String param1) {
        //Bundle args = new Bundle();

        if (instance == null) {
            instance = new OtNotifyItemFragment();
        }

        //frgment간 값을 수신하는 방법중에 하나이지만 , 한번  attached되면 사용할수 없다
        //instance.setArguments(args);
        //args.putString(ARG_PARAM1, param1);
        mParam1 = param1;

        return instance;

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);



        view = inflater.inflate(R.layout.fragment_my_item, container, false);


        //바탕화면의 불투명도를 0로 하여 투명하게 수정함
        FrameLayout layout_MainFrame = (FrameLayout) view.findViewById(R.id.mainframe);
        layout_MainFrame.getForeground().setAlpha( 0);

        txt_header = (TextView) view.findViewById(R.id.tv_header);
        txt_rnum = (TextView) view.findViewById(R.id.tv_rnum);
        txt_regdate = (TextView) view.findViewById(R.id.tv_regdate);
        txt_title = (TextView) view.findViewById(R.id.tv_title);
        txt_content = (TextView) view.findViewById(R.id.tv_content);
        txt_region = (TextView) view.findViewById(R.id.tv_region);
        txt_area = (TextView) view.findViewById(R.id.tv_area);
        txt_floor = (TextView) view.findViewById(R.id.tv_floor);
        txt_room = (TextView) view.findViewById(R.id.tv_room);

        txt_sprice = (TextView) view.findViewById(R.id.tv_sprice);
        txt_dprice = (TextView) view.findViewById(R.id.tv_dprice);
        txt_rprice = (TextView) view.findViewById(R.id.tv_rprice);

        closeBtn = (Button) view.findViewById(R.id.closeBtn);

        txt_header.setText("");

        txt_rnum.setText(mParam1);

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


        //타업소의 response history를 보여주기 위한  listview 생성
        //향후 popup에서 parent함수를 호출하기 위해서 this를 파라메터로 넘김

       // listViewOtResponseAdapter = new ListViewOtResponseAdapter(view,this);

        otresponselistView = (ListView) view.findViewById(R.id.lv_responselist);
        otresponselistView.setAdapter(listViewOtResponseAdapter);
        //setListViewHeightBasedOnChildren(listView);

        //loadData();

        closeBtn.setOnClickListener(new Button.OnClickListener() {
            /**
             * Called when a view has been clicked.
             *
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Start");

                FragmentManager mFragmentManager
                        = OtNotifyItemFragment.this.getFragmentManager();//getActivity().getSupportFragmentManager();//getFragmentManager();//getChildFragmentManager();
                FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();
                // and add the transaction to the back stack
                mFragmentTransaction.replace(R.id.root3_fragment, NotifyReqFragment.newInstance(false));
                //mFragmentTransaction.remove(MyItemActivity.newInstance());
                mFragmentTransaction.addToBackStack(null);

                mFragmentTransaction.commit();
                //mFragmentManager.executePendingTransactions();

                Log.d(TAG, "End");


            }
        });


        //최상위 view의 Floating ActionBar 감춤
        FloatingActionButton fab = (FloatingActionButton) container.getRootView().findViewById(R.id.fab);
        fab.hide();


        //scroll 이동 http://tiann.tistory.com/13
        final ScrollView scrollview = ((ScrollView) view.findViewById(R.id.my_scrollview));
        scrollview.post(new Runnable() {
            @Override
            public void run() {
                scrollview.fullScroll(ScrollView.FOCUS_UP);
            }
        });

        return view;

    }

//    public void loadData(){
//
//        //Data 가 없습니다
//        Log.d (TAG,"loadloadload");
//
//        //AsyncTask 호출
//        try {
//            Str_result = new ExecSelRequestDetail(getContext()).execute(mParam1).get();
//            processResult(view, Str_result);
//
//
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    private void setListViewHeightBasedOnChildren(ListView listView) {
//
//        //listview의 높이 구하는 방법 http://gpark.tistory.com/4
//        // Get list adpter of listview;
//        ListAdapter listAdapter = listView.getAdapter();
//        if (listAdapter == null) return;
//
//        int numberOfItems = listAdapter.getCount();
//
//        // Get total height of all items.
//        int totalItemsHeight = 0;
//        for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
//            View item = listAdapter.getView(itemPos, null, listView);
//            item.measure(0, 0);
//            totalItemsHeight += item.getMeasuredHeight();
//        }
//
//        // Get total height of all item dividers.
//        int totalDividersHeight = listView.getDividerHeight() * (numberOfItems - 1);
//
//        // Set list height.
//        ViewGroup.LayoutParams params = listView.getLayoutParams();
//        params.height = totalItemsHeight + totalDividersHeight;
//        listView.setLayoutParams(params);
//        listView.requestLayout();
//
//
//    }
//
//
//
//    private void processResult(View view, String Str_Json) {
//
//        String tmp_regdate = null, tmp_regcompany = null, tmp_content = null ,
//                tmp_replycnt = null,tmp_reponse_rnum = null;
//
//        try {
//            //{"data":[{"rnum":"1",xxx},{"rnum":"2",yyyyy],"sql":"Select...","cnt":1,"result":"Success"}
//            JSONObject jsonObject = new JSONObject(Str_Json);
//
//            String exeResult = jsonObject.getString("result"); //Success or Fail
//
//            Log.d(TAG, "jsonObject: " + jsonObject.toString());
//
//            listViewOtResponseAdapter.clearData(); //reload를 위해 기존 data clear
//
//            if (exeResult.equals("Success")) {
//
//                JSONArray jsonArray = jsonObject.getJSONArray("data"); //[{"username":"a","token":"test"}]
//
//
//                //req_type = 01 : request, 02: reply
//                for (int i = 0; i < jsonArray.length(); i++) {
//                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
//
//                    if ("01".equals(jsonObject1.getString("req_type"))) {
//
//                        Log.d(TAG, "Request Data :  " + jsonObject1.toString());
//
//                        txt_header.setText(jsonObject1.getString("type") + " - " + jsonObject1.getString("category"));
//                        txt_rnum.setText(jsonObject1.getString("rnum"));
//                        txt_regdate.setText(jsonObject1.getString("reg_date"));
//                        txt_title.setText(jsonObject1.getString("title"));
//                        txt_content.setText(jsonObject1.getString("content"));
//                        txt_region.setText(jsonObject1.getString("region"));
//                        txt_area.setText(jsonObject1.getString("area_from") + "-" + jsonObject1.getString("area_to"));
//                        txt_floor.setText(jsonObject1.getString("floor_from") + "-" + jsonObject1.getString("floor_to"));
//                        txt_room.setText(jsonObject1.getString("room_from") + "-" + jsonObject1.getString("room_to"));
//
//                        txt_sprice.setText(jsonObject1.getString("sprice_from") + "-" + jsonObject1.getString("sprice_to"));
//                        txt_dprice.setText(jsonObject1.getString("dprice_from") + "-" + jsonObject1.getString("dprice_to"));
//                        txt_rprice.setText(jsonObject1.getString("rprice_from") + "-" + jsonObject1.getString("rprice_to"));
//
//
//                        //지역 금액 등이  null로 화면에 나온다. 처리
//                        //금액은 숫자 처리를 해야 한다 3자릿수 표시하기
//
//                    } else {
//
//                        Log.d(TAG, "Response Data : " + jsonObject1.getString("req_type") + "//" + jsonObject1.toString());
//
//                        //Adapter를 통해서 Arraylist에 넣는다
//                        tmp_regdate = "[" + jsonObject1.getString("rnum") + "]" + jsonObject1.getString("reg_date");
//                        tmp_regcompany = jsonObject1.getString("reg_name") + "/" + jsonObject1.getString("cname");
//                        tmp_content = jsonObject1.getString("content");
//                        tmp_replycnt = jsonObject1.getString("replycnt");
//                        tmp_reponse_rnum = jsonObject1.getString("rnum");
//
//                        Log.d(TAG, "tmp_replycnt : " + tmp_replycnt);
//
//                        listViewOtResponseAdapter.addItem(tmp_regdate,tmp_regcompany, tmp_content,tmp_replycnt,tmp_reponse_rnum);
//                    }
//
//
//                }
//
//
//            } else {
//                //Data 가 없습니다
//                Snackbar.make(view, R.string.msg_appSelMyRequestFailDetail, Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//
//
//            }
//
//            //listview의 높이설정
//            setListViewHeightBasedOnChildren(otresponselistView);
//
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        Log.d(TAG, "processResult");
//
//    }
//
//    private class ExecSelRequestDetail extends AsyncTask<String, Void, String> {
//        //String:시작시 필요한 데이터 타입(doInBackground의 input)
//        //Void :진행상황을 표현하기 위한 데이터 타입
//        //String:Return 데이터 타입(doInBackground의 output 이고 onPostExecute의 input
//
//
//        private Context context;
//
//        private ProgressDialog pd;
//
//        public ExecSelRequestDetail(Context context) {
//            this.context = context;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            Log.d(TAG, "step: ExecSelRequest");
//
//                    //progressive bar의 진행을 보여준다
//            pd = ProgressDialog.show(context, context.getResources().getString(R.string.pd_title_selMyRequestDetail),
//                    context.getResources().getString(R.string.pd_msg_selMyRequestDetail));
//        }
//
//        @Override
//        protected String doInBackground(String... params) {
//
//            try {
//                String rnum = params[0];
//
//                String app_link = context.getResources().getString(R.string.app_link); //대표주소
//                String app_appSeleRequest = context.getResources().getString(R.string.url_appSelMyRequestDetail); //URL
//
//                //String link = new StringBuilder(app_link).append(app_appSeleRequest).toString();
//                String link = app_link.concat(app_appSeleRequest);
//
//                String data = URLEncoder.encode("rnum", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(rnum), "UTF-8");
//
//
//
//                Log.d(TAG, "step: doInBackground link=" + link);
//
//                URL url = new URL(link);
//                URLConnection conn = url.openConnection();
//
//                conn.setDoOutput(true); //post
//                //http전송을 위한 header값을 만든다
//                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
//                //post데이터를 생성하여 PHP를 호출한다
//                wr.write(data);
//                wr.flush();
//
//                //PHP의 값을 가져온다
//                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//
//                StringBuffer sb = new StringBuffer();
//                String json = null;
//
//                while ((json = reader.readLine()) != null) {
//                    sb.append(json + "\n");
//                }
//
//                Log.d(TAG, "return value json: " + sb.toString().trim());
//
//                return sb.toString().trim();
//
//            } catch (Exception e) {
//                return new String("Exception: " + e.getMessage());
//            }
//
//
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            super.onPostExecute(result);
//
//
//            //progressive bar의 진행을 종료한다
//            pd.dismiss();
//
//            Log.d(TAG, "notifyDataSetChanged");
//            listViewOtResponseAdapter.notifyDataSetChanged(); //listview를 refresh
//
//        }
//    }

}
