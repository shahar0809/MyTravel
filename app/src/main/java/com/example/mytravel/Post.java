package com.example.mytravel;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.maps.android.clustering.ClusterItem;

/**
 * A class that represents a post created by the user.
 * Each post contains:
 * location: The exact coordination of the post
 * description: A short sentence to describe the post.
 * name: The name of the post
 * imageLink: The link to the image of the post in firebase storage
 * owner: The user that uploaded the post
 * id: The key of the post node in firebase
 */
public class Post implements ClusterItem, Parcelable
{
    LatLng location;
    String description;
    String name;
    String imageLink;
    User owner;
    String id = "";

    public Post() { }

    /**
     * Gets the name of the post.
     * @return The post's name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the link of the post's image.
     * @return The link in firebase storage to the image
     */
    public String getImageLink() { return imageLink; }

    /**
     * Sets a new name to the post
     * @param name the new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the ID of the post.
     * @return The ID of the post node in firebase
     */
    public String getId() { return id; };

    /**
     * Constructor to the post object
     * @param location The location of the post
     * @param desc The description of the post
     * @param name The name of the post
     * @param owner The user that created the post
     * @param imageLink The link to the image in the storage
     */
    public Post(LatLng location, String desc, String name, User owner, Uri imageLink) {
        this.location = location;
        this.description = desc;
        this.name = name;
        this.owner = owner;
        this.imageLink = imageLink.toString();
    }

    /**
     * Constructor to the post object (with id)
     * @param id The id of post in firebase
     * @param location The location of the post
     * @param desc The description of the post
     * @param name The name of the post
     * @param owner The user that created the post
     * @param imageLink The link to the image in the storage
     */
    public Post(String id, LatLng location, String desc, String name, User owner, String imageLink) {
        this.id = id;
        this.location = location;
        this.description = desc;
        this.name = name;
        this.owner = owner;
        this.imageLink = imageLink;
    }

    /**
     * Constructor to post constructed from a data snapshot from firebase.
     * @param postSnapshot
     */
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

    /**
     * Gets the location of the post
     * @return The location of the post (in latLng format)
     */
    public LatLng getLocation() {
        return location;
    }

    /**
     * Gets the user that created the post
     * @return The user object
     */
    public User getOwner() {
        return owner;
    }

    /**
     * Gets the description of the post
     * @return the description
     */
    public String getDescription() {
        return description;
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