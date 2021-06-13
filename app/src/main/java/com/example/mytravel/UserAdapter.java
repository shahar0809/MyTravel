package com.example.mytravel;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class UserAdapter extends FirebaseRecyclerAdapter<User, UserAdapter.UserViewHolder>
{
    User currUser;

    public UserAdapter(@NonNull FirebaseRecyclerOptions<User> options, User user)
    {
        super(options);
        currUser = user;
    }

    @Override
    protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull final User model)
    {
        holder.username.setText(model.getUsername());
        holder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                stopListening();
                Intent intent = new Intent(v.getContext(), ShowUser.class);
                intent.putExtra("currUser", currUser);
                intent.putExtra("inputUser", model);
                v.getContext().startActivity(intent);
            }
        });
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new UserAdapter.UserViewHolder(view);
    }

    class UserViewHolder extends RecyclerView.ViewHolder
    {
        TextView username;
        public UserViewHolder(@NonNull View itemView)
        {
            super(itemView);
            username = itemView.findViewById(R.id.username);
        }
    }
}
