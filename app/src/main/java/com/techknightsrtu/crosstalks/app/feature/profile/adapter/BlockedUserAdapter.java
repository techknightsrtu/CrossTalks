package com.techknightsrtu.crosstalks.app.feature.profile.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.techknightsrtu.crosstalks.R;
import com.techknightsrtu.crosstalks.app.feature.home.viewholder.UserChatViewHolder;
import com.techknightsrtu.crosstalks.app.feature.profile.viewholder.BlockedUserViewHolder;
import com.techknightsrtu.crosstalks.app.models.User;

import androidx.annotation.NonNull;

public class BlockedUserAdapter extends FirestoreRecyclerAdapter<User, BlockedUserViewHolder> {


    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public BlockedUserAdapter(@NonNull FirestoreRecyclerOptions<User> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull BlockedUserViewHolder holder, int position, @NonNull User model) {

    }

    @NonNull
    @Override
    public BlockedUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.blocked_chat_item, parent, false);

        return new BlockedUserViewHolder(view);
    }


}
