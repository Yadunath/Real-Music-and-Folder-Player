package com.trialvynscloudup.utilities;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import music.real.com.realmusic.PlayBackUtility;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.StandardExceptionParser;
import com.google.android.gms.analytics.Tracker;
import com.trialvynscloudup.services.MusicPlaybackService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by yedunath on 19/8/15.
 */
public class LauncherApplication extends Application {
    CommonUtility commonUtility;
    PlayBackUtility sinterface;
    SharedPreferences sharedPreferences;
    private int type,trackPosition;
    private String playlistType;
    private boolean launchedOnce;
    int repeatState,shuffleState;
    UiUpdater uiUpdater;
    public static final String TAG = LauncherApplication.class
            .getSimpleName();

    private static LauncherApplication mInstance;
    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());

        commonUtility=new CommonUtility();
        sharedPreferences=getSharedPreferences("SONGINFO", MODE_PRIVATE);

        type=sharedPreferences.getInt("cursortype", 0);
        playlistType=sharedPreferences.getString("playlistId", "null");
        trackPosition=sharedPreferences.getInt("trackposition", 0);
        launchedOnce=sharedPreferences.getBoolean("launchTime", false);
        repeatState=sharedPreferences.getInt("repeatstate", 0);
        shuffleState=sharedPreferences.getInt("shufflestate",0);
        setRepeatandShuffleState();
        Handler handler=new Handler();
        mInstance = this;

        if (commonUtility.developementStatus==false)
        {
            AnalyticsTrackers.initialize(this);
            AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);

        }


        uiUpdater();
/*        if (launchedOnce)
        {
            commonUtility.setControlerStatus(true);

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        sinterface.savedDatas(type,trackPosition,playlistType);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            },800);
        }*/

    }
    public void uiUpdater()
    {
        uiUpdater=new UiUpdater();
        uiUpdater.mInfoListener=new UiUpdater.updateInfo() {
            @Override
            public void updateSongInfo(String title, String album, String artist, Uri artwork,String path) {

            }
        };
        uiUpdater.mSeekListener=new UiUpdater.updateSeekbar() {
            @Override
            public void updateSeekBar(int progress) {

            }
        };
    }
    public static synchronized LauncherApplication getInstance() {
        return mInstance;
    }

    public synchronized Tracker getGoogleAnalyticsTracker() {
        AnalyticsTrackers analyticsTrackers = AnalyticsTrackers.getInstance();
        return analyticsTrackers.get(AnalyticsTrackers.Target.APP);
    }

    /***
     * Tracking screen view
     *
     * @param screenName screen name to be displayed on GA dashboard
     */
    public void trackScreenView(String screenName) {
        Tracker t = getGoogleAnalyticsTracker();

        // Set screen name.
        t.setScreenName(screenName);

        // Send a screen view.
        t.send(new HitBuilders.ScreenViewBuilder().build());
        GoogleAnalytics.getInstance(this).dispatchLocalHits();
    }

    /***
     * Tracking exception
     *
     * @param e exception to be tracked
     */
    public void trackException(Exception e) {
        if (e != null) {
            Tracker t = getGoogleAnalyticsTracker();

            t.send(new HitBuilders.ExceptionBuilder()
                            .setDescription(
                                    new StandardExceptionParser(this, null)
                                            .getDescription(Thread.currentThread().getName(), e))
                            .setFatal(false)
                            .build()
            );
        }
    }

    /***
     * Tracking event
     *
     * @param category event category
     * @param action   action of the event
     * @param label    label
     */
    public void trackEvent(String category, String action, String label) {
        Tracker t = getGoogleAnalyticsTracker();

        // Build and send an Event.
        t.send(new HitBuilders.EventBuilder().setCategory(category).setAction(action).setLabel(label).build());
    }



    public void setRepeatandShuffleState()
    {
        commonUtility.setRepeatState(repeatState);
        commonUtility.setShuffleState(shuffleState);
    }


    private ServiceConnection serviceConnection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            sinterface= PlayBackUtility.Stub.asInterface((IBinder)iBinder);

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            sinterface=null;

        }
    };

}
