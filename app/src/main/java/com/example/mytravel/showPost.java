package com.example.mytravel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class showPost extends AppCompatActivity
{
    TextView nameBox, descBox, userBox;
    ImageButton profileButton;
    ImageView image;
    User currUser;
    Post post;

    // Database reference
    private FirebaseStorage mDatabase;
    final static long ONE_MEGABYTE = 1024 * 1024;

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
        userBox = findViewById(R.id.username);
        profileButton = findViewById(R.id.profileButton);
        image = findViewById(R.id.postImage);

        fillAttributes();
    }

    protected void fillAttributes() {
        nameBox.setText(post.getName());
        descBox.setText(post.getDescription());
        userBox.setText(post.getOwner().getUsername());
        fetchImage();
    }

    public void fetchImage() {
        mDatabase = FirebaseStorage.getInstance();
        StorageReference storage = mDatabase.getReferenceFromUrl(post.getImageLink());

        storage.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                image.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(getApplicationContext(), "Can't load image", Toast.LENGTH_SHORT).show();
                setResult(RESULT_CANCELED);
                finish();
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