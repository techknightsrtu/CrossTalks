package com.techknightsrtu.crosstalks.activity.chat.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.techknightsrtu.crosstalks.R;
import com.techknightsrtu.crosstalks.activity.chat.viewholder.MessageItemViewHolder;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MessagesAdapter extends RecyclerView.Adapter<MessageItemViewHolder> {

    private Activity activity;

    public MessagesAdapter(Activity activity) {
        this.activity = activity;
    }

    @NonNull
    @Override
    public MessageItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(activity);
        View view = inflater.inflate(R.layout.sended_message_item, parent, false);

        // This layout is for recieved messages
//        View view = inflater.inflate(R.layout.recieved_message_item, parent, false);

        return new MessageItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageItemViewHolder holder, int position) {

        holder.tvMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.tvTimeStamp.setVisibility(View.VISIBLE);
            }
        });

    }

    @Override
    public int getItemCount() {
        return 8;
    }
}
