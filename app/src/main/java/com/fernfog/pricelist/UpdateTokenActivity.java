package com.fernfog.pricelist;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Credentials;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UpdateTokenActivity extends AppCompatActivity {

    private Handler mHandler;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        editor = pref.edit();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_token);

        mHandler = new Handler(Looper.getMainLooper());

        findViewById(R.id.updateTokenButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference().child("dropBox").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (task.isSuccessful()) {
                            String token = String.valueOf(task.getResult().getValue());
                            new UpdateTokenTask().execute(token);
                        } else {
                            Toast.makeText(UpdateTokenActivity.this, "Error getting token from Firebase", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private class UpdateTokenTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... tokens) {
            OkHttpClient client = new OkHttpClient();

            RequestBody formBody = new FormBody.Builder()
                    .add("code", tokens[0].trim())
                    .add("grant_type", "authorization_code")
                    .build();

            Request request = new Request.Builder()
                    .url("https://api.dropbox.com/oauth2/token")
                    .post(formBody)
                    .header("Authorization", Credentials.basic("giz0b2124o2dxkp", "i6uppx2wyvjscml"))
                    .build();

            try (Response response = client.newCall(request).execute()) {
                return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (result != null) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (!jsonObject.getString("access_token").isEmpty()) {
                        String accessToken = jsonObject.getString("access_token");
                        editor.putString("token", accessToken);
                        editor.apply();

                        Toast.makeText(UpdateTokenActivity.this, getString(R.string.successUpdatedTokenText), Toast.LENGTH_LONG).show();
                        Log.wtf("wtf", accessToken);
                    } else {
                        Toast.makeText(UpdateTokenActivity.this, getString(R.string.errorText), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(UpdateTokenActivity.this, getString(R.string.errorText), Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(UpdateTokenActivity.this, getString(R.string.errorText), Toast.LENGTH_LONG).show();
            }
        }
    }
}
