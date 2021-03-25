package com.example.mytravel;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseMethods
{
    public void generateUser(String username, String password, String email)
    {
        Log.d("firebase", "in gen");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference users = database.getReference("Users");
        User user = new User(username, password, email);
        users.push().setValue(user);
    }

}
