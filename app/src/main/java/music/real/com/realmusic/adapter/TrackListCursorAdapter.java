package music.real.com.realmusic.adapter;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import music.real.com.realmusic.MainActivity;
import music.real.com.realmusic.R;
import music.real.com.realmusic.activities.AlbumItesmActivity;
import music.real.com.realmusic.recycler.CursorRecyclerViewAdapter;
import music.real.com.realmusic.recycler.MyListItem;
import music.real.com.realmusic.utilities.CommonUtility;

/**
 * Created by Yadunath.narayanan on 6/26/2015.
 */
public class TrackListCursorAdapter extends CursorRecyclerViewAdapter<TrackListCursorAdapter.ViewHolder>{

    Cursor cursor;
    public Context context;
    private  int type;
    private String  playlistId;
    public TrackListCursorAdapter(Context context, Cursor cursor,int type,String id) {
        super(context, cursor);
        this.cursor = cursor;
        this.context = context;
        this.type=type;
        this.playlistId=id;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View returnView= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_tracks, parent, false);
        ViewHolder viewHolder=new ViewHolder(returnView);
        return viewHolder;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView mSongName;
        TextView mArtistName;
        View mView;
        public ViewHolder(View itemView) {
            super(itemView);
             mSongName=(TextView)itemView.findViewById(R.id.listViewTitleText);
             mArtistName=(TextView)itemView.findViewById(R.id.listViewSubText);
              this.mView=itemView;
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder,  Cursor cursor) {
        MyListItem myListItem=MyListItem.forList(cursor);
        viewHolder.mSongName.setText(myListItem.getName());
        viewHolder.mArtistName.setText(myListItem.getArtistName());

    }
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        super.onBindViewHolder(viewHolder, position);
        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(type==0) {
                    MainActivity mainActivity = (MainActivity) context;
                    mainActivity.playBack(cursor, position, type, playlistId);
                }
                else
                {
                    AlbumItesmActivity mainActivity = (AlbumItesmActivity) context;
                    mainActivity.playBack( position, type, playlistId);
                }
            }
        });
    }

}