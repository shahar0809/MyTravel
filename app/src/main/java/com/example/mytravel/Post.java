package com.example.mytravel;
import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;

public class Post {
    LatLng location;
    String description;
    String name;
    String imageLink;
    User owner;

    public Post() {
    }

    public String getName() {
        return name;
    }

    public String getImageLink() { return imageLink; }

    public void setName(String name) {
        this.name = name;
    }

    public void setImageLink(String link) { imageLink = link; }

    public Post(LatLng location, String desc, String name, User owner, Uri imageLink) {
        this.location = location;
        this.description = desc;
        this.name = name;
        this.owner = owner;
        this.imageLink = imageLink.toString();
    }

    public Post(LatLng location, String desc, String name, User owner, String imageLink) {
        this.location = location;
        this.description = desc;
        this.name = name;
        this.owner = owner;
        this.imageLink = imageLink;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public User getOwner() {
        return owner;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}