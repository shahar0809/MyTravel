package com.example.mytravel;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;

import com.google.firebase.auth.FirebaseAuth;

public class SettingsActivity extends AppCompatActivity {
    static final int LOG_OUT = 25;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

    }

    public void logOut(View view) {
        FirebaseAuth.getInstance().signOut();
        setResult(LOG_OUT);
        finish();
    }

    public void goBack(View view) {
        finish();
    }

    public void changePassword(View view) {
        finish();
    }
}