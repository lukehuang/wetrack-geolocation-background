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

public class GeolocationManager implements LocationListener{
    private static GeolocationManager mInstance;
    private Context mContext;
    private static LocationManager mLocationManager;
    private static Criteria mLocationCriteria;
    private static Location mLocation;
    private String mLocationProvider;
    private static final int SECOND = 1000;
    private static final int KM = 1000;
    private GPSLocationListener mListener;

    public interface GPSLocationListener {
        public void onLocationChanged(Location location);
    }

    private GeolocationManager(Context context) {
        mContext = context;
        mLocationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        mLocationProvider = mLocationManager.getBestProvider(getCriteria(), true);
    }

    public void initGPSListener(GPSLocationListener listener){
        Log.e(GeolocationManager.class.getSimpleName(),"initGPSListener");
        mListener = listener;
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, SECOND * 30 , KM / 200 , this);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, SECOND * 30 , KM / 200, this);
    }

    public void uninitGPSListener() {
        Log.e(GeolocationManager.class.getSimpleName(),"uninitGPSListener");
        mLocationManager.removeUpdates(this);
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

    @Override
    public void onLocationChanged(Location location) {
        Log.e(GeolocationManager.class.getSimpleName(),"onLocationChanged");
        mListener.onLocationChanged(location);
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.e(GeolocationManager.class.getSimpleName(),"onProviderEnabled");
        mListener.onLocationChanged(getLocation());
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.e(GeolocationManager.class.getSimpleName(),"onProviderDisabled");
        mListener.onLocationChanged(null);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.e(GeolocationManager.class.getSimpleName(),"onStatusChanged");
        if(status == LocationProvider.AVAILABLE) {
            mListener.onLocationChanged(getLocation());
        } else {
            mListener.onLocationChanged(null);
        }
    }

}
