package com.example.mytravel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
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
    AlertDialog dialog;

    // Database reference
    private FirebaseStorage mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_post);

        /* User loading dialog */
        AlertDialog.Builder builder = new AlertDialog.Builder(showPost.this);
        builder.setCancelable(false);
        builder.setView(R.layout.user_dialog);
        dialog = builder.create();
        dialog.show();

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

        storage.getBytes(Utils.MAX_IMAGE_SIZE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                image.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                dialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                dialog.dismiss();
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

    /*public void likePost(View view)
    {
        //FirebaseDatabase.likePost(vie)
    }*/

    public void goToProfile(View view) {
        Intent intent = new Intent(this, ShowUser.class);
        intent.putExtra("currUser", currUser);
        intent.putExtra("inputUser", post.getOwner());
        startActivity(intent);
    }
}