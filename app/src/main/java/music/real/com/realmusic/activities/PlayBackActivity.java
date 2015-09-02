package music.real.com.realmusic.activities;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import music.real.com.realmusic.PlayBackUtility;
import music.real.com.realmusic.R;
import music.real.com.realmusic.services.MusicPlaybackService;
import music.real.com.realmusic.utilities.CommonUtility;
import music.real.com.realmusic.utilities.UiUpdater;

public class PlayBackActivity extends AppCompatActivity implements View.OnClickListener,SeekBar.OnSeekBarChangeListener
{
        private String Tag="PLAYBACKACTIVITY";
        public static PlayBackUtility mInterface;
        private int position,type;
        private ImageButton playButton;
        private ImageButton nextButton,previousButton,shuffleButton,repeatButton;
        private String playlistId;
        private SeekBar seekBar;
        private TextView songTitle,songAlbum,songArtist,currentDurationText,totalDurationText;
        private ImageView albumArtImage;
        ImageView closeButton;
//        private ViewPager viewPager;
        private CommonUtility commonUtility;
       private HeadsetPlugReceiver myReceiver;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        this.bindService(new Intent(PlayBackActivity.this, MusicPlaybackService.class), serviceConnection, BIND_AUTO_CREATE);
        playButton=(ImageButton)findViewById(R.id.imageButton);
        nextButton=(ImageButton)findViewById(R.id.imageButton3);
        previousButton=(ImageButton)findViewById(R.id.imageButton2);
        seekBar=(SeekBar)findViewById(R.id.seekBar);
        songTitle=(TextView)findViewById(R.id.songTittle);
        songAlbum=(TextView)findViewById(R.id.songAlbum);
        albumArtImage=(ImageView)findViewById(R.id.imageView6);
        shuffleButton=(ImageButton)findViewById(R.id.imageButton4);
        closeButton=(ImageView)findViewById(R.id.imageView5);
        repeatButton=(ImageButton)findViewById(R.id.imageButton5);
        currentDurationText=(TextView)findViewById(R.id.textView);
        totalDurationText=(TextView)findViewById(R.id.textView2);

//        viewPager=(ViewPager)findViewById(R.id.imageView6);

        playButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);
        previousButton.setOnClickListener(this);
        seekBar.setOnSeekBarChangeListener(this);
        shuffleButton.setOnClickListener(this);
        repeatButton.setOnClickListener(this);
        closeButton.setOnClickListener(this);
        Intent getIntent=getIntent();
        position=getIntent.getIntExtra("position", 0);
        type=getIntent.getIntExtra("type", 0);
        playlistId=getIntent.getStringExtra("playlistid");
        commonUtility=new CommonUtility();

        Handler handler=new Handler();
        myReceiver = new HeadsetPlugReceiver();


//        CustomPagerAdapter customPagerAdapter=new CustomPagerAdapter(getApplicationContext());
//        viewPager.setAdapter(customPagerAdapter);

        if (type==6)
        {
            songTitle.setText(commonUtility.getSongTitle());
            songAlbum.setText(commonUtility.getAlbum());
            Picasso.with(getApplicationContext()).load(commonUtility.geturi()).into(albumArtImage);


        }
        else
        {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        mInterface.playSong(type,position,playlistId);

                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            },200);

            commonUtility.setPlayerStatus(true);
        }

        seekBar.setProgress(0);
        seekBar.setMax(100);
        updateUI();
//        setButtonOnResume();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.imageButton:
                try {
                    mInterface.pauseSong();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.imageButton3:
                try {
                    mInterface.nextSong();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.imageButton2:
                try {
                    mInterface.previousSong();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.imageButton4:
                    setShuffle();
                break;
            case R.id.imageButton5:
                    setRepeat();
                break;
            case R.id.imageView5:

                break;
        }
    }
    public void setShuffle()
    {
        if (commonUtility.getShuffleState()==0)
        {
            commonUtility.setShuffleState(1);
            shuffleButton.setImageResource(R.drawable.shuffle_pressed);
        }
        else
        {
            commonUtility.setShuffleState(0);
            shuffleButton.setImageResource(R.drawable.shuffle);
        }
    }

    public void setRepeat()
    {
        switch (commonUtility.getRepeatState())
        {
            case 0:
                commonUtility.setRepeatState(1);
                repeatButton.setImageResource(R.drawable.repeat_one);
                break;
            case 1:
                commonUtility.setRepeatState(2);
                repeatButton.setImageResource(R.drawable.repeat_all);

                break;
            case 2:
                commonUtility.setRepeatState(0);
                repeatButton.setImageResource(R.drawable.repeat);
                break;
            default:
                commonUtility.setRepeatState(0);

        }
        if (commonUtility.getShuffleState()==0)
        {
            shuffleButton.setImageResource(R.drawable.shuffle);
        }
        else
        {
            shuffleButton.setImageResource(R.drawable.shuffle_pressed);
        }
    }

    /*          Button's on resume*/
    public void setButtonOnResume()
    {
        switch (commonUtility.getRepeatState()) {
            case 0:
                repeatButton.setImageResource(R.drawable.repeat);
                break;
            case 1:
                commonUtility.setRepeatState(2);
                repeatButton.setImageResource(R.drawable.repeat_one);

                break;
            case 2:
                commonUtility.setRepeatState(0);
                repeatButton.setImageResource(R.drawable.repeat_all);
                break;
            default:
                commonUtility.setRepeatState(0);
        }

        }
    public void updateUI()
    {
        UiUpdater uiUpdater=new UiUpdater();
        uiUpdater.setOnSeekListener(new UiUpdater.updateSeekbar() {
            @Override
            public void updateSeekBar(int progress) {
                seekBar.setProgress(progress);
                currentDurationText.setText(""+commonUtility.getCurrentDuration());
                totalDurationText.setText(""+commonUtility.getTotalDuration());

            }
        });

        uiUpdater.setSongInfoUpdate(new UiUpdater.updateInfo() {
            @Override
            public void updateSongInfo(String title, String album, String artist,Uri artwork) {
                songTitle.setText(title);
                songAlbum.setText(album + " | " + artist);
                Picasso.with(getApplicationContext()).load(artwork).into(albumArtImage);

            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
//        registerReceiver(myReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseService();
    }


    public void releaseService()
    {
        unbindService(serviceConnection);

    }


    /*                      Seekbar change listener         */
    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        try {
            mInterface.seekSong(seekBar.getProgress());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    /*                      Connecting with service             */
    public static ServiceConnection serviceConnection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                mInterface=PlayBackUtility.Stub.asInterface((IBinder)iBinder);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mInterface=null;
        }
    };

    private class HeadsetPlugReceiver extends BroadcastReceiver {
        @Override public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
                int state = intent.getIntExtra("state", -1);
                switch (state) {
                    case 0:

                        try {
                            mInterface.stopSong();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }

                        break;
                    case 1:
                        break;
                    default:
                }
            }
        }
        }
}
