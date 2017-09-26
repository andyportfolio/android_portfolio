package kr.co.infonetworks.www.app.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.CircleBitmapDisplayer;

import java.util.ArrayList;

import kr.co.infonetworks.www.app.R;

/**
 * Created by andy on 2016. 6. 12..
 */
public class ListViewMyAdapter extends BaseAdapter {
    private final String TAG = "ListViewMyAdapter"; //this.getClass().getName();
    private Context context;


    //data 저장을 위한 arraylist
    private ArrayList<ListViewMyItem> listViewMyItems = new ArrayList<ListViewMyItem>();
    private Bitmap ret_bitmap;
    private Bitmap roundBitmap;

    private DisplayImageOptions options;

    public ListViewMyAdapter(FragmentActivity activity) {
        Log.d(TAG, this.getClass().getName());

        this.context = activity.getApplicationContext();

        //생성시 image loader option 작성
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_stub)
                .showImageForEmptyUri(R.drawable.ic_empty)
                .showImageOnFail(R.drawable.ic_error)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new CircleBitmapDisplayer(Color.WHITE, 5))
                .build();
    }

    @Override
    public int getCount() {
        Log.d(TAG, "count" + listViewMyItems.size());
        return listViewMyItems.size();
    }

    @Override
    public Object getItem(int position) {
        Log.d(TAG, "getItem" + listViewMyItems.get(position).toString());
        return listViewMyItems.get(position);
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
            convertView = layoutInflater.inflate(R.layout.listview_myitem, parent, false);


            listViewHolder = new ListViewHolder();

            //view (viewgroup)에 있는 textview reference get
            listViewHolder.title = (TextView) convertView.findViewById(R.id.title);
            listViewHolder.cnt = (TextView) convertView.findViewById(R.id.cnt);
            listViewHolder.date = (TextView) convertView.findViewById(R.id.date);
            listViewHolder.display_data = (TextView) convertView.findViewById(R.id.display_data);
            listViewHolder.rnum = (TextView) convertView.findViewById(R.id.rnum);


            //profile
            listViewHolder.myProfile = (ImageView) convertView.findViewById(R.id.ivMyProfilePic);

            convertView.setTag(listViewHolder);
        }else {
            listViewHolder = (ListViewHolder) convertView.getTag();
        }

        //--------------------------------------------------------------------------
        //호출한 위치에 있는 item을 select
        ListViewMyItem listViewMyItem = listViewMyItems.get(position);

        listViewHolder.title.setText(listViewMyItem.getTitle());
        listViewHolder.cnt.setText(listViewMyItem.getCnt());
        listViewHolder.date.setText(listViewMyItem.getDate());
        listViewHolder.display_data.setText(listViewMyItem.getDisplay_data());
        listViewHolder.rnum.setText(listViewMyItem.getRnum());


        String profile_name = listViewMyItem.getReg_id();
        profile_name = profile_name.concat(".JPEG"); //1.JPEG, 2.JPEG, 101.JPEG
        String imageUrl = "http://www.infonetworks.co.kr/demo/app/file/profile/" + profile_name;

        //server에 등록이 되어 있으면 imageloader사용
        if (listViewMyItem.getImageYN()) {
            //https://github.com/nostra13/Android-Universal-Image-Loader/blob/master/sample/src/main/java/com/nostra13/universalimageloader/sample/fragment/ImageListFragment.java
            ImageLoader.getInstance().displayImage(imageUrl, listViewHolder.myProfile, options);

        } else {
            //server에 없으면 default image를 보여준다
            Log.d(TAG, "Using default image");
            listViewHolder.myProfile.setImageResource(R.drawable.ic_add_circle_outline_black_18dp);
        }

        Log.d(TAG + "Name", this.getClass().getName());
        Log.d(TAG + "g-pos:", String.valueOf(pos));

        return convertView;
    }


    public void addItem(String title, String cnt, String date, String display_data, String rnum, String regid, Boolean imageYN) {
        ListViewMyItem listViewMyitem = new ListViewMyItem();
        listViewMyitem.setTitle(title);
        listViewMyitem.setCnt(cnt);
        listViewMyitem.setDate(date);
        listViewMyitem.setDisplay_data(display_data);
        listViewMyitem.setRnum(rnum);

        listViewMyitem.setReg_id(regid); //등록자id
        listViewMyitem.setImageYN(imageYN); //profile image등록여부

        listViewMyItems.add(listViewMyitem);

    }

    //clear data
    public void clearData(){
        listViewMyItems.clear();
    }


    private static class ListViewHolder {
        TextView text;
        ImageView icon;

        TextView title;
        TextView cnt;
        TextView date;
        TextView display_data;
        TextView rnum;

        //profile
        ImageView myProfile ;

    }

}
