package music.real.com.realmusic.recycler;

import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

public class MyListItem {
  private String name;
    private Uri uriArtWork;
    private String artistName;
    private String albumId;
    private String artistId;
    private String genreId;
    private static final int TYPE_ARTIST=0;
    private static final int TYPE_GENRE=1;
  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return name;

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
        return myListItem;
  }
    public static MyListItem forList(Cursor cursor)
    {
        MyListItem myListItem=new MyListItem();
        myListItem.setName(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)));
        myListItem.setArtistName(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
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
        }
        return myListItem;

    }
  }