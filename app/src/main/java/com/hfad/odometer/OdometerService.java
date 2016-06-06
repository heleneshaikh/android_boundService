package com.hfad.odometer;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;

public class OdometerService extends Service {
    private final IBinder binder = new OdometerBinder();
    private static Location lastLocation = null;
    private static double distanceInMeters;
    private LocationManager locationManager;
    private LocationListener listener;

    public OdometerService() {
    }

    //3 SERVICE STARTS AND ITS ONBIND METHOD IS CALLED WITH A COPY OF THE INTENT FROM MAINACTIVITY
    @Override
    public IBinder onBind(Intent intent) {
       return binder;
    }

    public static double getDistanceInMeters() {
        return distanceInMeters;
    }

    //3A MEANWHILE LISTEN
    @Override
    public void onCreate() {
        super.onCreate();
        //CREATE LOCATION LISTENER
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (lastLocation == null) {
                    lastLocation = location;
                }
                distanceInMeters += location.distanceTo(lastLocation);
                lastLocation = location;
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        //REGISTER LOCATION LISTENER TO ANDROID'S LOCATION SERVICE
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, listener);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    //4 MAINACTIVITY GETS A REFERENCE TO ODOMETERSERVICE FROM THE BINDER AND  STARTS TO USE THE SERVICE DIRECTLY
    public class OdometerBinder extends Binder {
        OdometerService getOdometer() {
            return OdometerService.this;
        }
    }

    //STOP SENDING LOCATION UPDATES WHEN SERVICE IS DESTROYED
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (locationManager != null && listener != null) {
            try {
                locationManager.removeUpdates(listener);
            } catch (SecurityException e) {
                e.printStackTrace();
            }
            locationManager = null;
            listener = null;
        }
    }

}
