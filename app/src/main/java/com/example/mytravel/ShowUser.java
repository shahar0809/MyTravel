package com.example.mytravel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ShowUser extends AppCompatActivity {
    User currUser, inputUser;
    TextView username, postsCounter, followersCounter, followingCounter;
    Button followButton;
    Boolean isFollowing = false;

    long followersCount = 0;
    long followingCount = 0;
    long postsCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_user);

        Intent intent = getIntent();
        this.currUser = (User) intent.getParcelableExtra("currUser");
        this.inputUser = (User) intent.getParcelableExtra("inputUser");

        followingCounter = findViewById(R.id.followingCount);
        followersCounter = findViewById(R.id.followersCount);
        postsCounter = findViewById(R.id.postsCount);
        username = findViewById(R.id.username);
        username.setText(inputUser.getUsername());

        followButton = findViewById(R.id.followButton);

        if (currUser.getUsername().equals(inputUser.getUsername()))
        {
            followButton.setVisibility(View.GONE);
        }
        else
         {
            try
            {
                checkFollows();
            } catch (Exception e) {
                Log.e("get follow data", e.getMessage());
            }
        }
        try
        {
            checkFollows();
            countFollowers();
            countFollowing();
            countPosts();
        } catch (Exception e) {
            Log.e("get follow data", e.getMessage());
        }
    }

    public void checkFollows()
    {
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = mFirebaseDatabase.getReference("Follows").child(currUser.getUsername()).child("Following");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0)
                {
                    for (DataSnapshot followedUser: dataSnapshot.getChildren()) {
                        String currName = followedUser.child("username").getValue(String.class);
                        isFollowing = currName.equals(inputUser.getUsername());
                    }
                }

                if (isFollowing) {
                    followButton.setText("Following");
                    int imgResource = R.drawable.done;
                    followButton.setCompoundDrawablesWithIntrinsicBounds(imgResource, 0, 0, 0);
                } else {
                    followButton.setText("Follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ShowUser.this, "Can't load users", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void follow(View view)
    {
        // Unfollowing
        if (isFollowing) {
            FirebaseMethods.unfollowUser(currUser, inputUser);
            followButton.setText("Follow");
            followButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            isFollowing = false;
        }
        else
        {
            FirebaseMethods.followUser(currUser, inputUser);
            followButton.setText("Following");
            int imgResource = R.drawable.done;
            followButton.setCompoundDrawablesWithIntrinsicBounds(imgResource, 0, 0, 0);
            isFollowing = true;
        }
    }

    protected void countFollowers()
    {
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = mFirebaseDatabase.getReference("Follows").child(inputUser.getUsername()).child("Followers");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followersCount = snapshot.getChildrenCount();
                followersCounter.setText(Long.toString(followersCount));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Count Followers", error.getDetails());
            }
        });
    }

    protected void countFollowing()
    {
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = mFirebaseDatabase.getReference("Follows").child(inputUser.getUsername()).child("Following");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followingCount = snapshot.getChildrenCount();
                followingCounter.setText(Long.toString(followingCount));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Count Following", error.getDetails());
            }
        });
    }

    protected void countPosts()
    {
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = mFirebaseDatabase.getReference("Posts").child(inputUser.getUsername());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postsCount = snapshot.getChildrenCount();
                postsCounter.setText(Long.toString(postsCount));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Count Following", error.getDetails());
            }
        });
    }

    public void goBack(View view) {
        finish();
    }

    public void showFollowers(View view)
    {
        Intent intent = new Intent(this, ShowUsers.class);
        intent.putExtra("currUser", currUser);
        intent.putExtra("usersAction", "Followers");
        startActivity(intent);
    }

    public void showFollowing(View view)
    {
        Intent intent = new Intent(this, ShowUsers.class);
        intent.putExtra("currUser", currUser);
        intent.putExtra("usersAction", "Following");
        startActivity(intent);
    }
}