package kr.co.infonetworks.www.app.utility.imagecache;

import android.graphics.Bitmap;

import java.io.File;

/**
 * Created by andy on 2016. 9. 7..
 */
public interface ImageCache {
    void addBitmap(String key, Bitmap bitmap);

    void addBitmap(String key, File bitmapFile);

    Bitmap getBitmap(String key);

    void clear();
}
