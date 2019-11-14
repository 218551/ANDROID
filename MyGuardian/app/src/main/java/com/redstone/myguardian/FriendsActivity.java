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


public class FriendsActivity extends AppCompatActivity {
    private Button btnAddFriend;
    private Button btnInvitations;
    private Button btnBack;
    private TextView title;
    private String USERNAME;
    private Integer USER_ID;
    private ListView lv;

    private ArrayList<String> friendsData = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        title = (TextView) findViewById(R.id.viewTitle);
        USERNAME = this.getIntent().getExtras().getString("USERNAME");
        USER_ID = this.getIntent().getExtras().getInt("USER_ID");
        btnAddFriend = (Button) findViewById(R.id.btnAddFriend);
        btnInvitations = (Button) findViewById(R.id.btnInvitations);
        btnBack = (Button) findViewById(R.id.btnBack);
        lv = (ListView) findViewById(R.id.friends_list);
        loadFollowUsers();

        btnAddFriend.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText addEditText = new EditText(FriendsActivity.this);
                AlertDialog dialog = new AlertDialog.Builder(FriendsActivity.this)
                        .setTitle("Add friend")
                        .setMessage("Type friends username bellow")
                        .setView(addEditText)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                addNewFriend(addEditText.getText().toString());
                            }
                        })
                        .setNeutralButton("Cancel", null)
                        .create();
                dialog.show();

            }
        });

        btnBack.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
                myIntent.putExtra("USER_ID",USER_ID);
                myIntent.putExtra("USERNAME",USERNAME);
                startActivity(myIntent);
                finish();
            }
        });

        btnInvitations.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(getApplicationContext(), InvitationsActivity.class);
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
                viewHolder.title = (TextView) convertView.findViewById(R.id.friend_name);
                viewHolder.btnDelete = (Button) convertView.findViewById(R.id.btnDelete);
                convertView.setTag(viewHolder);
            }
            mainViewholder = (ViewHolder) convertView.getTag();

            mainViewholder.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    delFriend(lv.getItemAtPosition(position).toString());
                    friendsData.remove(position);
                    MyListAdaper.this.notifyDataSetChanged();
                }
            });
            mainViewholder.title.setText(getItem(position));
            return convertView;
        }
    }

    public class ViewHolder {
        TextView title;
        Button btnDelete;
    }

    void addNewFriend(final String username2) {
        final RequestQueue requestQueue = Volley.newRequestQueue(FriendsActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, DbConstants.URL_ADDFRIEND,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
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
                        Toast.makeText(getApplicationContext(),"Unable to add new friend. Check internet connection." ,Toast.LENGTH_LONG).show();
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
        final RequestQueue requestQueue = Volley.newRequestQueue(FriendsActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, DbConstants.URL_DELFRIEND,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
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
                        Toast.makeText(getApplicationContext(),"Unable to delete the friend. Check internet connection" ,Toast.LENGTH_SHORT).show();
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

    public void loadFollowUsers() {
        final RequestQueue requestQueue = Volley.newRequestQueue(FriendsActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, DbConstants.URL_LOADFOLLOWUSERS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray usernameArray = jsonObject.getJSONArray("usernames");

                            for(int i=0;i<usernameArray.length();i+=1)
                            {
                                String followUsername = usernameArray.getString(i);
                                friendsData.add((followUsername));
                            }
                            lv.setAdapter(new MyListAdaper(FriendsActivity.this, R.layout.friends_list_element, friendsData));
                            requestQueue.stop();
                        }catch(JSONException exc)
                        {
                            title.setText("Friends(empty)");
                            exc.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),"Unable to load friends list. Check internet connection." ,Toast.LENGTH_LONG).show();
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

    @Override
    public void onBackPressed() {
        Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
        myIntent.putExtra("USER_ID",USER_ID);
        myIntent.putExtra("USERNAME",USERNAME);
        startActivity(myIntent);
        finish();
    }
}