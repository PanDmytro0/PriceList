package com.fernfog.pricelist;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.dropbox.core.DbxDownloader;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.DownloadErrorException;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.users.DbxUserUsersRequests;
import com.dropbox.core.v2.users.FullAccount;
import com.google.android.material.button.MaterialButton;

import org.apache.commons.io.FileUtils;
import org.apache.poi.sl.usermodel.Line;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Objects;

public class ListFragment extends Fragment {

    private ArrayList<MyData> myDataArrayList;
    private boolean a;
    DbxRequestConfig config;
    DbxClientV2 client;


    public ListFragment(ArrayList<MyData> myDataArrayList, boolean a) {
        this.a = a;
        this.myDataArrayList = myDataArrayList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view;
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

        if (!a) {
            view = inflater.inflate(R.layout.fragment_list, container, false);
            GridLayout gridLayout = view.findViewById(R.id.parentLayout);
            for (MyData myData : myDataArrayList) {
                addCardToView(myData, gridLayout);
            }
        } else {
            view = inflater.inflate(R.layout.fragment_list_a, container, false);
            ImageView imageView = view.findViewById(R.id.a);
            Glide.with(this).load(myDataArrayList.get(0).getPhotoLink()).into(imageView);
        }

        return view;
    }

    private void addCardToView(MyData myData, GridLayout parentLayout) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        CardView mCard = new CardView(requireContext());
        GridLayout.LayoutParams mCardParams = new GridLayout.LayoutParams();
        mCardParams.width = dpToPx(Integer.parseInt(sharedPreferences.getString("cardPreviewSize",  "310")));
        mCardParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        mCardParams.setMargins(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16));
        mCardParams.setGravity(Gravity.CENTER); // Center both horizontally and vertically within GridLayout
        mCard.setLayoutParams(mCardParams);
        mCard.setRadius(dpToPx(22));
        mCard.setCardElevation(dpToPx(4));
        mCard.setContentPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16));

        LinearLayout insideCardLayout = new LinearLayout(requireContext());
        insideCardLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        insideCardLayout.setOrientation(LinearLayout.VERTICAL);

        ImageButton imageButton = new ImageButton(requireContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dpToPx(Integer.parseInt(sharedPreferences.getString("imageSizeW",  "100"))), dpToPx(Integer.parseInt(sharedPreferences.getString("imageSizeH",  "100"))));
        params.gravity = Gravity.CENTER;
        imageButton.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        imageButton.setLayoutParams(params);
        imageButton.setBackgroundColor(getResources().getColor(R.color.transparentColor));

        imageButton.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), DetailedActivity.class);
            intent.putExtra("photoLink", myData.getPhotoLink());
            intent.putExtra("name", myData.getName());
            intent.putExtra("count", myData.getCount());
            intent.putExtra("inPack", myData.getInPack());
            intent.putExtra("desc", myData.getDescription());
            intent.putExtra("action", false);
            startActivity(intent);
        });
        File photo = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), myData.photoLink + ".jpg");

        if (photo.exists()) {
            Glide.with(requireContext())
                    .load(photo)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                    .into(imageButton);
        } else {
            new AsyncTask<Void, Void, byte[]>() {
                @Override
                protected byte[] doInBackground(Void... voids) {
                    try {
                        String dropboxFileName = "/image/" + myData.photoLink + ".jpg";

                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        DbxDownloader<FileMetadata> downloader = client.files().download(dropboxFileName);
                        downloader.download(outputStream);

                        byte[] photoo = outputStream.toByteArray();

                        try {
                            FileOutputStream fos = new FileOutputStream(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), myData.photoLink + ".jpg"));

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
                        Glide.with(requireContext())
                                .load(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), myData.photoLink + ".jpg"))
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                                .into(imageButton);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.execute();
        }

        TextView mText = new TextView(requireContext());
        mText.setTextSize(dpToPx(Integer.parseInt(sharedPreferences.getString("fontSize",  "12"))));
        mText.setText(myData.getName());
        Typeface customFont = ResourcesCompat.getFont(requireContext(), R.font.marmelad);
        mText.setTypeface(customFont);
        mText.setGravity(Gravity.CENTER);

        TextView dateText = new TextView(requireContext());
        dateText.setTextColor(getResources().getColor(R.color.textColor));
        dateText.setTextSize(dpToPx(Integer.parseInt(sharedPreferences.getString("fontSize",  "12"))));
        dateText.setText(myData.getCount() + "/" + myData.getInPack());
        dateText.setTypeface(customFont);
        dateText.setGravity(Gravity.RIGHT);

        insideCardLayout.addView(mText);

        insideCardLayout.addView(imageButton);


        if (myData.isOPT){
            dateText.setTextColor(getResources().getColor(R.color.textColorOPT));
            TextView mTex = new TextView(requireContext());
            mTex.setTextSize(dpToPx(Integer.parseInt(sharedPreferences.getString("fontSize",  "12")) / 2));
            mTex.setText("Опт ціна від ящ");
            mTex.setTypeface(customFont);
            mTex.setGravity(Gravity.RIGHT);
            mTex.setTextColor(getResources().getColor(R.color.textColorOPT2));

            insideCardLayout.addView(mTex);
        }
        insideCardLayout.addView(dateText);



        if (myData.isOIOD){
            ImageView imageView = new ImageView(requireContext());

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(dpToPx(20), dpToPx(20));

            Glide.with(requireContext()).load(R.drawable.atr_fill0_wght400_grad0_opsz24).into(imageView);

            imageView.setLayoutParams(layoutParams);
            insideCardLayout.addView(imageView);
        }

        mCard.addView(insideCardLayout);
        parentLayout.addView(mCard);
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}
