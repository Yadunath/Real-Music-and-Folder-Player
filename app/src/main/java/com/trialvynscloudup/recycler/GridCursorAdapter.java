package com.trialvynscloudup.recycler;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;
import com.squareup.picasso.Picasso;

import com.trialvynscloudup.MainActivity;
import com.trialvynscloudup.R;

import java.util.Locale;

/**
 * Created by skyfishjy on 10/31/14.
 */
public class GridCursorAdapter extends CursorRecyclerViewAdapter<GridCursorAdapter.ViewHolder>
    implements FastScrollRecyclerView.SectionedAdapter{

    public  Context context;
    private Cursor mCursor;

    public GridCursorAdapter(Context context, Cursor cursor){
       super(context,cursor);
        this.context=context;
        this.mCursor=cursor;
    }

    @NonNull
    @Override
    public String getSectionName(int position) {
        return mCursor.getString(mCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)).substring(0,1).toUpperCase(Locale.ENGLISH);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView,mSubTextView;
        public ImageView mImageView;
        public int mWidth;
        public int mHeight;
        View mView;
        public String albumId;
        Uri imageUri;

        public ViewHolder(View view) {
            super(view);
            DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
            mWidth = ((metrics.widthPixels)/2)-10;
            mHeight = mWidth ;
            mTextView=(TextView)view.findViewById(R.id.gridViewTitleText);
            mSubTextView=(TextView)view.findViewById(R.id.gridViewSubText);
            mImageView=(ImageView)view.findViewById(R.id.imageView);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mImageView.getLayoutParams();
            params.width=mWidth;
            params.height=mWidth;
            mImageView.setLayoutParams(params);
            this.mView=view;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.grid_item_album, parent, false);
        ViewHolder vh = new ViewHolder(itemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        MyListItem myListItem = MyListItem.fromCursor(cursor);
        viewHolder.mTextView.setText(myListItem.getName());
        if (myListItem.getNoofsongs()==1)
        {
            viewHolder.mSubTextView.setText(""+myListItem.getNoofsongs()+" Track");

        }
        else
        {
            viewHolder.mSubTextView.setText(""+myListItem.getNoofsongs()+" Tracks");

        }
        viewHolder.albumId=myListItem.getAlbumId();
        Picasso.with(context).load(myListItem.getUri()).into(viewHolder.mImageView);
        viewHolder.imageUri=myListItem.getUri();

    }



    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        super.onBindViewHolder(viewHolder, position);
        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = view.getContext();
//                Fragment fragment=new TestFragment();
                switchFragment(viewHolder.imageUri,viewHolder.albumId,viewHolder.mImageView);
            }
        });
    }
    public void switchFragment(Uri  imageUri,String position,View view)
    {
        MainActivity mainActivity=(MainActivity)context;
        Log.v("Grdcursor",""+imageUri);
        mainActivity.albumClick(0,imageUri,position,view);

    }

}