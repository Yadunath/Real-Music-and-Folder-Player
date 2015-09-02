package music.real.com.realmusic.services;

import java.io.FileDescriptor;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.Random;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.RemoteControlClient;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

import music.real.com.realmusic.PlayBackUtility;
import music.real.com.realmusic.R;
import music.real.com.realmusic.receiver.NotificationBroadcast;
import music.real.com.realmusic.utilities.CommonUtility;
import music.real.com.realmusic.utilities.UiUpdater;
import music.real.com.realmusic.utilities.Utilities;

public class MusicPlaybackService extends Service implements OnCompletionListener,OnAudioFocusChangeListener {
	
	public static MediaPlayer musicPlayer =new MediaPlayer();
	String name;
	public static int pos;
	boolean repeat=false;
	private boolean playing=false;
	private Handler mHandler=new Handler();

	private Utilities utils=new Utilities();
	private static Cursor mCursor;
	private static int  trackPosition;
	private final int TYPE_TRACKS=0;
	private final int TYPE_ALBUMS=1;
	private final int TYPE_ARTIST=2;
	private final int TYPE_FOLDERS=4;
	private  CommonUtility commonUtility;
	private ComponentName remoteComponentName;
	private RemoteControlClient remoteControlClient;
	AudioManager audioManager;
	NotificationManager notificationManager;

