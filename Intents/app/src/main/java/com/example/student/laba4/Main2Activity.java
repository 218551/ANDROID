package com.example.student.laba4;

import android.app.IntentService;
import android.support.v7.app.AppCompatActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Main2Activity extends AppCompatActivity implements OnClickListener {
    Button button5;
    TextView textView3;
    TextView textView5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        textView3 = (TextView) findViewById(R.id.textView3);
        textView5 = (TextView) findViewById(R.id.textView5);
        button5 = (Button) findViewById(R.id.button5);
        button5.setOnClickListener(this);

        Intent localIntent = getIntent();
        Bundle myBundle = localIntent.getExtras();
        Double index = myBundle.getDouble("Value1");
        String result;

        textView3.setText(index.toString());

        if(index==218551) {
            textView5.setText("Tomasz Kubat");
            result="Tomasz Kubat";
        }
        else {
            textView5.setText("Brak takiego studenta");
            result = "Brak takiego studenta";
        }
        myBundle.putString("result", result);
        localIntent.putExtras(myBundle);

        setResult(Activity.RESULT_OK, localIntent);


    }
    @Override
    public void onClick(View view){
        finish();
    }
}
