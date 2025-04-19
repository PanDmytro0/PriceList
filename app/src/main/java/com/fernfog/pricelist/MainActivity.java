package com.fernfog.pricelist;

import static com.fernfog.pricelist.DownloadActivity.listAndDeleteFiles;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.View;
import android.webkit.PermissionRequest;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.security.Permission;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_FILE = 1;
    FirebaseStorage storage = FirebaseStorage.getInstance("gs://somephingg.appspot.com");
    private DatabaseReference mDatabase;
    double version;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listAndDeleteFiles("priceList");
        listAndDeleteFiles("priceList/promotions/");

        mDatabase = FirebaseDatabase.getInstance().getReference();

        Intent serviceIntent = new Intent(this, MyForegroundService.class);
        startService(serviceIntent);

        Log.d("android", "" + android.os.Build.VERSION.SDK_INT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_MEDIA_IMAGES}, 0);
            }

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_VIDEO)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_MEDIA_VIDEO}, 0);
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED  || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            }
        }

        findViewById(R.id.updateButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, DownloadActivity.class));
            }
        });

        findViewById(R.id.testButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ChangeSizeActivity.class));
            }
        });

        findViewById(R.id.viewButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("application/*");

                startActivityForResult(intent, PICK_FILE);
            }
        });

        findViewById(R.id.updateAppButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabase.child("актуальна_версія").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        version = Double.parseDouble(task.getResult().getValue().toString());

                        try {
                            if (Double.parseDouble(getApplicationContext().getPackageManager()
                                    .getPackageInfo(getApplicationContext().getPackageName(), 0).versionName) < version) {

                                storage.getReference().child("apks").child(String.valueOf(version)).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        downloadAndInstallApk(getApplicationContext(), uri.toString(), version + ".apk");
                                    }
                                });

                            }
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }

                    }
                });
            }
        });


        if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("anyUpdates", false))
            Toast.makeText(this, "Встановіть оновлення!", Toast.LENGTH_LONG).show();
    }

    // Завантаження та автоматичне встановлення APK
    public void downloadAndInstallApk(Context context, String fileUrl, String fileName) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(fileUrl));

        // Інформація про завантаження
        request.setTitle("Завантаження оновлення");
        request.setDescription("Зачекайте, триває завантаження...");
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        long downloadId = downloadManager.enqueue(request);

        // Відстеження завершення завантаження
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (id == downloadId) {
                    Uri uri = downloadManager.getUriForDownloadedFile(downloadId);
                    if (uri != null) {
                        installApk(context, uri);
                    }
                    context.unregisterReceiver(this); // Від'єднання BroadcastReceiver
                }
            }
        };
        context.registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    // Метод для запуску встановлення APK
    private void installApk(Context context, Uri apkUri) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(intent);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_FILE) {
            if (data != null) {
                Toast.makeText(MainActivity.this, getString(R.string.toastReadingFileText), Toast.LENGTH_LONG).show();

                Intent intent = new Intent(MainActivity.this, ListActivity.class);

                intent.putExtra("file", data.getData());

                startActivity(intent);
            }
        }
    }
}
