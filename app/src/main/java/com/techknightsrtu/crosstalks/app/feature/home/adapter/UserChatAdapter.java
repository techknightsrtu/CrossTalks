package com.techknightsrtu.crosstalks.app.feature.home.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.techknightsrtu.crosstalks.R;
import com.techknightsrtu.crosstalks.app.feature.home.interfaces.OnChatButtonClick;
import com.techknightsrtu.crosstalks.app.helper.constants.Avatar;
import com.techknightsrtu.crosstalks.firebase.FirebaseMethods;
import com.techknightsrtu.crosstalks.firebase.callbackInterfaces.GetOnlyUserData;
import com.techknightsrtu.crosstalks.app.models.User;
import com.techknightsrtu.crosstalks.app.feature.home.viewholder.UserChatViewHolder;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class UserChatAdapter extends FirestoreRecyclerAdapter<User, UserChatViewHolder> {

    private static final String TAG = "OnlineChatAdapter";

    private final OnChatButtonClick onChatButtonClick;
    private LinearLayout llEmptyView;

    public UserChatAdapter(FirestoreRecyclerOptions<User> options, OnChatButtonClick onChatButtonClick,
                           LinearLayout llEmptyView) {
        super(options);
        this.onChatButtonClick = onChatButtonClick;
        this.llEmptyView = llEmptyView;
    }

    @NonNull
    @Override
    public UserChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int pos) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.online_chat_item, parent, false);

        return new UserChatViewHolder(view,getItem(pos),onChatButtonClick);
    }


    @Override
    protected void onBindViewHolder(@NonNull final UserChatViewHolder holder, int position, @NonNull User model) {

        String userId = model.getUserId();

        FirebaseMethods.getUserOnlineStatus(userId, status -> {
            Log.d(TAG, "onBindViewHolder: " + status);

            if(status != null && status.equals("Online")){
                holder.ivOnlineIndicator.setVisibility(View.VISIBLE);
            }else{
                holder.ivOnlineIndicator.setVisibility(View.GONE);
            }
        });

        if(userId.equals(FirebaseMethods.getUserId()))
        {

            holder.itemView.setVisibility(View.GONE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));

        }
        else
        {
            holder.svChatLoading.setVisibility(View.VISIBLE);
            holder.rlChatLoaded.setVisibility(View.GONE);

            FirebaseMethods.getOnlyUserData(userId, new GetOnlyUserData() {
                @Override
                public void onCallback(User user) {

                    holder.svChatLoading.setVisibility(View.GONE);
                    holder.rlChatLoaded.setVisibility(View.VISIBLE);

                    holder.ivUserAvatar.setImageResource(Avatar.avatarList.get(Integer.parseInt(user.getAvatarId())));
                    holder.tvUserName.setText(Avatar.nameList.get(Integer.parseInt(user.getAvatarId())));

                }
            });

            holder.rlChatLoaded.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int avatarId = Avatar.nameList.indexOf(holder.tvUserName.getText().toString());

                    onChatButtonClick.onChatClick(avatarId,model.getUserId());

                }
            });

            holder.tvStartChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int avatarId = Avatar.nameList.indexOf(holder.tvUserName.getText().toString());

                    onChatButtonClick.onChatClick(avatarId,model.getUserId());

                }
            });

        }

    }


    @Override
    public void onDataChanged() {
        llEmptyView.setVisibility(getItemCount() == 1 ? View.VISIBLE : View.GONE);
    }
}
