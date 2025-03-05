package com.bbilandzi.diplomskiandroidapp.adapter;

import static java.security.AccessController.getContext;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bbilandzi.diplomskiandroidapp.R;
import com.bbilandzi.diplomskiandroidapp.activity.MessengerActivity;
import com.bbilandzi.diplomskiandroidapp.model.UserDTO;
import com.bbilandzi.diplomskiandroidapp.model.UserGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private static List<UserDTO> users = new ArrayList<>();
    private final List<Integer> avatarResIds;
    final Random random = new Random();

    public UserAdapter() {
        // Initialize the list of avatars
        avatarResIds = new ArrayList<>();
        Collections.addAll(avatarResIds,
                R.drawable.drawable_avatar_1,
                R.drawable.drawable_avatar_2,
                R.drawable.drawable_avatar_3,
                R.drawable.drawable_avatar_4,
                R.drawable.drawable_avatar_5,
                R.drawable.drawable_avatar_6,
                R.drawable.drawable_avatar_7,
                R.drawable.drawable_avatar_8,
                R.drawable.drawable_avatar_9,
                R.drawable.drawable_avatar_10
        );
    }

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
        getImage(holder);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {

        private final TextView textViewName;
        private final ImageView imageViewAvatar;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.text_view_user_name);
            imageViewAvatar = itemView.findViewById(R.id.image_view_avatar);

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

    public void getImage(UserViewHolder user) {
        int position = user.getBindingAdapterPosition();
        if (position != RecyclerView.NO_POSITION) {
            UserDTO userDTO = users.get(position);
            int avatarResId;
            avatarResId = avatarResIds.get(position%avatarResIds.size());
            user.imageViewAvatar.setImageResource(avatarResId);
        }
    }
}
