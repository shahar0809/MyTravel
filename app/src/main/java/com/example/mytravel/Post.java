package com.example.mytravel;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.maps.android.clustering.ClusterItem;

public class Post implements ClusterItem, Parcelable {
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

    public Post(DataSnapshot postSnapshot)
    {
        // Constructing all parameters of post
        name = postSnapshot.child("name").getValue(String.class);
        description = postSnapshot.child("description").getValue(String.class);
        owner = postSnapshot.child("owner").getValue(User.class);
        String latitude = postSnapshot.child("location").child("latitude").getValue().toString();
        String longitude = postSnapshot.child("location").child("longitude").getValue().toString();

        location = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
        imageLink = postSnapshot.child("imageLink").getValue(String.class);
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

    public LatLng getPosition() { return getLocation(); }

    public String getSnippet() { return getDescription(); }

    public String getTitle() { return getName(); }

    protected Post(Parcel in) {
        location = (LatLng) in.readValue(LatLng.class.getClassLoader());
        description = in.readString();
        name = in.readString();
        imageLink = in.readString();
        owner = (User) in.readValue(User.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(location);
        dest.writeString(description);
        dest.writeString(name);
        dest.writeString(imageLink);
        dest.writeValue(owner);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Post> CREATOR = new Parcelable.Creator<Post>() {
        @Override
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }

        @Override
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };
}