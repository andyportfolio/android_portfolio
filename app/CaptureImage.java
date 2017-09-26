//package com.example.andy.second;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.database.Cursor;
//import android.graphics.Bitmap;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.Environment;
//import android.provider.MediaStore;
//import android.support.v7.app.AppCompatActivity;
//import android.util.Log;
//import android.widget.ImageButton;
//import android.widget.Toast;
//
//import com.example.andy.second.db.ExecUploadImage;
//
//import org.json.JSONObject;
//
//import java.io.File;
//import java.util.concurrent.ExecutionException;
//
///**
// * Created by andy on 2016. 8. 22..
// */
//public class CaptureImage{
//    private static String TAG = "CaptureImage";
//
//    private static final int PICK_FROM_CAMERA = 0;
//    private static final int PICK_FROM_ALBUM = 1;
//    private static final int CROP_FROM_CAMERA = 2;
//
//    private Uri mImageCaptureUri;
//    private ImageButton imageButton;
//
//    Activity activity;
//
//
//    public CaptureImage() {
//    }
//
//    public CaptureImage(Activity activity, ImageButton imageButton) {
//        this.imageButton = imageButton;
//        this.activity = activity;
//
//    }
//
//    //http://theeye.pe.kr/archives/1285
//    //camera profile
//    /**
//     * 카메라에서 이미지 가져오기
//     */
//    public void doTakePhotoAction()
//    {
//    /*
//     * 참고 해볼곳
//     * http://2009.hfoss.org/Tutorial:Camera_and_Gallery_Demo
//     * http://stackoverflow.com/questions/1050297/how-to-get-the-url-of-the-captured-image
//     * http://www.damonkohler.com/2009/02/android-recipes.html
//     * http://www.firstclown.us/tag/android/
//     */
//
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//
//        // 임시로 사용할 파일의 경로를 생성
//        String url = "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
//        mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), url));
//
//        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
//        // 특정기기에서 사진을 저장못하는 문제가 있어 다음을 주석처리 합니다.
//        //intent.putExtra("return-data", true);
//        activity.startActivityForResult(intent, PICK_FROM_CAMERA);
//    }
//
//
//
//    /**
//     * 앨범에서 이미지 가져오기
//     */
//    public void doTakeAlbumAction()
//    {
//        // 앨범 호출
//        Intent intent = new Intent(Intent.ACTION_PICK);
//        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
//
//        activity.startActivityForResult(intent, PICK_FROM_ALBUM);
//    }
//
//
//
//
//    @Override
//    protected void Activity.onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (resultCode != Activity.RESULT_OK) {
//            return;
//        }
//
//        switch (requestCode) {
//            case CROP_FROM_CAMERA: {
//                // 크롭이 된 이후의 이미지를 넘겨 받습니다.
//                // 이미지뷰에 이미지를 보여준다거나 부가적인 작업 이후에
//                // 임시 파일을 삭제합니다.
//                final Bundle extras = data.getExtras();
//
//                if (extras != null) {
//                    Bitmap photo = extras.getParcelable("data");
//                    imageButton.setImageBitmap(photo);
//                }
//
//
//                //사진을 찍던 앨범에서 가져오던 이 rutine울 수행한다
//                //server로 upload하는 로직을 호출한다
//                //UploadImage uploadImage = new UploadImage();
//                //uploadImage.encodeImagetoString();
//
//
//                String selectedFilePath = getPathFromUri(mImageCaptureUri);
//                Log.d(TAG, "path: " + selectedFilePath);
//                //갤러리에서 가져올때 /storage/emulated/0/DCIM/Camera/20160818_223015.jpg
//
//
//                String Str_imageResult;
//                try {
//                    //AsyncTask 호출
//                    Str_imageResult = new ExecUploadImage(this).execute(selectedFilePath, "x").get();
//                    processResult(Str_imageResult);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                } catch (ExecutionException e) {
//                    e.printStackTrace();
//                }
//
//
//                File f = new File(mImageCaptureUri.getPath());
//                if (f.exists()) {
//                    f.delete();
//                }
//
//                break;
//            }
//
//            case PICK_FROM_ALBUM: {
//                // 이후의 처리가 카메라와 같으므로 일단  break없이 진행합니다. ==> case PICK_FROM_CAMERA: 로 이동함
//                // 실제 코드에서는 좀더 합리적인 방법을 선택하시기 바랍니다.
//
//                mImageCaptureUri = data.getData();
//            }
//
//            case PICK_FROM_CAMERA: {
//                // 이미지를 가져온 이후의 리사이즈할 이미지 크기를 결정합니다.
//                // 이후에 이미지 크롭 어플리케이션을 호출하게 됩니다.
//
//                Intent intent = new Intent("com.android.camera.action.CROP");
//                intent.setDataAndType(mImageCaptureUri, "image/*");
//
//                intent.putExtra("outputX", 90);
//                intent.putExtra("outputY", 90);
//                intent.putExtra("aspectX", 1);
//                intent.putExtra("aspectY", 1);
//                intent.putExtra("scale", true);
//                intent.putExtra("return-data", true);
//                startActivityForResult(intent, CROP_FROM_CAMERA);
//
//                break;
//            }
//        }
//
//
//    }
//
//    private String getPathFromUri(Uri uri) {
//        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
//        cursor.moveToNext();
//        String path = cursor.getString(cursor.getColumnIndex("_data"));
//        cursor.close();
//
//        return path;
//    }
//
//
//    private void processResult(String Str_Json) {
//
//        try {
//            //"{'msg':'xxxxx','result':'Success'}"
//            JSONObject jsonObject = new JSONObject(Str_Json);
//
//            String exeResult = jsonObject.getString("result"); //Success or Fail
//
//            Log.d("jsonObject: ", jsonObject.toString());
//
//
//            if (exeResult.equals("Success")) {
//                Toast.makeText(activity, "성공", Toast.LENGTH_LONG).show();
//
//
//            } else {
//                Toast.makeText(activity, "실패", Toast.LENGTH_LONG).show();
//
//                //없는 사용자
//                //                Snackbar.make(view, R.string.msg_loginFail, Snackbar.LENGTH_LONG)
//                //                        .setAction("Action", null).show();
//                //                //Toast.makeText(this, this.getResources().getString(R.string.loginFail), Toast.LENGTH_LONG).show();
//                //intent = new Intent(this, LoginActivity.class);
//                //startActivity(intent);
//
//            }
//
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//
//    }
//
//
//}
