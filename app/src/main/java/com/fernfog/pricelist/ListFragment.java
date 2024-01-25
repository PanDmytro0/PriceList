package com.fernfog.pricelist;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;

import org.w3c.dom.Text;

import java.util.ArrayList;


public class ListFragment extends Fragment {

    ArrayList<MyData> myDataArrayList;

    public ListFragment(ArrayList<MyData> myDataArrayList) {
        this.myDataArrayList = myDataArrayList;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        Log.d("ListFragment", "onCreateView: DataArrayList size: " + myDataArrayList.size());

        GridLayout gridLayout = view.findViewById(R.id.parentLayout);
        for (MyData myData : myDataArrayList) {
            if (myData.getDescription() == null) {
                addCardToView(myData.getPhotoLink(), gridLayout);
            } else {
                addCardToView(myData.getName(), myData.getPhotoLink(), myData.getCount(), myData.getInPack(), gridLayout, myData);
            }
        }

        return view;
    }

    public void addCardToView(String photoLink, GridLayout parentLayout) {
        CardView mCard = new CardView(getContext());
        LinearLayout.LayoutParams mCardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        mCardParams.setMargins(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16));
        mCard.setLayoutParams(mCardParams);
        mCard.setRadius(dpToPx(22));
        mCard.setCardElevation(dpToPx(4));
        mCard.setContentPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16));

        LinearLayout insideCardLayout = new LinearLayout(getContext());
        insideCardLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        insideCardLayout.setOrientation(LinearLayout.VERTICAL);

        ImageView mImageView = new ImageView(getContext());
        mImageView.setScaleType(ImageView.ScaleType.CENTER);

        Glide.with(getContext()).load(photoLink).override(200, 200).into(mImageView);
        Typeface customFont = ResourcesCompat.getFont(getContext(), R.font.marmelad);

        MaterialButton materialButton = new MaterialButton(getContext());
        materialButton.setCornerRadius(dpToPx(8));
        materialButton.setBackgroundColor(getResources().getColor(R.color.tintColor));
        materialButton.setTypeface(customFont);
        materialButton.setTextSize(dpToPx(3));
        materialButton.setLayoutParams(new LinearLayout.LayoutParams(dpToPx(100), dpToPx(40)));
        materialButton.setText(getString(R.string.buttonTextOpenDetails));
        materialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), DetailedActivity.class);
                intent.putExtra("photoLink", photoLink);
                intent.putExtra("action", true);
                startActivity(intent);
            }
        });

        insideCardLayout.addView(mImageView);
        insideCardLayout.addView(materialButton);
        mCard.addView(insideCardLayout);
        parentLayout.addView(mCard);
    }


    public void addCardToView(String name, String photoLink, String count, String inPack, GridLayout parentLayout, MyData myData) {
        CardView mCard = new CardView(getContext());
        LinearLayout.LayoutParams mCardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        mCardParams.setMargins(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16));
        mCard.setLayoutParams(mCardParams);
        mCard.setRadius(dpToPx(22));
        mCard.setCardElevation(dpToPx(4));
        mCard.setContentPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16));

        LinearLayout insideCardLayout = new LinearLayout(getContext());
        insideCardLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        insideCardLayout.setOrientation(LinearLayout.VERTICAL);

        ImageView mImageView = new ImageView(getContext());
        mImageView.setScaleType(ImageView.ScaleType.CENTER);

        Glide.with(getContext()).load(photoLink).override(200, 200).into(mImageView);

        TextView mText = new TextView(getContext());
        mText.setTextSize(dpToPx(3));
        mText.setText(name);
        Typeface customFont = ResourcesCompat.getFont(getContext(), R.font.marmelad);
        mText.setTypeface(customFont);
        mText.setGravity(Gravity.CENTER);


        TextView dateText = new TextView(getContext());
        dateText.setTextColor(getResources().getColor(R.color.textColor));
        dateText.setTextSize(dpToPx(3));
        dateText.setText(inPack  + "/" + count);
        dateText.setTypeface(customFont);
        dateText.setGravity(Gravity.CENTER);

        MaterialButton materialButton = new MaterialButton(getContext());
        materialButton.setCornerRadius(dpToPx(8));
        materialButton.setBackgroundColor(getResources().getColor(R.color.tintColor));
        materialButton.setTypeface(customFont);
        materialButton.setTextSize(dpToPx(3));
        materialButton.setLayoutParams(new LinearLayout.LayoutParams(dpToPx(100), dpToPx(40)));
        materialButton.setText(getString(R.string.buttonTextOpenDetails));

        materialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), DetailedActivity.class);
                intent.putExtra("photoLink", photoLink);
                intent.putExtra("name", name);
                intent.putExtra("count", count);
                intent.putExtra("inPack", inPack);
                intent.putExtra("desc", myData.getDescription());
                intent.putExtra("action", false);
                startActivity(intent);
            }
        });


        insideCardLayout.addView(mImageView);
        insideCardLayout.addView(dateText);
        insideCardLayout.addView(mText);
        insideCardLayout.addView(materialButton);
        mCard.addView(insideCardLayout);

        // Add the card to the current row layout
        parentLayout.addView(mCard);
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }
}