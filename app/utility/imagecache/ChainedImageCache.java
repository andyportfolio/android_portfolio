package kr.co.infonetworks.www.app.utility.imagecache;

import android.graphics.Bitmap;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by andy on 2016. 9. 8..
 */
public class ChainedImageCache implements ImageCache {

    private List<ImageCache> chain;

    public ChainedImageCache(List<ImageCache> chain) {
        this.chain = chain;
    }

    @Override
    public void addBitmap(String key, Bitmap bitmap) {

        for (ImageCache cache : chain) {
            cache.addBitmap(key, bitmap);
        }
    }

    @Override
    public void addBitmap(String key, File bitmapFile) {
        for (ImageCache cache : chain) {
            cache.addBitmap(key, bitmapFile);
        }
    }

    @Override
    public Bitmap getBitmap(String key) {
        //getBitmap()은 체인을 따라 Bitmap이 존재할 때까지 탐색한다. Bitmap이 발견되면
        // 해당 캐시 이전에 위치한 캐시들(previousCaches에 보관됨)에 Bitmap 정보를 추가해서,
        // 이후 동일 키로 요청이 오면 체인의 앞에서 발견되도록 한다.

        Bitmap bitmap = null;
        List<ImageCache> previousCaches = new ArrayList<ImageCache>();
        for (ImageCache cache : chain) {
            bitmap = cache.getBitmap(key);
            if (bitmap != null) {
                break;
            }
            previousCaches.add(cache);
        }
        if (bitmap == null)
            return null;

        if (!previousCaches.isEmpty()) {
            for (ImageCache cache : previousCaches) {
                cache.addBitmap(key, bitmap);
            }
        }
        return bitmap;
    }

    @Override
    public void clear() {
        for (ImageCache cache : chain) {
            cache.clear();
        }
    }
}
