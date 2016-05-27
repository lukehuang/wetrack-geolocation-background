package studio.wetrack.geolocation;

import android.location.Location;
import android.widget.Toast;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zhonghui on 16/5/25.
 */
public class WetrackGeolocationPlugin extends CordovaPlugin {

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
            this.watchPosition(args , callbackContext);
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
        JSONObject locationInfo = new JSONObject();
        try {
            if(location != null) {
                locationInfo.put("latitude" , location.getLatitude());
                locationInfo.put("longitude" , location.getLongitude());
            } else {
                locationInfo.put("latitude" , 0);
                locationInfo.put("longitude" , 0);
            }
        } catch (org.json.JSONException e) {

            callbackContext.error("error");
            Toast.makeText(this.cordova.getActivity().getApplicationContext() , locationInfo.toString()).show(Toast.LENGTH_SHORT);
        }

        Toast.makeText(this.cordova.getActivity().getApplicationContext() , locationInfo.toString()).show(Toast.LENGTH_SHORT);
        callbackContext.success(locationInfo.toString());
    }

    private void watchPosition(JSONArray args , CallbackContext callbackContext) {

    }

    private void stopWatch(CallbackContext callbackContext) {

    }
}
