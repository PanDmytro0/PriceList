package com.fernfog.pricelist;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.util.HashSet;

public class PassActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pass);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(PassActivity.this);
        TextInputLayout textInputLayout = findViewById(R.id.passEditText);
        String pass = sharedPreferences.getString("pass", "");

        if (pass.equals("")) {
            startActivity(new Intent(PassActivity.this, ChangeSizeActivity.class));
            finish();

        }
        findViewById(R.id.reviewPassButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textInputLayout.getEditText().getText().toString().trim().equals(pass.trim())) {
                    if (!getIntent().getStringExtra("group").equals("")) {
                        sendDataBackToActivityB();
                    } else {
                        startActivity(new Intent(PassActivity.this, ChangeSizeActivity.class));
                        finish();
                    }

                } else {
                    Toast.makeText(PassActivity.this, getString(R.string.toastWrongPassword), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void sendDataBackToActivityB() {
        Intent intent = new Intent();
        intent.putExtra("result_key", "send");
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

}