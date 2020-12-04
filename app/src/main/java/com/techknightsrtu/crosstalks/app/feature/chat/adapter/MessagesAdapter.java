package com.techknightsrtu.crosstalks.app.feature.chat.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.techknightsrtu.crosstalks.R;
import com.techknightsrtu.crosstalks.app.feature.chat.models.Message;
import com.techknightsrtu.crosstalks.app.feature.chat.models.MessageType;
import com.techknightsrtu.crosstalks.app.feature.chat.viewholder.MessageItemViewHolder;
import com.techknightsrtu.crosstalks.firebase.FirebaseMethods;
import com.techknightsrtu.crosstalks.app.helper.Utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MessagesAdapter extends FirebaseRecyclerAdapter<Message,MessageItemViewHolder> {

    private static final String TAG = "MessagesAdapter";

    public static final int MSG_TYPE_RECEIVED = 0;
    public static final int MSG_TYPE_SENT = 1;

    public int MSG_TYPE;

    int lastPosition = -1;

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public MessagesAdapter(@NonNull FirebaseRecyclerOptions<Message> options) {
        super(options);
    }


    @NonNull
    @Override
    public MessageItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

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
    protected void onBindViewHolder(@NonNull final MessageItemViewHolder holder, int position, @NonNull Message m) {

        if (m.getType() == MessageType.TEXT_REPLY){
            holder.tvDirectReplyMessage.setVisibility(View.VISIBLE);
            holder.tvDirectReplyMessage.setText(m.getReplyMessage());
        }

        holder.tvMessage.setText(m.getMessage());

        String timeDate = Utility.getTimeFromTimestamp(m.getTimestamp()) + ", " + Utility.getDateFromTimestamp(m.getTimestamp());
        holder.tvTimeStamp.setText(timeDate);

        setAnimation(holder.tvMessage, position);

        holder.ll1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(holder.tvTimeStamp.getVisibility() == View.GONE)
                    holder.tvTimeStamp.setVisibility(View.VISIBLE);
                else if(holder.tvTimeStamp.getVisibility() == View.VISIBLE)
                    holder.tvTimeStamp.setVisibility(View.GONE);

            }
        });

        if(position == getItemCount()-1){
            if(m.getIsSeen()){
                holder.tvMsgSeen.setText("Seen");
            }else{
                holder.tvMsgSeen.setText("Delivered");
            }

        }else{
            holder.tvMsgSeen.setVisibility(View.GONE);
        }

    }


    @Override
    public int getItemViewType(int position) {

        if(getItem(position).getSender().equals(FirebaseMethods.getUserId())){
                return MSG_TYPE_SENT;
        }else{
                return MSG_TYPE_RECEIVED;
        }
    }

    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition)
        {
            if(MSG_TYPE == MSG_TYPE_SENT){
                Animation expandIn = AnimationUtils.loadAnimation(viewToAnimate.getContext(), R.anim.send_msg_anim);
                viewToAnimate.startAnimation(expandIn);
            }else if(MSG_TYPE == MSG_TYPE_RECEIVED){
                Animation expandIn = AnimationUtils.loadAnimation(viewToAnimate.getContext(), R.anim.recieve_msg_anim);
                viewToAnimate.startAnimation(expandIn);
            }

            lastPosition = position;
        }

    }

}
