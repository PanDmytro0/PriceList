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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DetailedActivity extends AppCompatActivity {
    private MyFragmentPagerAdapter2 defadapter;

    FirebaseStorage storage = FirebaseStorage.getInstance();

    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPref = getSharedPreferences(
                "MyPref", Context.MODE_PRIVATE);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(DetailedActivity.this);

        Intent intent = getIntent();

        super.onCreate(savedInstanceState);

        boolean action = intent.getBooleanExtra("action", false);

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

            defadapter.addFragment(new ImageFragment(getMediaFileUri(this,Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/priceList/" + intent.getStringExtra("photoLink") + ".jpg")));


            for (int i = 0; i <= 10; i++) {
                Uri image = getMediaFileUri(this,Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/priceList/" + intent.getStringExtra("photoLink") + "_" + i + ".jpg");

                if (image != null) {
                    defadapter.addFragment((new ImageFragment(image)));
                }

            }

            String video = intent.getStringExtra("video");
            if (!video.isEmpty()) {
                defadapter.addFragment(new VideoFragment(getMediaFileUri(DetailedActivity.this, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/priceList/videos/" + video + ".mp4")));
            }

            TextView textView1 = findViewById(R.id.ImageCountText);

            int pos = viewPager2.getCurrentItem();
            pos+=1;

            textView1.setText(pos + "/" + defadapter.getItemCount());

            viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);

                    int pos = viewPager2.getCurrentItem();
                    pos+=1;

                    textView1.setText(pos + "/" + defadapter.getItemCount());
                }
            });
        }
    }

    public Uri getMediaFileUri(Context context, String filePath) {
        Uri mediaUri = null;

        try {
            ContentResolver contentResolver = context.getContentResolver();

            Uri uri = MediaStore.Files.getContentUri("external");

            String[] projection = { MediaStore.Files.FileColumns._ID };

            String selection = MediaStore.Files.FileColumns.DATA + " = ?";
            String[] selectionArgs = new String[] { filePath };

            Cursor cursor = contentResolver.query(
                    uri,
                    projection,
                    selection,
                    selectionArgs,
                    null
            );

            if (cursor != null) {
                try {
                    if (cursor.moveToFirst()) {
                        @SuppressLint("Range") long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns._ID));
                        mediaUri = Uri.withAppendedPath(uri, Long.toString(id));
                    }
                } finally {
                    cursor.close();
                }
            } else {
                Log.e("MediaLoader", "Cursor is null");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mediaUri;
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}