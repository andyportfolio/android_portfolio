package kr.co.infonetworks.www.app;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import kr.co.infonetworks.www.app.activity.OtReqFragment;
import kr.co.infonetworks.www.app.activity.MyReqFragment;
import kr.co.infonetworks.www.app.activity.NotifyReqFragment;
import kr.co.infonetworks.www.app.activity.RequestFragment;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class TabFragment extends Fragment {
    private static final String TAG = "TabFragment";
    private static TabFragment instance = null;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;


    public TabFragment() {
        // Required empty public constructor
    }


    public static TabFragment newInstance() {
        if (instance == null) {
            instance = new TabFragment();
        }
        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        /**
         *Inflate tab_layout and setup Views.
         */
        View view = inflater.inflate(R.layout.tab_layout,container, false);
        //View view = inflater.inflate(R.layout.tab_layout, null);


        viewPager = (ViewPager) view.findViewById(R.id.viewpager);

        //adapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager());

        //viepager에서 framgment를 추가할때는 getChildFragmentManager 를 사용한다
        //현재 프레임에서 하위 프로그램을 생성하기 위해서 child사용
        viewPagerAdapter = new ViewPagerAdapter(this.getChildFragmentManager());

        viewPagerAdapter.addFrament(MyReqFragment.newInstance(), getResources().getString(R.string.fragment_title1)); //당업소매수
        viewPagerAdapter.addFrament(OtReqFragment.newInstance(false,0,0), getResources().getString(R.string.fragment_title2)); //타업소매수
        viewPagerAdapter.addFrament(NotifyReqFragment.newInstance(false), getResources().getString(R.string.fragment_title3)); //당일알람

        /**
         *Set an Apater for the View Pager
         */
        viewPager.setAdapter(viewPagerAdapter);

        /**
         *Link tabLayout and viewPager , 자동으로 viewpager의 타이틀이 tab의 타이틀이 된다
         */
        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //2016-10-19 update
                FragmentManager fm = getActivity().getSupportFragmentManager(); //TabFragment.this.getFragmentManager();

                RequestFragment requestFragment = (RequestFragment)fm.findFragmentByTag("requestFragment");
                TabFragment tabFragment = (TabFragment) fm.findFragmentByTag("tabFragment");


                fm.beginTransaction().show(requestFragment).hide(tabFragment).commit();

            }
        });

        return view;


    }


    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<Fragment>();
        private final List<String> mFragmentTitleList = new ArrayList<String>();

        private final SparseArray< View > views = new SparseArray< View >();

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        /**
         * Return fragment with respect to Position .
         */

        @Override
        public Fragment getItem(int position) {
            Log.d(TAG,"getItem :" + position );
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            //Log.d(TAG,"getCount :" + mFragmentList.size() );
            return mFragmentList.size();

        }

        public void addFrament(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Log.d(TAG,"instantiateItem :" + position );
            return super.instantiateItem(container, position);

        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            //삭제 시키지 않는다
            //http://dreamofcoder.blogspot.kr/2013/01/viewpager.html
            Log.d(TAG,"no-destroyItem :" + position );
            //super.destroyItem(container, position, object);
        }
    }
}
