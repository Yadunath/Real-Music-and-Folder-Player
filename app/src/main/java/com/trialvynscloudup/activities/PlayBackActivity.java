package com.trialvynscloudup.activities;

import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import com.trialvynscloudup.MainActivity;
import music.real.com.realmusic.PlayBackUtility;

import com.trialvynscloudup.R;
import com.trialvynscloudup.adapter.AdapterUtility;
import com.trialvynscloudup.adapter.AlbumartPagerAdapter;
import com.trialvynscloudup.dialogues.SleepTimerDialogue;
import com.trialvynscloudup.services.MusicPlaybackService;
import com.trialvynscloudup.utilities.CommonUtility;
import com.trialvynscloudup.utilities.LauncherApplication;
import com.trialvynscloudup.utilities.UiUpdater;
import com.trialvynscloudup.utilities.Utilities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class PlayBackActivity extends AppCompatActivity implements View.OnClickListener,SeekBar.OnSeekBarChangeListener,
        ViewPager.OnPageChangeListener
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
        
        private CommonUtility commonUtility;
        private HeadsetPlugReceiver myReceiver;
        private UiUpdater uiUpdater;
        int noofsongsplayed=0;
        private SharedPreferences sharedPreferences;
        SharedPreferences.Editor editor;
        private final int CONTROLLER_OPEN=6;
        ImageLoader imageLoader;
        DisplayImageOptions options;
        private SleepTimerDialogue sleepTimerDialogue;
        private Utilities utilities=new Utilities();
        CountDownTimer countDownTimer;
        private TextView mTextView;
        private Button num1,num2,num3,num4,num5,num6,num7,num8,num9,num0;
        private GestureDetector gestureDetector;
        View.OnTouchListener gestureListener;

        private String sharePath;
        private ViewPager viewPagerAlbumArt;
        int currentApiversion;
        private int trackPosition;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playback);
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

        viewPagerAlbumArt=(ViewPager)findViewById( R.id.viewpageart);

        playButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);
        previousButton.setOnClickListener(this);
        seekBar.setOnSeekBarChangeListener(this);
        shuffleButton.setOnClickListener(this);
        repeatButton.setOnClickListener(this);
        closeButton.setOnClickListener(this);
        menuButton.setOnClickListener(this);
        lyricButton.setOnClickListener(this);
        seekBar.setProgress(0);
        seekBar.setMax(100);
        Intent getIntent=getIntent();
        position=getIntent.getIntExtra("position", 0);
        type=getIntent.getIntExtra("type", 0);

         currentApiversion = Build.VERSION.SDK_INT;


        playlistId=getIntent.getStringExtra("playlistid");
        commonUtility=new CommonUtility();

        Handler handler=new Handler();
        myReceiver = new HeadsetPlugReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        registerReceiver(myReceiver, filter);
        sharedPreferences=getSharedPreferences("SONGINFO", MODE_PRIVATE);
        editor=sharedPreferences.edit();
        uiUpdater=new UiUpdater();

        imageLoader=ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(getApplicationContext()));

        sleepTimerDialogue=new SleepTimerDialogue(this);

        // Gesture detection
        gestureDetector = new GestureDetector(this, new MyGestureDetector());
        gestureListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        };
        /*          when opening from controller */
        if (type==CONTROLLER_OPEN)
        {

            songTitle.setText(commonUtility.getSongTitle());
            songAlbum.setText(commonUtility.getAlbum());
            songArtist.setText(commonUtility.getAlbum());
            if (commonUtility.geturi()!=null)
            {
                setAlbumArt(commonUtility.geturi());

            }

//            Picasso.with(getApplicationContext()).load(commonUtility.geturi()).into(albumArtImage);

        }
        else {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {

                        mInterface.playSong(type, position, playlistId);
                        playButton.setBackgroundResource(R.drawable.new_pause);
                        
/*                        AdapterUtility adapterUtility=new AdapterUtility();
                        Cursor albumartCursor= adapterUtility.getCursor(PlayBackActivity.this, type, playlistId);
                        AlbumartPagerAdapter albumartPagerAdapter=new AlbumartPagerAdapter(PlayBackActivity.this,albumartCursor);
                        viewPagerAlbumArt.setAdapter(albumartPagerAdapter);
                        viewPagerAlbumArt.setCurrentItem(position);
                        viewPagerAlbumArt.setOnPageChangeListener(PlayBackActivity.this);
                        trackPosition=position;*/
                        commonUtility.setControlerStatus(true);
                        setSharedPreferences();
                        
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            },800);

        }

        if (CommonUtility.developementStatus)
        {

        }
        else
        {
            LauncherApplication.getInstance().trackScreenView("Playback activity");
        }

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
                playButtonChange();
                try {
                    mInterface.pauseSong(0);
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

                PopupMenu popupMenu=new PopupMenu(this,view);
                popupMenu.inflate(R.menu.popupmenu_settings);
                popupMenu.setOnMenuItemClickListener(listitemClick);
                popupMenu.show();
                break;
            case R.id.imageButton8:
                Intent intent;

                trackLyricsClick(songTitle.getText().toString());
                Uri uri = Uri.parse("http://www.google.com/#q="+songTitle.getText().toString()+" lyrics");
                intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);

                break;
            case R.id.imageButton7:
                    finish();
                break;


        }
    }

    public PlayBackActivity() {
        super();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        
    }

    @Override
    public void onPageSelected(int position) {
        Log.e("Tag",""+trackPosition);
        if (trackPosition<position)
        {
            try {
                mInterface.nextSong();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        else if (trackPosition>position)
        {
            try {
                mInterface.previousSong();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        
        
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

    /*      Change state of repeat  */
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
                currentDurationText.setText("" + commonUtility.getCurrentDuration());
                totalDurationText.setText("" + commonUtility.getTotalDuration());

            }
        });

        uiUpdater.setSongInfoUpdate(new UiUpdater.updateInfo() {
            @Override
            public void updateSongInfo(String title, String album, String artist, final Uri artwork,String path,int trackPosition) {
                
                songTitle.setText(title);
                songAlbum.setText(album + " | " + artist);
                songArtist.setText(album + " | " + artist);
                sharePath=path;
//                setAlbumArt(artwork);
                updateOnResume();
                trackPosition=position;
//                viewPagerAlbumArt.setCurrentItem(trackPosition);
                imageLoader.loadImage(artwork.toString(), new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        // Do whatever you want with Bitmap
                        if (currentApiversion >= 16) {
                            albumArtImage.setBackground(new BitmapDrawable(getApplicationContext().getResources(), loadedImage));


                        } else {
                            Picasso.with(getApplicationContext()).load(artwork.toString()).into(albumArtImage);
                        }


                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                        super.onLoadingFailed(imageUri, view, failReason);
                        if (currentApiversion >= 16) {
                            albumArtImage.setBackgroundResource(R.drawable.default_albumart);


                        } else {
                            Picasso.with(getApplicationContext()).load(R.drawable.default_albumart).into(albumArtImage);
                        }

                    }

                });
            }
        });
    }
    /*              In order to save the shuffle and repeat state even if app force closes or service closed this state will
     * be fetched in every restart of app and set this in commonutility class */
    public void setSharedPreferencesShuffle(int state)
    {
        editor.putInt("shufflestate", state);
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
        releaseService();


    }


    public void releaseService()
    {
        unbindService(serviceConnection);
        unregisterReceiver(myReceiver);

    }
    public void playButtonChange()
    {

        if (commonUtility.getPlayerStatus())
        {
            playButton.setBackgroundResource(R.drawable.new_play);
        }
        else
        {
            playButton.setBackgroundResource(R.drawable.new_pause);
        }
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

    public void setAlbumArt(final Uri albumArt)
    {
        imageLoader.loadImage(albumArt.toString(), new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                // Do whatever you want with Bitmap


                if (currentApiversion >= 16) {
                    albumArtImage.setBackground(new BitmapDrawable(getApplicationContext().getResources(), loadedImage));


                } else {
                    Picasso.with(getApplicationContext()).load(albumArt.toString()).into(albumArtImage);
                }
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                super.onLoadingFailed(imageUri, view, failReason);
                albumArtImage.setBackgroundResource(R.drawable.default_albumart);

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
                case R.id.share:

                    share();
                    break;
                case R.id.sleep:

                    sleep();
                    break;

            }

            return false;

        }
    };

    public void share()
    {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("audio/*");
        Log.e(Tag,""+sharePath);
        share.putExtra(Intent.EXTRA_STREAM,Uri.parse("file:///"+sharePath));
        startActivity(Intent.createChooser(share, "Share Sound File"));
    }
    public void sleep()
    {
        sleepTimerDialogue.show();
        View numpad=(View)sleepTimerDialogue.findViewById(R.id.numpad);
        init(sleepTimerDialogue);

        final Button startButton=(Button)sleepTimerDialogue.findViewById(R.id.button5);
        mTextView=(TextView)sleepTimerDialogue.findViewById(R.id.textView6);
        mTextView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
        Button cancelButton=(Button)sleepTimerDialogue.findViewById(R.id.button);
        if (commonUtility.getTimerStatus())
        {
            startButton.setText("stop");
            commonUtility.setTimerStatus(false);
            deActivateNumPad(true);
        }
        else
        {
            deActivateNumPad(false);
        }



        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mTextViewText=mTextView.getText().toString();

                if (commonUtility.getTimerStatus()) {
                    startButton.setText("start");
                    countDownTimer.cancel();
                    mTextView.setText(" mins");
                    commonUtility.setTimerStatus(false);
                    deActivateNumPad(false);

                } else if (mTextViewText.length()>5){
                    deActivateNumPad(true);
                    startButton.setText("stop");
                    int timer;
                    if (mTextViewText.length()==6)
                    {
                         timer = Integer.parseInt(mTextViewText.substring(0,1));
                    }
                    else
                    {
                         timer = Integer.parseInt(mTextViewText.substring(0,2));
                    }

                  countDownTimer=  new CountDownTimer(TimeUnit.MINUTES.toMillis(timer), 1000) {

                        public void onTick(long millisUntilFinished) {
                            commonUtility.setTimerStatus(true);
                            String timeleft = utilities.milliSecondsToTimer(millisUntilFinished);

                            UiUpdater.timerInterface.updateTimerText(timeleft);
                            //here you can have your logic to set text to edittext

                        }

                        public void onFinish() {
                            mTextView.setText(" mins");
                            commonUtility.setTimerStatus(false);
                            deActivateNumPad(false);
                            try {
                                mInterface.stopSong();
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }

                    }.start();
                }
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sleepTimerDialogue.dismiss();
            }
        });

        trackSleep();

    }
    public void init(SleepTimerDialogue dialogue)
    {
        num0=(Button)dialogue.findViewById(R.id.numpad0);
        num1=(Button)dialogue.findViewById(R.id.numpad1);
        num2=(Button)dialogue.findViewById(R.id.numpad2);
        num3=(Button)dialogue.findViewById(R.id.numpad3);
        num4=(Button)dialogue.findViewById(R.id.numpad4);
        num5=(Button)dialogue.findViewById(R.id.numpad5);
        num6=(Button)dialogue.findViewById(R.id.numpad6);
        num7=(Button)dialogue.findViewById(R.id.numpad7);
        num8=(Button)dialogue.findViewById(R.id.numpad8);
        num9=(Button)dialogue.findViewById(R.id.numpad9);
        num0.setOnClickListener(numpadButtonClickListener());
        num1.setOnClickListener(numpadButtonClickListener());
        num2.setOnClickListener(numpadButtonClickListener());
        num3.setOnClickListener(numpadButtonClickListener());
        num4.setOnClickListener(numpadButtonClickListener());
        num5.setOnClickListener(numpadButtonClickListener());
        num6.setOnClickListener(numpadButtonClickListener());
        num7.setOnClickListener(numpadButtonClickListener());
        num8.setOnClickListener(numpadButtonClickListener());
        num9.setOnClickListener(numpadButtonClickListener());
    }
    public void deActivateNumPad(boolean status)
    {
        if (status)
        {
            num0.setEnabled(false);
            num1.setEnabled(false);
            num2.setEnabled(false);
            num3.setEnabled(false);
            num4.setEnabled(false);
            num5.setEnabled(false);
            num6.setEnabled(false);
            num7.setEnabled(false);
            num8.setEnabled(false);
            num9.setEnabled(false);
        }
        else
        {
            num0.setEnabled(true);
            num1.setEnabled(true);
            num2.setEnabled(true);
            num3.setEnabled(true);
            num4.setEnabled(true);
            num5.setEnabled(true);
            num6.setEnabled(true);
            num7.setEnabled(true);
            num8.setEnabled(true);
            num9.setEnabled(true);
        }



    }

    private View.OnClickListener numpadButtonClickListener() {
        return new View.OnClickListener() {
            public void onClick(View v) {

                if (mTextView.getText().length()<7) {
                    String number = (((Button) v).getText().toString());
                    mTextView.setText(number + mTextView.getText().toString());
                }
            }
        };
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

    private boolean headsetConnected = false;

    private class HeadsetPlugReceiver extends BroadcastReceiver {
        @Override public void onReceive(Context context, Intent intent) {

            if (headsetConnected && intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
                headsetConnected=false;
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
            else if (!headsetConnected && intent.getIntExtra("state", 0) == 1)
            {
                headsetConnected=true;
            }
        }
        }
    public class UiUpdateAsync extends AsyncTask<Void,Void,Void>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            updateUI();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }


    class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_MIN_DISTANCE = 120;
        private static final int SWIPE_MAX_OFF_PATH = 250;
        private static final int SWIPE_THRESHOLD_VELOCITY = 200;
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                    return false;
                // right to left swipe
                if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    try {
                        mInterface.nextSong();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                    Animation animation= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rightswipe);
                    albumArtImage.startAnimation(animation);
                }  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    try {
                        mInterface.previousSong();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                // nothing
            }
            return false;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
    }

    public void trackLyricsClick(String title)
    {
        if (CommonUtility.developementStatus)
        {

        }
        else
        {
            String deviceName = Build.BRAND+" "+Build.MODEL;

            LauncherApplication.getInstance().trackEvent("lyricsClick",title,deviceName);
        }
    }
    public void trackSleep()
    {
        if (CommonUtility.developementStatus)
        {

        }
        else
        {
            String deviceName = Build.BRAND+" "+Build.MODEL;

            LauncherApplication.getInstance().trackEvent("sleep","sleepCheck",deviceName);
        }
    }
}
