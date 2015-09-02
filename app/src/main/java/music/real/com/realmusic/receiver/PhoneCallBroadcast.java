package music.real.com.realmusic.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;
import android.telephony.TelephonyManager;
import android.util.Log;

import music.real.com.realmusic.activities.PlayBackActivity;

/**
 * Created by yedunath on 2/9/15.
 */
public class PhoneCallBroadcast extends BroadcastReceiver {
    Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        try
        {

            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

            if(state.equals(TelephonyManager.EXTRA_STATE_RINGING))
            {
                stopSong();
            }

           else if(state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK))
            {
                stopSong();
            }

           else if (state.equals(TelephonyManager.EXTRA_STATE_IDLE))
            {
                startSong();

            }
        }
        catch(Exception e)
        {
            //your custom message
        }
    }
    public void stopSong()
    {
        try {
            PlayBackActivity.mInterface.stopSong();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    public void startSong()
    {
        try {
            PlayBackActivity.mInterface.pauseSong();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
