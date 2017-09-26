package kr.co.infonetworks.www.app.activity;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;

import kr.co.infonetworks.www.app.R;
import kr.co.infonetworks.www.app.TabFragment;
import kr.co.infonetworks.www.app.utility.Utils;


/**
 * A simple {@link Fragment} subclass.
 */
public class NotifyConfigurationFragment extends Fragment {

    private static final String TAG = "NotifyConfigurationFragment";
    private Button notifyConfigBtn, gotoListBtn;

    private Switch send_switch,receive_switch;
    private SharedPreferences app_preference;


    public NotifyConfigurationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notifyconfiguration, container, false);

        if (!Utils.isNetWork(getContext()))  return view;

        send_switch = (Switch) view.findViewById(R.id.send_switch);
        receive_switch = (Switch) view.findViewById(R.id.receive_switch);


        notifyConfigBtn = (Button) view.findViewById(R.id.notifyConfigBtn);
        gotoListBtn = (Button) view.findViewById(R.id.gotoListBtn);



        SharedPreferences app_preference  = PreferenceManager.getDefaultSharedPreferences(getContext());
        boolean notify_send_flag     = app_preference.getBoolean("notify_send_flag", true);
        boolean notify_receive_flag  = app_preference.getBoolean("notify_receive_flag", true);


        if (notify_send_flag){
            send_switch.setChecked(true);
        }else{
            send_switch.setChecked(false);
        }

        if (notify_receive_flag){
            receive_switch.setChecked(true);
        }else{
            receive_switch.setChecked(false);

        }


        //리셋버튼
        gotoListBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getSupportFragmentManager();

                TabFragment tabFragment
                        = (TabFragment) fm.findFragmentByTag("tabFragment");

                RequestFragment requestFragment
                        = (RequestFragment)fm.findFragmentByTag("requestFragment");


                NotifyConfigurationFragment notifyConfigurationFragment
                        = (NotifyConfigurationFragment)fm.findFragmentByTag("notifyConfigurationFragment");

                fm.beginTransaction().show(tabFragment).hide(requestFragment).hide(notifyConfigurationFragment).commit();

            }
        });

        //구합니다 버튼
        notifyConfigBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                SharedPreferences app_preference = PreferenceManager.getDefaultSharedPreferences(getContext());

                SharedPreferences.Editor editor = app_preference.edit();
                editor.putBoolean("notify_send_flag",send_switch.isChecked() );
                editor.putBoolean("notify_receive_flag",receive_switch.isChecked() );
                editor.apply();

                String msg;
                if (send_switch.isChecked() ){
                    msg = "수신시 알림 설정이 '켜기' , ";
                }
                else{
                    msg = "수신시 알림 설정이 '끄기' , ";
                }

                if (receive_switch.isChecked() ){
                    msg = msg + "송신시 알림 설정이 '켜기' 로 저장되었습니다.";
                }else{
                    msg = msg + "송신시 알림 설정이 '끄기' 로 저장되었습니다.";
                }

                Snackbar.make(v, msg,
                        Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });



        return view;
    }



}
