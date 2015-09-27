package music.real.com.realmusic;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import music.real.com.realmusic.activities.AlbumItesmActivity;
import music.real.com.realmusic.activities.PlayBackActivity;
import music.real.com.realmusic.fragments.AlbumsFragment;
import music.real.com.realmusic.fragments.ArtistFragment;
import music.real.com.realmusic.fragments.ControlFragment;
import music.real.com.realmusic.fragments.GenreFragment;
import music.real.com.realmusic.fragments.PlaylistFragment;
import music.real.com.realmusic.fragments.TabFragment;
import music.real.com.realmusic.fragments.TracksFragment;
import music.real.com.realmusic.fragments_folder.FilesFoldersFragment;
import music.real.com.realmusic.utilities.CommonUtility;

public class MainActivity extends AppCompatActivity {
        DrawerLayout mDrawerLayout;
    PlayBackUtility sinterface;
    private CommonUtility commonUtility;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        commonUtility=new CommonUtility();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        if (viewPager != null) {
            setupViewPager(viewPager);
        }


        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);


    }


    private void setupViewPager(ViewPager viewPager) {
        commonUtility.setCurrentFragmentId(1);
        Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(new TracksFragment(), "Tracks");
        adapter.addFragment(new AlbumsFragment(), "Albums");
        adapter.addFragment(new FilesFoldersFragment(), "Folders");
        adapter.addFragment(new ArtistFragment(), "Artists");
        adapter.addFragment(new GenreFragment(), "Genres");
        viewPager.setAdapter(adapter);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        Fragment fragment=null;
                        Class fragmentClass=null;
                        switch (menuItem.getItemId())
                        {
                            case R.id.nav_my_music:
                                fragmentClass=TabFragment.class;
                                break;
                            case R.id.nav_playlists:
                                fragmentClass= PlaylistFragment.class;
                        }
                        try {
                            fragment = (Fragment) fragmentClass.newInstance();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        FragmentManager fragmentManager=getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.parentLayout,fragment).commit();
                        return true;
                    }
                });
    }


    static class Adapter extends FragmentPagerAdapter {
        public static final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
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

    public void albumClick(int type,Uri imageUri,String albumId)
    {
        Intent intent=new Intent(MainActivity.this, AlbumItesmActivity.class);
        intent.putExtra("TYPE",type);
        intent.putExtra("imageuri",""+imageUri);
        intent.putExtra("media", albumId);
        startActivity(intent);
    }


    public void playBack(Cursor cursor,int position,int type,String playlistid)
    {


        Intent playBackIntent=new Intent(MainActivity.this, PlayBackActivity.class);

        playBackIntent.putExtra("position",position);
        playBackIntent.putExtra("type",type);
        playBackIntent.putExtra("playlistid", playlistid);

        startActivity(playBackIntent);

    }

    public void establishConnection(Cursor cursor,int position)
    {
        cursor.moveToPosition(position);
        String dataPath=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));

    }


    @Override
    protected void onResume() {
        super.onResume();

        if (commonUtility.getControllerStatus())
        {
            Handler handler=new Handler();
          /*  handler.postDelayed(new Runnable() {
                @Override
                public void run() {*/
                    ControlFragment fragment = new ControlFragment();
                    android.app.FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.bottombar, fragment).commit();
/*
                }
            }, 1000);*/


        }

    }
    @Override
    public void onBackPressed() {

        if (getCurrentFragmentId()==0)
        {

            Fragment fragment= Adapter.mFragments.get(2);
            if (((FilesFoldersFragment) fragment).getCurrentDir().equals("/"))
            {
                super.onBackPressed();
            }
            else
            {
                ((FilesFoldersFragment)fragment).getParentDir();

            }


            }
        else
        {
            super.onBackPressed();
        }
    }

    public int getCurrentFragmentId()
    {
        return commonUtility.getCurrentFragmentId();
    }

    private ServiceConnection serviceConnection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            sinterface=PlayBackUtility.Stub.asInterface((IBinder)iBinder);

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            sinterface=null;

        }
    };

}
