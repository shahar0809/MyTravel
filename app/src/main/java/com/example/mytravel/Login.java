package com.example.mytravel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity
{
    /* Elements in xml */
    EditText email, password;
    String email_str, password_str, username;
    FirebaseAuth auth;
    AlertDialog dialog;

    public static final int MIN_PASSWORD_LEN = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        bindElements();

        auth = FirebaseAuth.getInstance();
        final FirebaseUser loggedUser = auth.getCurrentUser();
        if (loggedUser != null) {
            FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = mFirebaseDatabase.getReference("Users");
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                        User user = childDataSnapshot.getValue(User.class);
                        assert user != null;
                        if (user.getEmail().equals(loggedUser.getEmail()))
                        {
                            username = user.getUsername();
                        }
                    }
                    User user = new User(username, loggedUser.getEmail());
                    Intent intent = new Intent(Login.this, MainApp.class);
                    intent.putExtra("user", user);
                    startActivity(intent);
                    finish();
                }
                @Override
                public void onCancelled(DatabaseError firebaseError) {
                    Log.e("The read failed: ", firebaseError.getMessage());
                }
            });
        }

        /* User loading dialog */
        AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
        builder.setCancelable(false);
        builder.setView(R.layout.user_dialog);
        dialog = builder.create();
    }

    protected void bindElements()
    {
        this.email = findViewById(R.id.email_box);
        this.password = findViewById(R.id.password_box);
    }


    public void login(View view)
    {
        dialog.show();

        // Fetching strings
        email_str = email.getText().toString();
        password_str = password.getText().toString();

        /* Checking that the fields are not empty */
        if (TextUtils.isEmpty(email_str))
        {
            Toast.makeText(getApplicationContext(), "Enter email!", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            return;
        }
        if (TextUtils.isEmpty(password_str))
        {
            Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            return;
        }
        if (password_str.length() < MIN_PASSWORD_LEN)
        {
            Toast.makeText(getApplicationContext(), "Password needs to be at least " + Integer.toString(MIN_PASSWORD_LEN) + " characters", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            return;
        }

        auth.signInWithEmailAndPassword(email_str, password_str)
                .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(Login.this, "Auth failed", Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        } else {
                            FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
                            DatabaseReference databaseReference = mFirebaseDatabase.getReference("Users");
                            databaseReference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                                        User user = childDataSnapshot.getValue(User.class);
                                        assert user != null;
                                        if (user.getEmail().equals(email_str))
                                        {
                                            username = user.getUsername();
                                        }
                                    }
                                    dialog.dismiss();
                                }
                                @Override
                                public void onCancelled(DatabaseError firebaseError) {
                                    Log.e("The read failed: ", firebaseError.getMessage());
                                    dialog.dismiss();
                                }
                            });

                            User user = new User(username,  email_str);
                            Intent intent = new Intent(Login.this, MainApp.class);
                            intent.putExtra("user", user);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
    }

    public void signUp(View view)
    {
        Intent intent = new Intent(this, SignUp.class);
        startActivity(intent);
    }
}