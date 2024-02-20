package com.fernfog.pricelist;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class ChangeSizeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_size);

        getFragmentManager().beginTransaction().add(R.id.idFrameLayout, new SettingsFragment()).commit();
    }
}