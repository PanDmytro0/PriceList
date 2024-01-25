package com.fernfog.pricelist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class DetailedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        Boolean action = intent.getBooleanExtra("action", false);

        if (action) {
            setContentView(R.layout.activity_detailed);

            ImageView imageView = findViewById(R.id.imageeView);

            String photoLink = intent.getStringExtra("photoLink");

            Glide.with(DetailedActivity.this).load(photoLink).into(imageView);
        } else {
            setContentView(R.layout.activity_detailed_std);

            ImageView imageView = findViewById(R.id.imageee);

            TextView textView = findViewById(R.id.NameTextView);
            TextView textViewDesc = findViewById(R.id.DescriptionTextView);
            TextView textViewCount = findViewById(R.id.countOrPackTextView);

            Glide.with(DetailedActivity.this).load(intent.getStringExtra("photoLink")).override(700, 700).into(imageView);
            textView.setText(intent.getStringExtra("name"));
            textViewCount.setText(intent.getStringExtra("count") + "/" + intent.getStringExtra("inPack"));
            textViewDesc.setText(intent.getStringExtra("desc"));

        }
    }
}