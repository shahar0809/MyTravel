package com.example.mytravel;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class SignUp extends Activity
{
    FirebaseAuth auth;
    FirebaseMethods firebaseMethods;
    AlertDialog dialog;

    /* Elements in xml */
    TextInputLayout emailInput, passwordInput, usernameInput;
    String username_str, email_str, password_str;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        bindElements();

        auth = FirebaseAuth.getInstance();
        firebaseMethods = new FirebaseMethods();

        /* User loading dialog */
        AlertDialog.Builder builder = new AlertDialog.Builder(SignUp.this);
        builder.setCancelable(false);
        builder.setView(R.layout.user_dialog);
        dialog = builder.create();
    }

    protected void bindElements()
    {
        usernameInput = findViewById(R.id.usernameField);
        emailInput = findViewById(R.id.emailField);
        passwordInput = findViewById(R.id.passwordField);
    }

    public void sign_up(View view)
    {
        dialog.show();

        // Fetching strings
        username_str = usernameInput.getEditText().getText().toString();
        email_str = emailInput.getEditText().getText().toString();
        password_str = passwordInput.getEditText().getText().toString();

        /* Checking that the fields are not empty */
        if (TextUtils.isEmpty(username_str) && !username_str.contains("~"))
        {
            Toast.makeText(getApplicationContext(), "Enter username!", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            return;
        }
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
        if (password_str.length() < Login.MIN_PASSWORD_LEN)
        {
            Toast.makeText(getApplicationContext(), "Password needs to be at least " + Integer.toString(Login.MIN_PASSWORD_LEN) + " characters", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            return;
        }

        // Firebase auth
        auth.createUserWithEmailAndPassword(email_str, password_str).
                addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        //Log.i("error is :", task.getException().getMessage());
                        Toast.makeText(SignUp.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        User user = new User(username_str,  email_str);
                        firebaseMethods.generateUser(username_str, email_str);

                        if (!task.isSuccessful()) {
                            dialog.dismiss();
                            Toast.makeText(SignUp.this, "Authentication failed." + task.getException(),
                                    Toast.LENGTH_SHORT).show();
                        }
                        else {
                            FirebaseUser currUser = FirebaseAuth.getInstance().getCurrentUser();
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(username_str).build();
                            currUser.updateProfile(profileUpdates);
                            dialog.dismiss();
                            Intent intent = new Intent(SignUp.this, MainApp.class);
                            intent.putExtra("user", user);
                            startActivity(intent);
                            finish();
                        }
                    }
                });

    }
}