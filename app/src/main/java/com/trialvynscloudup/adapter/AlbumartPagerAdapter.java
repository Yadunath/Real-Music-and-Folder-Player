package com.trialvynscloudup.adapter;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.squareup.picasso.Picasso;
import com.trialvynscloudup.R;

/**
 * Created by yedunath on 27/12/15.
 */
public class AlbumartPagerAdapter extends PagerAdapter {
    Context mcontext;
    Cursor mCursor;

    LayoutInflater inflater;
    public AlbumartPagerAdapter(Context mcontext,Cursor cursor) {
        this.mcontext = mcontext;
        this.mCursor=cursor;
    }


    @Override
    public int getCount() {
        return mCursor.getCount();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
		return true;
    }
    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        // Declare Variables
        mCursor.moveToPosition(position);
        int albumId = mCursor.getInt(mCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
        Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
        Uri uriArtWork = ContentUris.withAppendedId(sArtworkUri, albumId);

        inflater=(LayoutInflater)mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
       View itemView= inflater.inflate(R.layout.viewpager_item, container, false);
        ImageView imageView=(ImageView)itemView.findViewById(R.id.imageView3);
        Picasso.with(mcontext).load(uriArtWork).into(imageView);
        Log.e("TAGuri", "" + uriArtWork);
        ((ViewPager) container).addView(itemView);
        return itemView;
    }
    @Override
	public void destroyItem(ViewGroup container, int position, Object object) {
    		// Remove viewpager_item.xml from ViewPager
		((ViewPager) container).removeView((RelativeLayout) object);
}
	}