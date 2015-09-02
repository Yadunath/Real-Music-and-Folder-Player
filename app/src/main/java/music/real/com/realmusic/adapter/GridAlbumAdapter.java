package music.real.com.realmusic.adapter;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;


import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.InputStream;

import music.real.com.realmusic.R;

/**
 * Created by Yadunath.narayanan on 6/24/2015.
 */
public class GridAlbumAdapter  extends SimpleCursorAdapter
{
    Context context;
    int albumId;
    Cursor cursor;
    int layout;
    private int mWidth;
    private int mHeight;
    String album_name;

    public GridAlbumAdapter(Context context, int layout, Cursor c, String[] from, int[] to) {
        super(context, layout, c, from, to);
        this.context = context;
        this.cursor = c;
        this.layout=layout;
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        mWidth = (metrics.widthPixels)/2;
        mHeight = mWidth ;

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater layoutInflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view=layoutInflater.inflate(R.layout.grid_item_album,null);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ImageView imageView=(ImageView)view.findViewById(R.id.imageView);
        TextView  albumNameText=(TextView)view.findViewById(R.id.gridViewTitleText);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
        params.width=mWidth;
        params.height=mHeight;
        imageView.setLayoutParams(params);

        Picasso.with(context).load(getUri(albumId)).into(imageView);
        albumNameText.setText(album_name);

        super.bindView(view, context, cursor);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        cursor.moveToPosition(position);
        albumId=cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Albums._ID));
        album_name=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM));
        return super.getView(position, convertView, parent);
    }

    public Bitmap getAlbumArt(int album_id)
    {
        Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
        Uri uri = ContentUris.withAppendedId(sArtworkUri, album_id);
        ContentResolver res = context.getContentResolver();
        InputStream in=null;
        try {
             in = res.openInputStream(uri);

            Bitmap artwork = BitmapFactory.decodeStream(in);
            return artwork;
        }
        catch (FileNotFoundException exception)
        {
            Bitmap b = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);
            return b;
        }

    }
    public Uri getUri(int album_id)
    {
        Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
        Uri uri = ContentUris.withAppendedId(sArtworkUri, album_id);
        return  uri;
    }
}
