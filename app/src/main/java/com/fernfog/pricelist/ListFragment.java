package com.fernfog.pricelist;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;

import android.net.Uri;

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
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;

import org.apache.poi.sl.usermodel.Line;

import java.util.ArrayList;

public class ListFragment extends Fragment {
    private final ArrayList<MyData> myDataArrayList;
    private final boolean a;
    FirebaseStorage storage = FirebaseStorage.getInstance();

    public ListFragment(ArrayList<MyData> myDataArrayList, boolean a) {
        this.a = a;
        this.myDataArrayList = myDataArrayList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view;

        if (!a) {
            view = inflater.inflate(R.layout.fragment_list, container, false);
            GridLayout gridLayout = view.findViewById(R.id.parentLayout);

            for (MyData myData : myDataArrayList) {
                addCardToView(myData, gridLayout);
            }
        } else {
            if (myDataArrayList.get(0).getPhotoLink().toLowerCase().contains("video")) {

                Log.d("tagg", myDataArrayList.get(0).getPhotoLink().split("/")[1]);

                view = inflater.inflate(R.layout.fragment_list_video, container, false);

                PlayerView playerView = view.findViewById(R.id.player_view);

                ExoPlayer player = new ExoPlayer.Builder(requireContext()).build();
                playerView.setPlayer(player);

                MediaItem mediaItem = MediaItem.fromUri(getMediaFileUri(requireContext(), Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/priceList/videos/" + myDataArrayList.get(0).getPhotoLink().split("/")[1] + ".mp4"));
                player.setMediaItem(mediaItem);

                player.prepare();
                player.play();

            } else {
                view = inflater.inflate(R.layout.fragment_list_a, container, false);
                ImageView imageView = view.findViewById(R.id.a);

                Glide.with(requireContext()).load(getMediaFileUri(requireContext(), Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/priceList/promotions/" + myDataArrayList.get(0).getPhotoLink() + ".jpg")).into(imageView);
            }
        }

        return view;
    }

    public void addCardToView(MyData myData, GridLayout parentLayout) {
        Intent intent = new Intent(requireContext(), DetailedActivity.class);
        ImageButton imageButton = new ImageButton(requireContext());

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        CardView mCard = new CardView(requireContext());
        GridLayout.LayoutParams mCardParams = new GridLayout.LayoutParams();
        mCardParams.width = dpToPx(Integer.parseInt(sharedPreferences.getString("cardPreviewSizeW", "310")));
        mCardParams.height = dpToPx(Integer.parseInt(sharedPreferences.getString("cardPreviewSizeH", "310")));

        mCardParams.setMargins(dpToPx(Integer.parseInt(sharedPreferences.getString("marginsOfCards", "16"))),
                dpToPx(Integer.parseInt(sharedPreferences.getString("marginsOfCards", "16"))),
                dpToPx(Integer.parseInt(sharedPreferences.getString("marginsOfCards", "16"))),
                dpToPx(Integer.parseInt(sharedPreferences.getString("marginsOfCards", "16"))));

        mCardParams.setGravity(Gravity.CENTER);
        mCard.setLayoutParams(mCardParams);
        mCard.setRadius(dpToPx(22));
        mCard.setCardElevation(dpToPx(4));
        mCard.setContentPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16));

        LinearLayout insideCardLayout = new LinearLayout(requireContext());
        insideCardLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        ));
        insideCardLayout.setOrientation(LinearLayout.VERTICAL);

        imageButton.setId(View.generateViewId());
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(dpToPx(Integer.parseInt(sharedPreferences.getString("imageSizeW", "100"))), dpToPx(Integer.parseInt(sharedPreferences.getString("imageSizeH", "100"))));

        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

        imageButton.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        imageButton.setLayoutParams(params);
        imageButton.setBackgroundColor(getResources().getColor(R.color.transparentColor));

        imageButton.setOnClickListener(v -> {
            intent.putExtra("photoLink", myData.getPhotoLink());
            intent.putExtra("name", myData.getName());
            intent.putExtra("count", myData.getCount());
            intent.putExtra("inPack", myData.getInPack());
            intent.putExtra("desc", myData.getDescription());
            intent.putExtra("action", false);
            intent.putExtra("video", myData.getVideo());
            startActivity(intent);
        });

        TextView mText = new TextView(requireContext());
        mText.setTextSize(dpToPx(Integer.parseInt(sharedPreferences.getString("fontSize", "12"))));
        mText.setText(myData.getName());
        Typeface customFont = ResourcesCompat.getFont(requireContext(), R.font.marmelad);
        mText.setTypeface(customFont);

        LinearLayout.LayoutParams mTextParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mTextParams.gravity = Gravity.CENTER;
        mTextParams.setMargins(0, dpToPx(Integer.parseInt(sharedPreferences.getString("marginsTextTop", "0"))), 0, dpToPx(Integer.parseInt(sharedPreferences.getString("marginsTextBottom", "0"))));
        mText.setLayoutParams(mTextParams);

        TextView dateText = new TextView(requireContext());
        dateText.setTextColor(getResources().getColor(R.color.textColor));
        dateText.setTextSize(dpToPx(Integer.parseInt(sharedPreferences.getString("fontSize", "12"))));
        dateText.setText(myData.getCount() + "/" + myData.getInPack());
        dateText.setTypeface(customFont);

        LinearLayout.LayoutParams dateTextParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dateTextParams.gravity = Gravity.RIGHT;
        dateTextParams.setMargins(0, dpToPx(Integer.parseInt(sharedPreferences.getString("marginsCountTextTop", "0"))), 0, dpToPx(Integer.parseInt(sharedPreferences.getString("marginsCountTextBottom", "0"))));
        dateText.setLayoutParams(dateTextParams);

        insideCardLayout.addView(mText);

        RelativeLayout relativeLayout = new RelativeLayout(requireContext());
        relativeLayout.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(140)));
        relativeLayout.addView(imageButton);

        if (myData.isOIOD) {
            ImageView imageView = new ImageView(requireContext());

            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(dpToPx(20), dpToPx(20));
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

            Glide.with(requireContext()).load(R.drawable.atr_fill0_wght400_grad0_opsz24).into(imageView);

            imageView.setLayoutParams(layoutParams);
            relativeLayout.addView(imageView);
        }

        try {
            if (!myData.video.isEmpty()) {
                ImageView imageView = new ImageView(requireContext());

                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(dpToPx(20), dpToPx(20));
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);

                Glide.with(requireContext()).load(R.drawable.movie_24dp_fill0_wght400_grad0_opsz24).into(imageView);

                imageView.setLayoutParams(layoutParams);
                relativeLayout.addView(imageView);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

//        // Combined condition and ImageView placement
//        if (myData.isOIOD || !myData.video.isEmpty()) {
//            ImageView imageView = new ImageView(requireContext());
//
//            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(dpToPx(20), dpToPx(20));
//
//            if (myData.isOIOD) {
//                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
//            } else {
//                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
//                layoutParams.addRule(RelativeLayout.BELOW, imageButton.getId());
//            }
//
//            imageView.setLayoutParams(layoutParams);
//
//            Glide.with(requireContext())
//                    .load(myData.isOIOD ? R.drawable.atr_fill0_wght400_grad0_opsz24 : R.drawable.movie_24dp_fill0_wght400_grad0_opsz24)
//                    .into(imageView);
//
//            relativeLayout.addView(imageView);
//        }

        insideCardLayout.addView(relativeLayout);

        if (!myData.getSale().isEmpty()) {
//            dateText.setTextColor(getResources().getColor(R.color.textColorOPT));
            TextView mTex = new TextView(requireContext());
            mTex.setTextSize(dpToPx(Integer.parseInt(sharedPreferences.getString("fontSize2", "10"))));
            mTex.setText(myData.getSale());
//            mTex.setTypeface(customFont);
            mTex.setGravity(Gravity.RIGHT);
//            mTex.setTextColor(getResources().getColor(R.color.textColorOPT2));

            insideCardLayout.addView(mTex);
        }
        insideCardLayout.addView(dateText);

        mCard.addView(insideCardLayout);

        parentLayout.addView(mCard);

        Uri gif = getMediaFileUri(requireContext(),Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/priceList/gifs/" + myData.photoLink + ".gif");

        if (gif != null) {
            Glide.with(requireContext())
                    .load(gif)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                    .into(imageButton);
        } else {
            Glide.with(requireContext())
                    .load(getMediaFileUri(requireContext(), Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/priceList/" + myData.photoLink + ".jpg"))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                    .into(imageButton);
        }

    }


    public Uri getMediaFileUri(Context context, String filePath) {
        Uri mediaUri = null;
        ContentResolver contentResolver = context.getContentResolver();

        Uri uri = MediaStore.Files.getContentUri("external");

        String[] projection = {MediaStore.Files.FileColumns._ID};

        String selection = MediaStore.Files.FileColumns.DATA + " = ?";
        String[] selectionArgs = new String[]{filePath};

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

        return mediaUri;
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    public ArrayList<MyData> getMyDataArrayList() {
        return myDataArrayList;
    }
}
