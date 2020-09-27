package com.techknightsrtu.crosstalks.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Html;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.techknightsrtu.crosstalks.R;

public class ChooseCollegeActivity extends AppCompatActivity {

    private TextView tvChooseCollege;
    private Spinner spCollege;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_college);

        init();
        setupTvChooseCollege();
        setupSpinner();
    }

    private void setupSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.spinner_item, getResources()
                .getStringArray(R.array.college_name));//setting the country_array to spinner
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCollege.setAdapter(adapter);
    }

    private void setupTvChooseCollege() {
        tvChooseCollege.setText(Html.fromHtml("Choose your <font color=red>College 🏢"));
    }

    private void init() {
        tvChooseCollege = findViewById(R.id.tvChooseCollege);

        spCollege = findViewById(R.id.spCollege);
    }
}