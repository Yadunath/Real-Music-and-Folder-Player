package com.trialvynscloudup.utilities;

import android.net.Uri;

/**
 * Created by yedunath on 12/8/15.
 */
public class UiUpdater {

    public static updateSeekbar mSeekListener;
    public static updateInfo mInfoListener;
    public static timerInterface timerInterface;
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
        public void updateSongInfo(String title,String album,String artist,Uri artwork,String path);
    }
    public void setSongInfoUpdate(updateInfo listener)
    {
        this.mInfoListener=listener;
    }

    public interface timerInterface
    {
        public abstract void updateTimerText(String  millisUntilFinished);
    }
    public void setupdateTimerText(timerInterface deleteInterface)
    {
        this.timerInterface=deleteInterface;
    }
}
