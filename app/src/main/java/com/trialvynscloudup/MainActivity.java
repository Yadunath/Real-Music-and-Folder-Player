package com.trialvynscloudup;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import io.fabric.sdk.android.Fabric;
import music.real.com.realmusic.PlayBackUtility;

import com.crashlytics.android.Crashlytics;
import com.trialvynscloudup.activities.AlbumItesmActivity;
import com.trialvynscloudup.activities.PlayBackActivity;
import com.trialvynscloudup.equalizer.EqualizerFragment;
import com.trialvynscloudup.fragments.AlbumsFragment;
import com.trialvynscloudup.fragments.ArtistFragment;
import com.trialvynscloudup.fragments.ControlFragment;
import com.trialvynscloudup.fragments.GenreFragment;
import com.trialvynscloudup.fragments.PlaylistFragment;
import com.trialvynscloudup.fragments.TabFragment;
import com.trialvynscloudup.fragments.TracksFragment;
import com.trialvynscloudup.fragments_folder.FilesFoldersFragment;
import com.trialvynscloudup.fragments_folder_test.FileChooser;
import com.trialvynscloudup.services.MusicPlaybackService;
import com.trialvynscloudup.utilities.CommonUtility;
import com.trialvynscloudup.utilities.LauncherApplication;

public class MainActivity extends AppCompatActivity {
    
    private static String LOG_TAG=MainActivity.class.getName();
        DrawerLayout mDrawerLayout;
    PlayBackUtility sinterface;
    private CommonUtility commonUtility;
    SharedPreferences sharedPreferences;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        this.bindService(new Intent(MainActivity.this, MusicPlaybackService.class), serviceConnection, Context.BIND_AUTO_CREATE);

        commonUtility=new CommonUtility();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }
        Class fragmentclass=TabFragment.class;
        fragmentTransaction(fragmentclass);
        sharedPreferences=getSharedPreferences("SONGINFO", MODE_PRIVATE);

    }
    public void hiddenFeature()
    {
        final String[] colorValue={"#56627c","#59567c","#7c5673","#1E2028","#303540"};
        Random r = new Random();
        int Low = 0;
        int High = 5;
        int R = r.nextInt(High-Low) + Low;

    }
    private void setupViewPager(ViewPager viewPager) {
        commonUtility.setCurrentFragmentId(1);
        Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(new TracksFragment(), "Tracks");
        adapter.addFragment(new AlbumsFragment(), "Albums");
        adapter.addFragment(new ArtistFragment(), "Artists");
        adapter.addFragment(new GenreFragment(), "Genres");
        adapter.addFragment(new FilesFoldersFragment(), "Folders");
        viewPager.setAdapter(adapter);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        Fragment fragment = null;
                        Class fragmentClass = null;
                        switch (menuItem.getItemId()) {
                            case R.id.nav_my_music:
                                
                                
                                fragmentClass = TabFragment.class;
                                fragment=new TabFragment();
                                break;
                            case R.id.nav_playlists:
                                fragmentClass = PlaylistFragment.class;
                                fragment=new PlaylistFragment();
                                break;
                            case R.id.nav_equalizer:
                                fragment=new EqualizerFragment();
                                break;

                        }

//                        fragmentTransaction(fragmentClass);
                        fragmentTransactionTest(fragment);
                        
                        return true;
                    }
                });
    }

        public void fragmentTransaction(Class fragmentClass)
        {
            commonUtility.setCurrentFragmentId(1);
            Fragment fragment=null;
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.parentLayout, fragment).commit();
        }
    
    public void fragmentTransactionTest(Fragment fragment)
    {
        commonUtility.setCurrentFragmentId(1);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.parentLayout, fragment).commit();
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

    public void albumClick(int type,Uri imageUri,String albumId,View view)
    {
        Intent intent=new Intent(MainActivity.this, AlbumItesmActivity.class);
        intent.putExtra("TYPE",type);
        intent.putExtra("imageuri",""+imageUri);
        intent.putExtra("media", albumId);
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(this, view, "gridImage");
        if (Build.VERSION.SDK_INT>=16)
        {
            startActivity(intent, options.toBundle());
        }
        else 
        {
            startActivity(intent);
        }
        
        
    }
    public void albumClick(int type,Uri imageUri,String albumId) {
        Intent intent = new Intent(MainActivity.this, AlbumItesmActivity.class);
        intent.putExtra("TYPE", type);
        intent.putExtra("imageuri", "" + imageUri);
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
        if (commonUtility.getControllerStatus()) {
           displayControlFragment();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseService();
    }

    public void displayControlFragment()
    {
        findViewById(R.id.bottombar).setVisibility(View.VISIBLE);
        ControlFragment fragment = new ControlFragment();
        android.app.FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.bottombar, fragment).commit();
    }


    public void releaseService()
    {
        unbindService(serviceConnection);

    }
    @Override
    public void onBackPressed() {

        if (getCurrentFragmentId()==0)
        {

            Fragment fragment= TabFragment.Adapter.mFragments.get(2);
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
            boolean  launchedOnce=sharedPreferences.getBoolean("launchTime", false);

            if (launchedOnce)
            {
                if (commonUtility.getControllerStatus()==false) {
                    new launchFromLaststate().execute();
                    commonUtility.setControlerStatus(true);
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            sinterface=null;

        }
    };


    public class launchFromLaststate extends AsyncTask<Void,Void,Void>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Void doInBackground(Void... voids) {

            Set<String > stringSet=sharedPreferences.getStringSet("folderQuery",null);
            if (stringSet!=null)
            {
                ArrayList<String> folderList=new ArrayList<>(stringSet);
                commonUtility.setSubFolderList(folderList);
            }
            int  type=sharedPreferences.getInt("cursortype", 0);
            String playlistType=sharedPreferences.getString("playlistId", "null");
            int trackPosition=sharedPreferences.getInt("trackposition", 0);
                try {

                    sinterface.savedDatas(type,trackPosition,playlistType);

                } catch (RemoteException e) {
                    e.printStackTrace();
                }

            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            displayControlFragment();

        }
    }
}
