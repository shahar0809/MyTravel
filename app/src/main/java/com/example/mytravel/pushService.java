package com.example.mytravel;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class pushService extends Service implements ChildEventListener {
    User currUser;
    ArrayList<User> subbedUsers = new ArrayList<>();
    DatabaseReference userRef;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags , startId);
        /*
        Notification.Builder builder = new Notification.Builder(this);
        builder.setSmallIcon(android.R.drawable.star_on).setContentTitle("Foreground service")
                .setContentText("long lasting operation");

        startForeground(1, builder.build());(*/

        currUser = (User) intent.getParcelableExtra("currUser");
        Log.d("userserv", currUser.getUsername());
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        userRef = database.child("Follows").child(currUser.getUsername());

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    userRef.child("Following").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot user : snapshot.getChildren()) {
                                subbedUsers.add(new User(user));
                            }
                            addListeners();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });


                } else {
                    // Don't exist! Do something.

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return Service.START_STICKY;
    }

    @Override
    public void onCreate() {
    }

    public void addListeners() {
        for (User user: MainApp.subUsers)
        {
            DatabaseReference database = FirebaseDatabase.getInstance().getReference();
            DatabaseReference userRef = database.child("Posts").child(user.getUsername());
            userRef.addChildEventListener(this);
        }
    }

    @Override
    public void onDestroy() {
        //stopForeground(true);
    }

    @Override
    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
        String username = snapshot.child("owner").child("name").getValue(String.class);

        /*
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, getString(R.string.app_name))
                .setSmallIcon(R.drawable.logo_only_transperent)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(username + "just posted!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT); */
    }

    @Override
    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
    }

    @Override
    public void onChildRemoved(@NonNull DataSnapshot snapshot) {
    }

    @Override
    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {
    }
}
