package com.fernfog.pricelist;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageMetadata;
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
    ImageView imageView;
    boolean state = true;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (sharedPreferences.getBoolean("anyUpdates", false))
            Toast.makeText(this, "Встановіть оновлення!", Toast.LENGTH_LONG).show();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        MaterialButton materialButton = findViewById(R.id.updatePriceButton);

        MaterialButton downloadFiles = findViewById(R.id.downloadImagesButton);
        MaterialButton downloadPromotionImages = findViewById(R.id.downloadPromotions);
        MaterialButton downloadVideos = findViewById(R.id.downloadVideos);
        MaterialButton downloadAllButton = findViewById(R.id.downloadAll);
        MaterialButton partUpdateButton = findViewById(R.id.partUpdateButton);
        MaterialButton downloadGifsButton = findViewById(R.id.downloadGifs);

        Spinner spinner = findViewById(R.id.skladSpinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.spinner_array,
                R.layout.spinner_item
        );

        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                editor.putString("sklad", parent.getItemAtPosition(position).toString());
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        progressBar1 = findViewById(R.id.progressBar);
        percentageText = findViewById(R.id.percentage);
        imageView = findViewById(R.id.indicator);

        file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "priceList/");

        storage.getReference().child("images").listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                filesCounter = listResult.getItems().size();
            }
        });

        downloadAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadAllButton.setActivated(false);
                downloadImages();
                downloadVideos();
                downloadPromotions();
                downloadGifs();
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean("anyUpdates", false).apply();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(5000);
                            downloadPrice();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }).start();
            }
        });

        downloadPromotionImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadPromotionImages.setActivated(false);
                downloadPromotions();
            }
        });

        downloadVideos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadVideos.setActivated(false);
                downloadVideos();
            }
        });

        downloadFiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadFiles.setActivated(false);
                downloadImages();
            }
        });

        downloadGifsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadGifsButton.setActivated(false);
                downloadGifs();
            }
        });

        materialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialButton.setActivated(false);
                downloadPrice();
            }
        });

        partUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                partUpdateButton.setActivated(false);
                partUpdate();
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

    void downloadPrice() {
        StorageReference storageRef = storage.getReference();
        storageRef.child("price.xlsm").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                FileDownloader fileDownloader = new FileDownloader();
                fileDownloader.downloadFile(getApplicationContext(), uri.toString(), "price.xlsm");
            }
        });
    }

    BroadcastReceiver onComplete=new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent intent) {
            if (state) {
                imageView.setColorFilter(Color.argb(128,0, 128, 0));
                state = false;
            } else {
                imageView.setColorFilter(Color.argb(128,128, 0, 0));
                state = true;
            }
        }
    };

    void downloadImages() {
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
                                new FileDownloader().downloadImage(getApplicationContext(), new FileToDownload(item.getName(), uri.toString()), false);
                            }
                        });
                    } else {

                    }
                }
            }
        });
    }

    void partUpdate() {
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("файли_для_оновлення").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                for (String i: task.getResult().getValue().toString().split(",")) {
                    Log.d("tagtagtag", i);
                    StorageReference storageRef = storage.getReference();
                    storageRef.child("images").child(i+".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            // new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "priceList/promotions/").delete();
                            new FileDownloader().downloadImage(getApplicationContext(), new FileToDownload(i + ".jpg", uri.toString()), true);
                        }
                    });
                }
            }
        });


    }

    void downloadPromotions() {
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
                                new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "priceList/promotions/").delete();

                                new FileDownloader().downloadPromotion(getApplicationContext(), new FileToDownload(item.getName(), uri.toString()), false);
                            }
                        });
                    }
                }
            }
        });
    }

    void downloadVideos() {
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

    void downloadGifs() {
        storage.getReference().child("gifs").listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                int counter = 0;
                int sizeOfFiles = listResult.getItems().size();

                for (StorageReference item : listResult.getItems()) {
                    if (counter < sizeOfFiles) {
                        Log.d("gifsProps", "filesTotal: " + sizeOfFiles + " , " + "downloaded: " + counter);
                        counter++;
                        item.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                new FileDownloader().downloadGif(getApplicationContext(), new FileToDownload(item.getName(), uri.toString()));
                            }
                        });
                    }
                }
            }
        });
    }

    public static void listAndDeleteFiles(String directoryPath) {
        File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), directoryPath);

        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();

            if (files != null) {
                for (File file : files) {
                    if (file.getName().endsWith("-1.jpg") || file.getName().endsWith("-2.jpg") || file.getName().endsWith("-3.jpg")) {
                        System.out.println("Знайдено файл: " + file.getName());

                        if (file.delete()) {
                            Log.d("Файл видалено: ", file.getName());
                        } else {
                            Log.d("Не вдалося видалити: ", file.getName());
                        }
                    }
                }
            }
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