package studio.wetrack.geolocation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

/**
 * Created by zhonghui on 16/5/24.
 */
public class WakeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences iSharedPreference = context.getSharedPreferences(WetrackGeolocationPlugin.class.getSimpleName() , Context.MODE_PRIVATE);
        if(iSharedPreference.getBoolean(WetrackGeolocationPlugin.NEED_START_WATCH , false)) {
            context.startService(new Intent(context,GeolocationService.class));
        }
    }
}
