package com.example.b07_project;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class DOMINIK_UserScheduledEventRV extends AppCompatActivity {
    RecyclerView recyclerView;
    DatabaseReference databaseReferenceEventsScheduled;
    DOMINIK_userEventsJoinedAdapter userEventsAdapter;
    ArrayList<eventModel> list;
    SharedPreferences sharedPreferences;

    String username;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dominik_user_scheduled_event_rv);

        sharedPreferences = getSharedPreferences("MyUserPrefs", Context.MODE_PRIVATE);

        // Retrieve data from intent into shared preference
        if (getIntent().hasExtra("username")) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("username", getIntent().getStringExtra("username"));
            editor.putString("email", getIntent().getStringExtra("email"));
            editor.commit();
        }
        username = sharedPreferences.getString("username", null);
        email = sharedPreferences.getString("email", null);


        recyclerView = findViewById(R.id.userScheduledEventRVid);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        databaseReferenceEventsScheduled = FirebaseDatabase.getInstance().getReference().child("user/" +
                String.valueOf(email.hashCode()) + "/userScheduledEvents");
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<eventModel>();
        userEventsAdapter = new DOMINIK_userEventsJoinedAdapter(this, list);
        recyclerView.setAdapter(userEventsAdapter);

        databaseReferenceEventsScheduled.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                HashMap<String, HashMap<String, String>> temp = (HashMap<String, HashMap<String, String>>) snapshot.getValue();
                if (temp == null){
                    openNoEventScheduledActivity();
                }
                else {
                    for (HashMap<String, String> value : temp.values()) {
                        list.add(new eventModel(value.get("name"), value.get("date"), value.get("venue"),
                                value.get("maxParticipants"), value.get("noParticipants"),
                                value.get("startTime"), value.get("endTime")));
                    }
                    userEventsAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    public void openNoEventScheduledActivity(){
        Intent intent = new Intent(this, noEventScheduledActivity.class);
        startActivity(intent);
    }
}

