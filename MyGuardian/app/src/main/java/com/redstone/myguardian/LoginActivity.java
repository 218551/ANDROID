package com.redstone.myguardian;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private int USER_ID;
    private String USERNAME;

    Button btnLogin;
    Button btnRegister;
    EditText login;
    EditText password;
    ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btnLogin = (Button) findViewById(R.id.button);
        btnRegister = (Button) findViewById(R.id.button2);
        login = (EditText) findViewById(R.id.inputLogin);
        password = (EditText) findViewById(R.id.inputPassword);

        mProgress = new ProgressDialog(LoginActivity.this);
        mProgress.setTitle("Processing...");
        mProgress.setMessage("Please wait...");
        mProgress.setCancelable(false);
        mProgress.setIndeterminate(true);


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgress.show();
                final RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);
                StringRequest stringRequest = new StringRequest(Request.Method.POST, DbConstants.URL_LOGIN,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                                    USER_ID = jsonObject.getInt("userid");
                                    USERNAME = jsonObject.getString("username");

                                    if(USER_ID!=0){
                                        Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
                                        myIntent.putExtra("USERNAME",USERNAME);
                                        myIntent.putExtra("USER_ID",USER_ID);
                                        myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(myIntent);
                                        finish();
                                    }
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
                                mProgress.dismiss();
                            }
                        }

                ){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String,String> params = new HashMap<>();
                        params.put("username", login.getText().toString());
                        params.put("password", password.getText().toString());
                        return params;
                    }
                };
                requestQueue.add(stringRequest);
            }
        });

        btnRegister.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(getApplicationContext(), RegisterActivity.class);
                myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(myIntent);
                finish();
            }
        });
    }
}
