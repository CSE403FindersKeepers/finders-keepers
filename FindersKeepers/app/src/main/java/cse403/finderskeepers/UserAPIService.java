package cse403.finderskeepers;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import cse403.finderskeepers.data.FindersKeepersUserInfo;

/**
 * Created by Jared on 10/27/2016.
 */

public class UserAPIService extends Service {

    public FindersKeepersUserInfo userInfo;

    @Override
    public void onCreate() {
        this.userInfo = new FindersKeepersUserInfo("username");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
