package kr.co.infonetworks.www.app.activity;


import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotifyRootActivity extends AppCompatActivity {
    private static final String TAG = "NotifyRootFragment";
    private static NotifyRootActivity instance = null;

    private static int param_rnum;
    private static int param_origin_rnum;
    private static String param_req_type;



    public NotifyRootActivity() {
        // Required empty public constructor
    }

    public static NotifyRootActivity newInstance(int rnum, int origin_rnum, String req_type) {

        if (instance == null) {
            instance = new NotifyRootActivity();
        }

        param_rnum = rnum;
        param_origin_rnum = origin_rnum;
        param_req_type = req_type;

        return instance;

    }


//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//
//        // Inflate the layout for this fragment
//        View view = inflater.inflate(R.layout.fragment_notifyroot, container, false);
//
//        //2016-10-21 update
//        FragmentManager mFragmentManager = getActivity().getSupportFragmentManager();
//
//         switch (param_req_type) {
//
//            case "01": //매수
//                OtItemFragment otItemFragment = new OtItemFragment();
//
//                Bundle argsBundle = new Bundle();
//                argsBundle.putString("param1", String.valueOf(param_rnum));
//                argsBundle.putString("param2","fragment_notifyroot");
//                otItemFragment.setArguments(argsBundle);
//
//                mFragmentManager.beginTransaction().
//                        replace(R.id.notifyroot_fragment, otItemFragment,"otItemFragment_notify").
//                        commit();
//                break;
//
//            case "02": //답장
//                MyItemActivity myItemFragment = new MyItemActivity();
//
//                Bundle argsBundle2 = new Bundle();
//                argsBundle2.putString("param1", String.valueOf(param_origin_rnum));
//                argsBundle2.putString("param2","fragment_notifyroot");
//                myItemFragment.setArguments(argsBundle2);
//                mFragmentManager.beginTransaction().
//                        replace(R.id.notifyroot_fragment, myItemFragment,"myItemFragment_notify").
//                        commit();
//
//                break;
//            case "03": //확인
//                OtItemFragment otItemFragment2 = new OtItemFragment();
//
//                Bundle argsBundle3 = new Bundle();
//                argsBundle3.putString("param1",String.valueOf(param_origin_rnum));
//                argsBundle3.putString("param2","fragment_notifyroot");
//                otItemFragment2.setArguments(argsBundle3);
//
//                mFragmentManager.beginTransaction().
//                        replace(R.id.notifyroot_fragment, otItemFragment2,"otItemFragment_notify").
//                        commit();
//
//                break;
//            default:
//                Log.d(TAG,"default");
//                break;
//        }
//
//        return view;
//
//    }

}
