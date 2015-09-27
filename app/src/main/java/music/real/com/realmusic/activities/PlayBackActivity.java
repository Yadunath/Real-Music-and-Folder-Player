package music.real.com.realmusic.activities;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import music.real.com.realmusic.MainActivity;
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
        public static ImageButton playButton;
        private ImageButton nextButton,previousButton,shuffleButton,repeatButton;
        private String playlistId;
        private SeekBar seekBar;
        private TextView songTitle,songAlbum,songArtist,currentDurationText,totalDurationText;
        private ImageView albumArtImage;
        private  ImageButton closeButton,menuButton,lyricButton;
//        private ViewPager viewPager;
        private CommonUtility commonUtility;
        private HeadsetPlugReceiver myReceiver;
        private UiUpdater uiUpdater;
        int noofsongsplayed=0;
        private SharedPreferences sharedPreferences;
        SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_2);
        this.startService(new Intent(PlayBackActivity.this, MusicPlaybackService.class));
        playButton=(ImageButton)findViewById(R.id.imageButton);
        nextButton=(ImageButton)findViewById(R.id.imageButton2);
        previousButton=(ImageButton)findViewById(R.id.imageButton3);
        seekBar=(SeekBar)findViewById(R.id.seekBar);
        songTitle=(TextView)findViewById(R.id.songTittle);
        songAlbum=(TextView)findViewById(R.id.songAlbum);
        songArtist=(TextView)findViewById(R.id.textView2);
        albumArtImage=(ImageView)findViewById(R.id.imageView6);
        shuffleButton=(ImageButton)findViewById(R.id.imageButton5);
        repeatButton=(ImageButton)findViewById(R.id.imageButton4);
        closeButton=(ImageButton)findViewById(R.id.imageButton7);
        menuButton=(ImageButton)findViewById(R.id.imageButton6);
        lyricButton=(ImageButton)findViewById(R.id.imageButton8);
        currentDurationText=(TextView)findViewById(R.id.textView3);
        totalDurationText=(TextView)findViewById(R.id.textView4);
//        viewPager=(ViewPager)findViewById(R.id.imageView6);

        playButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);
        previousButton.setOnClickListener(this);
        seekBar.setOnSeekBarChangeListener(this);
        shuffleButton.setOnClickListener(this);
        repeatButton.setOnClickListener(this);
        closeButton.setOnClickListener(this);
        menuButton.setOnClickListener(this);
        lyricButton.setOnClickListener(this);

        Intent getIntent=getIntent();
        position=getIntent.getIntExtra("position", 0);
        type=getIntent.getIntExtra("type", 0);

        Log.v("type",""+type);
        playlistId=getIntent.getStringExtra("playlistid");
        commonUtility=new CommonUtility();

        Handler handler=new Handler();
        myReceiver = new HeadsetPlugReceiver();
        sharedPreferences=getSharedPreferences("SONGINFO",MODE_PRIVATE);
        editor=sharedPreferences.edit();

