package com.techknightsrtu.crosstalks.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import com.techknightsrtu.crosstalks.R;
import com.techknightsrtu.crosstalks.helper.ItemOffsetDecoration;

public class ChooseAvatarActivity extends AppCompatActivity {

    private static final String TAG = "ChooseAvatarActivity";

    // Widgets
    private TextView tvChooseAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_avatar);

        init();
        setupTvChooseAvatar();
    }

    private void setupTvChooseAvatar() {
        tvChooseAvatar.setText(Html.fromHtml("Choose your <font color=red>Avatar 😎"));
    }

    private void init() {
        // Text view
        tvChooseAvatar = findViewById(R.id.tvChooseAvatar);

    }
}