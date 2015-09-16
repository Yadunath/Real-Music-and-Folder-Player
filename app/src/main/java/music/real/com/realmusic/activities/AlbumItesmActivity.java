package music.real.com.realmusic.activities;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import music.real.com.realmusic.R;
import music.real.com.realmusic.adapter.TrackListCursorAdapter;
import music.real.com.realmusic.fragments.ControlFragment;
import music.real.com.realmusic.utilities.CommonUtility;

public class AlbumItesmActivity extends AppCompatActivity {
    String Tag="Albumitemactv";
    private ImageView imageViewTop;
    RecyclerView recyclerList;
    private LinearLayoutManager mLayoutManager;
    Cursor cursor;
    private CommonUtility commonUtility;
    private static final int TYPE_ALBUM=1;
    private static final int TYPE_ARTIST=2;
    private static final int TYPE_GENRE=3;

    private int type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_items);
        commonUtility=new CommonUtility();
        imageViewTop=(ImageView)findViewById(R.id.imageView2);
        Intent intent=getIntent();
        String uri=intent.getStringExtra("imageuri");
        final String mediaid=intent.getStringExtra("media");
        type=intent.getIntExtra("TYPE",1);
        Uri myUri = Uri.parse(uri);
        Picasso.with(getApplicationContext()).load(myUri).into(imageViewTop);

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
                where=MediaStore.Audio.Media._ID + "=?";
                cursor=managedQuery(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, where,wherVal ,order);
                Log.v("TYPE",""+cursor.getCount());
                listCursorAdapter=new TrackListCursorAdapter(AlbumItesmActivity.this,cursor,2,mediaid);
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
        Log.v(Tag,playlistid);
        startActivity(playBackIntent);
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (commonUtility.getControllerStatus())
        {
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
