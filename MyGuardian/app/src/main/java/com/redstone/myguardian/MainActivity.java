package com.redstone.myguardian;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.content.pm.PackageManager;
import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private int USER_ID;

    LocationManager locationManager;
    Button btnTrackView;
    Button btnSmsSend;
    Button btnRefresh;
    TextView coordinates;
    private LocationListener listener;
    private int FINE_LOCATION_PERMISSION_CODE=1;
    private int COARSE_LOCATION_PERMISSION_CODE=2;
    private GoogleMap mapGoogle;
    private Marker newmarker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         USER_ID= this.getIntent().getExtras().getInt("USER_ID");
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        btnTrackView = (Button) findViewById(R.id.button11);
        coordinates = (TextView) findViewById(R.id.textView4);
        btnRefresh = (Button) findViewById(R.id.button14);
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(MainActivity.this, "You have already granted FINE_LOCATION permission!",
                    Toast.LENGTH_SHORT).show();
        }else {
            requestFineLocationPermission();
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(MainActivity.this, "You have already granted FINE_LOCATION permission!",
                    Toast.LENGTH_SHORT).show();
        }else {
            requestCoarseLocationPermission();
        }

        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                coordinates.setText( location.getLongitude() + " " + location.getLatitude());
                LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude() );
                mapGoogle.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mapGoogle.animateCamera(CameraUpdateFactory.zoomTo(14));
                if(newmarker==null)
                newmarker = mapGoogle.addMarker(new MarkerOptions().position(latLng).title("Your position").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
                newmarker.setPosition(latLng);
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

                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };


        btnRefresh.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "ACCESS_FINE_LOCATION permission is granted!",
                            Toast.LENGTH_SHORT).show();
                    locationManager.requestLocationUpdates("gps", 60000, 0, listener);

                } else {
                    requestFineLocationPermission();

                }

            }
        });

        btnTrackView.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(getApplicationContext(), FollowActivity.class);
                myIntent.putExtra("USER_ID",USER_ID);
                startActivity(myIntent);

            }
        });


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapGoogle=googleMap;

    }
    private void requestFineLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {

            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed because of get your location")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_PERMISSION_CODE);
                        }

                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_PERMISSION_CODE);
        }
    }

    private void requestCoarseLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)) {

            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed because of get your location")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, COARSE_LOCATION_PERMISSION_CODE);
                        }

                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, COARSE_LOCATION_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == FINE_LOCATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "FINE_LOCATION Permission GRANTED", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "FINE_LOCATION Permission DENIED", Toast.LENGTH_LONG).show();
            }
        }
        if (requestCode == COARSE_LOCATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "COARSE_LOCATION Permission GRANTED", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "COARSE_LOCATION Permission DENIED", Toast.LENGTH_LONG).show();
            }
        }
    }

    void updateLocation(Location xy) {

       // final int user_ID = user_id;
        final Double x = xy.getLongitude();
        final Double y = xy.getLatitude();
        final RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, DbConstants.URL_ADD,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                            requestQueue.stop();
                        }catch(JSONException exc)
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
                params.put("userid",Integer.toString(USER_ID));
                return params;
            }
        };
        requestQueue.add(stringRequest);

    }

}


