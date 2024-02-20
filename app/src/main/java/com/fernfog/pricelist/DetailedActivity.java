package com.fernfog.pricelist;

import static java.security.AccessController.getContext;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
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
import com.dropbox.core.v2.users.DbxUserUsersRequests;
import com.dropbox.core.v2.users.FullAccount;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class DetailedActivity extends AppCompatActivity {

    DbxRequestConfig config;
    DbxClientV2 client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPref = getSharedPreferences(
                "MyPref", Context.MODE_PRIVATE);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(DetailedActivity.this);
        config = new DbxRequestConfig("dropbox/Price1998");
        String stringggToken = sharedPref.getString("token", "none");
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    if (!stringggToken.equals("none")) {
                        client = new DbxClientV2(config, stringggToken);

                        DbxUserUsersRequests r1 = client.users();
                        FullAccount account = r1.getCurrentAccount();

                        Log.wtf("WTF", account.getName().getDisplayName());
                    } else {
                        Toast.makeText(DetailedActivity.this, getString(R.string.tokenRequiredText), Toast.LENGTH_LONG).show();
                    }
                } catch (DbxException ex1) {
                    ex1.printStackTrace();
                } catch (NullPointerException ex2) {
                    ex2.printStackTrace();
                }

                return null;
            }
        }.execute();

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
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dpToPx(Integer.parseInt(sharedPreferences.getString("imageSize",  "100"))), dpToPx(Integer.parseInt(sharedPreferences.getString("imageSize",  "100"))));
            params.gravity = Gravity.CENTER;
            imageView.setLayoutParams(params);
            imageView.setScaleType(ImageView.ScaleType.CENTER);
            imageView.setBackgroundColor(getResources().getColor(R.color.transparentColor));

            TextView textView = findViewById(R.id.NameTextView);

            TextView textViewDesc = findViewById(R.id.DescriptionTextView);
            TextView textViewCount = findViewById(R.id.countOrPackTextView);
            textView.setText(intent.getStringExtra("name"));
            textViewCount.setText(intent.getStringExtra("count") + "/" + intent.getStringExtra("inPack"));
            textViewDesc.setText(intent.getStringExtra("desc"));

            textView.setTextSize(dpToPx(Integer.parseInt(sharedPreferences.getString("fontSize",  "12"))));
            textViewCount.setTextSize(dpToPx(Integer.parseInt(sharedPreferences.getString("fontSize",  "12"))));
            textViewDesc.setTextSize(dpToPx(Integer.parseInt(sharedPreferences.getString("fontSize",  "12"))));


            File photo = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), intent.getStringExtra("photoLink") + ".jpg");

            if (photo.exists()) {
                Glide.with(DetailedActivity.this)
                        .load(photo)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                        .into(imageView);
            } else {
                new AsyncTask<Void, Void, byte[]>() {
                    @Override
                    protected byte[] doInBackground(Void... voids) {
                        try {
                            String dropboxFileName = "/image/" + intent.getStringExtra("photoLink") + ".jpg";

                            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                            DbxDownloader<FileMetadata> downloader = client.files().download(dropboxFileName);
                            downloader.download(outputStream);

                            byte[] photoo = outputStream.toByteArray();

                            try {
                                FileOutputStream fos = new FileOutputStream(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), intent.getStringExtra("photoLink") + ".jpg"));

                                fos.write(photoo);
                                fos.close();
                            }
                            catch (java.io.IOException e) {
                                Log.e("PictureDemo", "Exception in photoCallback", e);
                            }

                            return photoo;

                        } catch (DbxException | IOException e) {
                            e.printStackTrace();
                            return null;
                        }
                    }

                    @Override
                    protected void onPostExecute(byte[] fileData) {
                        try {
                            Glide.with(DetailedActivity.this)
                                    .load(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), DetailedActivity.this + ".jpg"))
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                                    .into(imageView);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.execute();

        }
    }
}

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}