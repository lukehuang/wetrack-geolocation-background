package studio.wetrack.geolocation;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

import org.apache.cordova.CallbackContext;

/**
 * Created by zhonghui on 16/5/24.
 */
public class GeolocationService extends Service implements LocationListener{
    private LocationManager mLocationManager;
    private LocationProvider mLocationProvider;
    private GeolocationServiceBinder mBinder = new GeolocationServiceBinder();
    private CallbackContext mCallbackContext;

    public class GeolocationServiceBinder extends Binder {

        public GeolocationService getService() {
            return GeolocationService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        GeolocationManager.getInstance(getApplicationContext()).uninitGPSListener();
        return super.onUnbind(intent);
    }

    public void setCallbackContext(CallbackContext callbackContext) {
        mCallbackContext = callbackContext;
        GeolocationManager.getInstance(getApplicationContext()).initGPSListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onLocationChanged(Location location) {
        LocationCommiter.commit(location , mCallbackContext , this);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        LocationCommiter.commit(GeolocationManager.getInstance(this).getLocation() , mCallbackContext , this);

    }

    @Override
    public void onProviderEnabled(String provider) {
        LocationCommiter.commit(GeolocationManager.getInstance(this).getLocation() , mCallbackContext , this);
    }

    @Override
    public void onProviderDisabled(String provider) {
        LocationCommiter.commit(GeolocationManager.getInstance(this).getLocation() , mCallbackContext , this);
    }

}
