package music.real.com.realmusic.fragments;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import music.real.com.realmusic.R;
import music.real.com.realmusic.fragments_folder.FilesFoldersFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class TabFragment extends Fragment {


    ViewPager viewPager;
    TabLayout tabLayout;
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
         viewPager = (ViewPager) view.findViewById(R.id.viewpager);
         tabLayout = (TabLayout)view. findViewById(R.id.tabs);


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (viewPager != null) {
            setupViewPager(viewPager);
        }

        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
//        commonUtility.setCurrentFragmentId(1);
        Adapter adapter = new Adapter(getActivity().getSupportFragmentManager());
        adapter.addFragment(new TracksFragment(), "Tracks");
        adapter.addFragment(new AlbumsFragment(), "Albums");
        adapter.addFragment(new FilesFoldersFragment(), "Folders");
        adapter.addFragment(new ArtistFragment(), "Artists");
        adapter.addFragment(new GenreFragment(), "Genres");
        viewPager.setAdapter(adapter);
    }
    static class Adapter extends FragmentPagerAdapter {
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
