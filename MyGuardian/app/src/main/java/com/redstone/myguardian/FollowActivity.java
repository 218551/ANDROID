package com.redstone.myguardian;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.Toast;
import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
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
import java.util.Timer;
import java.util.TimerTask;

public class FollowActivity extends AppCompatActivity implements OnMapReadyCallback {

    private int USER_ID;
    private int PHONE_NR;
    Button btnRefresh;
    Button btnTrackView;
    Button btnChooseUser;
    private GoogleMap mapGoogle;
    private Marker newmarker;
    private boolean PAUSE_CTRL=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow);
        USER_ID= this.getIntent().getExtras().getInt("USER_ID");
        PHONE_NR = this.getIntent().getExtras().getInt("PHONE_NR");
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        final Context menuContener = new ContextThemeWrapper(this, R.style.MenuTheme);
        btnTrackView = (Button) findViewById(R.id.btnSwitchToCtrl);
        btnRefresh = (Button) findViewById(R.id.btnRefresh);
        btnChooseUser = (Button) findViewById(R.id.btnChooseUser);
        btnChooseUser.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {


                PopupMenu popupMenu = new PopupMenu(menuContener, btnChooseUser  );
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Toast.makeText(getApplicationContext(),"Choosed: "+ item.getTitle() ,Toast.LENGTH_LONG).show();
                        return true;
                    }
                });

                popupMenu.show();
            }

        });

        btnTrackView.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
                myIntent.putExtra("USER_ID",USER_ID);
                myIntent.putExtra("PHONE_NR",PHONE_NR);
                startActivity(myIntent);

            }
        });

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final RequestQueue requestQueue = Volley.newRequestQueue(FollowActivity.this);
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
                                        newmarker = mapGoogle.addMarker(new MarkerOptions().position(latLng).title("Tracked person").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
                                    newmarker.setPosition(latLng);

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
                               Toast.makeText(getApplicationContext(),"Connection error" ,Toast.LENGTH_LONG).show();
                                error.printStackTrace();
                                requestQueue.stop();
                            }
                        }

                ){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String,String> params = new HashMap<>();
                        params.put("userid", Integer.toString(USER_ID));
                        return params;
                    }
                };
                requestQueue.add(stringRequest);

            }
        });
        locationTimerTask();
    }

    public void locationTimerTask() {
        final Timer timer=new Timer();
        TimerTask timerTask =new TimerTask() {
            public void run() {
                final RequestQueue requestQueue = Volley.newRequestQueue(FollowActivity.this);
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
                                Toast.makeText(getApplicationContext(),"Connection error" ,Toast.LENGTH_LONG).show();
                                error.printStackTrace();
                                requestQueue.stop();
                            }
                        }

                ){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String,String> params = new HashMap<>();
                        params.put("userid", Integer.toString(USER_ID));
                        return params;
                    }
                };
                requestQueue.add(stringRequest);
                if(PAUSE_CTRL)
                    timer.cancel();
            }
        };
        timer.scheduleAtFixedRate(timerTask, 0,5000);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapGoogle=googleMap;
    }


    @Override
    protected void onPause() {
        super.onPause();
        PAUSE_CTRL=true;
        Toast.makeText(getApplicationContext(),"Tracking disabled" ,Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Toast.makeText(getApplicationContext(),"Tracking enabled" ,Toast.LENGTH_LONG).show();
        PAUSE_CTRL=false;
        locationTimerTask();
    }
}

