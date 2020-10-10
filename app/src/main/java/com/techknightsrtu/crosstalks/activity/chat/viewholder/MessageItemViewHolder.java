package com.techknightsrtu.crosstalks.activity.chat.viewholder;

import android.view.View;
import android.widget.TextView;

import com.techknightsrtu.crosstalks.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MessageItemViewHolder extends RecyclerView.ViewHolder {

    public TextView tvMessage, tvTimeStamp;

    public MessageItemViewHolder(@NonNull View itemView) {
        super(itemView);

        tvMessage = itemView.findViewById(R.id.tvMessage);
        tvTimeStamp = itemView.findViewById(R.id.tvTimeStamp);
    }
}
