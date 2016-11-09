package cse403.finderskeepers;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import cse403.finderskeepers.data.UserInfoHolder;
import java.util.List;

/**
 * Created by Jared on 10/27/2016.
 */

public class UserAPIService extends Service {

    @Override
    public void onCreate() {
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

    public enum ListType {
        WISHLIST, INVENTORY
    }
    public List<AddableItem> getUserItems(ListType type, UserInfoHolder user){
        return null;
    }
}
