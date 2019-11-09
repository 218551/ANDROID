package com.redstone.myguardian;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
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
import android.telephony.SmsManager;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.pm.PackageManager;
import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.app.ProgressDialog;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Console;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private String USERNAME;
    private Integer USER_ID;
    LocationManager locationManager;
    Button btnTrackView;
    Button btnSmsSend;
    Button btnAddFriend;

    private LocationListener listener;
    private int FINE_LOCATION_PERMISSION_CODE=1;
    private int COARSE_LOCATION_PERMISSION_CODE=2;
    private int SMS_PERMISSION_CODE = 3;
    private int READ_PHONE_STATE_PERMISSION_CODE=4;
    private GoogleMap mapGoogle;
    private Marker newmarker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        USER_ID= this.getIntent().getExtras().getInt("USER_ID");
        USERNAME = this.getIntent().getExtras().getString("USERNAME");
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        btnTrackView = (Button) findViewById(R.id.button11);
        btnSmsSend = (Button) findViewById(R.id.button12);
        btnAddFriend = (Button) findViewById(R.id.button13);
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                     requestFineLocationPermission();
        }

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    requestCoarseLocationPermission();
        }





        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude() );
                mapGoogle.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mapGoogle.animateCamera(CameraUpdateFactory.zoomTo(14));
                if(newmarker==null)
                newmarker = mapGoogle.addMarker(new MarkerOptions().position(latLng).title(location.getLongitude() + " " + location.getLatitude()).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
                newmarker.setPosition(latLng);
                newmarker.setTitle(location.getLongitude() + " " + location.getLatitude());
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


        btnTrackView.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(getApplicationContext(), FollowActivity.class);
                myIntent.putExtra("USERNAME",USERNAME);
                startActivity(myIntent);

            }
        });

        btnSmsSend.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {

                    if(ContextCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.READ_PHONE_STATE)==PackageManager.PERMISSION_GRANTED) {
                        getNumbers();
                    }else {
                        requestReadPhoneStatePermission();
                    }
                } else {
                    requestSendSmsPermission();

                }

            }
        });

        btnAddFriend.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText addEditText = new EditText(MainActivity.this);
                AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Add/Delete friend")
                        .setMessage("Type friends username bellow")
                        .setView(addEditText)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                addNewFriend(addEditText.getText().toString());
                            }
                        })
                        .setNegativeButton("DELETE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                delFriend(addEditText.getText().toString());
                            }
                        })
                        .setNeutralButton("Cancel", null)
                        .create();
                dialog.show();

            }
        });

        locationManager.requestLocationUpdates("gps", 5000, 0, listener);
        getLocation();
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

    private void requestReadPhoneStatePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_PHONE_STATE)) {

            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed because of read phone state")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.READ_PHONE_STATE}, READ_PHONE_STATE_PERMISSION_CODE);
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
                    new String[]{Manifest.permission.READ_PHONE_STATE}, READ_PHONE_STATE_PERMISSION_CODE);
        }
    }

    private void requestSendSmsPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.SEND_SMS)) {

            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed because of send sms")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_CODE);
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
                    new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_CODE);
        }
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == FINE_LOCATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "FINE_LOCATION Permission DENIED", Toast.LENGTH_LONG).show();
            }
        }
        if (requestCode == COARSE_LOCATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "COARSE_LOCATION Permission DENIED", Toast.LENGTH_LONG).show();
            }
        }
        if (requestCode == SMS_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "SEND_SMS Permission DENIED", Toast.LENGTH_LONG).show();
            }
        }
        if (requestCode == READ_PHONE_STATE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "READ_PHONE_STATE Permission DENIED", Toast.LENGTH_LONG).show();
            }
        }
    }


    public void getLocation() {
        final RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, DbConstants.URL_GET,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            LatLng latLng = new LatLng(jsonObject.getDouble("geowidth"), jsonObject.getDouble("geolength") );
                            mapGoogle.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                            mapGoogle.animateCamera(CameraUpdateFactory.zoomTo(14));
                            if(newmarker==null)
                                newmarker = mapGoogle.addMarker(new MarkerOptions().position(latLng).title(latLng.toString()).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
                            newmarker.setPosition(latLng);
                            newmarker.setTitle(latLng.toString());
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
                        Toast.makeText(getApplicationContext(),"Connection error." ,Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                        requestQueue.stop();
                    }
                }

        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("username", USERNAME);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }


    void updateLocation(Location xy) {

        final Double x = xy.getLongitude();
        final Double y = xy.getLatitude();
        final RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
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
                params.put("userid",Integer.toString(USER_ID));
                return params;
            }
        };
        requestQueue.add(stringRequest);

    }

    void addNewFriend(final String username2) {
        final RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, DbConstants.URL_ADDFRIEND,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getApplicationContext(),"Connection failure" ,Toast.LENGTH_LONG).show();
                        error.printStackTrace();
                        requestQueue.stop();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("username1", USERNAME);
                params.put("username2", username2);
                return params;
            }
        };
        requestQueue.add(stringRequest);


    }

    void delFriend(final String username2) {
        final RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, DbConstants.URL_DELFRIEND,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getApplicationContext(),"Connection failure" ,Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                        requestQueue.stop();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("username1", USERNAME);
                params.put("username2", username2);
                return params;
            }
        };
        requestQueue.add(stringRequest);


    }

    void getNumbers() {
        final RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, DbConstants.URL_GETNUMBERS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray numbersArray = jsonObject.getJSONArray("phoneNumbers");
                            for(int i=0;i<numbersArray.length();i+=1)
                            {
                                String phoneNumber = numbersArray.getString(i);
                                SmsManager.getDefault().sendTextMessage(phoneNumber, null, "JESTEM W NIEBEZPIECZENSTWIE!", null, null);
                            }
                            Toast.makeText(getApplicationContext(), "Alarm messages sent.", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getApplicationContext(),"Connection failure." ,Toast.LENGTH_LONG).show();
                        error.printStackTrace();
                        requestQueue.stop();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("username", USERNAME);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }
}


