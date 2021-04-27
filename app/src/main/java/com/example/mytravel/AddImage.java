package com.example.mytravel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class AddImage extends AppCompatActivity {
    EditText description, name;
    static int REQUEST_LOCATION=5, REQUEST_MEDIA=6;
    private Bitmap image;
    private User user;
    private LatLng postLocation;
    AlertDialog dialog;
    Post post;
    Uri imageLink;
    private Uri outputFileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_image);
        description = findViewById(R.id.description);
        name = findViewById(R.id.name);

        // Getting user and location from intent extras
        Intent intent = getIntent();
        this.user = (User) intent.getParcelableExtra("user");
        this.postLocation = (LatLng) intent.getParcelableExtra("location");

        /* User loading dialog */
        AlertDialog.Builder builder = new AlertDialog.Builder(AddImage.this);
        builder.setCancelable(false);
        builder.setView(R.layout.user_dialog);
        dialog = builder.create();
    }

    public void chooseImage(View view)
    {
        /*
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, REQUEST_MEDIA); */
        // Determine Uri of camera image to save.
        final File root = new File(Environment.getExternalStorageDirectory() + File.separator + "MyDir" + File.separator);
        root.mkdirs();
        final String fname = Utils.getUniqueImageFilename();
        final File sdImageMainDirectory = new File(root, fname);
        outputFileUri = Uri.fromFile(sdImageMainDirectory);

        // Camera.
        final List<Intent> cameraIntents = new ArrayList<Intent>();
        final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for(ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            cameraIntents.add(intent);
    }

    public void post(View view)
    {
        dialog.show();

        /* Upload image to storage */
        String postName = this.user.getUsername() + "~" + this.name.getText().toString();
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
                          post = new Post(postLocation, description.getText().toString(),
                                  name.getText().toString(), user, imageLink);
                          FirebaseMethods.generatePost(post);
                          dialog.dismiss();
                          setResult(RESULT_OK);
                          finish();
                      }
                  });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        Intent intent = getIntent();

       if (requestCode == REQUEST_MEDIA) {
            if (resultCode != Activity.RESULT_OK) {
                Toast.makeText(AddImage.this, "Couldn't get image",
                        Toast.LENGTH_SHORT).show();
            } else {
                Uri imageUri = data.getData();
                try {
                    this.image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                    if (this.image == null)
                        Log.e("err", "oof");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void goBack(View view) {
        setResult(RESULT_OK + 1);
        finish();
    }
}