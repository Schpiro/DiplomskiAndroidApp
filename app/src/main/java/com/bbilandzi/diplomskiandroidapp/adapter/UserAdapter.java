package com.bbilandzi.diplomskiandroidapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bbilandzi.diplomskiandroidapp.R;
import com.bbilandzi.diplomskiandroidapp.model.UserDTO;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private List<UserDTO> users = new ArrayList<>();
    private final OnUserClickListener onUserClickListener;

    public UserAdapter(OnUserClickListener onUserClickListener) {
        this.onUserClickListener = onUserClickListener;
    }

    public void setUsers(List<UserDTO> users) {
        this.users = users;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view, onUserClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        UserDTO user = users.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        private final TextView userNameTextView;
        private final OnUserClickListener onUserClickListener;

        public UserViewHolder(@NonNull View itemView, OnUserClickListener onUserClickListener) {
            super(itemView);
            userNameTextView = itemView.findViewById(R.id.username);
            this.onUserClickListener = onUserClickListener;
        }

        public void bind(UserDTO user) {
            userNameTextView.setText(user.getUsername());
            itemView.setOnClickListener(v -> onUserClickListener.onUserClick(user));
        }
    }

    public interface OnUserClickListener {
        void onUserClick(UserDTO user);
    }
}
