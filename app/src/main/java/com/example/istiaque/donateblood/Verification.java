package com.example.istiaque.donateblood;

import android.arch.core.executor.TaskExecutor;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class Verification extends AppCompatActivity {

    private String verificationid;
    private FirebaseAuth firebaseAuth;
    private EditText userverificationid;
    private Button verify;
    String number;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        firebaseAuth = FirebaseAuth.getInstance();

        userverificationid = findViewById(R.id.userverificationid);
        verify = findViewById(R.id.verify);
        number = getIntent().getStringExtra("number");

        sendverificationcode(number);

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = userverificationid.getText().toString().trim();
                if(code.isEmpty() || code.length() < 6){
                    userverificationid.setError("Enter Code...");
                    userverificationid.requestFocus();
                    return;
                }
                verifycode(code);
            }
        });

        
    }

    private void verifycode(String code){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationid,code);
        signinwithcredential(credential);
    }

    private void signinwithcredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    boolean isNewUser = task.getResult().getAdditionalUserInfo().isNewUser();
                    if(isNewUser) {
                        Intent Intent = new Intent(Verification.this,Registration.class);
                        Intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        Intent.putExtra("phnnumber",number);
                        startActivity(Intent);
                    }
                    else{
                        Intent Intent = new Intent(Verification.this,Userprofile.class);
                        Intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(Intent);
                    }
                }
                else{
                    Toast.makeText(Verification.this,"Enter a Valid Verification Code!",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void sendverificationcode(String number){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallBack
        );
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationid = s;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            String code =  phoneAuthCredential.getSmsCode();
            if(code != null){
                userverificationid.setText(code);
                verifycode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(Verification.this,e.getMessage(),Toast.LENGTH_LONG).show();
        }
    };
}
