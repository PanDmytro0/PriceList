package com.fernfog.pricelist;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.util.Date;

public class MyForegroundService extends Service {

    private static final int NOTIFICATION_ID = 1;
    boolean anyUpdates;

    FirebaseStorage storage = FirebaseStorage.getInstance("gs://somephingg.appspot.com");

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Notification notification = createNotification();
        startForeground(NOTIFICATION_ID, notification);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();

        int INTERVAL_minutes = sharedPreferences.getInt("frequencyUpdates", 72) * 60 * 1000;
        int oneDayInMillis = sharedPreferences.getInt("sizeUpdates", 72) * 60 * 60 * 1000;
        anyUpdates = sharedPreferences.getBoolean("anyUpdates", false);

        Handler handler = new Handler();
        Runnable task = new Runnable() {
            @Override
            public void run() {

            if (anyUpdates) {
                sendRegularNotification("Встановіть нові оновлення!");
            }

                storage.getReference().child("promotions").child("image").listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        int counter = 0;
                        int sizeOfFiles = listResult.getItems().size();

                        for (StorageReference item: listResult.getItems()) {
                            if (counter < sizeOfFiles) {
                                Log.d("promotionImages", "filesTotal: " + sizeOfFiles + " , " + "downloaded: " + counter);
                                counter++;

                                item.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                                    @Override
                                    public void onSuccess(StorageMetadata storageMetadata) {
                                        String fileName = storageMetadata.getName();
                                        Date lastModified = new Date(storageMetadata.getUpdatedTimeMillis());

                                        long currentTimeMillis = System.currentTimeMillis();

                                        if (currentTimeMillis - storageMetadata.getUpdatedTimeMillis() < oneDayInMillis) {
                                            anyUpdates = true;
                                            sharedPreferencesEditor.putBoolean("anyUpdates", true).apply();
                                            Log.d("MyData", "changedValue");
                                        }
                                        
                                        Log.d("MyData", "name: " + fileName + ", lastModified: " + lastModified);
                                    }
                                });
                            }
                        }
                    }
                });

                Log.d("MyForegroundService", "Service is running");

                updateNotification("Перевірка оновлень...");

                // Повторно викликаємо task через 30 хвилин
                handler.postDelayed(this, INTERVAL_minutes);
            }
        };

        // Запуск задачі
        handler.post(task);

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Notification createNotification() {
        NotificationChannel channel = new NotificationChannel("default", "Default", NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);

        return new NotificationCompat.Builder(this, "default")
                .setContentTitle("Фонова робота")
                .setContentText("Перевірка оновлень...")
                .setSmallIcon(com.google.android.gms.base.R.drawable.common_google_signin_btn_icon_dark)
                .build();
    }

    private void updateNotification(String message) {
        Notification notification = new NotificationCompat.Builder(this, "default")
                .setContentTitle("Фонова робота")
                .setContentText(message)
                .setSmallIcon(com.google.android.gms.base.R.drawable.common_google_signin_btn_icon_dark)
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    private void sendRegularNotification(String message) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel channel = new NotificationChannel("updates", "Оновлення", NotificationManager.IMPORTANCE_HIGH);
        notificationManager.createNotificationChannel(channel);

        int notificationId = (int) System.currentTimeMillis();

        Notification notification = new NotificationCompat.Builder(this, "updates")
                .setContentTitle("Нові оновлення!")
                .setContentText(message)
                .setSmallIcon(com.google.android.gms.base.R.drawable.common_google_signin_btn_icon_dark)
                .setAutoCancel(true)
                .build();

        notificationManager.notify(notificationId, notification);
    }

}

