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
        users.child(username).setValue(user);
    }

    public static String generatePost(Post post)
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference posts = database.getReference("Posts");
        DatabaseReference userRef = posts.child(post.getOwner().getUsername());
        userRef.push().setValue(post);
        return userRef.getKey();
    }

    public void likePost(Post post, User user)
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference likes = database.getReference("Likes");
        DatabaseReference postRef = likes.child(post.getId());
        postRef.child(user.getUsername()).setValue(user);
    }

    public static void followUser(User currUser, User inputUser)
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference follows = database.getReference("Follows");
        DatabaseReference userRef = follows.child(currUser.getUsername());
        userRef.child("Following").child(inputUser.getUsername()).setValue(inputUser);

        follows.child(inputUser.getUsername()).child("Followers").child(currUser.getUsername()).setValue(currUser);
    }

    public static void unfollowUser(User currUser, User inputUser)
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference follows = database.getReference("Follows");
        DatabaseReference userRef = follows.child(currUser.getUsername()).child("Following").child(inputUser.getUsername());
        userRef.removeValue();

        follows.child(inputUser.getUsername()).child("Followers").child(currUser.getUsername()).removeValue();
    }

}
