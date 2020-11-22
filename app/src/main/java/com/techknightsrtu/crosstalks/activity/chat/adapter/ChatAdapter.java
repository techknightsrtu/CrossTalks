package com.techknightsrtu.crosstalks.activity.chat.adapter;

import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.techknightsrtu.crosstalks.R;
import com.techknightsrtu.crosstalks.activity.chat.models.EngagedChatChannel;
import com.techknightsrtu.crosstalks.activity.chat.models.Message;
import com.techknightsrtu.crosstalks.activity.chat.onClickListeners.OnChatButtonClick;
import com.techknightsrtu.crosstalks.activity.chat.viewholder.ChatViewHolder;
import com.techknightsrtu.crosstalks.firebase.ChatMethods;
import com.techknightsrtu.crosstalks.firebase.FirebaseMethods;
import com.techknightsrtu.crosstalks.firebase.callbackInterfaces.GetLastMessage;
import com.techknightsrtu.crosstalks.firebase.callbackInterfaces.GetOnlyUserData;
import com.techknightsrtu.crosstalks.firebase.callbackInterfaces.GetUserOnlineStatus;
import com.techknightsrtu.crosstalks.helper.Avatar;
import com.techknightsrtu.crosstalks.helper.Utility;
import com.techknightsrtu.crosstalks.models.User;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ChatAdapter extends FirebaseRecyclerAdapter<EngagedChatChannel,ChatViewHolder> {


    private static final String TAG = "ChatAdapter";
    
    private final OnChatButtonClick onChatButtonClick;
    private final LinearLayout llEmptyView;

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     * @param llEmpty
     */
    public ChatAdapter(@NonNull FirebaseRecyclerOptions<EngagedChatChannel> options,
                       OnChatButtonClick onChatButtonClick, LinearLayout llEmpty) {
        super(options);
        this.onChatButtonClick = onChatButtonClick;
        this.llEmptyView = llEmpty;
    }


    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.chat_item, parent, false);

        return new ChatViewHolder(view);
    }


    @Override
    protected void onBindViewHolder(@NonNull final ChatViewHolder holder, int position,
                                    @NonNull EngagedChatChannel model) {

        Log.d(TAG, "onBindViewHolder: " + model.getChannelId());

        final String userId = getRef(position).getKey();
        String channelId = model.getChannelId();

        FirebaseMethods.getOnlyUserData(userId, new GetOnlyUserData() {
            @Override
            public void onCallback(User user) {

                Log.d(TAG, "onBindViewHolder: " + user.toString());

                holder.ivUserAvatar.setImageResource(Avatar.avatarList.get(Integer.parseInt(user.getAvatarId())));
                holder.tvUserName.setText(Avatar.nameList.get(Integer.parseInt(user.getAvatarId())));
                holder.svChatLoading.setVisibility(View.GONE);
                holder.rlChatLoaded.setVisibility(View.VISIBLE);
            }
        });

        FirebaseMethods.getUserOnlineStatus(userId, new GetUserOnlineStatus() {
            @Override
            public void onCallback(String status) {

                Log.d(TAG, "onBindViewHolder: " + status);

                if(status != null && status.equals("Online")){
                    holder.ivOnlineIndicator.setVisibility(View.VISIBLE);
                }else{
                    holder.ivOnlineIndicator.setVisibility(View.GONE);
                }
            }
        });

        ChatMethods.getLastMessage(channelId, new GetLastMessage() {
            @Override
            public void onCallback(Message lastMessage) {

                if(lastMessage != null){

                    if(Utility.isYesterday(lastMessage.getTimestamp())){
                        holder.tvLastMessageTime.setText(Utility.getDateFromTimestamp(lastMessage.getTimestamp()));
                    }else{
                        holder.tvLastMessageTime.setText(Utility.getTimeFromTimestamp(lastMessage.getTimestamp()));
                    }

                    holder.tvLastMessage.setEllipsize(TextUtils.TruncateAt.END);
                    holder.tvLastMessage.setMaxLines(1);

                    if(lastMessage.getSender().equals(userId) && !lastMessage.getIsSeen())
                        holder.tvLastMessage.setTypeface(holder.tvLastMessage.getTypeface(), Typeface.BOLD_ITALIC);
                    else
                        holder.tvLastMessage.setTypeface(holder.tvLastMessage.getTypeface(), Typeface.NORMAL);

                    String msg = lastMessage.getMessage();
                    msg.replace(" ", "\u00A0");
                    holder.tvLastMessage.setText(msg);

                }
            }
        });

        holder.rlChatLoaded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int avatarId = Avatar.nameList.indexOf(holder.tvUserName.getText().toString());

                onChatButtonClick.onChatClick(avatarId,userId);
            }
        });

        holder.rlChatLoaded.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                onChatButtonClick.onChatLongClick(userId, holder.tvLastMessageTime);

                return true;
            }
        });
    }

    @Override
    public void onDataChanged() {
        llEmptyView.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }
}
