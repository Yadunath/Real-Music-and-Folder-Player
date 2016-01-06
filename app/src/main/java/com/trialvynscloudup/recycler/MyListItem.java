package com.trialvynscloudup.recycler;

import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

public class MyListItem {
  private String name;
    private int noofsongs;
    private Uri uriArtWork;
    private String artistName;
    private String albumId;
    private String artistId;
    private String genreId;
    private static final int TYPE_ARTIST=1;
    private static final int TYPE_GENRE=2;
    private static final int TYPE_PLAYLIST=3;
    private static final int TYPE_SUGGESTED=4;
  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return name;

  }
    public void setNoofSongs(int num)
    {
        this.noofsongs=num;

    }
    public int getNoofsongs()
    {
        return noofsongs;
    }
    public void setUri( int albumId)
    {
        Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");

        this.uriArtWork = ContentUris.withAppendedId(sArtworkUri, albumId);
    }
    public Uri getUri()
    {
        return uriArtWork;
    }
    public void setAlbumId(String albumId)
    {
         this.albumId=albumId;
    }
    public String getAlbumId()
    {
        return albumId;
    }
    public void setArtistName(String artistName)
    {
        this.artistName=artistName;
    }
    public String getArtistName()
    {
        return artistName;
    }

  public static MyListItem fromCursor(Cursor cursor) {
    //TODO return your MyListItem from cursor.
        MyListItem myListItem=new MyListItem();
        myListItem.setName(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM)));
        myListItem.setUri(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Albums._ID)));
        myListItem.setAlbumId(cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media._ID)));
        myListItem.setNoofSongs(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Albums.NUMBER_OF_SONGS)));
        return myListItem;
  }
    public static MyListItem forList(Cursor cursor)
    {
        MyListItem myListItem=new MyListItem();
        myListItem.setName(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
        myListItem.setArtistName(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
        return myListItem;
    }
    public static MyListItem forSuggested(Cursor cursor)
    {
        MyListItem myListItem=new MyListItem();
        myListItem.setName(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
        myListItem.setUri(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)));
        myListItem.setArtistName(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)));

        return myListItem;
    }
    public static MyListItem artistList(Cursor cursor,int type)
    {
        MyListItem myListItem=new MyListItem();

        switch (type)
        {
            case TYPE_ARTIST:
                myListItem.setName(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_TRACKS)));
                myListItem.setArtistName(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Artists.ARTIST)));
                myListItem.setAlbumId(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID)));
                break;
            case TYPE_GENRE:

                myListItem.setName("1");
                myListItem.setArtistName(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Genres.NAME)));
                myListItem.setAlbumId(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Genres._ID)));
                break;
            case TYPE_PLAYLIST:
                myListItem.setName("1");
                myListItem.setArtistName(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Playlists.NAME)));
                myListItem.setAlbumId(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Playlists._ID)));
                break;
            case TYPE_SUGGESTED:

                break;

        }
        return myListItem;

    }
  }