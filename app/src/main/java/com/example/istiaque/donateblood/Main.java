package com.example.istiaque.donateblood;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Main extends AppCompatActivity {

    private Button Finddonar,Bedonar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Finddonar = (Button) findViewById(R.id.findonar);
        Bedonar = (Button) findViewById(R.id.bedonar);

        Bedonar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Main.this,Registration.class));
            }
        });
    }
}
