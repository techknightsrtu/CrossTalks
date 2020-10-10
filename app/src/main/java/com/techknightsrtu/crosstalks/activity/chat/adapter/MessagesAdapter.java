package com.techknightsrtu.crosstalks.activity.chat.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.techknightsrtu.crosstalks.R;
import com.techknightsrtu.crosstalks.activity.chat.models.Message;
import com.techknightsrtu.crosstalks.activity.chat.viewholder.MessageItemViewHolder;
import com.techknightsrtu.crosstalks.firebase.FirebaseMethods;
import com.techknightsrtu.crosstalks.helper.Utility;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessageItemViewHolder> {


    public static final int MSG_TYPE_RECEIVED = 0;
    public static final int MSG_TYPE_SENT = 1;
    public int MSG_TYPE;
    private int lastPosition = -1;

    private List<Message> messages;

    private Activity activity;

    public MessagesAdapter(Activity activity, List<Message> messages) {
        this.activity = activity;
        this.messages = messages;
    }

    @NonNull
    @Override
    public MessageItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(activity);

        if(viewType == MSG_TYPE_SENT){
            MSG_TYPE = MSG_TYPE_SENT;

            View view = inflater.inflate(R.layout.sent_message_item, parent, false);
            return new MessageItemViewHolder(view);

        }else{
            MSG_TYPE = MSG_TYPE_RECEIVED;

            View view = inflater.inflate(R.layout.received_message_item, parent, false);
            return new MessageItemViewHolder(view);

        }

    }

    @Override
    public void onBindViewHolder(@NonNull final MessageItemViewHolder holder, int position) {

        Message m = messages.get(position);

        holder.tvMessage.setText(m.getMessage());

        String timeDate = Utility.getTimeFromTimestamp(m.getTimestamp()) + Utility.getDateFromTimestamp(m.getTimestamp());
        holder.tvTimeStamp.setText(timeDate);

        setAnimation(holder.tvMessage, position);

        holder.tvMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(holder.tvTimeStamp.getVisibility() == View.GONE)
                    holder.tvTimeStamp.setVisibility(View.VISIBLE);
                else if(holder.tvTimeStamp.getVisibility() == View.VISIBLE)
                    holder.tvTimeStamp.setVisibility(View.GONE);

            }
        });

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {

        if(messages.get(position).getSender().equals(FirebaseMethods.getUserId())){
                return MSG_TYPE_SENT;
        }else{
                return MSG_TYPE_RECEIVED;
        }
    }

    private void setAnimation(View viewToAnimate, int position)
    {
        if (position > lastPosition)
        {
            if(MSG_TYPE == MSG_TYPE_SENT){
                Animation expandIn = AnimationUtils.loadAnimation(activity, R.anim.send_msg_anim);
                viewToAnimate.startAnimation(expandIn);
            }else if(MSG_TYPE == MSG_TYPE_RECEIVED){
                Animation expandIn = AnimationUtils.loadAnimation(activity, R.anim.recieve_msg_anim);
                viewToAnimate.startAnimation(expandIn);
            }

            lastPosition = position;
        }
    }
}
