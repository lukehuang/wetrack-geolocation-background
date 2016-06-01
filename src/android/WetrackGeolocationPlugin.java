package studio.wetrack.geolocation;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import studio.wetrack.geolocation.GeolocationService.GeolocationServiceBinder;
/**
 * Created by zhonghui on 16/5/25.
 */
public class WetrackGeolocationPlugin extends CordovaPlugin {
    public static final String NEED_START_WATCH = "need_start_watch";
    public static final String NEED_UPLOAD = "need_upload";
    public static final String UPLOAD_INFO = "upload_info";
    private CallbackContext mCallbackContext;

    public WetrackGeolocationPlugin() {
    }

    /**
     * Sets the context of the Command. This can then be used to do things like
     * get file paths associated with the Activity.
     *
     * @param cordova The context of the main Activity.
     * @param webView The CordovaWebView Cordova is running in.
     */
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
    }

    /**
     * Executes the request and returns PluginResult.
     *
     * @param action            The action to execute.
     * @param args              JSONArry of arguments for the plugin.
     * @param callbackContext   The callback id used when calling back into JavaScript.
     * @return                  True if the action was valid, false if not.
     */
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if ("getCurrentPosition".equals(action)) {
            this.getCurrentPosition(callbackContext);
            return true;
        }
        if ("watchPosition".equals(action)) {
            this.watchPosition(callbackContext);
            return true;
        }
        if ("stopWatch".equals(action)) {
            this.stopWatch(callbackContext);
            return true;
        }
        return false;  // Returning false results in a "MethodNotFound" error.
    }

    private void getCurrentPosition(CallbackContext callbackContext) {
//        Toast.makeText(this.cordova.getActivity().getApplicationContext() , "getCurrentPosition").show(Toast.LENGTH_SHORT);
        Location location = GeolocationManager.getInstance(this.cordova.getActivity().getApplicationContext()).getLocation();
        LocationCommiter.commit(location , callbackContext , this.cordova.getActivity().getApplicationContext());
    }

    private void watchPosition(CallbackContext callbackContext) {
        Context applicationContext = this.cordova.getActivity().getApplicationContext();
        SharedPreferences iSharedPreference = applicationContext.getSharedPreferences(WetrackGeolocationPlugin.class.getSimpleName() , Context.MODE_PRIVATE);
        SharedPreferences.Editor iEditor = iSharedPreference.edit();
        iEditor.putBoolean(NEED_START_WATCH , true);
        iEditor.commit();
        mCallbackContext = callbackContext;
        applicationContext.bindService(new Intent(applicationContext , GeolocationService.class) , mConnection , Context.BIND_AUTO_CREATE);
    }

    private void stopWatch(CallbackContext callbackContext) {
        Context applicationContext = this.cordova.getActivity().getApplicationContext();
        SharedPreferences iSharedPreference = applicationContext.getSharedPreferences(WetrackGeolocationPlugin.class.getSimpleName() , Context.MODE_PRIVATE);
        SharedPreferences.Editor iEditor = iSharedPreference.edit();
        iEditor.putBoolean(NEED_START_WATCH , false);
        iEditor.commit();
        applicationContext.unbindService(mConnection);
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            GeolocationServiceBinder binder = (GeolocationServiceBinder) service;
            GeolocationService binderService = binder.getService();
            binderService.setCallbackContext(mCallbackContext);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

}
