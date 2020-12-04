package com.techknightsrtu.crosstalks.app.feature.chat.viewholder;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.techknightsrtu.crosstalks.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MessageItemViewHolder extends RecyclerView.ViewHolder {

    public TextView tvMessage, tvTimeStamp,tvMsgSeen;
    public LinearLayout ll1;

    public MessageItemViewHolder(@NonNull View itemView) {
        super(itemView);

        tvMessage = itemView.findViewById(R.id.tvMessage);
        tvTimeStamp = itemView.findViewById(R.id.tvTimeStamp);
        tvMsgSeen = itemView.findViewById(R.id.tvMsgSeen);

        ll1 = itemView.findViewById(R.id.ll1);
    }
}
