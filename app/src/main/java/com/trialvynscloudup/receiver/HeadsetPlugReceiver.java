package com.trialvynscloudup.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;
import android.util.Log;

import com.trialvynscloudup.activities.PlayBackActivity;

/**
 * Created by yedunath on 27/12/15.
 */
public class HeadsetPlugReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("HEADSETRECEIVER","rece");
        int state = intent.getIntExtra("state", -1);
        switch (state) {
            case 0:

                try {
                    PlayBackActivity.mInterface.stopSong();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

                break;
            case 1:
                break;
            default:
        }
    }
}
