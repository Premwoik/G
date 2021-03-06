package com.example.geoxplore;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.geoxplore.api.model.UserStatsRanking;
import com.example.geoxplore.dummy.DummyContent;

import java.util.List;


///**
// * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
//// * specified {@link OnListFragmentInteractionListener}.
// */
public class MyRankingRecyclerViewAdapter extends RecyclerView.Adapter<MyRankingRecyclerViewAdapter.ViewHolder> {

    //private final List<DummyContent.DummyItem> mValues;
    private List<UserStatsRanking> usersStats;
//    private final OnListFragmentInteractionListener mListener;

    public MyRankingRecyclerViewAdapter(List<UserStatsRanking> items) {
        usersStats = items;
//        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_ranking, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        UserStatsRanking userStatsRanking = usersStats.get(position);
        String username = userStatsRanking.getUsername();
        int openedChests = userStatsRanking.getOpenedChests();
        int level = userStatsRanking.getLevel();

        holder.mUserInfo.setText(position +". "+ username+" "+level+"lvl "+openedChests+"oc");

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Clicked!", Toast.LENGTH_SHORT).show();
//                if (null != mListener) {
                // Notify the active callbacks interface (the activity, if the
                // fragment is attached to one) that an item has been selected.
//                    mListener.onListFragmentInteraction(holder.mItem);
//                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return usersStats.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mUserInfo;


        public ViewHolder(View view) {
            super(view);
            mView = view;
            mUserInfo = (TextView) view.findViewById(R.id.user_info);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mUserInfo.getText() + "'";
        }
    }
}
