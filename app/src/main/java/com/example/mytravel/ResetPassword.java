package com.example.mytravel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPassword extends AppCompatActivity
{
    TextInputLayout emailInput;
    TextView confirmation;
    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        emailInput = findViewById(R.id.emailField);
        confirmation = findViewById(R.id.confirmation);

        /* User loading dialog */
        AlertDialog.Builder builder = new AlertDialog.Builder(ResetPassword.this);
        builder.setCancelable(false);
        builder.setView(R.layout.user_dialog);
        dialog = builder.create();
    }

    public void resetPassword(View view)
    {
        dialog.show();
        String email_str = emailInput.getEditText().getText().toString();

        /* Checking that the fields are not empty */
        if (TextUtils.isEmpty(email_str))
        {
            Toast.makeText(getApplicationContext(), "Enter email!", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            return;
        }

        FirebaseAuth.getInstance().sendPasswordResetEmail(email_str)
                .addOnCompleteListener(new OnCompleteListener<Void>()
                {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            Log.d("Reset", "Email sent.");
                            confirmation.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

}