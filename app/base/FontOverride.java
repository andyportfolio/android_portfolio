package kr.co.infonetworks.www.app.base;

/**
 * Created by andy on 2016. 7. 14..
 */

import android.content.Context;
import android.graphics.Typeface;

import java.lang.reflect.Field;

/**
 * 일반적으로 폰트를 수정하기 위해서는 아래와 같이 처리
 * Typeface tf = Typeface.createFromAsset( getAssets() , "fonts/NanumGothic.ttf" );
 * TextView textView1 = (TextView)findViewById(R.id.apptitle);
 * textView1.setTypeface( tf );
 * <p/>
 * 전체를 바꾸기 위해서는 아래와 같은 방법 사용
 * https://medium.com/@ggikko/7-전체-글꼴-네이버-나눔-폰트로-교체하기-dccaaae5da68#.8ekmp0lia
 * Application에서 전역으로 쓰기위해서 Font Override할 클래스
 * <p/>
 * style에 하기의 코드 삽입
 * <item name="android:typeface">monospace</item>
 */
public class FontOverride {

    /**
     * 기본 폰트 셋팅
     */
    public static void setDefaultFont(Context context, String staticTypefaceFieldName, String fontAssetName) {
        final Typeface regular = Typeface.createFromAsset(context.getAssets(), fontAssetName);
        replaceFont(staticTypefaceFieldName, regular);
    }

    /**
     * 기존의 폰트에서 설정한 폰트로 설정
     */
    protected static void replaceFont(String staticTypefaceFieldName, final Typeface newTypeface) {
        try {
            final Field staticField = Typeface.class.getDeclaredField(staticTypefaceFieldName);
            staticField.setAccessible(true);
            staticField.set(null, newTypeface);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}