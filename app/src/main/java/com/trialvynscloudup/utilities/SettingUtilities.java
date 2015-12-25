package com.trialvynscloudup.utilities;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;

import java.io.File;

/**
 * Created by yedunath on 17/9/15.
 */
public class SettingUtilities
{
    Cursor cursor;
    int currentPosition;
    Context context;
    public void alertDialog(Context context,final int type,String title,String message,Cursor cursor,int currentPosition)
    {
        this.context=context;
        this.cursor=cursor;
        this.currentPosition=currentPosition;
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set title

        alertDialogBuilder.setTitle("Your Title");

        // set dialog message
        alertDialogBuilder
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, close
                        // current activity
                        switch (type)
                        {
                            case 1:
                                break;
                            case 2:
                                setringTone();

                                break;
                            case 3:
                                deleteSong();

                                break;
                        }

                    }
                })
                .setNegativeButton("No",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();


    }

    public void deleteSong()
    {

        cursor.moveToPosition(currentPosition);
        String data=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
        String name=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
        context.getContentResolver().delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, MediaStore.Audio.Media.DATA + "='" + data + "'", null);
        File f=new File(name);
        f.delete();
//        notifyItemRemoved(currentPosition);
//        notifyItemRangeChanged(currentPosition, cursor.getCount());


    }
    public void setringTone(){
        cursor.moveToPosition(currentPosition);
        int setid=cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
        Uri ringUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, setid);
        try {
            ContentValues values = new ContentValues(2);
            values.put(MediaStore.Audio.Media.IS_RINGTONE, "1");
            values.put(MediaStore.Audio.Media.IS_ALARM, "1");
            context.getContentResolver().update(ringUri, values, null, null);
        } catch (UnsupportedOperationException ex) {
            // most likely the card just got unmounted
            Log.e("ts", "couldn't set ringtone flag for id " + setid);
            return;
        }
        String[] cols = new String[] {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.TITLE
        };
        String where = MediaStore.Audio.Media._ID + "=" + setid;
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                cols, where, null, MediaStore.Audio.Media.DISPLAY_NAME);
//				if(0 ==c.getInt(c.getColumnIndex(Media.IS_RINGTONE))){
        Settings.System.putString(context.getContentResolver(), Settings.System.RINGTONE, ringUri.toString());
        //RingtoneManager.setActualDefaultRingtoneUri(DingActivity.this, RingtoneManager.TYPE_RINGTONE,
        //	getUri());
    }

}
