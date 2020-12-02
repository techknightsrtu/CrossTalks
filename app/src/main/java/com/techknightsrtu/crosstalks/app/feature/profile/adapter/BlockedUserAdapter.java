package com.techknightsrtu.crosstalks.app.feature.profile.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.techknightsrtu.crosstalks.R;
import com.techknightsrtu.crosstalks.app.feature.home.interfaces.OnChatButtonClick;
import com.techknightsrtu.crosstalks.app.feature.profile.interfaces.OnUnblockButtonClick;
import com.techknightsrtu.crosstalks.app.feature.profile.viewholder.BlockedUserViewHolder;
import com.techknightsrtu.crosstalks.app.helper.Utility;
import com.techknightsrtu.crosstalks.app.helper.constants.Avatar;
import com.techknightsrtu.crosstalks.app.models.BlockedUser;
import com.techknightsrtu.crosstalks.firebase.FirebaseMethods;

import androidx.annotation.NonNull;

public class BlockedUserAdapter extends FirebaseRecyclerAdapter<BlockedUser,BlockedUserViewHolder> {

    private static final String TAG = "BlockedUserAdapter";

    private final OnUnblockButtonClick onUnblockButtonClick;
    private final LinearLayout llEmptyView;

    private Context context;

    public BlockedUserAdapter(@NonNull FirebaseRecyclerOptions<BlockedUser> options,
                              OnUnblockButtonClick onUnblockButtonClick, LinearLayout llEmptyView) {
        super(options);

        this.onUnblockButtonClick = onUnblockButtonClick;
        this.llEmptyView = llEmptyView;

    }

    @NonNull
    @Override
    public BlockedUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.blocked_chat_item, parent, false);

        context = parent.getContext();

        return new BlockedUserViewHolder(view);
    }


    @Override
    protected void onBindViewHolder(@NonNull BlockedUserViewHolder holder, int position,
                                    @NonNull BlockedUser model) {

        final String userId = getRef(position).getKey();
        String timestamp = model.getTimestamp();

        String msg = "Blocked Since " + Utility.getDateFromTimestamp(timestamp);

        FirebaseMethods.getOnlyUserData(userId, user -> {

            Log.d(TAG, "onBindViewHolder: " + user.toString());

            holder.ivUserAvatar.setImageResource(Avatar.avatarList.get(Integer.parseInt(user.getAvatarId())));
            holder.tvUserName.setText(Avatar.nameList.get(Integer.parseInt(user.getAvatarId())));
            holder.tvTimestamp.setText(msg);
            holder.svChatLoading.setVisibility(View.GONE);
            holder.rlChatLoaded.setVisibility(View.VISIBLE);

        });

        holder.tvUnblockChat.setOnClickListener(view -> {

            int avatarId = Avatar.nameList.indexOf(holder.tvUserName.getText().toString());

            onUnblockButtonClick.onUnblockClick(avatarId,userId);

        });

    }

    @Override
    public void onDataChanged() {
        llEmptyView.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }
}
