package kr.co.infonetworks.www.app.utility.imagecache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by andy on 2016. 9. 7..
 */
public class ImageCacheFactory {

    private static ImageCacheFactory instance = new ImageCacheFactory();
    private HashMap<String, ImageCache> cacheMap = new HashMap<String, ImageCache>();

    private ImageCacheFactory() {
    }

    public static ImageCacheFactory getInstance() {
        return instance;
    }

    public ImageCache createMemoryCache(String cacheName, int imageMaxCounts) {

        //하나의 객체에 여러개의 스레드가 접근해서 처리하고자 할때
        //static으로 선언한 객체에 여러 스레드가 동시에 사용할때
        synchronized (cacheMap) {
            checkAlreadyExists(cacheName);

            //http://hyeonstorage.tistory.com/186
            //형변환 : 클래스는 클래스가 구현하고 있는 인터페이스 유형으로도 변환할 수 있다
            ImageCache cache = new MemoryImageCache(imageMaxCounts);
            cacheMap.put(cacheName, cache);
            return cache;
        }
    }

    private void checkAlreadyExists(String cacheName) {
        ImageCache cache = cacheMap.get(cacheName);
        if (cache != null) {

            try {
                throw new ImageCacheAlreadyExistException(String.format(
                        "ImageCache[%s] aleady exists", cacheName));
            } catch (ImageCacheAlreadyExistException e) {
                e.printStackTrace();
            }

        }
    }

    public ImageCache createTwoLevelCache(String cacheName, int imageMaxCounts) {
        synchronized (cacheMap) {
            checkAlreadyExists(cacheName);
            List<ImageCache> chain = new ArrayList<ImageCache>();
            chain.add(new MemoryImageCache(imageMaxCounts));
            //chain.add(new FileImageCache(cacheName));
            ChainedImageCache cache = new ChainedImageCache(chain);
            cacheMap.put(cacheName, cache);
            return cache;
        }
    }

    private class ImageCacheAlreadyExistException extends Exception {
        public ImageCacheAlreadyExistException(String format) {
            //TO-DO

        }

    }
}
