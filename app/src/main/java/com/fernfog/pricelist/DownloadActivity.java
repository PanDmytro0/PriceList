package com.fernfog.pricelist;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class DownloadActivity extends AppCompatActivity {

    FirebaseStorage storage = FirebaseStorage.getInstance("gs://somephingg.appspot.com");
    int downloadCounter = 0;
    int filesCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        MaterialButton materialButton = findViewById(R.id.updatePriceButton);
        MaterialButton downloadFiles = findViewById(R.id.downloadImagesButton);

        downloadFiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadFiles.setActivated(false);
                storage.getReference().child("images").listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        filesCounter = listResult.getItems().size();

                        for (StorageReference item: listResult.getItems()) {
                            if (downloadCounter < filesCounter) {
                                Log.d("counters", "filesTotal: " + filesCounter + " , " + "downloaded: " + downloadCounter);
                                downloadCounter++;
                                item.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        new FileDownloader().downloadImage(getApplicationContext(), new FileToDownload(item.getName(), uri.toString()));
                                    }
                                });
                            } else {

                            }
                        }
                    }
                });

            }
        });

        materialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialButton.setActivated(false);
                StorageReference storageRef = storage.getReference();
                storageRef.child("price.xlsm").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        FileDownloader fileDownloader = new FileDownloader();
                        fileDownloader.downloadFile(getApplicationContext(), uri.toString(), "price.xlsm");
                    }
                });
            }
        });
    }
}