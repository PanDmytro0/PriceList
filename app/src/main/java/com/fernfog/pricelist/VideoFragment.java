package com.fernfog.pricelist;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.PlayerView;

public class VideoFragment extends Fragment {

    Uri video;
    private PlayerView playerView;
    private ExoPlayer player;

    VideoFragment(Uri video) {
        this.video = video;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video, container, false);

        SharedPreferences sharedPref = requireContext().getSharedPreferences(
                "MyPref", Context.MODE_PRIVATE);

        playerView = view.findViewById(R.id.player_view);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dpToPx(Integer.parseInt(sharedPref.getString("imageSizeW", "100")) * 5), dpToPx(Integer.parseInt(sharedPref.getString("imageSizeH", "100")) * 5));
        params.gravity = Gravity.CENTER;

        playerView.setLayoutParams(params);

        player = new ExoPlayer.Builder(requireContext()).build();
        playerView.setPlayer(player);

        MediaItem mediaItem = MediaItem.fromUri(video);
        player.setMediaItem(mediaItem);

        player.prepare();
        player.play();


        return view;
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}
