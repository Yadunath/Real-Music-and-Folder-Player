package com.trialvynscloudup.fragments;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import com.trialvynscloudup.MainActivity;
import com.trialvynscloudup.R;
import com.trialvynscloudup.fragments_folder.FilesFoldersFragment;
import com.trialvynscloudup.fragments_folder_test.FileChooser;
import com.trialvynscloudup.utilities.CommonUtility;
import com.trialvynscloudup.utilities.LauncherApplication;

/**
 * A simple {@link Fragment} subclass.
 */
public class TabFragment extends Fragment {


    ViewPager viewPager;
    TabLayout tabLayout;
    CommonUtility commonUtility=new CommonUtility();
    public TabFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tab, container, false);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toolbar toolbar = (Toolbar)view. findViewById(R.id.toolbar);
        ((MainActivity) getActivity()).setSupportActionBar(toolbar);
        final ActionBar ab =  ((MainActivity) getActivity()).getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);


        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
         tabLayout = (TabLayout)view. findViewById(R.id.tabs);


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (CommonUtility.developementStatus)
        {

        }
        else
        {
            LauncherApplication.getInstance().trackScreenView("Tabs Fragment");
        }
        if (viewPager != null) {
            setupViewPager(viewPager);
        }

        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private void setupViewPager(ViewPager viewPager) {
        commonUtility.setCurrentFragmentId(1);
        Adapter adapter = new Adapter(getChildFragmentManager());
        adapter.addFragment(new TracksFragment(), "Tracks");
        adapter.addFragment(new AlbumsFragment(), "Albums");
        adapter.addFragment(new FileChooser(),"folder");
        adapter.addFragment(new ArtistFragment(), "Artists");
        adapter.addFragment(new SuggestedFragment(),"SUGGESTED");
        adapter.addFragment(new GenreFragment(), "Genres");
        viewPager.setAdapter(adapter);

    }

   public static class Adapter extends FragmentPagerAdapter {
        public static final List<android.support.v4.app.Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);

        }

        public void addFragment(android.support.v4.app.Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentTitles.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }

    }

}
