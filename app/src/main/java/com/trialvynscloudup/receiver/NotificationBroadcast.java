package com.trialvynscloudup.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;
import android.view.KeyEvent;

import com.trialvynscloudup.activities.PlayBackActivity;
import com.trialvynscloudup.services.MusicPlaybackService;


public class NotificationBroadcast extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(Intent.ACTION_MEDIA_BUTTON)) {
            KeyEvent keyEvent = (KeyEvent) intent.getExtras().get(Intent.EXTRA_KEY_EVENT);
            if (keyEvent.getAction() != KeyEvent.ACTION_DOWN)
                return;

            switch (keyEvent.getKeyCode()) {
                case KeyEvent.KEYCODE_HEADSETHOOK:
                case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:

					pauseSong();
					break;
                case KeyEvent.KEYCODE_MEDIA_PLAY:
                	break;
                case KeyEvent.KEYCODE_MEDIA_PAUSE:
                	break;
                case KeyEvent.KEYCODE_MEDIA_STOP:
                	break;
                case KeyEvent.KEYCODE_MEDIA_NEXT:
					next();

					break;
                case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
					previous();
                	break;
            }


		}

		else{
			if (intent.getAction().equals(MusicPlaybackService.NOTIFY_PLAY)) {
				pauseSong();
			} else if (intent.getAction().equals(MusicPlaybackService.NOTIFY_PAUSE)) {

				pauseSong();
			} else if (intent.getAction().equals(MusicPlaybackService.NOTIFY_NEXT)) {
				next();
			} else if (intent.getAction().equals(MusicPlaybackService.NOTIFY_DELETE)) {
				cancelNotification();

			}else if (intent.getAction().equals(MusicPlaybackService.NOTIFY_PREVIOUS)) {
				previous();
			}
		}
	}
	
	public String ComponentName() {
		return this.getClass().getName(); 
	}

	public void pauseSong()
	{
		try {
			if (PlayBackActivity.mInterface!=null)
			{
				PlayBackActivity.mInterface.pauseSong(1);
			}

		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	public void previous()
	{
		try {
			if (PlayBackActivity.mInterface!=null) {
				PlayBackActivity.mInterface.previousSong();
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	public void next()
	{
		try {
			if (PlayBackActivity.mInterface!=null) {
				PlayBackActivity.mInterface.nextSong();
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}
	public void cancelNotification()
	{

		try {
			if (PlayBackActivity.mInterface!=null) {
				PlayBackActivity.mInterface.cancelNotification();
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

}
