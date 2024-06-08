package com.fernfog.pricelist;

import static com.fernfog.pricelist.ListActivity.ACTION_CUSTOM_BROADCAST;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.button.MaterialButton;

import java.util.HashSet;
import java.util.List;

public class ChangeDialog extends DialogFragment {

    List<ListFragment> m;
    private final MyFragmentPagerAdapter defadapter;

    ChangeDialog(MyFragmentPagerAdapter defadapter) {
        this.defadapter = defadapter;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_group, container, false);

        new LoadButtonsTask().execute();

        return view;
    }

    private class LoadButtonsTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            m = defadapter.getAll();
            for (final ListFragment mm : m) {
                for (final MyData mmm : mm.getMyDataArrayList()) {
                    if (mmm.nameOfGroup != null && !mmm.nameOfGroup.isEmpty()) {
                        Log.d("tag", mmm.nameOfGroup);
                        try {
                            final MaterialButton materialButton = new MaterialButton(requireContext());
                            materialButton.setText(mmm.nameOfGroup);

                            materialButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    int index = defadapter.getAll().indexOf(mm);

                                    Intent broadcastIntent = new Intent(ACTION_CUSTOM_BROADCAST);
                                    broadcastIntent.putExtra("change", index);
                                    requireContext().sendBroadcast(broadcastIntent);
                                    dismiss();
                                }
                            });

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    LinearLayout linearLayout = requireView().findViewById(R.id.buttonsLayout);
                                    linearLayout.addView(materialButton);
                                }
                            });

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            return null;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }
}