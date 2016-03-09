package com.trialvynscloudup.adapter;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.trialvynscloudup.MainActivity;
import com.trialvynscloudup.R;
import com.trialvynscloudup.recycler.CursorRecyclerViewAdapter;
import com.trialvynscloudup.recycler.MyListItem;

/**
 * Created by Yadunath.narayanan on 6/27/2015.
 */
public class ArtistListCursorAdapter extends CursorRecyclerViewAdapter<ArtistListCursorAdapter.ViewHolder> {

    Cursor cursor;
    private Context context;
    int type;
    public static int TYPE_PLAYLIST=3;

    public ArtistListCursorAdapter(Context context, Cursor cursor,int type) {
        super(context, cursor);
        cursor = cursor;
        this.context = context;
        this.type=type;
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View returnView= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_tracks, parent, false);
        ViewHolder viewHolder=new ViewHolder(returnView);
        return viewHolder;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView mArtistName;
        TextView mNoofsongs;
        View mView;
        public String artistId;

        public ViewHolder(View itemView) {
            super(itemView);
            mArtistName=(TextView)itemView.findViewById(R.id.listViewTitleText);
            mNoofsongs=(TextView)itemView.findViewById(R.id.listViewSubText);
            this.mView=itemView;
        }
    }
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        MyListItem myListItem=MyListItem.artistList(cursor,type);

        viewHolder.mArtistName.setText(myListItem.getArtistName());
        if (type==TYPE_PLAYLIST)
        {
            viewHolder.mNoofsongs.setText("  ");

        }
        else 
        {
            viewHolder.mNoofsongs.setText(myListItem.getName()+"  track");

        }

        viewHolder.artistId=myListItem.getAlbumId();

    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        super.onBindViewHolder(viewHolder, position);
        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                        switchFragment(viewHolder.artistId);

            }
        });
    }

    public void switchFragment(String artistId)
    {

        Uri uri=Uri.parse("content://media/external/audio/albumart/3");
        MainActivity mainActivity=(MainActivity)context;
        mainActivity.albumClick(type,uri, artistId);


    }
}
