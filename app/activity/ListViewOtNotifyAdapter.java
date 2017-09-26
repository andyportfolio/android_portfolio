package kr.co.infonetworks.www.app.activity;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import kr.co.infonetworks.www.app.R;

/**
 * Created by andy on 2016. 6. 12..
 */
public class ListViewOtNotifyAdapter extends BaseAdapter {
    private final String TAG = "ListViewOtNotifyAdapter"; //this.getClass().getName();
    private Context context;


    //data 저장을 위한 arraylist
    private ArrayList<ListViewNotifyItem> listViewNotifyItems = new ArrayList<ListViewNotifyItem>();

    public ListViewOtNotifyAdapter(FragmentActivity activity) {
        Log.d(TAG, this.getClass().getName());

        this.context = activity.getApplicationContext();

    }

    @Override
    public int getCount() {
        Log.d(TAG, "count" + listViewNotifyItems.size());
        return listViewNotifyItems.size();
    }

    @Override
    public Object getItem(int position) {
        Log.d(TAG, "getItem" + listViewNotifyItems.get(position).toString());
        return listViewNotifyItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int pos = position;
        Context context = parent.getContext();  //parent는 listview가 보여질 parent

        //viewholder pattern
        //http://bellgori.tistory.com/entry/Android-pattern-01-ViewHolder-pattern
        ListViewHolder listViewHolder;

        //convertView를 생성
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.listview_notifyitem, parent, false);


            listViewHolder = new ListViewHolder();

            //view (viewgroup)에 있는 textview reference get
            listViewHolder.imageView = (ImageView) convertView.findViewById(R.id.notify_image);

            listViewHolder.rnum = (TextView) convertView.findViewById(R.id.rnum);
            listViewHolder.refer_rnum = (TextView) convertView.findViewById(R.id.refer_rnum);
            listViewHolder.origin_rnum = (TextView) convertView.findViewById(R.id.origin_rnum);
            listViewHolder.reg_time = (TextView) convertView.findViewById(R.id.reg_time);
            listViewHolder.reg_name = (TextView) convertView.findViewById(R.id.reg_name);

            listViewHolder.req_type = (TextView) convertView.findViewById(R.id.req_type);
            listViewHolder.reqtype_name = (TextView) convertView.findViewById(R.id.reqtype_name);
            listViewHolder.display_data = (TextView) convertView.findViewById(R.id.display_data);
            
            convertView.setTag(listViewHolder);
        }else {
            listViewHolder = (ListViewHolder) convertView.getTag();
        }

        //--------------------------------------------------------------------------
        //호출한 위치에 있는 item을 select
        ListViewNotifyItem listViewNotifyItem = listViewNotifyItems.get(position);

        String req_type = listViewNotifyItem.getReq_type();
        switch (req_type){
            case "01":
                //수신 매수
                listViewHolder.imageView.setImageResource(R.drawable.ic_email_white_24dp);
                break;

            case "02":
                //수신 답장
                listViewHolder.imageView.setImageResource(R.drawable.ic_forward_white_24dp);
                break;

            case "03":
                //수신 확인
                listViewHolder.imageView.setImageResource(R.drawable.ic_low_priority_white_24dp);
                break;

            default:
                break;
        }




        listViewHolder.rnum.setText(listViewNotifyItem.getRnum());
        listViewHolder.refer_rnum.setText(listViewNotifyItem.getRefer_rnum());
        listViewHolder.origin_rnum.setText(listViewNotifyItem.getOrigin_rnum());
        listViewHolder.reg_time.setText(listViewNotifyItem.getReg_time());
        listViewHolder.reg_name.setText(listViewNotifyItem.getReg_name());

        listViewHolder.req_type.setText(listViewNotifyItem.getReq_type());
        listViewHolder.reqtype_name.setText(listViewNotifyItem.getReqtype_name());
        listViewHolder.display_data.setText(listViewNotifyItem.getDisplay_data());

        return convertView;
    }


    public void addItem(String rnum, String refer_rnum, String origin_rnum, String reg_time,String reg_name,
                        String req_type,String reqtype_name, String display_data) {

        ListViewNotifyItem listViewNotifyitem = new ListViewNotifyItem();

        listViewNotifyitem.setRnum(rnum);
        listViewNotifyitem.setRefer_rnum(refer_rnum);
        listViewNotifyitem.setOrigin_rnum(origin_rnum);
        listViewNotifyitem.setReg_time(reg_time);
        listViewNotifyitem.setReg_name(reg_name);

        listViewNotifyitem.setReq_type(req_type);
        listViewNotifyitem.setReqtype_name(reqtype_name);
        listViewNotifyitem.setDisplay_data(display_data);

        listViewNotifyItems.add(listViewNotifyitem);

    }

    //clear data
    public void clearData(){
        listViewNotifyItems.clear();
    }


    private static class ListViewHolder {
        ImageView imageView;
        TextView rnum;
        TextView refer_rnum;
        TextView origin_rnum;
        TextView reg_time;
        TextView reg_name;
        TextView req_type;
        TextView reqtype_name;
        TextView display_data;

    }

}
