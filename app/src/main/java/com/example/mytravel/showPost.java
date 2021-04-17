package com.example.mytravel;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class showPost extends AppCompatActivity
{
    TextView nameBox, descBox;
    ImageButton profileButton;
    User currUser;
    Post post;

    // Database reference
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_post);

        Intent intent = getIntent();
        this.currUser = (User) intent.getParcelableExtra("user");
        this.post = (Post) intent.getParcelableExtra("post");

        // Bind views
        nameBox = findViewById(R.id.name);
        descBox = findViewById(R.id.description);
        profileButton = findViewById(R.id.profileButton);

        nameBox.setText(post.getName());
        descBox.setText(post.getDescription());


    }

    public void fetchImage() {
        mDatabase = FirebaseDatabase.getInstance().getReference(post.getImageLink());

        //adding an event listener to fetch values
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Bitmap image = snapshot.getValue(Bitmap.class);
                uploads.add(upload);
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {

                }
                //creating adapter
                adapter = new MyAdapter(getApplicationContext(), uploads);

                //adding adapter to recyclerview
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });
    }

    public void goBack(View view)
    {
        setResult(RESULT_OK);
        finish();
    }

    public void likePost(View view)
    {
    }
}