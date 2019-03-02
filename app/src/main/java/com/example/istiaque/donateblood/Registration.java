package com.example.istiaque.donateblood;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Registration extends AppCompatActivity {


    private EditText Name;
    private EditText Email;
    private EditText Bloodgroup;
    private EditText Location;
    private EditText Date,Month,Year;

    private Button Register;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private DatabaseReference firebaseDatabase;

    private ProgressDialog progressDialog;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        firebaseAuth = FirebaseAuth.getInstance();

        firebaseDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        progressDialog = new ProgressDialog(this);

        Name = (EditText) findViewById(R.id.name);
        Email = (EditText) findViewById(R.id.email);
        Bloodgroup = (EditText) findViewById(R.id.bloodgroup);
        Location = (EditText) findViewById(R.id.location);
        Date = (EditText) findViewById(R.id.date);
        Month = (EditText) findViewById(R.id.month);
        Year = (EditText) findViewById(R.id.year);

        Register = (Button) findViewById(R.id.register);

        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startRegister();
            }
        });

    }

    private void startRegister() {
        String name = Name.getText().toString().trim();
        String email = Email.getText().toString().trim();
        String bloodgroup = Bloodgroup.getText().toString().trim();
        String location = Location.getText().toString().trim();

        int date,month,year;
        String date1 = Date.getText().toString().trim();
        date = Integer.parseInt(date1);
        String month1 = Month.getText().toString().trim();
        month = Integer.parseInt(month1);
        String year1 = Year.getText().toString().trim();
        year = Integer.parseInt(year1);

        if(name.equals("") || email.equals("") ||  bloodgroup.equals("") && location.equals("") && date1.equals("") && month1.equals("") && year1.equals("")){
            Toast.makeText(this,"Fill all the areas with Valid Information.",Toast.LENGTH_LONG).show();
        }
        else{
            progressDialog.setMessage("Registering User");
            progressDialog.show();

            String user_id = firebaseDatabase.push().getKey();

            firebaseDatabase.child(user_id).child("Name").setValue(name);
            firebaseDatabase.child(user_id).child("Email").setValue(email);
            firebaseDatabase.child(user_id).child("Bloodgroup").setValue(bloodgroup);
            firebaseDatabase.child(user_id).child("Location").setValue(location);
            firebaseDatabase.child(user_id).child("LastDonation").child("Date").setValue(date);
            firebaseDatabase.child(user_id).child("LastDonation").child("Month").setValue(month);
            firebaseDatabase.child(user_id).child("LastDonation").child("Year").setValue(year);


            startActivity(new Intent(Registration.this,Userprofile.class));
        }
    }
}
