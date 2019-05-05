package com.example.istiaque.donateblood;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Registration extends AppCompatActivity {


    private EditText Name;
    private EditText Email;
    private EditText Bloodgroup;
    private EditText Date,Month,Year;

    private Button Register;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private DatabaseReference firebaseDatabase;
    private ProgressDialog progressDialog;
    String phnnumber;

    private PlacesClient placesClient;
    private List<AutocompletePrediction> predictionList;

    private MaterialSearchBar  materialSearchBar;

    private String location;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        progressDialog = new ProgressDialog(this);
        phnnumber = getIntent().getStringExtra("phnnumber");

        firebaseAuth = FirebaseAuth.getInstance();

        firebaseDatabase = FirebaseDatabase.getInstance().getReference().child("Users");


        Name = (EditText) findViewById(R.id.name);
        Email = (EditText) findViewById(R.id.email);
        Bloodgroup = (EditText) findViewById(R.id.bloodgroup);
        Date = (EditText) findViewById(R.id.date);
        Month = (EditText) findViewById(R.id.month);
        Year = (EditText) findViewById(R.id.year);

        Register = (Button) findViewById(R.id.register);

        materialSearchBar = findViewById(R.id.searchBar);

        // Initialize Places.
        if(!Places.isInitialized()){
            Places.initialize(getApplicationContext(), "AIzaSyBSpe5Q1r6C1p9OBv_oC2QZ3ppZQl3ts4o");
        }

        // Create a new Places client instance.
        placesClient = Places.createClient(this);

        final AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();

        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {

            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                startSearch(text.toString(),true,null,true);
            }

            @Override
            public void onButtonClicked(int buttonCode) {
                if(buttonCode == MaterialSearchBar.BUTTON_BACK){
                    materialSearchBar.disableSearch();
                }
            }
        });

        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                FindAutocompletePredictionsRequest predictionsRequest = FindAutocompletePredictionsRequest.builder()
                        .setCountry("bd")
                        .setTypeFilter(TypeFilter.CITIES)
                        .setSessionToken(token)
                        .setQuery(s.toString())
                        .build();
                placesClient.findAutocompletePredictions(predictionsRequest).addOnCompleteListener(new OnCompleteListener<FindAutocompletePredictionsResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<FindAutocompletePredictionsResponse> task) {
                        if(task.isSuccessful()){
                            FindAutocompletePredictionsResponse predictionsResponse = task.getResult();
                            if(predictionsResponse != null){
                                predictionList = predictionsResponse.getAutocompletePredictions();
                                List<String> suggestionList = new ArrayList<>();

                                for(int i = 0;i < predictionList.size();i++){
                                    AutocompletePrediction prediction  = predictionList.get(i);
                                    suggestionList.add(prediction.getFullText(null).toString());
                                }

                                materialSearchBar.updateLastSuggestions(suggestionList);

                                if(!materialSearchBar.isSuggestionsVisible()){
                                    materialSearchBar.showSuggestionsList();
                                }

                            }
                        }
                        else{
                            Log.i("mytag","prediction fetching task successfull");
                        }
                    }
                });
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        materialSearchBar.setSuggestionsClickListener(new SuggestionsAdapter.OnItemViewClickListener() {
            @Override
            public void OnItemClickListener(int position, View v) {
                if(position >= predictionList.size()){
                    return;
                }
                AutocompletePrediction autocompletePrediction = predictionList.get(position);
                String suggestion = materialSearchBar.getLastSuggestions().get(position).toString();
                materialSearchBar.setText(suggestion);
                materialSearchBar.clearSuggestions();
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

                if(inputMethodManager != null)
                    inputMethodManager.hideSoftInputFromWindow(materialSearchBar.getWindowToken(),InputMethodManager.HIDE_IMPLICIT_ONLY );
                String placeid = autocompletePrediction.getPlaceId();

                List<Place.Field> placefields = Arrays.asList(Place.Field.LAT_LNG);
                FetchPlaceRequest fetchPlaceRequest = FetchPlaceRequest.builder(placeid,placefields).build();
                placesClient.fetchPlace(fetchPlaceRequest).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
                    @Override
                    public void onSuccess(FetchPlaceResponse fetchPlaceResponse) {
                        Place place = fetchPlaceResponse.getPlace();
                        Log.i("mytag","Place found " + place.getName());
                        LatLng latLng = place.getLatLng();
                        double Lat = latLng.latitude;
                        double Long = latLng.longitude;


                        location = "" + Double.toString(Lat) + "_" + Double.toString(Long);

                        materialSearchBar.clearSuggestions();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if(e instanceof ApiException){
                            ApiException apiException = (ApiException) e;
                            apiException.printStackTrace();
                            int statuscode = apiException.getStatusCode();
                            Log.i("mytag","Place not found");
                            Log.i("mytag","status code : " + statuscode);
                        }
                    }
                });
            }

            @Override
            public void OnItemDeleteListener(int position, View v) {

            }
        });


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
        String bloodgroup = Bloodgroup.getText().toString().trim().toLowerCase();

        int date = 0,month = 0,year = 0;
        String date1 = Date.getText().toString().trim();
        if(!date1.isEmpty()){
            date = Integer.parseInt(date1);
            if(!(date >= 1 && date <= 31)){
                date1 = "";
                Toast.makeText(this,"Date Must be between 1 to 31",Toast.LENGTH_LONG).show();
            }
        }
        String month1 = Month.getText().toString().trim();
        if(!month1.isEmpty()){
            month = Integer.parseInt(month1);
            if(!(month >= 1 && month <= 12)){
                month1 = "";
                Toast.makeText(this,"Month Must be between 1 to 12",Toast.LENGTH_LONG).show();
            }
        }
        String year1 = Year.getText().toString().trim();
        if(!year1.isEmpty()){
            year = Integer.parseInt(year1);
        }

        if(name.equals("") || email.equals("") ||  bloodgroup.equals("") || location.isEmpty() || date1.equals("") || month1.equals("") || year1.equals("")){
            Toast.makeText(this,"Fill all the areas with Valid Information.",Toast.LENGTH_LONG).show();
        }
        else{

            progressDialog.setMessage("Singing in..");
            progressDialog.show();

            String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();

            String bloodgroup_Location = bloodgroup + "_" + location;

            firebaseDatabase.child(user_id).child("Name").setValue(name);
            firebaseDatabase.child(user_id).child("Email").setValue(email);
            firebaseDatabase.child(user_id).child("Bloodgroup").setValue(bloodgroup);
            firebaseDatabase.child(user_id).child("Phone Number").setValue(phnnumber);
            firebaseDatabase.child(user_id).child("Location").setValue(location);
            firebaseDatabase.child(user_id).child("Bloodgroup_Location").setValue(bloodgroup_Location);
            firebaseDatabase.child(user_id).child("LastDonation").child("Date").setValue(date);
            firebaseDatabase.child(user_id).child("LastDonation").child("Month").setValue(month);
            firebaseDatabase.child(user_id).child("LastDonation").child("Year").setValue(year);

            Toast.makeText(this,"Registration Successful.",Toast.LENGTH_LONG).show();

            Intent user = new Intent(Registration.this,Main.class);
            user.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            progressDialog.cancel();
            startActivity(user);
            finish();
        }
    }
}
