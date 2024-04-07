package com.fernfog.pricelist;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

public class ImageFragment extends Fragment {

    String image;
    DbxRequestConfig config;
    DbxClientV2 client;

    ImageFragment(String image) {
        this.image = image;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        SharedPreferences sharedPref = requireContext().getSharedPreferences(
                "MyPref", Context.MODE_PRIVATE);
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
                        Toast.makeText(requireContext(), getString(R.string.tokenRequiredText), Toast.LENGTH_LONG).show();
                    }
                } catch (DbxException ex1) {
                    ex1.printStackTrace();
                } catch (NullPointerException ex2) {
                    ex2.printStackTrace();
                }

                return null;
            }
        }.execute();

        Log.d("FromFragment", image);

        View view = inflater.inflate(R.layout.fragment_image, container, false);

        ImageView imageView = view.findViewById(R.id.imageViewFragment);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dpToPx(Integer.parseInt(sharedPref.getString("imageSizeW", "100")) * 5), dpToPx(Integer.parseInt(sharedPref.getString("imageSizeH", "100")) * 5));
        params.gravity = Gravity.CENTER;
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        imageView.setLayoutParams(params);
        imageView.setBackgroundColor(getResources().getColor(R.color.transparentColor));


        File photo = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), image);

        if (photo.exists()) {
            Glide.with(ImageFragment.this)
                    .load(photo)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                    .into(imageView);
        } else {
            new AsyncTask<Void, Void, byte[]>() {
                @Override
                protected byte[] doInBackground(Void... voids) {
                    try {
                        String dropboxFileName = "/image/" + image;

                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        DbxDownloader<FileMetadata> downloader = client.files().download(dropboxFileName);
                        downloader.download(outputStream);

                        byte[] photoo = outputStream.toByteArray();

                        try {
                            FileOutputStream fos = new FileOutputStream(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), image));

                            fos.write(photoo);
                            fos.close();
                        } catch (java.io.IOException e) {
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
                        Glide.with(ImageFragment.this)
                                .load(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), image))
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                                .into(imageView);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.execute();
        }


        return view;
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}
