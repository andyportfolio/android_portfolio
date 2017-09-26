package kr.co.infonetworks.www.app.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import kr.co.infonetworks.www.app.R;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * http://recipes4dev.tistory.com/43
 * https://medium.com/@henen/빠르게-배우는-안드로이드-리스트뷰-listview-98f7b9fe3c93#.3y19aep0r
 * Created by andy on 2016. 7. 26..
 */
public class ListViewMyResponseAdapter extends BaseAdapter {
    private static String TAG = "ListViewMyResponseAdapter";
    private ArrayList<ListViewMyResponseItem> listViewMyResponseItems = new ArrayList<ListViewMyResponseItem>();

    private Context context;


    //Listner-2.member변수 선언 (Parent와 연결하기 위해서)
    private PopupListner popupListner;

    //listViewMyResponseAdapter 의 btn_replyBtn.setOnClickListener을 처리하기 위하여
    //Listner-1. 인터페이스 정의
    public interface PopupListner {
        void OnloadPopupWindow(String str_rnum);
    }

    public ListViewMyResponseAdapter(PopupListner popupListner) {
        //Listner-3.인터페이스를 통해서 parent의 refernece를 받아온다
        //사실상 MyItemActivity가 넘어온 것으로
        //OtItemActivity가 PopupListner를 상속받았기때문에
        //부모클래스 = 자식클래스 를 하게 되면 내부적으로 형변환(casting)이 발생하여 레퍼런스가 전달된다.
        this.popupListner = popupListner;
        this.context  = (Context)popupListner;


    }

    /**
     *  이 리스트뷰가 몇개의 아이템을 가지고있는지를 알려주는 함수입니다.
     *  arraylist의 size(갯수) 만큼 가지고있으므로 size 를 return
     */
    @Override
    public int getCount() {
        return listViewMyResponseItems.size();
    }

    /**
     * 현재 어떤 아이템인지를 알려주는 부분으로 우리는 arraylist에 저장되있는 객체중
     * position에 해당하는 data를 가져옴
     */
    @Override
    public Object getItem(int position) {
        return listViewMyResponseItems.get(position);
    }

    /**
     * 현재 어떤 포지션인지를 알려주는 부분으로 현재 포지션을 return
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * 리스트뷰에서 아이템과 xml을 연결하여 화면에 표시해주는 부분
     * getView부분에서 반복문이 실행된다고 이해하시면 편하며 순차적으로 한칸씩 화면을 구성해주는 부분입니다
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final String str_rnum;

        //listview item layout을 inflate하여 convertView참조 획득
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_my_responseitem, parent, false);
        }

        //화면에 표시될 view(layout이 inflate된 으로부터 위젯에 대한 참조 획득
        TextView tv_regtime         = (TextView) convertView.findViewById(R.id.tv_regtime);
        TextView tv_regcompany      = (TextView) convertView.findViewById(R.id.tv_regcompay);
        TextView tv_content         = (TextView) convertView.findViewById(R.id.tv_content);
        TextView tv_replycnt        = (TextView) convertView.findViewById(R.id.tv_replycnt);
        TextView tv_response_rnum   = (TextView) convertView.findViewById(R.id.tv_response_rnum);

        final Button btn_replyBtn   = (Button) convertView.findViewById(R.id.replyBtn);

        //Data set(listviewItemlist)에서 position에 위치한 데이터 참조 획득
        ListViewMyResponseItem listViewResponseItem = listViewMyResponseItems.get(position);

        //아이템 내 각 위젝에 데이터 반영
        tv_regtime.setText(listViewResponseItem.getRegtime());
        tv_regcompany.setText(listViewResponseItem.getRegcompany());
        tv_content.setText(listViewResponseItem.getContent());
        tv_replycnt.setText(listViewResponseItem.getReplycnt());
        tv_response_rnum.setText(listViewResponseItem.getResponse_rnum());


        btn_replyBtn.setText("확인(" + listViewResponseItem.getReplycnt() + ")");

        str_rnum = listViewResponseItem.getResponse_rnum(); //호출할 Key값

        //call window popup
        btn_replyBtn.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
               popupListner.OnloadPopupWindow(str_rnum);
            }
        });


        return convertView;
    }

    //아이템 데이터 추가를 위한 함수
    public void addItem(String regtime,String regcompany, String content,String replycnt , String reponse_rnum) {

        ListViewMyResponseItem item = new ListViewMyResponseItem();
        item.setRegtime(regtime);
        item.setRegcompany(regcompany);
        item.setContent(content);
        item.setReplycnt(replycnt);
        item.setResponse_rnum(reponse_rnum);

        listViewMyResponseItems.add(item);

    }

    //clear data
    public void clearData(){
        listViewMyResponseItems.clear();
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







}
