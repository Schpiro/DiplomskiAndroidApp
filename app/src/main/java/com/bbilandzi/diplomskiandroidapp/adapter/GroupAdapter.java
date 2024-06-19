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

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupViewHolder> {

    private static List<UserGroup> groups = new ArrayList<>();

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
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    public void setGroups(List<UserGroup> groups) {
        this.groups = groups;
        notifyDataSetChanged();
    }

    static class GroupViewHolder extends RecyclerView.ViewHolder {

        TextView groupName;

        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            groupName = itemView.findViewById(R.id.text_view_group_name);

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




}
