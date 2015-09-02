package music.real.com.realmusic.utilities;

import android.net.Uri;

/**
 * Created by yedunath on 12/8/15.
 */
public class UiUpdater {

    public static updateSeekbar mSeekListener;
    public static updateInfo mInfoListener;

    public interface updateSeekbar
    {
        public void updateSeekBar(int progress);
    }
    public void setOnSeekListener(updateSeekbar seekListener)
    {
        this.mSeekListener=seekListener;
    }

    public interface updateInfo
    {
        public void updateSongInfo(String title,String album,String artist,Uri artwork);
    }
    public void setSongInfoUpdate(updateInfo listener)
    {
        this.mInfoListener=listener;
    }

}
