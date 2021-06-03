package com.example.mytravel;

import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.clustering.ClusterManager;

public class FirebaseMethods
{
    ClusterManager mClusterManager;

    public static void generateUser(String username, String email)
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

    public static void loadPosts(final ClusterManager<Post> clusterManager, DatabaseReference postsPath)
    {

        postsPath.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren())
                {
                    if (dataSnapshot.getChildrenCount() > 0)
                    {
                        for (DataSnapshot postSnapshot : childDataSnapshot.getChildren())
                        {
                            String currId = postSnapshot.getKey();

                            // Constructing all parameters of post
                            String name = postSnapshot.child("name").getValue(String.class);
                            String description = postSnapshot.child("description").getValue(String.class);
                            User owner = postSnapshot.child("owner").getValue(User.class);
                            String latitude = postSnapshot.child("location").child("latitude").getValue().toString();
                            String longitude = postSnapshot.child("location").child("longitude").getValue().toString();
                            LatLng location = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                            Uri imageLink = Uri.parse(postSnapshot.child("imageLink").getValue(String.class));

                            assert imageLink != null;
                            Post post = new Post(currId, location, description, name, owner, imageLink.toString());
                            clusterManager.addItem(post);
                        }
                    }
                }
                clusterManager.cluster();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError firebaseError)
            {
                Log.d("Image load error", firebaseError.getDetails());
            }
        });
    }
}
