package com.techknightsrtu.crosstalks.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.techknightsrtu.crosstalks.R;
import com.techknightsrtu.crosstalks.viewholder.OnlineChatViewHolder;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class OnlineChatAdapter extends RecyclerView.Adapter<OnlineChatViewHolder> {

    private Activity activity;

    public OnlineChatAdapter(Activity activity) {
        this.activity = activity;
    }

    @NonNull
    @Override
    public OnlineChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(activity);
        View view = inflater.inflate(R.layout.online_chat_item, parent, false);

        return new OnlineChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OnlineChatViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 8;
    }
}
