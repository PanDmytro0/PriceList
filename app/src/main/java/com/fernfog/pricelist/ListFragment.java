package com.fernfog.pricelist;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
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
    public ListFragment(ArrayList<MyData> myDataArrayList, boolean a) {
        this.a = a;
        this.myDataArrayList = myDataArrayList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view;
        SharedPreferences sharedPref = requireContext().getSharedPreferences(
                "MyPref", Context.MODE_PRIVATE);

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
        mCardParams.width = dpToPx(Integer.parseInt(sharedPreferences.getString("cardPreviewSizeW",  "310")));
        mCardParams.height = dpToPx(Integer.parseInt(sharedPreferences.getString("cardPreviewSizeH",  "310")));

        mCardParams.setMargins(dpToPx(Integer.parseInt(sharedPreferences.getString("marginsOfCards",  "16"))),
                dpToPx(Integer.parseInt(sharedPreferences.getString("marginsOfCards",  "16"))),
                dpToPx(Integer.parseInt(sharedPreferences.getString("marginsOfCards",  "16"))),
                dpToPx(Integer.parseInt(sharedPreferences.getString("marginsOfCards",  "16"))));

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

            Glide.with(requireContext())
                    .load(getImageUri(requireContext(), myData.photoLink + ".jpg"))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                    .into(imageButton);

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

    public Uri getImageUri(Context context, String imageName) {
        Uri imageUri = null;
        ContentResolver contentResolver = context.getContentResolver();

        // Construct the query to get the image with the specified name
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = { MediaStore.Images.Media._ID };
        String selection = MediaStore.Images.Media.DISPLAY_NAME + " = ?";
        String[] selectionArgs = new String[] { imageName };

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
                // If the cursor has data, move to the first item
                if (cursor.moveToFirst()) {
                    // Get the image URI from the cursor
                    @SuppressLint("Range") long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media._ID));
                    imageUri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, Long.toString(id));
                }
            } finally {
                cursor.close();
            }
        } else {
            Log.e("ImageLoader", "Cursor is null");
        }

        return imageUri;
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    public ArrayList<MyData> getMyDataArrayList() {
        return myDataArrayList;
    }
}
