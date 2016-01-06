package com.trialvynscloudup.services;

import java.io.FileDescriptor;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.nfc.Tag;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.os.PowerManager;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.RemoteViews;

import music.real.com.realmusic.PlayBackUtility;

import com.trialvynscloudup.R;
import com.trialvynscloudup.activities.PlayBackActivity;
import com.trialvynscloudup.receiver.NotificationBroadcast;
import com.trialvynscloudup.utilities.CommonUtility;
import com.trialvynscloudup.utilities.LauncherApplication;
import com.trialvynscloudup.utilities.UiUpdater;
import com.trialvynscloudup.utilities.Utilities;

public class MusicPlaybackService extends Service implements OnCompletionListener,OnAudioFocusChangeListener,MediaPlayer.OnErrorListener {

	private static String TAG="MUSICPLAYBACKSERVICE";
	public static MediaPlayer musicPlayer =new MediaPlayer();
	String name;
	public static int pos;
	boolean repeat=false;
	private Handler mHandler=new Handler();

	private Utilities utils=new Utilities();
	private static Cursor mCursor;
	private static int  trackPosition;

	private final int TYPE_TRACKS=0;
	private final int TYPE_ALBUMS=1;
	private final int TYPE_ARTIST=2;
	private final int TYPE_GENRE=3;
	private final int TYPE_PLAYLIST=4;
	private final int TYPE_FOLDERS=5;

	private final int TYPE_SUGGESTED=7;
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

	private SharedPreferences sharedPreferences;
	SharedPreferences.Editor editor;

	RemoteViews notificationViews;

	private final int REPEAT_OFF=0;
	private final int REPEAT_ONE=1;
	private final int REPEAT_ALL=2;
	private final int SHUFFLE_OFF=0;
	private final int SHUFFLE_ON=1;
	PendingIntent pendingIntent;


	private boolean restoreState=false;

	List<Integer> randomNumberList;

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
		Intent intent=new Intent(this,PlayBackActivity.class);
		intent.putExtra("type", 6);
		intent.putExtra("position", 2);
		 pendingIntent=PendingIntent.getActivity(this,0,intent,0);


		// Make sure the media player will acquire a wake-lock while
		// playing. If we don't do that, the CPU might go to sleep while the
		// song is playing, causing playback to stop.

		musicPlayer.setWakeMode(MusicPlaybackService.this, PowerManager.PARTIAL_WAKE_LOCK);


		RegisterRemoteClient();
		if (musicPlayer.isPlaying())
		{
			remoteControlClient.setPlaybackState(RemoteControlClient.PLAYSTATE_PLAYING);

		}
		else
		{
			remoteControlClient.setPlaybackState(RemoteControlClient.PLAYSTATE_PAUSED);

		}
		sharedPreferences=getSharedPreferences("SONGINFO",MODE_PRIVATE);
		editor=sharedPreferences.edit();
		randomNumberList = new ArrayList<Integer>();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {


		return START_STICKY;

	}


	public void mediaPlayBack(String datapath)
	{

		musicPlayer.reset();
		musicPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

		try {
			musicPlayer.setDataSource(datapath);
			musicPlayer.prepare();
			musicPlayer.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		musicPlayer.setOnCompletionListener(this);
		musicPlayer.setOnErrorListener(this);

		updateprogressbar();
		setNotificationLayout();
		commonUtility.setPlayerStatus(true);
		UpdateMetadata();
		remoteControlClient.setPlaybackState(RemoteControlClient.PLAYSTATE_PLAYING);
	}

	public void getCursor( int type,String playlistId)
	{
		randomNumberList.clear();
		String order= MediaStore.Audio.Media.TITLE;
		switch (type)
		{

			case TYPE_TRACKS:
				mCursor=getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,null,null,null,order+" COLLATE NOCASE ASC; ");
				break;
			case TYPE_ALBUMS:
				String where= MediaStore.Audio.Media.ALBUM_ID + "=?";
				String wherVal[]={playlistId};
				mCursor=getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,null,where,wherVal,order);
				break;
			case TYPE_ARTIST:
				String whereArtist= MediaStore.Audio.Media.ARTIST_ID + "=?";
				String wherValArtist[]={playlistId};
				mCursor=getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,null,whereArtist,wherValArtist,order);
				break;
			case TYPE_GENRE:
				Uri genreUri=MediaStore.Audio.Genres.Members.getContentUri("external", Long.parseLong(playlistId));
				mCursor=getContentResolver().query(genreUri, null, null,null ,order);

				break;
			case TYPE_PLAYLIST:

				long id=Long.parseLong(playlistId);
				Uri contentUri=MediaStore.Audio.Playlists.Members.getContentUri("external", id);
				mCursor=getContentResolver().query(contentUri, null, null,null ,order);
				break;

