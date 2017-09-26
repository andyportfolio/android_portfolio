package kr.co.infonetworks.www.app.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.CircleBitmapDisplayer;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import kr.co.infonetworks.www.app.R;

/**
 * Created by andy on 2016. 6. 18..
 */
public class RecycleOtAdapter extends RecyclerView.Adapter<RecycleOtAdapter.MyViewHolder> {

    private static final String TAG = "RecycleOtAdapter";
    StringBuilder builder = new StringBuilder();
    private ArrayList<RecycleViewOtItem> otItemList;

    private Context context;

    private DisplayImageOptions options;

    public RecycleOtAdapter(ArrayList<RecycleViewOtItem> otItemList, Context context) {
        this.otItemList = otItemList;
        this.context = context;


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
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_ot_item_20160927, parent, false);

        //Log.d(TAG, "onCreateViewHolder" + itemView.getContext());
        return new MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        //Log.d(TAG, "onBindViewHolder:" + String.valueOf(position));

        RecycleViewOtItem otItem = otItemList.get(position);

        String profile_name = otItem.getReg_id();
        profile_name = profile_name.concat(".JPEG"); //1.JPEG, 2.JPEG, 101.JPEG
        String imageUrl = "http://www.infonetworks.co.kr/demo/app/file/profile/" + profile_name;

        //server에 등록이 되어 있으면 imageloader사용
        if (otItem.getImageYN()) {
            //https://github.com/nostra13/Android-Universal-Image-Loader/blob/master/sample/src/main/java/com/nostra13/universalimageloader/sample/fragment/ImageListFragment.java
            ImageLoader.getInstance().displayImage(imageUrl, holder.rc_iv, options);


        } else {
            //server에 없으면 default image를 보여준다
            Log.d(TAG, "Using default image");
            holder.rc_iv.setImageResource(R.drawable.ic_add_circle_outline_black_18dp);
        }




        //Server에서 받아온 이미지를 넣는다 만약 이미지가 없는경우 default image를 표시한다
//        String profile_name = otItem.getReg_id();
//        profile_name = profile_name.concat(".jpg"); //1.jpg, 2.jpg, 101.jpg

        //[비즈니스 로직]
        //Web Server에서 불러오기 전에 Local에 있는지 확인하여 , Local에 존재하는 경우
        //Local에서 읽어오고, 없는 경우 web에서 호출하고 Local에 저장한다.
        //web에도 없는 경우 default icon 을 보여준다

