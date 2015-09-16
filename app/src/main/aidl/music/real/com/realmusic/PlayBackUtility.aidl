// PlayBackUtility.aidl
package music.real.com.realmusic;

// Declare any non-default types here with import statements

interface PlayBackUtility {

    void playSong(int type,int position,String playlistId);
    void pauseSong();
    void nextSong();
    void previousSong();
    void seekSong(int progress);
    void stopSong();
    void savedDatas(int type,int position,String playlistId);
}
