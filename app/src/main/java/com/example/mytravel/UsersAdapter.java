package com.example.mytravel;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder>
{
    private List<User> mUsers;
    private final OnUserClickListener mListener;
    private DatabaseReference reference;

    // Pass in the contact array into the constructor
    public UsersAdapter(List<User> users, DatabaseReference ref, OnUserClickListener listener)
    {
        mUsers = users;
        mListener = listener;
        reference = ref;
    }

    private void updateNames()
    {
        reference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                for (DataSnapshot user: dataSnapshot.getChildren())
                {
                    mUsers.add(new User(user));
                    notifyItemInserted(getItemCount() - 1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {
                Log.d("adapter error", error.getDetails());
            }
        });
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder
    {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView nameTextView;
        public ImageView profile;

        public ViewHolder(View itemView)
        {
            super(itemView);

            nameTextView = itemView.findViewById(R.id.username);
            profile = itemView.findViewById(R.id.profile);
        }

        public void bind(final User item, final OnUserClickListener listener)
        {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public UsersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View userView = inflater.inflate(R.layout.list_item, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(userView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(UsersAdapter.ViewHolder holder, int position)
    {
        User user = mUsers.get(position);
        if (user == null) { return; }

        TextView textView = holder.nameTextView;
        textView.setText(user.getUsername());

        textView.setText(user.getUsername());
        holder.bind(mUsers.get(position), mListener);
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount()
    {
        return mUsers.size();
    }
}


