package com.fernfog.pricelist;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONObject;

import okhttp3.Credentials;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UpdateTokenActivity extends AppCompatActivity {

    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_token);

        TextInputLayout textttt = findViewById(R.id.tokenEditText);

        mHandler = new Handler(Looper.getMainLooper());


        findViewById(R.id.openLinkButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://www.dropbox.com/oauth2/authorize?token_access_type=offline&response_type=code&client_id=giz0b2124o2dxkp")));
            }
        });

        findViewById(R.id.updateTokenButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        OkHttpClient client = new OkHttpClient();

                        RequestBody formBody = new FormBody.Builder()
                                .add("code", textttt.getEditText().getText().toString().trim())
                                .add("grant_type", "authorization_code")
                                .build();

                        Request request = new Request.Builder()
                                .url("https://api.dropbox.com/oauth2/token")
                                .post(formBody)
                                .header("Authorization", Credentials.basic("giz0b2124o2dxkp", "i6uppx2wyvjscml"))
                                .build();

                            try (Response response = client.newCall(request).execute()) {

                                String texttt = response.body().string();

                                JSONObject jsonObject = new JSONObject(texttt);

                                if (!jsonObject.getString("access_token").isEmpty()) {
                                    editor.putString("token", jsonObject.getString("access_token"));
                                    editor.apply();
                                    mHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(UpdateTokenActivity.this, getString(R.string.successUpdatedTokenText), Toast.LENGTH_LONG).show();
                                        }
                                    });
                                    Log.wtf("wtf", jsonObject.getString("access_token"));
                                } else {
                                    mHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(UpdateTokenActivity.this, getString(R.string.errorText), Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }

                                Log.wtf("wtf", texttt);

                            } catch (Exception e) {
                                Toast.makeText(UpdateTokenActivity.this, getString(R.string.errorText), Toast.LENGTH_LONG).show();
                            }

                    }
                };

                Thread thread = new Thread(runnable);

                thread.start();
            }
        });
    }
}