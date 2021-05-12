package com.example.mytravel;

import android.net.Uri;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseMethods
{
    public void generateUser(String username, String email)
    {
        Log.d("firebase", "in gen");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference users = database.getReference("Users");
        User user = new User(username, email);
        users.push().setValue(user);
    }

    public static String generatePost(Post post)
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference posts = database.getReference("Posts");
        DatabaseReference userRef = posts.child(post.getOwner().getUsername());
        userRef.push().setValue(post);
        return userRef.getKey();
    }

    public void likePost(Post post, String username)
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference likes = database.getReference("Likes");
        DatabaseReference postRef = likes.child(post.getId());
        postRef.push().setValue(username);
    }
}
