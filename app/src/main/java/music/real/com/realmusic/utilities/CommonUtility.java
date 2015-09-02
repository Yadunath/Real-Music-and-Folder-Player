package music.real.com.realmusic.utilities;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import music.real.com.realmusic.MainActivity;
import music.real.com.realmusic.PlayBackUtility;
import music.real.com.realmusic.services.MusicPlaybackService;

/**
 * Created by yedunath on 1/8/15.
 */
public class CommonUtility   {
    private static String songTitle;
    private static String albumName;
    private static Uri albumArtUri;
    private static boolean playerStatus=false;
    private static int shuffleState=0;
    private static int repeatState=0;
    private static String totalDuration,currentDuration;
    private static String subFolderName;
    private static int currentFragmentId;
    public CommonUtility() {
    }

    public static String getSongTitle()
    {
        return songTitle;
    }
    public static String getAlbum()
    {
        return albumName;

    }
    public static Uri geturi()
    {
        return albumArtUri;
    }
    public static String getCurrentDuration()
    {
        return currentDuration;
    }
    public static String getTotalDuration()
    {
        return totalDuration;
    }
    public static boolean getStatus()
    {
        return playerStatus;
    }
    public static int getShuffleState()
    {
        return shuffleState;
    }

    public static int getRepeatState()
    {
        return repeatState;
    }

    public static  String getSubFolderName(){
        return subFolderName;
    }
    public static int getCurrentFragmentId()
    {
        return  currentFragmentId;
    }

    public void setSongTitle(String songTitle)
    {
        this.songTitle=songTitle;
    }
    public void setAlbumName(String name)
    {
        this.albumName=name;
    }
    public void setAlbumArtUri(Uri uri)
    {
        this.albumArtUri=uri;
    }
    public void setCurrentDuration(String currentDuration)
    {
        this.currentDuration=currentDuration;
    }
    public void setTotalDuration(String duration)
    {
        this.totalDuration=duration;
    }
    public void setPlayerStatus(boolean status)
    {
        this.playerStatus=status;
    }
    public void setShuffleState(int state)
    {
        this.shuffleState=state;
    }
    public void setRepeatState(int repeatState)
    {
        this.repeatState=repeatState;
    }
    public void setSubFolderName(String name)
    {
        this.subFolderName=name;
    }
    public void setCurrentFragmentId(int id)
    {
        this.currentFragmentId=id;
    }


}
