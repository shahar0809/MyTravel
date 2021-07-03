package com.example.mytravel;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.DataSnapshot;

import java.util.TreeMap;

/**
 * A class that represents a user in the system.
 * Each user has:
 * username: The user's display name
 * email: The user's email
 */
public class User implements Parcelable
{
    String username;
    String email;

    /**
     * Empty constructor for firebase
     */
    public User() {}

    /**
     * Constructs the User
     * @param username The user's display name
     * @param email The user's email
     */
    public User(String username, String email)
    {
        this.username = username;
        this.email = email;
    }

    /**
     * Constructs the user from a firebase snapshot.
     * @param snapshot: Snapshot from realtime database
     */
    public User(DataSnapshot snapshot)
    {
        username = snapshot.child("username").getValue(String.class);
        email = snapshot.child("email").getValue(String.class);
    }

    /**
     * Gets the username of the user.
     * @return the username
     */
    public String getUsername() { return username; }

    /**
     * Sets the username of the user
     * @param username the new username
     */
    public void setUsername(String username) { this.username = username; }

    /**
     * Gets the email of the user.
     * @return The email
     */
    public String getEmail() { return email; }

    protected User(Parcel in)
    {
        username = in.readString();
        email = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(username);
        dest.writeString(email);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>()
    {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}

