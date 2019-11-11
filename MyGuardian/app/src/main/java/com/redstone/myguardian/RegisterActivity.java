package com.redstone.myguardian;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    Integer SUCCES;
    Button btnBack;
    Button btnRegister;
    EditText login;
    EditText password;
    EditText myPhoneNr;
    EditText firstname;
    EditText lastname;
    ProgressDialog mProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        btnRegister = (Button) findViewById(R.id.button3);
        btnBack = (Button) findViewById(R.id.button4);
        login = (EditText) findViewById(R.id.editText3);
        password = (EditText) findViewById(R.id.editText4);
        myPhoneNr = (EditText) findViewById(R.id.editText5);
        firstname = (EditText) findViewById(R.id.editText6);
        lastname = (EditText) findViewById(R.id.editText7);

        mProgress = new ProgressDialog(RegisterActivity.this);
        mProgress.setTitle("Processing...");
        mProgress.setMessage("Please wait...");
        mProgress.setCancelable(false);
        mProgress.setIndeterminate(true);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgress.show();
                final RequestQueue requestQueue = Volley.newRequestQueue(RegisterActivity.this);
                StringRequest stringRequest = new StringRequest(Request.Method.POST, DbConstants.URL_REGISTER,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    mProgress.dismiss();
                                    JSONObject jsonObject = new JSONObject(response);
                                    Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                                    SUCCES = Integer.parseInt(jsonObject.getString("succes"));
                                    if(SUCCES==1){
                                        Intent myIntent = new Intent(getApplicationContext(), LoginActivity.class);
                                        startActivity(myIntent);
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
                                mProgress.dismiss();
                                requestQueue.stop();

                            }
                        }

                ){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String,String> params = new HashMap<>();
                        params.put("username", login.getText().toString());
                        params.put("password", md5(password.getText().toString()));
                        params.put("nrtel", myPhoneNr.getText().toString());
                        params.put("firstname", firstname.getText().toString());
                        params.put("lastname", lastname.getText().toString());
                        return params;
                    }
                };
                requestQueue.add(stringRequest);
            }
        });

        btnBack.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(getApplicationContext(), LoginActivity.class);
                myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(myIntent);
                finish();
            }
        });

    }
    public String md5(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i=0; i<messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

}
