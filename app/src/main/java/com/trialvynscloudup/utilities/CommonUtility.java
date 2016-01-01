package com.trialvynscloudup.utilities;

import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by yedunath on 1/8/15.
 */
public class CommonUtility   {
    private static String songTitle;
    private static String albumName;
    private static Uri albumArtUri;
    private static boolean playerStatus=false;
    private static boolean controllerStatus=false;
    private static int shuffleState=0;
    private static int repeatState=0;
    private static String totalDuration,currentDuration;
    private static String subFolderName;
    private static ArrayList<String > subFolderList;
    private static int currentFragmentId=1;
    private static boolean timerStatus=false;
    public static boolean developementStatus=true;

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
    public static boolean getPlayerStatus()
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
    public static ArrayList<String > getSubFolderList()
    {
        return subFolderList;
    }
    public static int getCurrentFragmentId()
    {
        return  currentFragmentId;
    }

    public static boolean getControllerStatus()
    {
        return controllerStatus;
    }
    public static boolean getTimerStatus()
    {
        return timerStatus;
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
    public void setSubFolderList(ArrayList<String> list)
    {
        this.subFolderList=list;
    }
    public void setCurrentFragmentId(int id)
    {

        this.currentFragmentId=id;
    }
    public void setControlerStatus(boolean status)
    {
        this.controllerStatus=status;
    }
    public void setTimerStatus(boolean status)
    {
        this.timerStatus=status;
    }

}
