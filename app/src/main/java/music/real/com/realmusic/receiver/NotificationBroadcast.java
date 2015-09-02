package music.real.com.realmusic.receiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;
import android.util.Log;
import android.view.KeyEvent;

import music.real.com.realmusic.MainActivity;
import music.real.com.realmusic.activities.PlayBackActivity;
import music.real.com.realmusic.services.MusicPlaybackService;


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
			PlayBackActivity.mInterface.pauseSong();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	public void previous()
	{
		try {
			PlayBackActivity.mInterface.previousSong();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	public void next()
	{
		try {
			PlayBackActivity.mInterface.nextSong();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

}