//        CustomPagerAdapter customPagerAdapter=new CustomPagerAdapter(getApplicationContext());
//        viewPager.setAdapter(customPagerAdapter);

        if (type==6)
        {
            songTitle.setText(commonUtility.getSongTitle());
            songAlbum.setText(commonUtility.getAlbum());
            songArtist.setText(commonUtility.getAlbum());
            setAlbumArt(commonUtility.geturi());
//            Picasso.with(getApplicationContext()).load(commonUtility.geturi()).into(albumArtImage);

        }
        else
        {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {

                        mInterface.playSong(type,position,playlistId);
                        playButton.setBackgroundResource(R.drawable.new_pause);
                        commonUtility.setControlerStatus(true);
                        setSharedPreferences();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            },800);

        }

        seekBar.setProgress(0);
        seekBar.setMax(100);
        uiUpdater=new UiUpdater();

        updateUI();

    }

    public void setSharedPreferences()
    {

        editor.putBoolean("launchTime",true);
        editor.commit();
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
            case R.id.imageButton2:
                try {
                    mInterface.nextSong();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.imageButton3:
                try {
                    mInterface.previousSong();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.imageButton5:
                    setShuffle();
                break;
            case R.id.imageButton4:
                    setRepeat();
                break;
            case R.id.imageButton6:
                Intent intent=new Intent(this, MainActivity.class);
//                startActivity(intent);
                break;
            case R.id.imageButton8:
                Toast.makeText(getApplicationContext(),"Lyrics option will be available soon",Toast.LENGTH_SHORT).show();
                break;
            case R.id.imageButton7:
                    finish();
                break;


        }
    }
    public void setShuffle()
    {
        if (commonUtility.getShuffleState()==0)
        {
            commonUtility.setShuffleState(1);
            shuffleButton.setBackgroundResource(R.drawable.new_shuffle_on);
            setSharedPreferencesShuffle(1);

        }
        else
        {
            commonUtility.setShuffleState(0);
            shuffleButton.setBackgroundResource(R.drawable.new_shuffle);
            setSharedPreferencesShuffle(0);

        }
    }

    public void setRepeat()
    {
        switch (commonUtility.getRepeatState())
        {
            case 0:
                commonUtility.setRepeatState(1);
                repeatButton.setBackgroundResource(R.drawable.new_repeat_one);
                setSharedPreferencesRepeat(1);
                break;
            case 1:
                commonUtility.setRepeatState(2);
                repeatButton.setBackgroundResource(R.drawable.new_repeat_all);
                setSharedPreferencesRepeat(2);

                break;
            case 2:
                commonUtility.setRepeatState(0);
                repeatButton.setBackgroundResource(R.drawable.new_repeat);
                setSharedPreferencesRepeat(0);
                break;
            default:
                commonUtility.setRepeatState(0);

        }
        if (commonUtility.getShuffleState()==0)
        {
            shuffleButton.setBackgroundResource(R.drawable.new_shuffle);
        }
        else
        {
            shuffleButton.setBackgroundResource(R.drawable.new_shuffle_on);
        }
    }

    /*          Button's on resume*/
    public void setButtonOnResume()
    {
        switch (commonUtility.getRepeatState()) {
            case 0:
                repeatButton.setBackgroundResource(R.drawable.new_repeat);
                break;
            case 1:
                repeatButton.setBackgroundResource(R.drawable.new_repeat_one);

                break;
            case 2:
                repeatButton.setBackgroundResource(R.drawable.new_repeat_all);
                break;
            default:

        }
        switch (commonUtility.getShuffleState())
        {
            case 0:
                shuffleButton.setBackgroundResource(R.drawable.new_shuffle);
                break;
            case 1:
                shuffleButton.setBackgroundResource(R.drawable.new_shuffle_on);
                break;
        }
        }

    public void updateUI()
    {
        noofsongsplayed++;
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
                songArtist.setText(album + " | " + artist);

                setAlbumArt(artwork);

//                Picasso.with(getApplicationContext()).load(artwork).into(albumArtImage);

            }
        });
    }
    /*              In order to save the shuffle and repeat state even if app force closes or service closed this state will
     * be fetched in every restart of app and set this in commonutility class */
    public void setSharedPreferencesShuffle(int state)
    {
        editor.putInt("shufflestate",state);
        editor.commit();
    }
    public void setSharedPreferencesRepeat(int state)
    {
        editor.putInt("repeatstate", state);
        editor.commit();

    }
    @Override
    protected void onResume() {
        super.onResume();
        this.bindService(new Intent(PlayBackActivity.this, MusicPlaybackService.class), serviceConnection, BIND_AUTO_CREATE);
        updateOnResume();
        setButtonOnResume();
        IntentFilter filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);

//      registerReceiver(myReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (commonUtility.getPlayerStatus())
        {
            
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (commonUtility.getPlayerStatus()==false)
        {
            releaseService();

        }
    }


    public void releaseService()
    {
        unbindService(serviceConnection);

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

           /*                      Update AlbumArt                     */

    public void setAlbumArt(Uri albumArt)
    {
        Picasso.with(getApplicationContext()).load(albumArt).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                albumArtImage.setBackground(new BitmapDrawable(getApplicationContext().getResources(), bitmap));

            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });
    }

    /*          change status bar color             */

    public void changeStatusbarColor(int color)
    {
        if (Integer.valueOf(Build.VERSION.SDK)>21)
        {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(color);
        }

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