        //[2016.09.05 수정로직]
        //서버에 프로파일을 올릴때 파일을 올렸다고 마킹을 해야 한다
        //왜먄하면 서버에 파일이 있는지 없는지를 알고 서버에가서 데이터를 받아와야 한다
        //일단 서버에 가서 없구나 하면 속도가 느리게 된다
        //그리고 당업소 매수에 해당 로직을 넣고 타없소 매수에는 외부 라이브러리를 사용해라

//        Log.d(TAG, "nNum & image YN" + otItem.getReg_id() + "--" + otItem.getImageYN().toString());
//
//        File cacheDir = ImageUtility.getCacheFolder(context);
//
//        String profile_path = cacheDir.toString() + "/" + profile_name;
//
//        //Log.d(TAG, "cacheDir.toString: " + cacheDir.toString());
//        //Log.d(TAG, "profile_name: " + profile_name);
//
//        File profile_image = new File(profile_path); // /storage/emulated/0/cachefolder/3.jpg
//
//        Bitmap bitmap = null;
//        Bitmap roundBitmap = null;
//        int w, h;
//        int radius;
//
//
//        if (profile_image.exists() == true) {
//            Log.d(TAG, "file exists in local: " + profile_path);
//
//            roundBitmap = ImageUtility.resizeImage(holder.rc_iv, profile_path);
//            holder.rc_iv.setImageBitmap(roundBitmap);
//
//
////            bitmap = BitmapFactory.decodeFile(profile_path);
////
////            w = bitmap.getWidth();
////            h = bitmap.getHeight();
////            radius = w > h ? h : w; // set the smallest edge as radius.
////            roundBitmap = getCroppedBitmap(bitmap, radius);
////
////            holder.rc_iv.setImageBitmap(roundBitmap);
//
//        } else {
//            Log.d(TAG, "file does not exisit in local: " + profile_path);
//            //holder.rc_iv.setImageResource(R.drawable.ic_add_circle_outline_black_18dp); //otItem.getImageView());
//
//            //server에 등록이 되어 있으면 서버에서 가져온다
//            if (otItem.getImageYN()) {
//                //AysncTask 호출
//                try {
//                    bitmap = new DownloadAsyncTask().execute(profile_name).get();
//
//                    if (bitmap == null) {
//                        //server에 image가 없으면 default image사용
//                        Log.d(TAG, "****No server image: it is not good");
//                        holder.rc_iv.setImageResource(R.drawable.ic_add_circle_outline_black_18dp);
//                    } else {
//                        Log.d(TAG, "load bitmap from server & save to local: ");
//
////                        w = bitmap.getWidth();
////                        h = bitmap.getHeight();
////                        radius = w > h ? h : w; // set the smallest edge as radius.
////                        roundBitmap = getCroppedBitmap(bitmap, radius);
//
//                        roundBitmap = getCroppedBitmap(bitmap);
//                        holder.rc_iv.setImageBitmap(roundBitmap);
//
//                        //cashe file로 저장한다
//                        //Log.d(TAG, "Save to cachedir: " + cacheDir.toString());
//                        ImageUtility.writeintoCache(profile_path, bitmap);
//
//                    }
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                } catch (ExecutionException e) {
//                    e.printStackTrace();
//                }
//            } else {
//                //server에 없으면 default image를 보여준다
//                Log.d(TAG, "Using default image");
//                holder.rc_iv.setImageResource(R.drawable.ic_add_circle_outline_black_18dp);
//            }
//        }


        holder.rc_title.setText(otItem.getTitle());

        builder.setLength(0); //초기화
        holder.rc_cnt.setText(builder.append("(").append(otItem.getReplycnt()).append(")"));

        builder.setLength(0); //초기화
        holder.rc_DisplayData.setText(builder.append("[").append(otItem.getRnum()).append("]").append(otItem.getType()).append("-")
                .append(otItem.getCategory()).append("-").append(otItem.getRegion()).toString());

        holder.rc_rnum.setText(otItem.getRnum());

        holder.rc_date.setText(otItem.getReg_date());

        builder.setLength(0); //초기화

        holder.rc_company.setText(builder.append(otItem.getCompany()).append(" / ").append(otItem.getPerson()).toString());



    }

    /**
     * Returns the total number of items in the data set hold by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return otItemList.size();
    }


    public void clearData() {
        otItemList.clear();

    }

    //innerclass
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView rc_title, rc_cnt, rc_DisplayData, rc_rnum, rc_date, rc_company;
        private ImageView rc_iv;

        public MyViewHolder(View view) {
            super(view);

            rc_iv = (ImageView) view.findViewById(R.id.ivProfilePic);

            rc_title = (TextView) view.findViewById(R.id.tvTitle);
            rc_cnt = (TextView) view.findViewById(R.id.tvCnt);
            rc_DisplayData = (TextView) view.findViewById(R.id.tvDisplayData);
            rc_rnum = (TextView) view.findViewById(R.id.tvRnum);


            rc_date = (TextView) view.findViewById(R.id.tvDate);
            rc_company = (TextView) view.findViewById(R.id.tvCompany);



        }

    }

    private class DownloadAsyncTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {
            // TODO Auto-generated method stub
            //load image directly
            String filename = params[0];
            Bitmap bitmap = null;

            try {
                //imageuRL = /host/home2/re1/html/demo/app/file/profile/
                //http://www.infonetworks.co.kr/demo/app/file/profile/2.jpg
                //기존  URL + filename
                String iURL = "http://www.infonetworks.co.kr/demo/app/file/profile/" + filename;
                Log.d(TAG, "iURL: " + iURL);
                URL imageURL = new URL(iURL);


                bitmap = BitmapFactory.decodeStream(imageURL.openStream());

            } catch (IOException e) {
                // TODO: handle exception
                Log.e("error", "Downloading Image Failed");
                bitmap = null;
            }

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            // TODO Auto-generated method stub
//            if (result == null) {
//
//                rc_iv.set
//                result.imageView.setImageResource(R.drawable.postthumb_loading);
//            } else {
//                result.imageView.setImageBitmap(result.bitmap);
//            }
        }
    }

}
