package kr.co.infonetworks.www.app.activity;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import kr.co.infonetworks.www.app.R;
import kr.co.infonetworks.www.app.db.ExecSelRequest;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotifyRequestFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static String TAG = "NotifyRequestFragment";
    private static NotifyRequestFragment instance = null;

    private View view;
    private ListView listView;
    private ListViewMyReplyAdapter listViewMyReplyAdapter;

    private int rnum;
    private String Str_result;
    private TextView txt_header, txt_rnum, txt_regdate, txt_title, txt_content, txt_region,
            txt_area, txt_floor, txt_room, txt_sprice, txt_dprice, txt_rprice, txt_company, txt_person, txt_tel, txt_mobile;

    public NotifyRequestFragment() {
        // Required empty public constructor
    }


    // https://medium.com/@henen/빠르게배우는-안드로이드-fragment-4-fragment간-에-데이터전달-c6ab1d0766af#.zboolqob0
// google keyword : android fragment parameter
    public static NotifyRequestFragment newInstance(int param1) {
        instance = new NotifyRequestFragment();

        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        instance.setArguments(args);
        return instance;
    }

    /**
     * Called to do initial creation of a fragment.  This is called after
     * and before
     * {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * <p/>
     * <p>Note that this can be called while the fragment's activity is
     * still in the process of being created.  As such, you can not rely
     * on things like the activity's content view hierarchy being initialized
     * at this point.  If you want to do work once the activity itself is
     * created, see {@link #onActivityCreated(Bundle)}.
     *
     * @param savedInstanceState If the fragment is being re-created from
     *                           a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            rnum = getArguments().getInt(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_three, container, false);

        Log.d(TAG, "onCreateView: " + String.valueOf(rnum));
        view = inflater.inflate(R.layout.fragment_notifyrequest, container, false);


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

        txt_company = (TextView) view.findViewById(R.id.tv_company);
        txt_person = (TextView) view.findViewById(R.id.tv_person);
        txt_tel = (TextView) view.findViewById(R.id.tv_tel);
        txt_mobile = (TextView) view.findViewById(R.id.tv_mobile);


        txt_header.setText("");
        txt_rnum.setText("");
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


        //reply history를 보여주기 위한  listview 생성
        listViewMyReplyAdapter = new ListViewMyReplyAdapter();

        listView = (ListView) view.findViewById(R.id.lv_list);
        listView.setAdapter(listViewMyReplyAdapter);
        //setListViewHeightBasedOnChildren(listView);

        Context context = getContext();
        //AsyncTask 호출
        try {
            Str_result = new ExecSelRequest(context).execute(rnum).get();
            processResult(view, Str_result);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        //data를 불러와
        //인텐트로 받은 변수값을 받아와서.불러와얗 한다
        return view;

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

        String tmp_regdate = null, tmp_regname = null, tmp_content = null;

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

                    if ("01".equals(jsonObject1.getString("req_type"))) {

                        Log.d(TAG, "req_type=01==>: " + jsonObject1.toString());

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
                        txt_company.setText(jsonObject1.getString("cname") + "-" + jsonObject1.getString("caddress"));
                        txt_person.setText(jsonObject1.getString("reg_name"));
                        txt_tel.setText(jsonObject1.getString("tel1") + "-" + jsonObject1.getString("tel2") + "-" + jsonObject1.getString("tel3"));
                        txt_mobile.setText(jsonObject1.getString("mobile1") + "-" + jsonObject1.getString("mobile2") + "-" + jsonObject1.getString("mobile3"));

                        //지역 금액 등이  null로 화면에 나온다. 처리
                        //금액은 숫자 처리를 해야 한다 3자릿수 표시하기

                    } else {

                        Log.d(TAG, "req_type <> 01==>: " + jsonObject1.getString("req_type") + "//" + jsonObject1.toString());

                        //list에 넣는다
                        //기존 답장 목록이다
                        tmp_regdate = jsonObject1.getString("reg_date");
                        tmp_regname = " - " + jsonObject1.getString("reg_name");

                        tmp_regdate = tmp_regdate.concat(tmp_regname);
                        tmp_content = jsonObject1.getString("content");

                        listViewMyReplyAdapter.addItem(tmp_regdate, tmp_content);
                    }


                }


            } else {
                //Data 가 없습니다
                Snackbar.make(view, kr.co.infonetworks.www.app.R.string.msg_appSelRequestFail, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();


            }

            //listview의 높이설정
            setListViewHeightBasedOnChildren(listView);


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}
