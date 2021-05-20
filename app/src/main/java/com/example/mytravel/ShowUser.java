package com.example.mytravel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
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
    TextView username;
    Button followButton;
    Boolean isFollowing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_user);

        Intent intent = getIntent();
        this.currUser = (User) intent.getParcelableExtra("currUser");
        this.inputUser = (User) intent.getParcelableExtra("inputUser");

        username = findViewById(R.id.username);
        username.setText(inputUser.getUsername());

        followButton = findViewById(R.id.followButton);

        if (currUser.getUsername().equals(inputUser.getUsername()))
        {
            followButton.setVisibility(View.GONE);
        } else {
            checkFollows();
        }
    }

    public void checkFollows()
    {
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = mFirebaseDatabase.getReference("Follows").child(currUser.getUsername()).child("Following");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot followedUser: dataSnapshot.getChildren()) {
                    String currName = followedUser.child("name").getValue(String.class);
                    isFollowing = currName.equals(inputUser.getUsername());
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
        if (isFollowing) {
            FirebaseMethods.unfollowUser(currUser, inputUser);
            followButton.setText("Follow");
            followButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            isFollowing = false;

        } else {
            FirebaseMethods.followUser(currUser, inputUser);
            followButton.setText("Following");
            int imgResource = R.drawable.done;
            followButton.setCompoundDrawablesWithIntrinsicBounds(imgResource, 0, 0, 0);
            isFollowing = true;
        }

    }

    public void goBack(View view) {
        finish();
    }
}