package com.fernfog.pricelist;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
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

//import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.jsibbold.zoomage.ZoomageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageFragment extends Fragment {

    Uri image;

    ImageFragment(Uri image) {
        this.image = image;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        SharedPreferences sharedPref = requireContext().getSharedPreferences(
                "MyPref", Context.MODE_PRIVATE);

        View view = inflater.inflate(R.layout.fragment_image, container, false);

        ZoomageView imageView = view.findViewById(R.id.myZoomageView);

        //imageView.setOnTouchListener(new ImageMatrixTouchHandler(view.getContext()));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dpToPx(Integer.parseInt(sharedPref.getString("imageSizeW", "100")) * 5), dpToPx(Integer.parseInt(sharedPref.getString("imageSizeH", "100")) * 5));
        params.gravity = Gravity.CENTER;
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        imageView.setLayoutParams(params);
        imageView.setBackgroundColor(getResources().getColor(R.color.transparentColor));

            Glide.with(ImageFragment.this)
                    .load(image)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                    .into(imageView);


        return view;
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}
