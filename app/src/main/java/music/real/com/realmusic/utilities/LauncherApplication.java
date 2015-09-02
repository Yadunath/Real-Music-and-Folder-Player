package music.real.com.realmusic.utilities;

import android.app.Application;
import android.util.Log;

/**
 * Created by yedunath on 19/8/15.
 */
public class LauncherApplication extends Application {
    CommonUtility commonUtility;
    @Override
    public void onCreate() {
        super.onCreate();

        commonUtility=new CommonUtility();
        commonUtility.setRepeatState(0);
        commonUtility.setShuffleState(0);

    }
}
