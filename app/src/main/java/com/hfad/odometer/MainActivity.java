package com.hfad.odometer;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends Activity {
    private OdometerService odometerService;
    private boolean bound = false; //whether or not the service is bound to the activity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        watchMeters();
    }

    //1. CREATE A SERVICE CONNECTION
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            OdometerService.OdometerBinder odometerBinder = (OdometerService.OdometerBinder) binder;
            odometerService = odometerBinder.getOdometer();
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bound = false;
        }
    };

    //2 CONNECT MAIN ACTIVITY TO ODOMETERSERVICE WITH INTENT, BIND TO SERVICE WHEN ACTIVITY STARTS
    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, OdometerService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE); //CREATE IF IT DOESN'T EXIST YET
    }

    //5 ODOMETER GETMETERS GETS CALLED DIRECTLY EVERY SECOND
    private void watchMeters() {
        final TextView distanceView = (TextView) findViewById(R.id.tv_location);
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                double distance = 0.0;
                if (odometerService != null) {
                    distance = odometerService.getDistanceInMeters();
                }
                String distanceString = String.format("%1$, .2f meters", distance);
                distanceView.setText(distanceString);
                handler.postDelayed(this, 1000); //run every second
            }
        });
    }

    //6. DISCONNECT MAIN ACTIVITY TO ODOMETERSERVICE, UNBIND FROM SERVICE WHEN ACTIVITY STOPS
    @Override
    protected void onStop() {
        super.onStop();
        if (bound) {
            unbindService(connection);
            bound = false;
        }
    }

}
