package com.example.istiaque.donateblood;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseArray;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import org.w3c.dom.Text;

public class Userprofile extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FirebaseRecyclerAdapter adapter;
    private LinearLayoutManager linearLayoutManager;
    private String bloodgroup_location;

    public TextView donorcount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userprofile);

        donorcount = (TextView) findViewById(R.id.donorcount);

        bloodgroup_location = getIntent().getStringExtra("bloodgroup_location");

        recyclerView = findViewById(R.id.list);

        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        query();
    }

    private void query() {
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Users").orderByChild("Bloodgroup_Location").equalTo(bloodgroup_location);


        FirebaseRecyclerOptions<Model> options =
                new FirebaseRecyclerOptions.Builder<Model>()
                        .setQuery(query, new SnapshotParser<Model>() {
                            @NonNull
                            @Override
                            public Model parseSnapshot(@NonNull DataSnapshot snapshot) {
                                return new Model(snapshot.child("Name").getValue().toString(),
                                        snapshot.child("Email").getValue().toString(),
                                        snapshot.child("Phone Number").getValue().toString(),
                                        snapshot.child("Location").getValue().toString());
                            }
                        })
                        .build();

        adapter = new FirebaseRecyclerAdapter<Model, ViewHolder>(options) {
            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item, parent, false);

                return new ViewHolder(view);
            }


            @Override
            protected void onBindViewHolder(ViewHolder holder, final int position, final Model model) {
                holder.setname(model.getname());
                holder.setemail(model.getemail());

                holder.callnow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String phnnumber = model.getphnnumber();
                        Intent call = new Intent(Intent.ACTION_DIAL);
                        phnnumber = "tel:" + phnnumber;
                        call.setData(Uri.parse(phnnumber));
                        startActivity(call);
                    }
                });

                holder.location.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        double Lat = model.getlatitude();
                        double Long = model.getlongitude();

                        Intent mapintent = new Intent(Userprofile.this,MapsActivity.class);
                        mapintent.putExtra("Lat",Lat);
                        mapintent.putExtra("Long",Long);
                        startActivity(mapintent);
                    }
                });


            }

        };
        recyclerView.setAdapter(adapter);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout root;
        public TextView name;
        public TextView email;
        public Button callnow,location;

        public ViewHolder(View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.list_root);
            name = itemView.findViewById(R.id.list_name);
            email = itemView.findViewById(R.id.list_email);
            callnow = itemView.findViewById(R.id.callnow);
            location = itemView.findViewById(R.id.location);
        }




        public void setname(String string) {
            name.setText(string);
        }


        public void setemail(String string) {
            email.setText(string);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
