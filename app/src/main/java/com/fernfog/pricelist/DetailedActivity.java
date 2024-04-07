package com.fernfog.pricelist;

import static java.security.AccessController.getContext;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
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
import com.dropbox.core.DbxDownloader;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.users.DbxUserUsersRequests;
import com.dropbox.core.v2.users.FullAccount;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class DetailedActivity extends AppCompatActivity {

    DbxRequestConfig config;
    DbxClientV2 client;
    private MyFragmentPagerAdapter2 defadapter;

    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPref = getSharedPreferences(
                "MyPref", Context.MODE_PRIVATE);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(DetailedActivity.this);
        config = new DbxRequestConfig("dropbox/Price1998");
        String stringggToken = sharedPref.getString("token", "none");

        Intent intent = getIntent();

        ArrayList<String> imagesAvailable = new ArrayList<>();

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

            new AsyncTask<Void, Void, Void>() {
                @Override
                protected void onPostExecute(Void unused) {
                    String image = intent.getStringExtra("photoLink");

                    ArrayList<String> imagesToLoad = new ArrayList<>();

                    for (String i: imagesAvailable) {
                        if (i.contains(image)) {
                            imagesToLoad.add(i);
                        }
                    }

                    for (String i: imagesToLoad) {
                        defadapter.addFragment(new ImageFragment(i));
                    }
                }

                @Override
                protected Void doInBackground(Void... voids) {
                    try {
                        if (!stringggToken.equals("none")) {
                            client = new DbxClientV2(config, stringggToken);

                            try {
                                ListFolderResult result = client.files().listFolder("/image/");

                                while (true) {
                                    for (Metadata metadata : result.getEntries()) {
                                        imagesAvailable.add(metadata.getName());
                                    }

                                    if (!result.getHasMore()) {
                                        break;
                                    }

                                    result = client.files().listFolderContinue(result.getCursor());
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        } else {
                            Toast.makeText(DetailedActivity.this, getString(R.string.tokenRequiredText), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return null;
                }
            }.execute();
        }
    }
    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}