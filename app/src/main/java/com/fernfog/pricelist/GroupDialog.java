package com.fernfog.pricelist;

import static com.fernfog.pricelist.ListActivity.ACTION_CUSTOM_BROADCAST;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.fragment.app.DialogFragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.material.button.MaterialButton;

import java.util.HashSet;

public class GroupDialog extends DialogFragment {

    HashSet<String> groupSet;

    GroupDialog(HashSet<String> groupSet) {
        this.groupSet = groupSet;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_group, container, false);

        for (String s: groupSet) {
            MaterialButton materialButton = new MaterialButton(requireContext());

            if (s.equals("")) {
                materialButton.setText(getString(R.string.seeAllButtonText));
            } else {
                materialButton.setText(s);
            }

            materialButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent broadcastIntent = new Intent(ACTION_CUSTOM_BROADCAST);
                    broadcastIntent.putExtra("group", s);
                    requireContext().sendBroadcast(broadcastIntent);
                    dismiss();
                }
            });

            LinearLayout linearLayout = view.findViewById(R.id.buttonsLayout);
            linearLayout.addView(materialButton);
        }

        return view;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null)
        {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }
}
