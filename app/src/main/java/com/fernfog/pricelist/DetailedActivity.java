package com.fernfog.pricelist;

import static java.security.AccessController.getContext;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dropbox.core.DbxDownloader;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class DetailedActivity extends AppCompatActivity {

    DbxRequestConfig config;
    DbxClientV2 client;

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
            new AsyncTask<Void, Void, byte[]>() {
                @Override
                protected byte[] doInBackground(Void... voids) {
                    try {
                        String dropboxFileName = "/image/" + intent.getStringExtra("photoLink") + ".jpg";

                        config = new DbxRequestConfig("dropbox/APPLICATION-NAME-HERE");
                        client = new DbxClientV2(config, "sl.BvIh2t0MkxS7-7p06-k90EBAIihL5dvzBMdRH_-dTCc33bzRHFNGxi32-mPfBWCTlkuGNoNShzW71A_VHOlie0MglAmZT8gfqrn0AzDbaL83Fo1mTUfu8a-gPTUok24M0YFMc9vNToC7k8HNdCcehF4");

                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        DbxDownloader<FileMetadata> downloader = client.files().download(dropboxFileName);
                        downloader.download(outputStream);

                        return outputStream.toByteArray();

                    } catch (DbxException | IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                }

                @Override
                protected void onPostExecute(byte[] fileData) {
                    if (!isDestroyed() && fileData != null) {
                        ImageView imageView = findViewById(R.id.imageee);
                        Glide.with(DetailedActivity.this)
                                .load(fileData)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(imageView);
                    } else {
                        // Handle the case where the activity is destroyed or fileData is null
                    }
                }

            }.execute();
            textView.setText(intent.getStringExtra("name"));
            textViewCount.setText(intent.getStringExtra("count") + "/" + intent.getStringExtra("inPack"));
            textViewDesc.setText(intent.getStringExtra("desc"));

        }
    }
}