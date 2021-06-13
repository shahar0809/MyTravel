package com.example.mytravel;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;

public class SettingsActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener
{
    static final int LOG_OUT = 25;
    SwitchMaterial music;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        music = findViewById(R.id.musicSwitch);
        music.setOnCheckedChangeListener(this);
    }

    public void logOut(View view)
    {
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

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Intent intent = new Intent(this, MusicService.class);
        if(isChecked)
        {
            startService(intent);
        }
        else
         {
            stopService(intent);
        }
    }
}