package com.example.mytravel;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;

public class Post
{
    LatLng location;
    String description;
    String name;
    User owner;

    public Post() {}

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public Post(LatLng location, String desc, String name, User owner)
    {
        this.location = location;
        this.description = desc;
        this.name = name;
        this.owner = owner;
    }

    public LatLng getLocation() { return location; }

    public void setLocation(LatLng location) { this.location = location; }

    public User getOwner() { return owner; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }
}
