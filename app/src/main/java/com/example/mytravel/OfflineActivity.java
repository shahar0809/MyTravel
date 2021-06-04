package com.example.mytravel;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class OfflineActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline);
    }

    public void checkConnection(View view)
    {
        if(Utils.checkConnection(getApplicationContext()))
        {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }
}