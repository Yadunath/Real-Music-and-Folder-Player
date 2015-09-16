package music.real.com.realmusic.utilities;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import music.real.com.realmusic.MainActivity;
import music.real.com.realmusic.PlayBackUtility;
import music.real.com.realmusic.services.MusicPlaybackService;

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
    @Override
    public void onCreate() {
        super.onCreate();

        commonUtility=new CommonUtility();
        commonUtility.setRepeatState(0);
        commonUtility.setShuffleState(0);
        this.bindService(new Intent(this, MusicPlaybackService.class), serviceConnection, Context.BIND_AUTO_CREATE);
        sharedPreferences=getSharedPreferences("SONGINFO", MODE_PRIVATE);

        type=sharedPreferences.getInt("cursortype", 0);
        playlistType=sharedPreferences.getString("playlistId", "null");
        trackPosition=sharedPreferences.getInt("trackposition", 0);
        launchedOnce=sharedPreferences.getBoolean("launchTime", false);

        Log.v("Launcherappctn",""+type+"trackpo"+trackPosition+" platype "+playlistType);
        Handler handler=new Handler();
      /*  if (launchedOnce)
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
        }
*/

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
