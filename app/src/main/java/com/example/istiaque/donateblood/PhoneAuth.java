package com.example.istiaque.donateblood;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class PhoneAuth extends AppCompatActivity {

    private EditText phnumber;
    private Button sendverification;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_auth);

        phnumber = findViewById(R.id.phnnumber);
        sendverification = findViewById(R.id.sendverification);

        sendverification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String number = phnumber.getText().toString().trim();

                if(number.isEmpty() || number.length() != 10){
                    phnumber.setError("Enter a Valid Number.");
                    phnumber.requestFocus();
                    return;
                }

                number = "+880" + number;
                Intent Verify = new Intent(PhoneAuth.this,Verification.class);
                Verify.putExtra("number",number);
                Verify.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(Verify);
            }
        });
    }
}
