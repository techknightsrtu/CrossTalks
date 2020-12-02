package com.techknightsrtu.crosstalks.app.feature.home.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.techknightsrtu.crosstalks.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class StoriesViewHolder extends RecyclerView.ViewHolder {

    public ImageView ivStory, ivAddIcon;
    public RelativeLayout rlStoryView;

    public StoriesViewHolder(@NonNull View itemView) {
        super(itemView);

        ivStory = itemView.findViewById(R.id.ivStory);
        ivAddIcon = itemView.findViewById(R.id.ivAddIcon);

        rlStoryView = itemView.findViewById(R.id.rlStoryView);
    }
}
