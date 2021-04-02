package com.example.mytravel;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;

public class Post
{
    LatLng location;
    String description;
    Bitmap image;
    User owner;

    public LatLng getLocation() { return location; }

    public void setLocation(LatLng location) { this.location = location; }

    public Bitmap getImage() { return image; }

    public User getOwner() { return owner; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }
}
