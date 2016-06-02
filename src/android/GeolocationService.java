package studio.wetrack.geolocation;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import org.apache.cordova.CallbackContext;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import studio.wetrack.geolocation.HttpConnectionWithArgs.HttpResultListener;

/**
 * Created by zhonghui on 16/5/24.
 */
public class GeolocationService extends Service implements LocationListener , HttpResultListener{
    private LocationManager mLocationManager;
    private LocationProvider mLocationProvider;
    private GeolocationServiceBinder mBinder = new GeolocationServiceBinder();
    private CallbackContext mWatchPopsitionCallbackContext;
    private ArrayList<JSONObject> mLocationList = new ArrayList<JSONObject>();
    private TimerTask mTimerTask = new TimerTask() {
        @Override
        public void run() {
            uploadLocation();
        }
    };
    private Timer mTimer = new Timer();

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

    public void setWatchPositionCallbackContext(CallbackContext callbackContext) {
        mWatchPopsitionCallbackContext = callbackContext;
        GeolocationManager.getInstance(getApplicationContext()).initGPSListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onLocationChanged(Location location) {
        Log.e(GeolocationService.class.getSimpleName(),"onLocationChanged");
        JSONObject position = LocationCommiter.commit(location , mWatchPopsitionCallbackContext , this);
        mLocationList.add(position);
        uploadLocation();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        JSONObject position = LocationCommiter.commit(GeolocationManager.getInstance(this).getLocation() , mWatchPopsitionCallbackContext , this);
        mLocationList.add(position);
        uploadLocation();
    }

    @Override
    public void onProviderEnabled(String provider) {
        JSONObject position = LocationCommiter.commit(GeolocationManager.getInstance(this).getLocation() , mWatchPopsitionCallbackContext , this);
        mLocationList.add(position);
        uploadLocation();
    }

    @Override
    public void onProviderDisabled(String provider) {
        JSONObject position = LocationCommiter.commit(GeolocationManager.getInstance(this).getLocation() , mWatchPopsitionCallbackContext , this);
        mLocationList.add(position);
        uploadLocation();
    }

    private void uploadLocation() {
        SharedPreferences iSharedPreference = this.getSharedPreferences(WetrackGeolocationPlugin.class.getSimpleName() , Context.MODE_PRIVATE);
        boolean needUpload = iSharedPreference.getBoolean(WetrackGeolocationPlugin.NEED_UPLOAD , false);
        SharedPreferences.Editor iEditor = iSharedPreference.edit();
        if(!needUpload) {
            mTimer.cancel();
            mTimerTask.cancel();
            iEditor.putBoolean(WetrackGeolocationPlugin.UPLOAD_STATE , false);
            iEditor.commit();
            return;
        } else {
            boolean isRuning = iSharedPreference.getBoolean(WetrackGeolocationPlugin.UPLOAD_STATE , false);
            if(!isRuning) {
                mTimer.schedule(mTimerTask , 6000);
                iEditor.putBoolean(WetrackGeolocationPlugin.UPLOAD_STATE , true);
                iEditor.commit();
            }
        }
        JSONObject position = new JSONObject();
        if(mLocationList == null || mLocationList.size() == 0) {
            position = LocationCommiter.commit(GeolocationManager.getInstance(this).getLocation() , mWatchPopsitionCallbackContext , this);
        } else {
            position = mLocationList.get(0);
        }
        uploadLocationNow(position);
    }

    private void uploadLocationNow(JSONObject location) {
        SharedPreferences iSharedPreference = this.getSharedPreferences(WetrackGeolocationPlugin.class.getSimpleName() , Context.MODE_PRIVATE);
        String info = iSharedPreference.getString(WetrackGeolocationPlugin.UPLOAD_INFO , "");
        if(info.equals("")) {
            mUploadPopsitionCallbackContext.error("error , upload info is missing");
            return;
        }
        UploadTask task = new UploadTask();
        task.setHttpResultListener(this);
        task.execute(info , location.toString());
    }

    @Override
    public void onSuccess(String result) {
        mUploadPopsitionCallbackContext.success(result);
        Toast.makeText(this , result + " , Post : " + mLocationList.get(0).toString() , Toast.LENGTH_SHORT).show();
        mLocationList.remove(0);
        if(mLocationList == null || mLocationList.size() == 0) {
            return;
        }
        uploadLocationNow(mLocationList.get(0));
    }

    @Override
    public void onError(String result) {
        uploadLocationNow(mLocationList.get(0));
    }

}
