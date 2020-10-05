package com.techknightsrtu.crosstalks.activity.chat.adapter;

import android.app.Activity;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.techknightsrtu.crosstalks.R;
import com.techknightsrtu.crosstalks.activity.chat.viewholder.StoriesViewHolder;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class StoriesAdapter extends RecyclerView.Adapter<StoriesViewHolder> {

    private Activity activity;

    public StoriesAdapter(Activity activity) {
        this.activity = activity;
    }

    @NonNull
    @Override
    public StoriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        View view = layoutInflater.inflate(R.layout.story_item, parent, false);

        return new StoriesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoriesViewHolder holder, int position) {

        // For first position
        if(position == 0){
            holder.ivAddIcon.setVisibility(View.VISIBLE);
        }


        if(position > 3){
            ColorMatrix matrix = new ColorMatrix();
            matrix.setSaturation(0);
            ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);

            holder.ivStory.setColorFilter(filter);
        }

    }

    @Override
    public int getItemCount() {
        return 8;
    }

}
