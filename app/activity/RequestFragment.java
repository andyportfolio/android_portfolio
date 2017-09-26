package kr.co.infonetworks.www.app.activity;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import kr.co.infonetworks.www.app.R;
import kr.co.infonetworks.www.app.TabFragment;
import kr.co.infonetworks.www.app.db.ExecInsRequest;
import kr.co.infonetworks.www.app.utility.Utils;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestFragment extends Fragment {

    private static final String TAG = "RequestFragment";
    private Button insRequestBtn, resetBtn;
    private EditText editType, editTitle, editContent;
    private Spinner spinnerType, spinnerCategory;
    private SharedPreferences app_preference;

    private String sType, sCategory, sTitle, sContent, sUserID;

    public RequestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_request, container, false);


        //http://arabiannight.tistory.com/entry/안드로이드Android-arrays-xml-을-이용한-스피너-만들기
        spinnerType = (Spinner) view.findViewById(R.id.sp_type);
        spinnerType.setAdapter(new ArrayAdapter<String>(getContext(), kr.co.infonetworks.www.app.R.layout.spinner_item,
                (String[])getResources().getStringArray(R.array.spinnerArray1)));


        spinnerCategory = (Spinner) view.findViewById(R.id.sp_category);
        spinnerCategory.setAdapter(new ArrayAdapter<String>(getContext(), kr.co.infonetworks.www.app.R.layout.spinner_item,
                (String[])getResources().getStringArray(R.array.spinnerArray2)));

        editType = (EditText) view.findViewById(R.id.typeText);
        editTitle = (EditText) view.findViewById(R.id.reqTitle);
        editContent = (EditText) view.findViewById(R.id.reqContent);

        insRequestBtn = (Button) view.findViewById(R.id.requestBtn);
        resetBtn = (Button) view.findViewById(R.id.resetBtn);

//        //default value for test
//        ((EditText) view.findViewById(R.id.reqTitle)).setText("Test Title");
//        ((EditText) view.findViewById(R.id.reqContent)).setText("Test content");
//        int i = 3;
//        ((Spinner) view.findViewById(R.id.sp_type)).setSelection(i);
//        ((Spinner) view.findViewById(R.id.sp_category)).setSelection(i);
//

        //http://kd3302.tistory.com/67
        //Type - 아파트
        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //직접입력을 선택한 경우 직접입력 EditText를 visible
                //그외에는 EditText에 값을 넣는다

                //Toast.makeText(getContext(), "Pos: " + String.valueOf(position) + " data" + parent.getItemAtPosition(position), Toast.LENGTH_LONG).show();

                if (position == 1) {
                    editType.setText("");
                    editType.setVisibility(View.VISIBLE); //보여준다
                } else {
                    editType.setText(parent.getItemAtPosition(position).toString());
                    editType.setVisibility(View.GONE); //화면에서 안보이고, 영역도 사라진다
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                editType.setText("");
                //Toast.makeText(getContext(), "nothing1", Toast.LENGTH_LONG).show();
            }
        });

        //Category-매매
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getContext(), "Pos: " + String.valueOf(position) + " data" + parent.getItemAtPosition(position), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Toast.makeText(getContext(), "nothing2", Toast.LENGTH_LONG).show();
            }
        });

        //리셋버튼
        resetBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                resetData();
            }
        });

        //구합니다 버튼
        insRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!Utils.isNetWork(getContext()))  return;

                //Login시 저장된 preferece에서 값 가져오기
                //Activity,Service 와 같이 contextwarraper를 상속받은 곳에서는 getApplicationContext가
                //this 를 return하지만 view에서는 해당액티비티.this 또는 getApplicationContext를 쓴다
                app_preference = PreferenceManager.getDefaultSharedPreferences(RequestFragment.this.getContext());

                String str_cCode = app_preference.getString("cCode", ""); //업소코드
                String str_cName = app_preference.getString("cName", ""); //업소명
                String str_uNum = app_preference.getString("uNum", ""); //사용자번호
                String str_uName = app_preference.getString("uName", ""); //사용자 이름

                //화면값
                //sType = spinnerType.getSelectedItem().toString();
                sType = editType.getText().toString();
                if (!checkValidation(sType, "1", v)) return;

                sCategory = spinnerCategory.getSelectedItem().toString();
                if (!checkValidation(sCategory, "2", v)) return;

                sTitle = editTitle.getText().toString();
                if (!checkValidation(sTitle, "3", v)) {
                    editTitle.requestFocus(); //EditText에 focus를 줄수 있다
                    return;
                }

                sContent = editContent.getText().toString();
                if (!checkValidation(sContent, "4", v)) {
                    editContent.requestFocus();
                    return;
                }


                Context context = getContext();

                //Toast.makeText(getContext(),"type: " + sType + "sCategory" + sCategory + "sTitle" + sTitle + "sContent" + sContent, Toast.LENGTH_LONG).show();

