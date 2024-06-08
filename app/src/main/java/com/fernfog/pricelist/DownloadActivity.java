package com.fernfog.pricelist;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;

public class DownloadActivity extends AppCompatActivity {

    FirebaseStorage storage = FirebaseStorage.getInstance("gs://somephingg.appspot.com");
    int downloadCounter = 0;
    int filesCounter = 0;

    File file;
    private boolean isRunning = true;
    ProgressBar progressBar1;
    TextView percentageText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        MaterialButton materialButton = findViewById(R.id.updatePriceButton);

        MaterialButton downloadFiles = findViewById(R.id.downloadImagesButton);
        MaterialButton downloadPromotionImages = findViewById(R.id.downloadPromotions);
        MaterialButton downloadVideos = findViewById(R.id.downloadVideos);

        progressBar1 = findViewById(R.id.progressBar);
        percentageText = findViewById(R.id.percentage);

        file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "priceList/");

        storage.getReference().child("images").listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                filesCounter = listResult.getItems().size();
            }
        });

        downloadPromotionImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadPromotionImages.setActivated(false);
                storage.getReference().child("promotions").child("image").listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        int counter = 0;
                        int sizeOfFiles = listResult.getItems().size();

                        for (StorageReference item: listResult.getItems()) {
                            if (counter < sizeOfFiles) {
                                Log.d("promotionImages", "filesTotal: " + sizeOfFiles + " , " + "downloaded: " + counter);
                                counter++;
                                item.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        new FileDownloader().downloadPromotion(getApplicationContext(), new FileToDownload(item.getName(), uri.toString()), false);
                                    }
                                });
                            }
                        }
                    }
                });
            }
        });

        downloadVideos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadVideos.setActivated(false);
                storage.getReference().child("video").listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        int counter = 0;
                        int sizeOfFiles = listResult.getItems().size();

                        for (StorageReference item: listResult.getItems()) {
                            if (counter < sizeOfFiles) {
                                Log.d("videosProps", "filesTotal: " + sizeOfFiles + " , " + "downloaded: " + counter);
                                counter++;
                                item.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        new FileDownloader().downloadVideo(getApplicationContext(), new FileToDownload(item.getName(), uri.toString()));
                                    }
                                });
                            }
                        }
                    }
                });

                storage.getReference().child("promotions").child("video").listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        int counter = 0;
                        int sizeOfFiles = listResult.getItems().size();

                        for (StorageReference item: listResult.getItems()) {
                            if (counter < sizeOfFiles) {
                                Log.d("promotionVideos", "filesTotal: " + sizeOfFiles + " , " + "downloaded: " + counter);
                                counter++;
                                item.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        new FileDownloader().downloadPromotion(getApplicationContext(), new FileToDownload(item.getName(), uri.toString()), true);
                                    }
                                });
                            }
                        }
                    }
                });
            }
        });

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
                        Toast.makeText(DownloadActivity.this, getString(R.string.priceUpdatedText), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });


        if (file.exists() && file.isDirectory()) {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (isRunning) {
                        try {
                            Thread.sleep(5000);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateProgress();
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();

        }
    }

    void updateProgress() {
        try {
            double percentage = ((double) file.listFiles().length / (double) filesCounter) * 100;


            progressBar1.setProgress((int) percentage);
            percentageText.setText(Math.round(percentage) + "%");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isRunning = false;
    }
}