	public static final String NOTIFY_PREVIOUS = "com.music.audioplayer.previous";
	public static final String NOTIFY_DELETE = "com.music.audioplayer.delete";
	public static final String NOTIFY_PAUSE = "com.music.audioplayer.pause";
	public static final String NOTIFY_PLAY = "com.music.audioplayer.play";
	public static final String NOTIFY_NEXT = "com.music.audioplayer.next";


	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return playbackBinder;
	}
	public IBinder getBinder() {
		return playbackBinder;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		commonUtility=new CommonUtility();

		audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		RegisterRemoteClient();
		if (musicPlayer.isPlaying())
		{
			remoteControlClient.setPlaybackState(RemoteControlClient.PLAYSTATE_PLAYING);

		}
		else
		{
			remoteControlClient.setPlaybackState(RemoteControlClient.PLAYSTATE_PAUSED);

		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		return super.onStartCommand(intent, flags, startId);

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		playing=false;
		super.onDestroy();
		
	}


	public void mediaPlayBack(String datapath)
	{

		musicPlayer.reset();
		try {
			musicPlayer.setDataSource(datapath);
			musicPlayer.prepare();
			musicPlayer.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		musicPlayer.setOnCompletionListener(this);
		updateprogressbar();
		setNotificationLayout();
		UpdateMetadata();
		remoteControlClient.setPlaybackState(RemoteControlClient.PLAYSTATE_PLAYING);


	}

	public void getCursor( int type,String playlistId)
	{
		String order= MediaStore.Audio.Media.DISPLAY_NAME;
		switch (type)
		{

			case TYPE_TRACKS:
				mCursor=getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,null,null,null,null);
				break;
			case TYPE_ALBUMS:
				String where= MediaStore.Audio.Media.ALBUM_ID + "=?";
				String wherVal[]={playlistId};
				mCursor=getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,null,where,wherVal,order);
				break;
			case TYPE_ARTIST:
				String whereArtist= MediaStore.Audio.Media.ARTIST_ID + "=?";
				String wherValArtist[]={playlistId};
				Log.v("service",""+playlistId);
				mCursor=getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,null,whereArtist,wherValArtist,null);
				break;
			case TYPE_FOLDERS:

				String wherFolder=MediaStore.Audio.Media.DATA + " like ? "+" and "+MediaStore.Audio.Media.DATA + " not like ?";
				String whereValue[]=new String[]{"%"+ playlistId +"%","%"+ commonUtility.getSubFolderName() +"%"};
				mCursor=getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,null,wherFolder,whereValue,order+" COLLATE NOCASE ASC; ");
				break;

		}

	}
	public String getDatapath()
	{
		mCursor.moveToPosition(trackPosition);
		String dataPath=mCursor.getString(mCursor.getColumnIndex(MediaStore.Audio.Media.DATA));
		String songTitle=mCursor.getString(mCursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
		String albumName=mCursor.getString(mCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
		String artistName=mCursor.getString(mCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
		int albumId=mCursor.getInt(mCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
		Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
		Uri uriArtWork = ContentUris.withAppendedId(sArtworkUri, albumId);
		UiUpdater.mInfoListener.updateSongInfo(songTitle, albumName, artistName, uriArtWork);
		commonUtility.setSongTitle(songTitle);
		commonUtility.setAlbumName(albumName);
		commonUtility.setAlbumArtUri(uriArtWork);
		return dataPath;
	}
	public void setPlaySong(int type,int position,String playlistId)
	{
		trackPosition=position;
		getCursor(type, playlistId);
		mediaPlayBack(getDatapath());

	}
	public void pauseSong()
	{

		if (musicPlayer.isPlaying())
		{
			musicPlayer.pause();
			remoteControlClient.setPlaybackState(RemoteControlClient.PLAYSTATE_PAUSED);

		}
		else
		{
			musicPlayer.start();
			remoteControlClient.setPlaybackState(RemoteControlClient.PLAYSTATE_PLAYING);

		}
	}
	public void stopSong()
	{
		if (musicPlayer.isPlaying())
		{
			musicPlayer.pause();
			remoteControlClient.setPlaybackState(RemoteControlClient.PLAYSTATE_PAUSED);

		}
	}

	public void nextSong()
	{

		if (commonUtility.getRepeatState()==0)
		{
			if (mCursor.getCount()==trackPosition)
			{

			}
			else
			{
				if (commonUtility.getShuffleState()==1)
				{
					trackPosition=getRandomNumber();
					mediaPlayBack(getDatapath());
				}
				else
				{
					trackPosition++;
					mediaPlayBack(getDatapath());
				}

			}
		}
		else if (commonUtility.getRepeatState()==1)
		{
			mediaPlayBack(getDatapath());
		}
		else if (commonUtility.getRepeatState()==2) {
			if (commonUtility.getShuffleState()==1)
			{
				trackPosition=getRandomNumber();
				mediaPlayBack(getDatapath());
			}
			else
			{
				trackPosition++;
				mediaPlayBack(getDatapath());
			}
		}




	}

	public void previousSong()
	{
		trackPosition--;
		if (trackPosition<0)
		{

		}
		else
		{
			mediaPlayBack(getDatapath());

		}

	}


	public void seekSong(int progress)
	{
		mHandler.removeCallbacks(mUpdateTimeTask);
		int totalDuration = musicPlayer.getDuration();
		int currentPosition = utils.progressToTimer(progress, totalDuration);
		musicPlayer.seekTo(currentPosition);
		updateprogressbar();
	}


	public void  setNotificationLayout()
	{
	/*	Notification notification = new NotificationCompat.Builder(getApplicationContext())
				.setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle("title").build();
		RemoteViews notificationViews=new RemoteViews(getApplicationContext().getPackageName(),R.layout.custom_notification);
		notification.contentView=notificationViews;
		notification.flags |=  Notification.FLAG_AUTO_CANCEL | Notification.FLAG_SHOW_LIGHTS;
		startForeground(12,notification);
		*/

		notificationManager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		Notification notification=new Notification(R.drawable.ic_launcher,null,System.currentTimeMillis());
		RemoteViews notificationViews=new RemoteViews(getPackageName(),R.layout.custom_notification);

		Notification status = new Notification();
		status.contentView = notificationViews;
		notificationViews.setImageViewBitmap(R.id.imageViewAlbumArt,getAlbumArtBitmap());
		notificationViews.setTextViewText(R.id.textSongName, commonUtility.getSongTitle());
		notificationViews.setTextViewText(R.id.textAlbumName, commonUtility.getAlbum());
		status.flags |= Notification.FLAG_AUTO_CANCEL | Notification.FLAG_SHOW_LIGHTS;
		status.icon = R.drawable.ic_launcher;
		setListeners(notificationViews);
//		startForeground(1458, status);


	}
	public void setListeners(RemoteViews view) {
		Intent previous = new Intent(NOTIFY_PREVIOUS);
		Intent delete = new Intent(NOTIFY_DELETE);
		Intent pause = new Intent(NOTIFY_PAUSE);
		Intent next = new Intent(NOTIFY_NEXT);
		Intent play = new Intent(NOTIFY_PLAY);

		PendingIntent pPrevious = PendingIntent.getBroadcast(getApplicationContext(), 0, previous, PendingIntent.FLAG_UPDATE_CURRENT);
		view.setOnClickPendingIntent(R.id.btnPrevious, pPrevious);

		PendingIntent pDelete = PendingIntent.getBroadcast(getApplicationContext(), 0, delete, PendingIntent.FLAG_UPDATE_CURRENT);
		view.setOnClickPendingIntent(R.id.btnDelete, pDelete);

		PendingIntent pPause = PendingIntent.getBroadcast(getApplicationContext(), 0, pause, PendingIntent.FLAG_UPDATE_CURRENT);
		view.setOnClickPendingIntent(R.id.btnPause, pPause);

		PendingIntent pNext = PendingIntent.getBroadcast(getApplicationContext(), 0, next, PendingIntent.FLAG_UPDATE_CURRENT);
		view.setOnClickPendingIntent(R.id.btnNext, pNext);

		PendingIntent pPlay = PendingIntent.getBroadcast(getApplicationContext(), 0, play, PendingIntent.FLAG_UPDATE_CURRENT);
		view.setOnClickPendingIntent(R.id.btnPlay, pPlay);

	}

				/*			LockScreen playback ui registering  		*/

	private void RegisterRemoteClient(){
		remoteComponentName = new ComponentName(getApplicationContext(), new NotificationBroadcast().ComponentName());
		try {
			if(remoteControlClient == null) {
				audioManager.registerMediaButtonEventReceiver(remoteComponentName);
				Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
				mediaButtonIntent.setComponent(remoteComponentName);
				PendingIntent mediaPendingIntent = PendingIntent.getBroadcast(this, 0, mediaButtonIntent, 0);
				remoteControlClient = new RemoteControlClient(mediaPendingIntent);
				audioManager.registerRemoteControlClient(remoteControlClient);
			}
			remoteControlClient.setTransportControlFlags(
					RemoteControlClient.FLAG_KEY_MEDIA_PLAY |
							RemoteControlClient.FLAG_KEY_MEDIA_PAUSE |
							RemoteControlClient.FLAG_KEY_MEDIA_PLAY_PAUSE |
							RemoteControlClient.FLAG_KEY_MEDIA_STOP |
							RemoteControlClient.FLAG_KEY_MEDIA_PREVIOUS |
							RemoteControlClient.FLAG_KEY_MEDIA_NEXT);
		}catch(Exception ex) {
		}
	}
			/*			Lockscreen details update		*/
	private void UpdateMetadata(){
		if (remoteControlClient == null)
			return;
		RemoteControlClient.MetadataEditor metadataEditor = remoteControlClient.editMetadata(true);
		metadataEditor.putString(MediaMetadataRetriever.METADATA_KEY_ALBUM, commonUtility.getAlbum());
		metadataEditor.putString(MediaMetadataRetriever.METADATA_KEY_ARTIST, commonUtility.getAlbum());
		metadataEditor.putString(MediaMetadataRetriever.METADATA_KEY_TITLE, commonUtility.getSongTitle());

		metadataEditor.putBitmap(RemoteControlClient.MetadataEditor.BITMAP_KEY_ARTWORK, getAlbumArtBitmap());
		metadataEditor.apply();
		audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
	}


	public  void cancelNotification()
	{
		notificationManager.cancel(1458);

	}


	public void updateprogressbar() {
	// TODO Auto-generated method stub
    mHandler.postDelayed(mUpdateTimeTask, 100);        

}

	private Runnable mUpdateTimeTask = new Runnable() {
	   public void run() {
		   long totalDuration = musicPlayer.getDuration();
		   long currentDuration = musicPlayer.getCurrentPosition();

		   String totalDuration1=utils.milliSecondsToTimer(musicPlayer.getDuration());
		   commonUtility.setTotalDuration(totalDuration1);
		   String currentPosition=utils.milliSecondsToTimer(musicPlayer.getCurrentPosition());
		   commonUtility.setCurrentDuration(currentPosition);
		   // time.setText(""+utils.milliSecondsToTimer(totalDuration));
		   // Displaying time completed playing
		  //tim.setText(""+utils.milliSecondsToTimer(currentDuration));
		   
		   // Updating progress bar
		   int progress = (int)(utils.getProgressPercentage(currentDuration, totalDuration));
		   UiUpdater.mSeekListener.updateSeekBar(progress);
		   // Running this thread after 100 milliseconds
	       mHandler.postDelayed(this, 100);
	   }
	};


	public Bitmap getAlbumArtBitmap()
	{
		Bitmap bm = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		try{

			ParcelFileDescriptor pfd = getApplicationContext().getContentResolver().openFileDescriptor(commonUtility.geturi(), "r");
			if (pfd != null){
				FileDescriptor fd = pfd.getFileDescriptor();
				bm = BitmapFactory.decodeFileDescriptor(fd, null, options);
				pfd = null;
				fd = null;
			}
		} catch(Error ee){}
		catch (Exception e) {}
		return bm;
	}
	public int getRandomNumber()
	{
		Random r = new Random();
		int number = r.nextInt(mCursor.getCount() - 0) + 0;
		Log.v("number",""+number);
		return number;
	}
	@Override
	public void onCompletion(MediaPlayer mediaPlayer) {
		nextSong();
	}

	@Override
	public void onAudioFocusChange(int i) {

	}

	static class PlaybackStub extends PlayBackUtility.Stub
	{
		private WeakReference<MusicPlaybackService> playbackStubWeakReference;
		PlaybackStub(MusicPlaybackService service)
		{
			playbackStubWeakReference=new WeakReference<MusicPlaybackService>(service);
		}
		@Override
		public void pauseSong() throws RemoteException {
			playbackStubWeakReference.get().pauseSong();
		}

		@Override
		public void nextSong() throws RemoteException {
			playbackStubWeakReference.get().nextSong();
		}

		@Override
		public void playSong(int type, int position, String playlistId) throws RemoteException {
			playbackStubWeakReference.get().setPlaySong(type,position,playlistId);
		}

		@Override
		public void previousSong() throws RemoteException {
			playbackStubWeakReference.get().previousSong();

		}

		@Override
		public void seekSong(int progress) throws RemoteException {
			playbackStubWeakReference.get().seekSong(progress);
		}

		@Override
		public void stopSong() throws RemoteException {
			playbackStubWeakReference.get().stopSong();
		}

	}
	private final IBinder playbackBinder=new PlaybackStub(this);

}
