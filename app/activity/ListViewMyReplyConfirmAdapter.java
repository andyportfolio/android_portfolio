package kr.co.infonetworks.www.app.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import kr.co.infonetworks.www.app.R;

import java.util.ArrayList;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class ListViewMyReplyConfirmAdapter extends BaseAdapter {

    private static String TAG = "ListViewMyReplyConfirmAdapter";
    private ArrayList<ListViewMyReplyConfirmItem> listViewMyReplyConfirmItems
            = new ArrayList<ListViewMyReplyConfirmItem>();


    public ListViewMyReplyConfirmAdapter() {
    }

    /**
     *  이 리스트뷰가 몇개의 아이템을 가지고있는지를 알려주는 함수입니다.
     *  arraylist의 size(갯수) 만큼 가지고있으므로 size 를 return
     */
    @Override
    public int getCount() {
        return listViewMyReplyConfirmItems.size();
    }

    /**
     * 현재 어떤 아이템인지를 알려주는 부분으로 우리는 arraylist에 저장되있는 객체중
     * position에 해당하는 data를 가져옴
     */
    @Override
    public Object getItem(int position) {
        return listViewMyReplyConfirmItems.get(position);
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
        final Context context = parent.getContext();
        final String str_rnum;


        //listview item layout을 inflate하여 convertView참조 획득
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_replyconfirm_myitem, parent, false);
        }

        //화면에 표시될 view(layout이 inflate된 으로부터 위젯에 대한 참조 획득
        TextView tv_reginfo = (TextView) convertView.findViewById(R.id.tv_reginfo);
        TextView tv_content = (TextView) convertView.findViewById(R.id.tv_content);


        //Data set(listviewItemlist)에서 position에 위치한 데이터 참조 획득
        ListViewMyReplyConfirmItem listViewMyReplyConfirmItem = listViewMyReplyConfirmItems.get(position);

        tv_reginfo.setText(listViewMyReplyConfirmItem.getReginfo());
        tv_content.setText(listViewMyReplyConfirmItem.getContent());

        return convertView;
    }

    //아이템 데이터 추가를 위한 함수
    public void addItem(String reginfo, String content) {

        ListViewMyReplyConfirmItem item = new ListViewMyReplyConfirmItem();
        item.setReginfo(reginfo);
        item.setContent(content);

        listViewMyReplyConfirmItems.add(item);

    }

    //clear data
    public void clearData(){
        listViewMyReplyConfirmItems.clear();
    }


}
