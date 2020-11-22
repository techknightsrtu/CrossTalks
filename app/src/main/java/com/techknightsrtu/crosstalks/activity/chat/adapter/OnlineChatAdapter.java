package com.techknightsrtu.crosstalks.activity.chat.adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.techknightsrtu.crosstalks.R;
import com.techknightsrtu.crosstalks.activity.chat.onClickListeners.OnChatButtonClick;
import com.techknightsrtu.crosstalks.firebase.callbackInterfaces.GetUserOnlineStatus;
import com.techknightsrtu.crosstalks.helper.Avatar;
import com.techknightsrtu.crosstalks.firebase.FirebaseMethods;
import com.techknightsrtu.crosstalks.firebase.callbackInterfaces.GetOnlyUserData;
import com.techknightsrtu.crosstalks.models.User;
import com.techknightsrtu.crosstalks.activity.chat.viewholder.OnlineChatViewHolder;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class OnlineChatAdapter extends FirestoreRecyclerAdapter<User,OnlineChatViewHolder> {

    private static final String TAG = "OnlineChatAdapter";

    private final OnChatButtonClick onChatButtonClick;

    public OnlineChatAdapter(FirestoreRecyclerOptions<User> options, OnChatButtonClick onChatButtonClick ) {
        super(options);
        this.onChatButtonClick = onChatButtonClick;
    }

    @NonNull
    @Override
    public OnlineChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int pos) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.online_chat_item, parent, false);

        return new OnlineChatViewHolder(view,getItem(pos),onChatButtonClick);
    }


    @Override
    protected void onBindViewHolder(@NonNull final OnlineChatViewHolder holder, int position, @NonNull User model) {

        String userId = model.getUserId();

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

            FirebaseMethods.getUserOnlineStatus(userId, status -> {
                Log.d(TAG, "onBindViewHolder: " + status);

                if(status != null && status.equals("Online")){
                    holder.ivOnlineIndicator.setVisibility(View.VISIBLE);
                }else{
                    holder.ivOnlineIndicator.setVisibility(View.GONE);
                }
            });
        }



    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
