package com.example.mytravel;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;

public class LoginAndSignup extends AppCompatActivity
{
    FirebaseAuth auth;

    /* Elements in xml */
    EditText username, password;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        bindElements();
        // Getting an instance of the Firebase auth process
        this.auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser()
        if (currentUser != null)
        {
            Intent intent = new Intent(this, MainApp.class);
            startActivity(intent);
        }
    }

    protected void bindElements()
    {
        this.username = findViewById(R.id.username_box);
        this.password = findViewById(R.id.password_box);
    }


    public void login(View view)
    {
        // Fetching strings
        String username_str = username.getText().toString();
        String password_str = password.getText().toString();

        // Trying to log in


    }
}