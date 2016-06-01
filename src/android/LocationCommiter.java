package studio.wetrack.geolocation;

import android.content.Context;
import android.location.Location;
import android.widget.Toast;

import org.apache.cordova.CallbackContext;
import org.json.JSONObject;

/**
 * Created by zhonghui on 16/6/1.
 */
public class LocationCommiter {
    public static void commit(Location location , CallbackContext callbackContext , Context context) {

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
            Toast.makeText(context , locationInfo.toString() , Toast.LENGTH_SHORT).show();
        }

        Toast.makeText(context , locationInfo.toString() , Toast.LENGTH_SHORT).show();
        callbackContext.success(locationInfo.toString());
    }
}
