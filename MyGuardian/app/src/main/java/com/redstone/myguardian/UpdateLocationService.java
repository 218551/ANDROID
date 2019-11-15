package com.redstone.myguardian;

import android.Manifest;
import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.tv.TvContract;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.nio.channels.Channel;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class UpdateLocationService extends Service {
    private static Timer timer = new Timer();
    LocationManager locationManager;
    private LocationListener listener;
    String USERNAME;

    public UpdateLocationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(UpdateLocationService.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            onDestroy();
        }

        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude() );
                updateLocation(location);

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };
        locationManager.requestLocationUpdates("gps", 5000, 0, listener);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        USERNAME = intent.getStringExtra("USERNAME");
        Notification notification = new NotificationCompat.Builder(this, "redstoneChannel")
                .setContentTitle("title")
                .setContentText("text")
                .setSmallIcon(R.drawable.my_guardian_logo)
                .build();
        startForeground(2001,notification);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    void updateLocation(Location xy) {
        final Double x = xy.getLongitude();
        final Double y = xy.getLatitude();
        final RequestQueue requestQueue = Volley.newRequestQueue(UpdateLocationService.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, DbConstants.URL_ADD,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            requestQueue.stop();
                        }catch(Exception exc)
                        {
                            exc.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        requestQueue.stop();
                    }
                }

        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("geolength",x.toString());
                params.put("geowidth",y.toString());
                params.put("username", USERNAME);
                return params;
            }
        };
        requestQueue.add(stringRequest);

    }

}
