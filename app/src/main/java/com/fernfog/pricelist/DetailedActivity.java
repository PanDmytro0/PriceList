package com.fernfog.pricelist;

import static java.security.AccessController.getContext;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DetailedActivity extends AppCompatActivity {
    private MyFragmentPagerAdapter2 defadapter;

    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPref = getSharedPreferences(
                "MyPref", Context.MODE_PRIVATE);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(DetailedActivity.this);

        Intent intent = getIntent();

        super.onCreate(savedInstanceState);

        Boolean action = intent.getBooleanExtra("action", false);

        if (action) {
            setContentView(R.layout.activity_detailed);

            ImageView imageView = findViewById(R.id.imageeView);

            String photoLink = intent.getStringExtra("photoLink");

            Glide.with(DetailedActivity.this).load(photoLink).into(imageView);
        } else {
            setContentView(R.layout.activity_detailed_std);

            CardView cardView = findViewById(R.id.cardddeded);
            LinearLayout.LayoutParams paramsmsmsm = new LinearLayout.LayoutParams(dpToPx(Integer.parseInt(sharedPreferences.getString("cardFullSize", "500"))), ViewGroup.LayoutParams.WRAP_CONTENT);
            paramsmsmsm.gravity = Gravity.CENTER;
            cardView.setLayoutParams(paramsmsmsm);

            LinearLayout linearLayout = findViewById(R.id.root);
            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DetailedActivity.super.onBackPressed();
                }
            });

            ViewPager2 viewPager2 = findViewById(R.id.imagePager);
            defadapter = new MyFragmentPagerAdapter2(getSupportFragmentManager(), getLifecycle());
            viewPager2.setAdapter(defadapter);

            TextView textView = findViewById(R.id.NameTextView);

            TextView textViewDesc = findViewById(R.id.DescriptionTextView);
            TextView textViewCount = findViewById(R.id.countOrPackTextView);
            textView.setText(intent.getStringExtra("name"));
            textViewCount.setText(intent.getStringExtra("count") + "/" + intent.getStringExtra("inPack"));
            textViewDesc.setText(intent.getStringExtra("desc"));

            textView.setTextSize(dpToPx(Integer.parseInt(sharedPreferences.getString("fontSize",  "12"))));
            textViewCount.setTextSize(dpToPx(Integer.parseInt(sharedPreferences.getString("fontSize",  "12"))));
            textViewDesc.setTextSize(dpToPx(Integer.parseInt(sharedPreferences.getString("fontSize",  "12"))));


            List<Uri> uris = searchImagesByName(this, intent.getStringExtra("photoLink"));

            for (Uri uri: uris) {
                defadapter.addFragment(new ImageFragment(uri));
            }
        }
    }

    public List<Uri> searchImagesByName(Context context, String searchString) {
        List<Uri> imageUris = new ArrayList<>();
        ContentResolver contentResolver = context.getContentResolver();

        // Construct the query to get images with names containing the search string
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = { MediaStore.Images.Media._ID };
        String selection = MediaStore.Images.Media.DISPLAY_NAME + " LIKE ?";
        String[] selectionArgs = new String[] { "%" + searchString + "%" };

        // Query the MediaStore
        Cursor cursor = contentResolver.query(
                uri,
                projection,
                selection,
                selectionArgs,
                null
        );

        if (cursor != null) {
            try {
                // Iterate through the cursor to get image URIs
                while (cursor.moveToNext()) {
                    // Get the image URI from the cursor
                    @SuppressLint("Range") long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media._ID));
                    Uri contentUri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, Long.toString(id));
                    imageUris.add(contentUri);
                }
            } finally {
                cursor.close();
            }
        } else {
            Log.e("ImageSearcher", "Cursor is null");
        }

        return imageUris;
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}