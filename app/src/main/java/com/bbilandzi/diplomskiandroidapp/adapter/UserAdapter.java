package com.bbilandzi.diplomskiandroidapp.adapter;

import static java.security.AccessController.getContext;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bbilandzi.diplomskiandroidapp.R;
import com.bbilandzi.diplomskiandroidapp.activity.MessengerActivity;
import com.bbilandzi.diplomskiandroidapp.model.UserDTO;
import com.bbilandzi.diplomskiandroidapp.model.UserGroup;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private static List<UserDTO> users = new ArrayList<>();

    public void setUsers(List<UserDTO> users) {
        this.users = users;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
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

    public static class UserViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewName;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.text_view_user_name);

            itemView.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    UserDTO clickedUser = users.get(position);
                    onUserClicked(clickedUser);
                }
            });
        }

        public void bind(UserDTO user) {
            textViewName.setText(user.getUsername());
        }

        private void onUserClicked(UserDTO user) {
            Context context = itemView.getContext();
            Intent intent = new Intent(context, MessengerActivity.class);
            intent.putExtra("recipientId", user.getId());
            intent.putExtra("recipientUsername", user.getUsername());
            intent.putExtra("recipientType", "user");
            context.startActivity(intent);
        }
    }


}
