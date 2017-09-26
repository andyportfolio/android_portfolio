package kr.co.infonetworks.www.app.utility;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

/**
 * Created by andy on 2016. 9. 7..
 */
public class ImageCacheUtility {

    //private으로 선언한 이유는 ImageCacheUtility의 메소드 를 통해서만 접근을 가능하게 하려고 함
    private static ImageCacheUtility instance = null;
    private static LruCache<String, Bitmap> mMemoryCache = null;

    public ImageCacheUtility() {
    }

    public static ImageCacheUtility newInstance() {
        if (instance == null) {
            instance = new ImageCacheUtility();
        }
        return instance;
    }

    public void createMemmoryCache(int maxMemory) {

        final int cacheSize = maxMemory / 8;

        if (mMemoryCache == null) {
            // Use 1/8th of the available memory for this memory cache.

            mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
                @Override
                protected int sizeOf(String key, Bitmap bitmap) {
                    // The cache size will be measured in kilobytes rather than
                    // number of items.
                    return bitmap.getByteCount() / 1024;
                }
            };
        }

    }

    //add
    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    //get
    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    public void clearCache() {
        mMemoryCache.evictAll();
    }

}