//향후 추가 해라 (2016-10-21)
//3. 급매처리 (급매는 동일 업소에서 2번만됨 경고 메세지가 떠야 한다)

                try {
                    RequestFragment.this.getContext();
                    String Str_result = new ExecInsRequest(context).execute(sType, sCategory, sTitle, sContent, str_cCode, str_cName, str_uNum, str_uName).get();
                    processResult(v, Str_result);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    //for JSONObject
                }

            }
        });

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            //2016-10-19 update
            FragmentManager fm = getActivity().getSupportFragmentManager(); //TabFragment.this.getFragmentManager();

            RequestFragment requestFragment = (RequestFragment)fm.findFragmentByTag("requestFragment");
            TabFragment tabFragment = (TabFragment) fm.findFragmentByTag("tabFragment");


            fm.beginTransaction().hide(requestFragment).show(tabFragment).commit();

            }
        });


        return view;
    }

     /*reset data*/
    private void resetData(){
        spinnerType.setSelection(0);
        spinnerCategory.setSelection(0);

        editType.setText("");
        editTitle.setText("");
        editContent.setText("");

        editTitle.requestFocus(); //EditText에 focus를 준다다
    }

    /*Vlidation check*/
    private boolean checkValidation(String val, String flag, View view) {
        boolean retval = false;

        if (val.toString().trim().length() < 1) {

            switch (flag) {
                case "1":
                    showMsg(getContext().getString(R.string.msg_type).toString(), view);
                    break;

                case "2":
                    showMsg(getContext().getString(R.string.msg_category).toString(), view);
                    break;

                case "3":
                    showMsg(getContext().getString(R.string.msg_title).toString(), view);
                    break;

                case "4":
                    showMsg(getContext().getString(R.string.msg_content).toString(), view);
                    break;

            }

            return retval;
        } else {


            switch (flag) {
                case "1":
                    if (spinnerType.getSelectedItemPosition() == 0) {
                        showMsg(getContext().getString(R.string.msg_type).toString(), view);
                        retval = false;
                    } else {
                        retval = true;
                    }
                    break;

                case "2":
                    if (spinnerCategory.getSelectedItemPosition() == 0) {
                        showMsg(getContext().getString(R.string.msg_category).toString(), view);
                        retval = false;
                    } else {
                        retval = true;
                    }
                    break;

                default:
                    retval = true;
                    break;
            }
            return retval;
        }
    }

    private void showMsg(String msg, View view) {
        Snackbar.make(view, msg, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    private void processResult(View view, String Str_result) {
        FragmentManager mFragmentManager;
        FragmentTransaction mFragmentTransaction;

        try {
            //{"data":number,"'status'":"Success"}
            JSONObject jsonObject = new JSONObject(Str_result);

            String exeResult = jsonObject.getString("status"); //Success or Fail

            if (exeResult.equals("Success")) {


//                /* List로 돌아간다다 *///                FragmentManager fm = getActivity().getSupportFragmentManager();
//
//                TabFragment tabFragment = (TabFragment)fm.findFragmentByTag("tabFragment");
//                RequestFragment requestFragment = (RequestFragment)fm.findFragmentByTag("requestFragment");
//                fm.
//                    beginTransaction().
//                    show(tabFragment).
//                    hide(requestFragment).
//                    commit();


                resetData();

                //snack bar 동작안함
                //snack bar를 이용해도 좋을것 같다
                Snackbar.make(view, kr.co.infonetworks.www.app.R.string.msg_requestSucess, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();


            } else {

                //snack bar 동작안함
                //snack bar를 이용해도 좋을것 같다
                Snackbar.make(view, kr.co.infonetworks.www.app.R.string.msg_requestFail, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

            }


        } catch (Exception e) {
            e.printStackTrace();

        }

    }


}
