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
import androidx.constraintlayout.widget.Group;
import androidx.recyclerview.widget.RecyclerView;

import com.bbilandzi.diplomskiandroidapp.R;
import com.bbilandzi.diplomskiandroidapp.activity.MessengerActivity;
import com.bbilandzi.diplomskiandroidapp.model.UserDTO;
import com.bbilandzi.diplomskiandroidapp.model.UserGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupViewHolder> {

    private static List<UserGroup> groups = new ArrayList<>();

    private final List<Integer> avatarResIds;
    final Random random = new Random();

    public GroupAdapter() {
        // Initialize the list of avatars
        avatarResIds = new ArrayList<>();
        Collections.addAll(avatarResIds,
                R.drawable.vinyl_record,
                R.drawable.d20,
                R.drawable.people
        );
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group, parent, false);
        return new GroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        UserGroup group = groups.get(position);
        holder.groupName.setText(group.getGroupName());
        getImage(holder);
    }


    @Override
    public int getItemCount() {
        return groups.size();
    }

    public void setGroups(List<UserGroup> groups) {
        this.groups = groups;
        notifyDataSetChanged();
    }

    public class GroupViewHolder extends RecyclerView.ViewHolder {

        TextView groupName;

        ImageView groupImage;


        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            groupName = itemView.findViewById(R.id.text_view_group_name);
            groupImage = itemView.findViewById(R.id.image_view_group_avatar);
            itemView.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    UserGroup clickedGroup = groups.get(position);
                    onGroupClicked(clickedGroup);
                }
            });        }

        private void onGroupClicked(UserGroup group) {
            Context context = itemView.getContext();
            Intent intent = new Intent(context, MessengerActivity.class);
            intent.putExtra("recipientId", group.getId());
            intent.putExtra("recipientGroupName", group.getGroupName());
            intent.putExtra("recipientType", "group");
            context.startActivity(intent);
        }

    }

    public void getImage(GroupViewHolder group) {
        int position = group.getBindingAdapterPosition();
        if (position != RecyclerView.NO_POSITION) {
            UserGroup userGroup = groups.get(position);
            int avatarResId;
            avatarResId = avatarResIds.get(position%avatarResIds.size());
            group.groupImage.setImageResource(avatarResId);
        }
    }



}
