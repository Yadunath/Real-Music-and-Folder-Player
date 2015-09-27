package music.real.com.realmusic.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

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
    private int currentPosition;
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
        ImageButton listItemsetting;
        View mView;
        public ViewHolder(View itemView) {
            super(itemView);
             mSongName=(TextView)itemView.findViewById(R.id.listViewTitleText);
             mArtistName=(TextView)itemView.findViewById(R.id.listViewSubText);
             listItemsetting=(ImageButton)itemView.findViewById(R.id.listViewOverflow);
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
              playsong(position);
            }
        });

        viewHolder.listItemsetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu=new PopupMenu(context,view);
                popupMenu.inflate(R.menu.artist_overflow_menu);
                currentPosition=position;
                popupMenu.setOnMenuItemClickListener(listitemClick);
                popupMenu.show();
            }
        });

    }
    private PopupMenu.OnMenuItemClickListener listitemClick=new PopupMenu.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            String title,message=null;
            int type=0;
            switch (menuItem.getItemId())
            {
                case R.id.play_menu:
                        playsong(currentPosition);
                    break;
                case R.id.add_to_playlist:
                    type=1;
                    break;
                case R.id.set_ringtone:
                    message="Set Ringtone?";
                    type=2;
                    break;
                case R.id.delet_song:
                    message="Do you want to delete this file?";
                    type=3;
                    break;
            }
            title="Music";
            alertDialog(type,title,message);
            return false;

        }
    };



    public void playsong(int position)
    {
        if (type == 0) {
            MainActivity mainActivity = (MainActivity) context;
            mainActivity.playBack(cursor, position, type, playlistId);
        } else {
            AlbumItesmActivity mainActivity = (AlbumItesmActivity) context;
            mainActivity.playBack(position, type, playlistId);
        }
    }
    public void deleteSong()
    {

        cursor.moveToPosition(currentPosition);
        String data=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
        String name=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
        context.getContentResolver().delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, MediaStore.Audio.Media.DATA + "='"+data+"'", null);
        File f=new File(name);
        f.delete();
        notifyItemRemoved(currentPosition);
        notifyItemRangeChanged(currentPosition, cursor.getCount());


    }
    public void setringTone(){
        cursor.moveToPosition(currentPosition);
        int setid=cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
        Uri ringUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, setid);
        try {
            ContentValues values = new ContentValues(2);
            values.put(MediaStore.Audio.Media.IS_RINGTONE, "1");
            values.put(MediaStore.Audio.Media.IS_ALARM, "1");
            context.getContentResolver().update(ringUri, values, null, null);
        } catch (UnsupportedOperationException ex) {
            // most likely the card just got unmounted
            Log.e("ts", "couldn't set ringtone flag for id " + setid);
            return;
        }
        String[] cols = new String[] {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.TITLE
        };
        String where = MediaStore.Audio.Media._ID + "=" + setid;
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                cols, where, null, MediaStore.Audio.Media.DISPLAY_NAME);
//				if(0 ==c.getInt(c.getColumnIndex(Media.IS_RINGTONE))){
        Settings.System.putString(context.getContentResolver(), Settings.System.RINGTONE, ringUri.toString());
        //RingtoneManager.setActualDefaultRingtoneUri(DingActivity.this, RingtoneManager.TYPE_RINGTONE,
        //	getUri());
    }


    public void alertDialog(final int type,String title,String message )
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set title


        // set dialog message
        alertDialogBuilder
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, close
                        // current activity
                        switch (type)
                        {
                            case 1:
                                break;
                            case 2:
                                setringTone();

                                break;
                            case 3:
                                deleteSong();

                                break;
                        }

                    }
                })
                .setNegativeButton("No",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

    }

}