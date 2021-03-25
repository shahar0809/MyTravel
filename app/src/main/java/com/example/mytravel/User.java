package com.example.mytravel;
import android.graphics.Bitmap;
import android.location.Location;

import java.util.TreeMap;

public class User
{
    String username;
    String email;
    String password;
    Bitmap profile_pic;
    TreeMap<Bitmap,Location> map;

    /* Empty c'tor for firebase */
    public User() {}

    public User(String username, String password, String email)
    {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }
}

