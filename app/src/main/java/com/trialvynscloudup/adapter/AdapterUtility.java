package com.trialvynscloudup.adapter;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

/**
 * Created by yedunath on 27/12/15.
 */
public class AdapterUtility {

    private final int TYPE_TRACKS=0;
    private final int TYPE_ALBUMS=1;
    private final int TYPE_ARTIST=2;
    private final int TYPE_GENRE=3;
    private final int TYPE_PLAYLIST=4;
    private final int TYPE_FOLDERS=5;
    Cursor mCursor;

    public Cursor getCursor( Context mContext,int type,String playlistId)
    {

        String order= MediaStore.Audio.Media.TITLE;
        switch (type)
        {

            case TYPE_TRACKS:
                mCursor=mContext.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,null,null,null,order+" COLLATE NOCASE ASC; ");
                break;
            case TYPE_ALBUMS:
                String where= MediaStore.Audio.Media.ALBUM_ID + "=?";
                String wherVal[]={playlistId};
                mCursor=mContext.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,null,where,wherVal,order);
                break;
            case TYPE_ARTIST:
                String whereArtist= MediaStore.Audio.Media.ARTIST_ID + "=?";
                String wherValArtist[]={playlistId};
                mCursor=mContext.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,null,whereArtist,wherValArtist,order);
                break;
            case TYPE_PLAYLIST:

                long id=Long.parseLong(playlistId);
                Uri contentUri=MediaStore.Audio.Playlists.Members.getContentUri("external", id);
                mCursor=mContext.getContentResolver().query(contentUri, null, null,null ,order);
                break;
            case TYPE_FOLDERS:

/*				String wherFolder=MediaStore.Audio.Media.DATA + " like ? "+" and "+MediaStore.Audio.Media.DATA + " not like ?";
				String whereValue[]=new String[]{"%"+ playlistId +"%","%"+ commonUtility.getSubFolderName() +"%"};
//	*/
                break;
        }

        return mCursor;
    }
}
