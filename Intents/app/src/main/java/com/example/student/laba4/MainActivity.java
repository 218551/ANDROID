package com.example.student.laba4;

import android.app.Activity;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
Button button;
Button button2;
Button button3;
Button button4;
EditText editText;
EditText editText2;
EditText editText3;
EditText editText4;
EditText editText5;
TextView textView;
String data="content://contacts/people";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button= (Button) findViewById(R.id.button);
        button.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View view){
                Intent activity1 = new Intent (Intent.ACTION_VIEW, Uri.parse(data));
                startActivity(activity1);
            }
        });

        button2= (Button) findViewById(R.id.button2);
        editText= (EditText) findViewById(R.id.editText);
        editText2= (EditText) findViewById(R.id.editText2);
        button2.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View view){
                Intent activity2= new Intent (Intent.ACTION_SENDTO, Uri.parse("smsto:"+editText.getText().toString()));
               activity2.putExtra("sms_body",editText2.getText().toString());
                startActivity(activity2);
            }
        });

        button3= (Button) findViewById(R.id.button3);
        editText3= (EditText) findViewById(R.id.editText3);
        editText4= (EditText) findViewById(R.id.editText4);
        button3.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View view){
                Intent activity3= new Intent (Intent.ACTION_VIEW, Uri.parse("geo:"+editText3.getText().toString()+","+editText4.getText().toString()));
                startActivity(activity3);
            }
        });

        button4= (Button) findViewById(R.id.button4);
        editText5= (EditText) findViewById(R.id.editText5);
        textView= (TextView) findViewById(R.id.textView);
        button4.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View view){
                Double indeks = Double.parseDouble(editText5.getText().toString());
                Intent activity4= new Intent (MainActivity.this, Main2Activity.class);
                Bundle dataBundle = new Bundle();
                dataBundle.putDouble("Value1", indeks);
                activity4.putExtras(dataBundle);
                startActivityForResult(activity4, 007);
            }
        });
        }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if ((requestCode == 007 ) && (resultCode == Activity.RESULT_OK)){
                Bundle myResultBundle = data.getExtras();
                String myResult = myResultBundle.getString("result");
                textView.setText("Indeks nalezy do: " + myResult);
            }
        }
        catch (Exception e) {
            textView.setText("Problems - " + requestCode + " " + resultCode);
        }
    }

}

