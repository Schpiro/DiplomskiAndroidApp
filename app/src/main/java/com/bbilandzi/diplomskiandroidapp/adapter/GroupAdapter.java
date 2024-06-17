package com.bbilandzi.diplomskiandroidapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bbilandzi.diplomskiandroidapp.R;
import com.bbilandzi.diplomskiandroidapp.model.UserGroup;

import java.util.ArrayList;
import java.util.List;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupViewHolder> {

    private List<UserGroup> groups = new ArrayList<>();
    private final OnGroupClickListener onGroupClickListener;

    public GroupAdapter(OnGroupClickListener onGroupClickListener) {
        this.onGroupClickListener = onGroupClickListener;
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group, parent, false);
        return new GroupViewHolder(view, onGroupClickListener);
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

        public GroupViewHolder(@NonNull View itemView, OnGroupClickListener onGroupClickListener) {
            super(itemView);
            groupName = itemView.findViewById(R.id.groupName);

            itemView.setOnClickListener(v -> onGroupClickListener.onGroupClick((UserGroup) v.getTag()));
        }
    }

    public interface OnGroupClickListener {
        void onGroupClick(UserGroup group);
    }
}
