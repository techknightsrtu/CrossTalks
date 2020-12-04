package com.techknightsrtu.crosstalks.app.feature.chat.viewholder;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.techknightsrtu.crosstalks.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MessageItemViewHolder extends RecyclerView.ViewHolder {

    public TextView tvMessage, tvTimeStamp,tvMsgSeen, tvDirectReplyMessage;
    public LinearLayout ll1;
    public LinearLayout rlDirectReply;

    public MessageItemViewHolder(@NonNull View itemView) {
        super(itemView);

        tvMessage = itemView.findViewById(R.id.tvMessage);
        tvTimeStamp = itemView.findViewById(R.id.tvTimeStamp);
        tvMsgSeen = itemView.findViewById(R.id.tvMsgSeen);
        tvDirectReplyMessage = itemView.findViewById(R.id.tvDirectReplyMessage);

        ll1 = itemView.findViewById(R.id.ll1);
    }
}
