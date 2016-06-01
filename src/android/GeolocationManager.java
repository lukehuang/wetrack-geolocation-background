package studio.wetrack.geolocation;

import java.util.List;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

public class GeolocationManager{
    private static GeolocationManager mInstance;
    private Context mContext;
    private static LocationManager mLocationManager;
    private static Criteria mLocationCriteria;
    private static Location mLocation;
    private String mLocationProvider;
    private static final int SECOND = 1000;
    private static final int KM = 1000;
    private LocationListener mListener;

    public interface GPSLocationListener {
        public void onLocationChanged(Location location);
    }

    private GeolocationManager(Context context) {
        mContext = context;
        mLocationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        mLocationProvider = mLocationManager.getBestProvider(getCriteria(), true);
    }

    public void initGPSListener(LocationListener listener){
        Log.e(GeolocationManager.class.getSimpleName(),"initGPSListener");
        mListener = listener;
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, SECOND * 60 , KM / 200 , listener);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, SECOND * 60 , KM / 200, listener);
    }

    public void uninitGPSListener() {
        Log.e(GeolocationManager.class.getSimpleName(),"uninitGPSListener");
        mLocationManager.removeUpdates(mListener);
    }

    public static GeolocationManager getInstance(Context context){
        if(mInstance == null){
            mInstance = new GeolocationManager(context);
        }
        return mInstance;
    }

    private Criteria getCriteria(){
        if(mLocationCriteria == null) {
            mLocationCriteria = new Criteria();
            mLocationCriteria.setAccuracy(Criteria.ACCURACY_FINE);
            mLocationCriteria.setAltitudeRequired(false);
            mLocationCriteria.setBearingRequired(false);
            mLocationCriteria.setCostAllowed(true);
            mLocationCriteria.setPowerRequirement(Criteria.POWER_LOW);
        }
        return mLocationCriteria;
    }

    public Location getLocation() {
        Log.e(GeolocationManager.class.getSimpleName(),"getLocation");
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                bestLocation = l;
            }
        }
        return bestLocation;
    }

}
