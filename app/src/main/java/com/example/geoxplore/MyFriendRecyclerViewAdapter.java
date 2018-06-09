package com.example.geoxplore;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.geoxplore.api.model.Friend;

import java.util.List;



public class MyFriendRecyclerViewAdapter extends RecyclerView.Adapter<MyFriendRecyclerViewAdapter.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    List<Friend> friends;
    String token;



    public MyFriendRecyclerViewAdapter(List<Friend> friends, String token) {
        this.friends = friends;
        this.token = token;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_friend, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        if (position > 0) {

            holder.mItem = getItem(position);
            holder.mLvl.setText(String.valueOf(getItem(position).getLevel()));
            holder.mName.setText(getItem(position).getUsername());
            holder.mChests.setText(String.valueOf(getItem(position).getOpenedChests()));
        }
        else{
            holder.mItem = null;
            holder.mLvl.setText("Level:");
            holder.mName.setText("Username:");
            holder.mChests.setText("Chests:");
        }


        holder.mView.setOnClickListener(v -> {

        });
    }

    @Override
    public int getItemCount() {
        return friends.size() + 1;
    }

    private Friend getItem(int position) {
        return friends.get(position - 1);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mLvl;
        public final TextView mName;
        public final TextView mChests;
        public Friend mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mLvl = (TextView) view.findViewById(R.id.friend_lvl);
            mName = (TextView) view.findViewById(R.id.friend_name);
            mChests = (TextView) view.findViewById(R.id.friend_chests);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mName.getText() + "'";
        }
    }
}
