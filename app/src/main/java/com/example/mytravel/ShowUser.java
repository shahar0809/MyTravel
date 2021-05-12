package com.example.mytravel;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class ShowUser extends AppCompatActivity {
    User currUser, inputUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_user);

        Intent intent = getIntent();
        this.currUser = (User) intent.getParcelableExtra("currUser");
        this.inputUser = (User) intent.getParcelableExtra("inputUser");
    }
}