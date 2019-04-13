package com.example.laba2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
Button obliczNapiwek;
CheckBox checkBox1;
CheckBox checkBox2;
CheckBox checkBox3;
EditText cenaParagon;
TextView cenaNapiwku;
Button resetNapiwku;
double mnoznik=0;
double wynik;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cenaParagon=(EditText) findViewById(R.id.cenaParagonu);
        cenaNapiwku = (TextView) findViewById(R.id.cenaNapiwku);
        checkBox1= (CheckBox) findViewById(R.id.checkBox1);
        checkBox2= (CheckBox) findViewById(R.id.checkBox2);
        checkBox3= (CheckBox) findViewById(R.id.checkBox3);
        obliczNapiwek= (Button) findViewById(R.id.obliczNapiwek);
        resetNapiwku = (Button) findViewById(R.id.resetNapiwku);
        obliczNapiwek.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View view){

            if(checkBox1.isChecked()) mnoznik=0.01;
            if(checkBox2.isChecked()) mnoznik=0.05;
            if(checkBox3.isChecked()) mnoznik=0.1;

            wynik=mnoznik*Double.valueOf(cenaParagon.getText().toString());
            cenaNapiwku.setText(String.valueOf(wynik));
            }
        });

        resetNapiwku.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View view){

                checkBox1.setChecked(false);
                checkBox2.setChecked(false);
                checkBox3.setChecked(false);

                cenaParagon.setText("");
                cenaNapiwku.setText("");
            }
        });
    }
}
