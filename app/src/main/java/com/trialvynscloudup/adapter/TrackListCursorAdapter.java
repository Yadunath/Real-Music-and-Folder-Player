package com.trialvynscloudup.adapter;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AlphabetIndexer;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;
import com.trialvynscloudup.MainActivity;
import com.trialvynscloudup.R;
import com.trialvynscloudup.activities.AlbumItesmActivity;
import com.trialvynscloudup.dialogues.Custom_2_dialog;
import com.trialvynscloudup.dialogues.CustomizeDialog;
import com.trialvynscloudup.recycler.CursorRecyclerViewAdapter;
import com.trialvynscloudup.recycler.MyListItem;
import com.trialvynscloudup.utilities.UiUpdater;

/**
 * Created by Yadunath.narayanan on 6/26/2015.
 */
public class TrackListCursorAdapter extends CursorRecyclerViewAdapter<TrackListCursorAdapter.ViewHolder>
    implements FastScrollRecyclerView.SectionedAdapter{

    private String TAG="TrackListCursorAdapter";
    Cursor cursor;
    public Context context;
    private  int type;
    private String  playlistId;
    private int currentPosition;
    private int TYPE_PLAYLIST=4;
    
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

    @NonNull
    @Override
    public String getSectionName(int position) {
        
        cursor.moveToPosition(position);
        String displayName=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
        return displayName.substring(0,1).toUpperCase(Locale.ENGLISH);
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
                    addtoPlaylist();
                    break;
                case R.id.set_ringtone:
                    message="Set Ringtone?";

                    type=2;
                    title="Music";
                    alertDialog(type,title,message);
                    break;
                case R.id.delet_song:
                    message="Do you want to delete this file?";
                    type=3;
                    title="Music";
                    alertDialog(type,title,message);
                    break;
            }

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

        if (type==TYPE_PLAYLIST)
        {
            long id = Long.parseLong(playlistId);
            String name=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
            context.getContentResolver().delete(MediaStore.Audio.Playlists.Members.getContentUri("external", id), MediaStore.Audio.Media._ID + "='"+name+"'", null);
            Log.e(TAG,"playlsi"+id);
        }
        else
        {
            cursor.moveToPosition(currentPosition);
            String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
            String name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
            context.getContentResolver().delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, MediaStore.Audio.Media.DATA + "='" + data + "'", null);
            File f = new File(name);
            f.delete();
            notifyItemRemoved(currentPosition);
            notifyItemRangeChanged(currentPosition, cursor.getCount());
        }
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
    public void addtoPlaylist()
    {

      final Cursor  playlistCursor=context.getContentResolver().query(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Audio.Playlists._ID, MediaStore.Audio.Playlists.NAME}, null, null, null);
        final Custom_2_dialog playlistDialogue=new Custom_2_dialog(context);
        playlistDialogue.show();
        String from="CreateNew";
        ArrayList<String> crt=new ArrayList<String>();
        crt.add(from);
        ArrayAdapter<String> sd=new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1,crt);
        ListView createnew=(ListView)playlistDialogue.findViewById(R.id.listView2);
        createnew.setAdapter(sd);
        ListView dialogplst=(ListView)playlistDialogue.findViewById(R.id.listView1);
        String[] from1={MediaStore.Audio.Playlists.NAME};
        int[] to={android.R.id.text1};
        dialogplst.setAdapter(new SimpleCursorAdapter(context, android.R.layout.simple_list_item_1, playlistCursor, from1, to));
        dialogplst.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {


            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int arg2, long arg3)
            {
                playlistCursor.moveToPosition(arg2);
                cursor.moveToPosition(currentPosition);
                String  paste1=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
                int name=cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME);
                String displayname=cursor.getString(name);
                int pid=playlistCursor.getInt(playlistCursor.getColumnIndex(MediaStore.Audio.Playlists._ID));
                ContentValues cv=new ContentValues(2);
                cv.put(MediaStore.Audio.Playlists.Members.AUDIO_ID, paste1);
                cv.put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, 1);
                ContentResolver cr=context.getContentResolver();
                Uri uri=cr.insert(MediaStore.Audio.Playlists.Members.getContentUri("external", pid), cv);
                playlistDialogue.dismiss();
            }
        });
        createnew.setOnItemClickListener(new AdapterView.OnItemClickListener() {


            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                playlistDialogue.dismiss();
                final CustomizeDialog newPlaylistDialogue = new CustomizeDialog(context);
                newPlaylistDialogue.show();
                final  EditText et = (EditText) newPlaylistDialogue.findViewById(R.id.textView2);
                et.setText("New Playlist1");
                String p = et.getText().toString();
                Button  ok = (Button) newPlaylistDialogue.findViewById(R.id.button1);
                Button cancel = (Button) newPlaylistDialogue.findViewById(R.id.button2);

                if (playlistCursor.getCount()>0) {
                    String isthr = playlistCursor.getString(playlistCursor.getColumnIndex(MediaStore.Audio.Playlists.NAME));
                    if (isthr.contains(p)) {
//                        ok.setEnabled(false);
                    }
                }

                // OK BUTTON NEW BOX
                ok.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View v) {
                        newPlaylistDialogue.dismiss();
                        int alc=0;
                        if (playlistCursor.getCount()>0) {
                            playlistCursor.moveToLast();


                            int albd = playlistCursor.getInt(playlistCursor.getColumnIndex(MediaStore.Audio.Playlists._ID));
                             alc = albd + 1;
                        }
                        cursor.moveToPosition(currentPosition);
                        String s1 = et.getText().toString();
                        ContentValues values = new ContentValues(2);
                        values.put(MediaStore.Audio.PlaylistsColumns.NAME, s1);
                        values.put(MediaStore.Audio.PlaylistsColumns.DATE_ADDED, System.currentTimeMillis() / 1000);
                        ContentResolver cre =context. getContentResolver();
                        Uri uri = cre.insert(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, values);
                       String add = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
                        ContentValues cv = new ContentValues(3);
                        cv.put(MediaStore.Audio.Playlists.Members.AUDIO_ID, add);
                        cv.put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, 1);
                        ContentResolver cr = context.getContentResolver();
                        Uri uri1 = cr.insert(MediaStore.Audio.Playlists.Members.getContentUri("external", alc), cv);
                    }
                });
                // 	CANCEL BUTTON
                cancel.setOnClickListener(new View.OnClickListener() {


                    public void onClick(View v) {
                        newPlaylistDialogue.dismiss();
                    }
                });
            }
        });


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