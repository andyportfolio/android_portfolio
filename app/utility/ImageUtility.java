package kr.co.infonetworks.www.app.utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static kr.co.infonetworks.www.app.utility.RoundedImageView.getCroppedBitmap;

/**
 * Created by andy on 2016. 8. 25..
 * http://nashorn.tistory.com/entry/이미지를-원형-모양으로-Crop하는-방법
 * <p/>
 * http://chiwoos.tistory.com/185
 * http://blog.daum.net/hopefullife/214
 * <p/>
 * 이미지 리사이즈
 * http://javalove.egloos.com/67828
 */
public class ImageUtility {

    public static final int SampleSize = 1;
    public static final int dstWidth = 120;
    public static final int dstHeight = 120;
    private static final String TAG = "ImageUtility";

    public ImageUtility() {
    }

    //http://jmsliu.com/1954/android-save-downloading-file-locally.html
    //저장할 cache folder를 만들고, 위치를 return
    public static File getCacheFolder(Context context) {
        File cacheDir = null;

        Log.d(TAG, "context.getCacheDir():" + context.getCacheDir().toString());

        //sd 카드가 있는지 확인한다
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //cachefolder 가 있으면 가져오고, 없으면 만든다
            cacheDir = new File(Environment.getExternalStorageDirectory(), "cachefolder");
            if (!cacheDir.isDirectory()) {
                cacheDir.mkdirs();
            }
        }

        //위의 작업이 실패하면 시스템의 캐쉬폴더를 가져온다
        if (!cacheDir.isDirectory()) {
            cacheDir = context.getCacheDir(); //get system cache folder
        }

        return cacheDir;
    }

    //sd 카드에 data folder를 만들고, 위치를 return
    public static File getDataFolder(Context context) {
        File dataDir = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            dataDir = new File(Environment.getExternalStorageDirectory(), "myappdata");
            if (!dataDir.isDirectory()) {
                dataDir.mkdirs();
            }
        }

        if (!dataDir.isDirectory()) {
            dataDir = context.getFilesDir();
        }

        return dataDir;
    }

    //Write File into Android Cache Folder
    public static void writeintoCache(String strFilePath, Bitmap bitmap) {

        File fileCacheItem = new File(strFilePath);
        OutputStream out = null;

        try {
            fileCacheItem.createNewFile();
            out = new FileOutputStream(fileCacheItem);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

//        try {
//            FileOutputStream fos = new FileOutputStream(cacheDir);
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
//            fos.close();
//        } catch (FileNotFoundException e) {
//            Log.d(TAG, "File not found: " + e.getMessage());
//        } catch (IOException e) {
//            Log.d(TAG, "Error accessing file: " + e.getMessage());
//        }

    }

    //resize for image button
    public static Bitmap resizeImage(ImageButton imageButton, String path) {

        //resize 수행
        // Get the dimensions of the View
        int targetW = imageButton.getWidth();
        int targetH = imageButton.getHeight();


        if (targetW == 0 || targetH == 0) {
            imageButton.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

            targetW = imageButton.getMeasuredWidth();
            targetH = imageButton.getMeasuredHeight();
        }

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        Log.d(TAG, "scaleFactor: " + String.valueOf(scaleFactor)); //4 가 return됨
        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;


        Bitmap photo = BitmapFactory.decodeFile(path, bmOptions);

        Bitmap roundBitmap = getCroppedBitmap(photo);

        return roundBitmap;

    }


    //resize for imageview
    public static Bitmap resizeImage(ImageView imageView, String path) {

        //resize 수행
        // Get the dimensions of the View
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();


        if (targetW == 0 || targetH == 0) {
            imageView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

            targetW = imageView.getMeasuredWidth();
            targetH = imageView.getMeasuredHeight();
        }

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        Log.d(TAG, "scaleFactor: " + String.valueOf(scaleFactor)); //4 가 return됨
        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;


        Bitmap photo = BitmapFactory.decodeFile(path, bmOptions);

        Bitmap roundBitmap = getCroppedBitmap(photo);

        return roundBitmap;

    }


    //resize for imageview with bitmap
    public static Bitmap resizeImage(ImageView imageView, Bitmap bitmap) {

        //resize 수행
        // Get the dimensions of the View
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();


        if (targetW == 0 || targetH == 0) {
            imageView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

            targetW = imageView.getMeasuredWidth();
            targetH = imageView.getMeasuredHeight();
        }

        //Bitmap을 원하는 size로 줄인다
        Bitmap resize = Bitmap.createScaledBitmap(bitmap, targetW, targetH, true);

        Bitmap roundBitmap = getCroppedBitmap(resize);

        return roundBitmap;

    }
}
