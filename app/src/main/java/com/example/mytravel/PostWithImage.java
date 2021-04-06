package com.example.mytravel;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;

public class PostWithImage
{
    private Post post;
    private Bitmap image;

    public PostWithImage(Post post, Bitmap image)
    {
        this.post = post;
        this.image = image;
    }

    public PostWithImage(LatLng location, String desc, String name, Bitmap image, User owner)
    {
        post = new Post(location, desc, name, owner);
        this.image = image;
    }

    public Bitmap getImage() { return image; }

    public LatLng getLocation() { return this.post.getLocation(); }

    public void setLocation(LatLng location) { this.post.setLocation(location); }

    public User getOwner() { return post.getOwner(); }

    public String getDescription() { return post.getDescription(); }

    public void setDescription(String description) { this.post.setDescription(description); }


}
