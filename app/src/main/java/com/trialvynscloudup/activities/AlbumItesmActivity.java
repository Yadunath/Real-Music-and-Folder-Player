package com.trialvynscloudup.activities;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;


import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import com.trialvynscloudup.R;
import com.trialvynscloudup.adapter.TrackListCursorAdapter;
import com.trialvynscloudup.fragments.ControlFragment;
import com.trialvynscloudup.utilities.CommonUtility;
import com.trialvynscloudup.utilities.LauncherApplication;

import java.util.HashMap;

public class AlbumItesmActivity extends AppCompatActivity {
    String Tag="Albumitemactv";
    private ImageView imageViewTop;
    RecyclerView recyclerList;
    private LinearLayoutManager mLayoutManager;
    Cursor cursor;
    private CommonUtility commonUtility;
    private static final int TYPE_ALBUM=0;
    private static final int TYPE_ARTIST=1;
    private static final int TYPE_GENRE=2;
    private static final int TYPE_PLAYLIST=3;
    private int type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_items);

        if (CommonUtility.developementStatus)
        {

        }
        else
        {

        }
        commonUtility=new CommonUtility();
        imageViewTop=(ImageView)findViewById(R.id.imageView2);
        Intent intent=getIntent();
        String uri=intent.getStringExtra("imageuri");
        final String mediaid=intent.getStringExtra("media");
        type=intent.getIntExtra("TYPE",1);
        Uri myUri = Uri.parse(uri);
//        Picasso.with(getApplicationContext()).load(myUri).into(imageViewTop);
        setAlbumArt(myUri);
      /*        Recycler view adapter --adding songs from albums  to listview*/


        recyclerList=(RecyclerView)findViewById(R.id.recyclerViewList);
        recyclerList.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerList.setLayoutManager(mLayoutManager);

        String[] column={MediaStore.Audio.Media._ID,MediaStore.Audio.Media.DATA,MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DISPLAY_NAME	,MediaStore.Audio.Media. MIME_TYPE, MediaStore.Audio.Media.ALBUM, MediaStore.Audio.Media.ALBUM_ID, MediaStore.Audio.Media.ARTIST};
        String where=null;
        String wherVal[]={mediaid};
        String order= MediaStore.Audio.Media.TITLE;
        TrackListCursorAdapter listCursorAdapter=null;
        switch (type)
        {
            case TYPE_ALBUM:
                 where= MediaStore.Audio.Media.ALBUM_ID + "=?";
                cursor=managedQuery(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, where,wherVal ,order);
                listCursorAdapter=new TrackListCursorAdapter(AlbumItesmActivity.this,cursor,1,mediaid);
                break;

            case TYPE_ARTIST:
                where= MediaStore.Audio.Media.ARTIST_ID + "=?";
                cursor=managedQuery(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, where,wherVal ,order);

                listCursorAdapter=new TrackListCursorAdapter(AlbumItesmActivity.this,cursor,2,mediaid);
                break;
            case TYPE_GENRE:
                Uri uri2=MediaStore.Audio.Genres.Members.getContentUri("external", Long.parseLong(mediaid));
                where=MediaStore.Audio.Media._ID + "=?";
                cursor=managedQuery(uri2, null, null,null ,order);
                listCursorAdapter=new TrackListCursorAdapter(AlbumItesmActivity.this,cursor,3,mediaid);
                break;

            case TYPE_PLAYLIST:
                where=MediaStore.Audio.Media._ID + "=?";
                long id=Long.parseLong(mediaid);
                Uri contentUri=MediaStore.Audio.Playlists.Members.getContentUri("external", id);
                cursor=managedQuery(contentUri, null, null,null ,order);
                listCursorAdapter=new TrackListCursorAdapter(AlbumItesmActivity.this,cursor,type+1,mediaid);
                break;
        }

        recyclerList.setAdapter(listCursorAdapter);
    }

    public void playBack(int position,int type,String playlistid)
    {

        Intent playBackIntent=new Intent(AlbumItesmActivity.this, PlayBackActivity.class);
        playBackIntent.putExtra("position",position);
        playBackIntent.putExtra("type",type);
        playBackIntent.putExtra("playlistid", playlistid);
        startActivity(playBackIntent);
    }

           /*                      Update AlbumArt                     */

    public void setAlbumArt(final Uri albumArt)
    {
        Picasso.with(getApplicationContext()).load(albumArt).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                if ( Build.VERSION.SDK_INT >= 16) {
                    imageViewTop.setBackground(new BitmapDrawable(getApplicationContext().getResources(), bitmap));


                } else {
                    Picasso.with(getApplicationContext()).load(albumArt).into(imageViewTop);

                }
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (commonUtility.getControllerStatus())
        {
            findViewById(R.id.bottombar).setVisibility(View.VISIBLE);
            ControlFragment fragment=new ControlFragment();
            android.app.FragmentManager fragmentManager=getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.bottombar,fragment).commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_album_itesm, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