			case TYPE_FOLDERS:

/*				String wherFolder=MediaStore.Audio.Media.DATA + " like ? "+" and "+MediaStore.Audio.Media.DATA + " not like ?";
				String whereValue[]=new String[]{"%"+ playlistId +"%","%"+ commonUtility.getSubFolderName() +"%"};
	*/			folderSelectionQuery(playlistId);
				break;
			case TYPE_SUGGESTED:
				mCursor=getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,null,null,null,  MediaStore.Audio.Media.DATE_ADDED +" DESC"+" LIMIT 25");
				trackEvent();
				break;
		}

	}
	public void folderSelectionQuery(String playlistId)
	{
		//Build the where clause
		StringBuilder where=new StringBuilder();

		//first add in all values from the exclusion array

		for (int i=0;i<commonUtility.getSubFolderList().size();i++)
		{
			where.append(MediaStore.Audio.Media.DATA + " not like ? ");
			where.append(" and ");
		}
		//then add in the final like clause, e.g. the folder the user selected

		where.append(MediaStore.Audio.Media.DATA + " like ? ");
		String selection=where.toString();


		//Build the arguments.  the array is set to the size of the exlcusion list, plus 1 for the fodler selected

		String[] dirs=new String[commonUtility.getSubFolderList().size()+1];
		//first add in a value for each excluded folder

		for (int i=0;i<commonUtility.getSubFolderList().size();i++)
		{
			dirs[i]="%"+commonUtility.getSubFolderList().get(i)+"%";

		}
		//add the argument value for the like element of the query

		dirs[commonUtility.getSubFolderList().size()]="%"+playlistId+"%";


		//start building the cursor
		/*	Saving foldername and subfolder names	*/
		saveArrayList(commonUtility.getSubFolderList());
		String order1= MediaStore.Audio.Media.DATA;
		mCursor=getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,null,selection,dirs,order1+" COLLATE NOCASE ASC; ");

	}

	public String getDatapath() {
		editor.putInt("trackposition", trackPosition);
		editor.commit();
		String dataPath = null;
		if (mCursor.getCount() > 0) {
			mCursor.moveToPosition(trackPosition);
			dataPath = mCursor.getString(mCursor.getColumnIndex(MediaStore.Audio.Media.DATA));
			String songTitle = mCursor.getString(mCursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
			String albumName = mCursor.getString(mCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
			String artistName = mCursor.getString(mCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
			int albumId = mCursor.getInt(mCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
			Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
			Uri uriArtWork = ContentUris.withAppendedId(sArtworkUri, albumId);
			UiUpdater.mInfoListener.updateSongInfo(songTitle, albumName, artistName, uriArtWork,dataPath);
			commonUtility.setSongTitle(songTitle);
			commonUtility.setAlbumName(albumName);
			commonUtility.setAlbumArtUri(uriArtWork);
		}
		return dataPath;
	}
	public void setPlaySong(int type,int position,String playlistId)
	{
		trackPosition=position;
		getCursor(type, playlistId);
		editor.putInt("cursortype", type);
		editor.putString("playlistId", playlistId);
		editor.commit();
		mediaPlayBack(getDatapath());

	}
	public void pauseSong(int pauseType)
	{

		if (musicPlayer!=null) {

			if (musicPlayer.isPlaying()) {
				musicPlayer.pause();
				commonUtility.setPlayerStatus(false);
				remoteControlClient.setPlaybackState(RemoteControlClient.PLAYSTATE_PAUSED);

				switch (pauseType)
				{
					case 0:
						cancelNotification();
						break;
					case 1:
						break;

				}
			} else {
				if (restoreState)
				{
					restoreState=false;
					String dataPath=mCursor.getString(mCursor.getColumnIndex(MediaStore.Audio.Media.DATA));
					mediaPlayBack(dataPath);

					int progress=sharedPreferences.getInt("progress",10);
					seekSong(progress);
				}
				else {
					musicPlayer.start();
				}
				commonUtility.setPlayerStatus(true);
				remoteControlClient.setPlaybackState(RemoteControlClient.PLAYSTATE_PLAYING);
				setNotificationLayout();
			}
		}
		else
		{
			mediaPlayBack(getDatapath());

		}
	}
	public void stopSong()
	{
		if (musicPlayer.isPlaying())
		{
			musicPlayer.pause();
			commonUtility.setPlayerStatus(false);

			remoteControlClient.setPlaybackState(RemoteControlClient.PLAYSTATE_PAUSED);

		}
	}

	public void nextSong()
	{

		if (commonUtility.getRepeatState()==REPEAT_OFF)
		{
			if (mCursor.getCount()-1==trackPosition)
			{

				if (commonUtility.getShuffleState()==SHUFFLE_ON)
				{
					trackPosition=getRandomNumber();
					mediaPlayBack(getDatapath());
				}
			}
			else
			{
				if (commonUtility.getShuffleState()==SHUFFLE_ON)
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
		else if (commonUtility.getRepeatState()==REPEAT_ONE)
		{

			mediaPlayBack(getDatapath());
		}
		else if (commonUtility.getRepeatState()==REPEAT_ALL) {
			if (commonUtility.getShuffleState()==SHUFFLE_ON)
			{
				trackPosition=getRandomNumber();
				mediaPlayBack(getDatapath());
			}
			else
			{
				if (mCursor.getCount()-1==trackPosition)
				{
					trackPosition=0;
				}
				else {
					trackPosition++;
				}
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

	public void restoreCursor(int type,int trackPosition,String playlistId)
	{
		this.trackPosition=trackPosition;
		getCursor(type, playlistId);
		if (mCursor.getCount()>0) {
			mCursor.moveToPosition(trackPosition);
			String songTitle = mCursor.getString(mCursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
			String albumName = mCursor.getString(mCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
			String artistName = mCursor.getString(mCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
			int albumId = mCursor.getInt(mCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
			Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
			Uri uriArtWork = ContentUris.withAppendedId(sArtworkUri, albumId);
			commonUtility.setSongTitle(songTitle);
			commonUtility.setAlbumName(albumName);
			commonUtility.setAlbumArtUri(uriArtWork);
			restoreState = true;
		}
	}

	public void  setNotificationLayout()
	{
		notificationManager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
//		Notification notification=new Notification(R.drawable.ic_launcher,null,System.currentTimeMillis());
		notificationViews=new RemoteViews(getPackageName(),R.layout.custom_notification);
		Notification status = new Notification();
		status.contentView = notificationViews;
		Bitmap bitmap=getAlbumArtBitmap();
		if (bitmap!=null)
		{
			notificationViews.setImageViewBitmap(R.id.imageViewAlbumArt, bitmap);
		}
		else
		{

			notificationViews.setImageViewResource(R.id.imageViewAlbumArt,R.drawable.default_albumart);
		}
		notificationViews.setTextViewText(R.id.textSongName, commonUtility.getSongTitle());
		notificationViews.setTextViewText(R.id.textAlbumName, commonUtility.getAlbum());
		notificationViews.setOnClickPendingIntent(R.id.notificationLayout,pendingIntent);
		status.flags |= Notification.FLAG_AUTO_CANCEL | Notification.FLAG_SHOW_LIGHTS;
		status.icon = R.drawable.ic_launcher;
		setListeners(notificationViews);
		startForeground(1458, status);

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


	public  void cancelNotification()
	{
		stopForeground(true);
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

	public void saveArrayList(ArrayList<String> list)
	{
		Set<String> set=new HashSet<>();
		set.addAll(list);
		editor.putStringSet("folderQuery",set);
		editor.commit();
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
		   // Updating progress bar
		   int progress = (int)(utils.getProgressPercentage(currentDuration, totalDuration));

		   UiUpdater.mSeekListener.updateSeekBar(progress);
		   // Running this thread after 100 milliseconds
		   editor.putInt("progress",progress);
		   editor.commit();
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

	public int getRandomNumber() {

		Random r = new Random();

		int number = 0;


		if (randomNumberList.size()<mCursor.getCount()) {
			do {
				number = r.nextInt(mCursor.getCount() - 0) + 0;

			}
			while (randomNumberList.contains(number));
		}

		else
		{
			randomNumberList.clear();
		}

		randomNumberList.add(number);
		return number;
	}
		/*						Save playBack info in sharedPreference*/
	public void savePlayBackInfo()
	{
		SharedPreferences.Editor editor=sharedPreferences.edit();

	}


	@Override
	public void onCompletion(MediaPlayer mediaPlayer) {
		nextSong();
	}

	@Override
	public void onAudioFocusChange(int i) {

	}

	@Override
	public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {

		nextSong();
		return true;
	}
	public void trackEvent()
	{
		if (CommonUtility.developementStatus)
		{

		}
		else
		{


			LauncherApplication.getInstance().trackEvent("Service", "suggested list","music playing" );
		}
	}


	static class PlaybackStub extends PlayBackUtility.Stub
	{
		private WeakReference<MusicPlaybackService> playbackStubWeakReference;
		PlaybackStub(MusicPlaybackService service)
		{
			playbackStubWeakReference=new WeakReference<MusicPlaybackService>(service);
		}
		@Override
		public void pauseSong(int pauseType) throws RemoteException {
			playbackStubWeakReference.get().pauseSong(pauseType);
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

		@Override
		public void savedDatas(int type, int position, String playlistId) throws RemoteException {
			playbackStubWeakReference.get().restoreCursor(type,position,playlistId);
		}

		@Override
		public void cancelNotification() throws RemoteException {
			playbackStubWeakReference.get().cancelNotification();
		}
	}
	private final IBinder playbackBinder=new PlaybackStub(this);

}
