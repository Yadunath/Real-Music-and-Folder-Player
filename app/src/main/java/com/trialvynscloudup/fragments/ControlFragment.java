package com.trialvynscloudup.fragments;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import music.real.com.realmusic.PlayBackUtility;

import com.trialvynscloudup.R;
import com.trialvynscloudup.activities.PlayBackActivity;
import com.trialvynscloudup.services.MusicPlaybackService;
import com.trialvynscloudup.utilities.CommonUtility;
import com.trialvynscloudup.utilities.UiUpdater;

/**
 * A simple {@link Fragment} subclass.
 */


public class ControlFragment extends Fragment implements View.OnClickListener {
    private TextView songTitle,songAlbum;
    private ImageView albumArtView;
    public static ImageButton playButton;
    private CommonUtility commonUtility;
    PlayBackUtility mInterface;
    private UiUpdater uiUpdater;
    public ControlFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View contentView=inflater.inflate(R.layout.fragment_control, container, false);
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(), PlayBackActivity.class);
                intent.putExtra("type", 6);
                intent.putExtra("position", 2);

                startActivity(intent);
            }
        });
        return contentView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        songTitle=(TextView)view.findViewById(R.id.title);
        songAlbum=(TextView)view.findViewById(R.id.artist);
        albumArtView=(ImageView)view.findViewById(R.id.album_art);
        playButton=(ImageButton)view.findViewById(R.id.play_pause);
        playButton.setOnClickListener(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        commonUtility=new CommonUtility();
        updateInfo();
        getActivity().bindService(new Intent(getActivity(), MusicPlaybackService.class), serviceConnection, Context.BIND_AUTO_CREATE);
        uiUpdater=new UiUpdater();
        updateUI();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateOnResume();



    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.play_pause:
                try {
                    mInterface.pauseSong(0);

                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                updatePlayButton();
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releaseService();
    }

    public void releaseService()
    {
        getActivity().unbindService(serviceConnection);

    }
    public void updateUI()
    {
        uiUpdater.setSongInfoUpdate(new UiUpdater.updateInfo() {
            @Override
            public void updateSongInfo(String title, String album, String artist, Uri artwork,String path,int trackPosition) {

                songTitle.setText(title);
                songAlbum.setText(album + " | " + artist);
                Picasso.with(getActivity()).load(artwork).into(albumArtView);

            }
        });
    }
    public void updateInfo()
    {
        songTitle.setText(commonUtility.getSongTitle());
        songAlbum.setText(commonUtility.getAlbum());
        Picasso.with(getActivity()).load(commonUtility.geturi()).into(albumArtView);

    }
    public void updateOnResume()
    {

        if (commonUtility.getPlayerStatus())
        {
            playButton.setBackgroundResource(R.drawable.new_pause);
        }
        else
        {
            playButton.setBackgroundResource(R.drawable.new_play);
        }
    }
    public void updatePlayButton()
    {
        if (commonUtility.getPlayerStatus())
        {
            playButton.setBackgroundResource(R.drawable.new_pause);
        }
        else
        {
            playButton.setBackgroundResource(R.drawable.new_play);
        }
    }

    /*                      Connecting with service             */
    private ServiceConnection serviceConnection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mInterface= PlayBackUtility.Stub.asInterface((IBinder)iBinder);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mInterface=null;
        }
    };
}
