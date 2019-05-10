package com.redstone.myguardian;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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

public class FollowActivity extends AppCompatActivity implements OnMapReadyCallback {

    private int USER_ID;
    private int PHONE_NR;
    Button btnRefresh;
    Button btnTrackView;
    private GoogleMap mapGoogle;
    private Marker newmarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow);
        USER_ID= this.getIntent().getExtras().getInt("USER_ID");
        PHONE_NR = this.getIntent().getExtras().getInt("PHONE_NR");
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        btnTrackView = (Button) findViewById(R.id.button11);
        btnRefresh = (Button) findViewById(R.id.button5);

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
                                    Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();

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
                               Toast.makeText(getApplicationContext(),"sth wnet wrong " ,Toast.LENGTH_LONG).show();
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
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapGoogle=googleMap;
    }


}

