package kr.co.infonetworks.www.app.activity;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import kr.co.infonetworks.www.app.R;

/**
 * http://recipes4dev.tistory.com/43
 * Created by andy on 2016. 7. 26..
 */
public class ListViewMyReplyAdapter extends BaseAdapter {

    private ArrayList<ListViewMyReplyItem> listViewMyReplyItems = new ArrayList<ListViewMyReplyItem>();

    public ListViewMyReplyAdapter() {

    }

    /**
     * How many items are in the data set represented by this Adapter.
     * Adapter에 사용되는 데이터의 갯수를 리턴
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {
        return listViewMyReplyItems.size();
    }

    /**
     * Get the data item associated with the specified position in the data set.
     * 지정된 위치에 있는 데이터를 리턴
     *
     * @param position Position of the item whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    @Override
    public Object getItem(int position) {
        return listViewMyReplyItems.get(position);
    }

    /**
     * Get the row id associated with the specified position in the list.
     * 지정된 위치에 있는 데이터와 관계된 아이템(row)의 ID를 리턴
     *
     * @param position The position of the item within the adapter's data set whose row id we want.
     * @return The id of the item at the specified position.
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Get a View that displays the data at the specified position in the data set. You can either
     * create a View manually or inflate it from an XML layout file. When the View is inflated, the
     * parent View (GridView, ListView...) will apply default layout parameters unless you use
     * {@link LayoutInflater#inflate(int, ViewGroup, boolean)}
     * to specify a root view and to prevent attachment to the root.
     * postion에 위치한 데이터를 화면에 출력하는데 사용될 view를 return
     *
     * @param position    The position of the item within the adapter's data set of the item whose view
     *                    we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view
     *                    is non-null and of an appropriate type before using. If it is not possible to convert
     *                    this view to display the correct data, this method can create a new view.
     *                    Heterogeneous lists can specify their number of view types, so that this View is
     *                    always of the right type (see {@link #getViewTypeCount()} and
     *                    {@link #getItemViewType(int)}).
     * @param parent      The parent that this view will eventually be attached to
     * @return A View corresponding to the data at the specified position.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Context context = parent.getContext();

        //listview item layout을 inflate하여 convertView참조 획득
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_replymyitem, parent, false);
        }

        //화면에 표시될 view(layout이 inflate된 으로부터 위젯에 대한 참조 획득
        TextView tv_reginfo = (TextView) convertView.findViewById(R.id.tv_reginfo);
        TextView tv_content = (TextView) convertView.findViewById(R.id.tv_content);

        //Data set(listviewItemlist)에서 position에 위치한 데이터 참조 획득
        ListViewMyReplyItem listViewMyReplyItem = listViewMyReplyItems.get(position);

        //아이템 내 각 위젝에 데이터 반영
        tv_reginfo.setText(listViewMyReplyItem.getReginfo());
        tv_content.setText(listViewMyReplyItem.getContent());


        return convertView;
    }

    //아이템 데이터 추가를 위한 함수
    public void addItem(String reginfo, String content) {

        Log.d("addItem", reginfo + "/" + content);
        ListViewMyReplyItem item = new ListViewMyReplyItem();
        item.setReginfo(reginfo);
        item.setContent(content);

        listViewMyReplyItems.add(item);

    }
}
