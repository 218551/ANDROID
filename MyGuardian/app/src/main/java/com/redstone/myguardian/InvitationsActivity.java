package com.redstone.myguardian;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class InvitationsActivity extends AppCompatActivity {
    private Button btnBack;
    private String USERNAME;
    private Integer USER_ID;
    private ListView lv;

    private ArrayList<String> invitationsData = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invitations);
        USERNAME = this.getIntent().getExtras().getString("USERNAME");
        USER_ID = this.getIntent().getExtras().getInt("USER_ID");
        btnBack = (Button) findViewById(R.id.btnBack);
        lv = (ListView) findViewById(R.id.invitations_list);
        loadInvitations();


        btnBack.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(getApplicationContext(), FriendsActivity.class);
                myIntent.putExtra("USER_ID",USER_ID);
                myIntent.putExtra("USERNAME",USERNAME);
                startActivity(myIntent);
                finish();
            }
        });

    }

    private class MyListAdaper extends ArrayAdapter<String> {
        private int layout;
        private List<String> mObjects;

        private MyListAdaper(Context context, int resource, List<String> objects) {
            super(context, resource, objects);
            mObjects = objects;
            layout = resource;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder mainViewholder = null;
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(layout, parent, false);
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.title = (TextView) convertView.findViewById(R.id.inviter_name);
                viewHolder.btnAccept = (Button) convertView.findViewById(R.id.btnAccept);
                viewHolder.btnDeny = (Button) convertView.findViewById(R.id.btnDeny);
                convertView.setTag(viewHolder);
            }
            mainViewholder = (ViewHolder) convertView.getTag();

            mainViewholder.btnDeny.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    delFriend(lv.getItemAtPosition(position).toString());
                    invitationsData.remove(position);
                    MyListAdaper.this.notifyDataSetChanged();
                }
            });

            mainViewholder.btnAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    confirmFriendship(lv.getItemAtPosition(position).toString());
                    invitationsData.remove(position);
                    MyListAdaper.this.notifyDataSetChanged();
                }
            });
            mainViewholder.title.setText(getItem(position));
            return convertView;
        }
    }

    public class ViewHolder {
        TextView title;
        Button btnDeny;
        Button btnAccept;
    }

    void confirmFriendship(final String username2) {
        final RequestQueue requestQueue = Volley.newRequestQueue(InvitationsActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, DbConstants.URL_CONFIRMFRIENDSHIP,
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
        final RequestQueue requestQueue = Volley.newRequestQueue(InvitationsActivity.this);
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

    public void loadInvitations() {
        final RequestQueue requestQueue = Volley.newRequestQueue(InvitationsActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, DbConstants.URL_LOADINVITATIONS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray usernameArray = jsonObject.getJSONArray("usernames");

                            for(int i=0;i<usernameArray.length();i+=1)
                            {
                                String inviterUsername = usernameArray.getString(i);
                                invitationsData.add((inviterUsername));
                            }
                            lv.setAdapter(new MyListAdaper(InvitationsActivity.this, R.layout.invitations_list_element, invitationsData));
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
                        Toast.makeText(getApplicationContext(),"Connection error." ,Toast.LENGTH_LONG).show();
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