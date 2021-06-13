package com.example.mytravel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    LinearProgressIndicator progressBar;
    FirebaseAuth authService;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progress);

        OfflineBroadcastReceiver offlineForeground = new OfflineBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(offlineForeground, filter);

        authService = FirebaseAuth.getInstance();
        getCurrentUser();
    }

    protected void getCurrentUser()
    {
        final FirebaseUser loggedUser = authService.getCurrentUser();
        Intent intent = new Intent(MainActivity.this, Login.class);
        startActivity(intent);
        if (loggedUser != null)
        {
            Log.d("logged", "there is a logged user");
            FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = mFirebaseDatabase.getReference("Users");
            databaseReference.addValueEventListener(new ValueEventListener()
            {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren())
                    {
                        User user = childDataSnapshot.getValue(User.class);
                        assert user != null;
                        Log.d("email", loggedUser.getEmail());
                        if (user.getEmail().equals(loggedUser.getEmail()))
                        {
                            username = user.getUsername();
                        }

                        user = new User(username, loggedUser.getEmail());
                        Intent intent = new Intent(MainActivity.this, MainApp.class);
                        intent.putExtra("user", user);
                        startActivity(intent);
                        finish();
                    }

                }
                @Override
                public void onCancelled(DatabaseError firebaseError) {
                    Log.e("The read failed: ", firebaseError.getMessage());
                }
            });
        }
        else
            {
            Intent i = new Intent(MainActivity.this, Login.class);
            startActivity(i);
        }
    }

}