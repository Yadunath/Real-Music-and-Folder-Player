package com.trialvynscloudup.recycler;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.trialvynscloudup.MainActivity;
import com.trialvynscloudup.R;
import com.trialvynscloudup.activities.AlbumItesmActivity;

/**
 * Created by yedunath on 2/1/16.
 */
public class SuggestedCursorAdapter extends CursorRecyclerViewAdapter<SuggestedCursorAdapter.ViewHolder> {

    public Context context;
    public  Cursor cursor;

    public SuggestedCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor);
        this.context = context;
        this.cursor=cursor;
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView, mSubTextView;
        public ImageView mImageView;
        public int mWidth;
        public int mHeight;
        View mView;
        public String albumId;
        Uri imageUri;

        public ViewHolder(View view) {
            super(view);
            DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
            mWidth = ((metrics.widthPixels) / 2) - 10;
            mHeight = mWidth;
            mTextView = (TextView) view.findViewById(R.id.gridViewTitleText);
            mSubTextView = (TextView) view.findViewById(R.id.gridViewSubText);
            mImageView = (ImageView) view.findViewById(R.id.imageView);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mImageView.getLayoutParams();
            params.width = mWidth;
            params.height = mWidth;
            mImageView.setLayoutParams(params);
            this.mView = view;
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


        MyListItem myListItem = MyListItem.forSuggested(cursor);
        viewHolder.mTextView.setText(myListItem.getName());
        viewHolder.mSubTextView.setText(myListItem.getArtistName());
        Picasso.with(context).load(myListItem.getUri()).into(viewHolder.mImageView);
        viewHolder.imageUri=myListItem.getUri();

    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        super.onBindViewHolder(viewHolder, position);
        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    playsong(position);
            }
        });
    }

    public void playsong(int position)
    {
            MainActivity mainActivity = (MainActivity) context;
            mainActivity.playBack(null, position, 7, "");
    }
}

