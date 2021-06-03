package com.example.mytravel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static androidx.core.graphics.TypefaceCompatUtil.getTempFile;

public class AddImage extends AppCompatActivity {
    TextInputLayout description, name;
    static int REQUEST_STORAGE = 6, REQUEST_CAMERA = 7;

    private Bitmap image;
    private User user;
    private LatLng postLocation;
    Post post;
    Uri imageLink;

    AlertDialog dialog;
    final String[] Options = {"Gallery", "Camera"};
    AlertDialog.Builder window;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_image);
        description = findViewById(R.id.descField);
        name = findViewById(R.id.nameField);

        // Getting user and location from intent extras
        Intent intent = getIntent();
        this.user = intent.getParcelableExtra("user");
        this.postLocation = intent.getParcelableExtra("location");

        /* User loading dialog */
        AlertDialog.Builder builder = new AlertDialog.Builder(AddImage.this);
        builder.setCancelable(false);
        builder.setView(R.layout.user_dialog);
        dialog = builder.create();
    }

    public void chooseImage(View view)
    {
        window = new AlertDialog.Builder(this);
        window.setTitle("Pick a photo from:");
        window.setItems(Options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == 0)
                {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto , REQUEST_STORAGE);//one can be replaced with any action code
                }
                else if(which == 1)
                {
                    Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, REQUEST_CAMERA);//zero can be replaced with any action code (called requestCode)
                }
                else
                 {
                    Toast.makeText(getApplicationContext(), "Hmmm I messed up. I detected that you clicked on : " + which + "?", Toast.LENGTH_LONG).show();
                }
            }
        });

        window.show();
    }

    protected boolean checkValidity(String name, String desc)
    {
        /* Checking that the fields are not empty */
        if (TextUtils.isEmpty(name))
        {
            Toast.makeText(getApplicationContext(), "Enter name!", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            return false;
        }
        return true;
    }

    public void post(View view)
    {
        dialog.show();

        // Fetching strings
        final String desc_str = description.getEditText().getText().toString();
        final String name_str = name.getEditText().getText().toString();

        if (checkValidity(desc_str, name_str))
        {
            /* Upload image to storage */
            String postName = this.user.getUsername() + "~" + name_str;
            FirebaseStorage storage = FirebaseStorage.getInstance();
            // Create a storage reference from our app
            final StorageReference userRef = storage.getReference().child("Users").child(this.user.getUsername()).child(postName);

            Bitmap bitmap = this.image;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            UploadTask uploadTask = userRef.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Error uploading image", Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    userRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
                    {
                        @Override
                        public void onSuccess(Uri uri) {
                            imageLink = uri;
                            Toast.makeText(AddImage.this, "Post is up!", Toast.LENGTH_LONG).show();
                            post = new Post(postLocation, desc_str,
                                    name_str, user, imageLink);
                            FirebaseMethods.generatePost(post);
                            dialog.dismiss();
                            setResult(RESULT_OK);
                            finish();
                        }
                    });
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        Intent intent = getIntent();

        if (resultCode != Activity.RESULT_OK) {
            Toast.makeText(AddImage.this, "Couldn't get image",
                    Toast.LENGTH_SHORT).show();
        }
        else if (requestCode == REQUEST_STORAGE) {
            assert data != null;
            Uri imageUri = data.getData();
            try {
                this.image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                if (this.image == null)
                    Log.e("err", "oof");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == REQUEST_CAMERA) {
            try {
                assert data != null;
                image = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
            } catch (Exception e) {
                Log.e("err", "oof");
            }
        }
    }

    public void goBack(View view) {
        setResult(RESULT_OK + 1);
        finish();
    }
}