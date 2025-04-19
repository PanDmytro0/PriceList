package com.fernfog.pricelist;

import android.app.DownloadManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Debug;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class FileDownloader {

    public long downloadImage(Context context, FileToDownload fileToDownload, boolean partUp) {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "priceList/" + fileToDownload.name);
        long downloadId = 0;

        if (file.exists()) {
            if (partUp) {
                file.delete();
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(fileToDownload.url));

                request.setTitle(fileToDownload.name);

                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "priceList/" + fileToDownload.name);

                DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                downloadId = downloadManager.enqueue(request);
            }

        } else {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(fileToDownload.url));

            request.setTitle(fileToDownload.name);

            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "priceList/" + fileToDownload.name);

            DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            downloadId = downloadManager.enqueue(request);
        }

        return downloadId;
    }

    public long downloadPromotion(Context context, FileToDownload fileToDownload, boolean isVideo) {
        File file;
        long downloadId = 0;
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(fileToDownload.url));
        request.setTitle(fileToDownload.name);

        if (isVideo) {
            file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "priceList/promotions/videos/" + fileToDownload.name);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "priceList/promotions/videos/" + fileToDownload.name);
        } else {
            file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "priceList/promotions/" + fileToDownload.name);
            file.delete();

            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "priceList/promotions/" + fileToDownload.name);
        }

        if (file.exists()) {
        } else {
            DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            downloadId = downloadManager.enqueue(request);
        }

        return downloadId;
    }

    public long downloadVideo(Context context, FileToDownload fileToDownload) {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "priceList/videos/" + fileToDownload.name);
        long downloadId = 0;

        if (file.exists()) {
        } else {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(fileToDownload.url));

            request.setTitle(fileToDownload.name);

            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "priceList/videos/" + fileToDownload.name);

            DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            downloadId = downloadManager.enqueue(request);
        }
        return downloadId;
    }

    public long downloadGif(Context context, FileToDownload fileToDownload) {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "priceList/gifs/" + fileToDownload.name);
        long downloadId = 0;

        if (file.exists()) {
        } else {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(fileToDownload.url));

            request.setTitle(fileToDownload.name);

            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "priceList/gifs/" + fileToDownload.name);

            DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            downloadId = downloadManager.enqueue(request);
        }
        return downloadId;
    }

    public long downloadFile(Context context, String fileUrl, String fileName) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(fileUrl));

        request.setTitle(fileName);

        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOCUMENTS, "priceList/" + fileName);

        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        return downloadManager.enqueue(request);
    }

    public long downloadApk(Context context, String fileUrl, String fileName) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(fileUrl));

        request.setTitle(fileName);

        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOCUMENTS, "priceList/apks/" + fileName);

        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        return downloadManager.enqueue(request);
    }

    public static boolean deleteFileByPath(Context context, String filePath) {
        ContentResolver contentResolver = context.getContentResolver();
        Uri uriExternal = MediaStore.Files.getContentUri("external");

        String selection = MediaStore.Files.FileColumns.DATA + "=?";
        String[] selectionArgs = new String[]{filePath};

        Cursor cursor = contentResolver.query(uriExternal, new String[]{MediaStore.Files.FileColumns._ID}, selection, selectionArgs, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID));
                Uri uri = ContentUris.withAppendedId(uriExternal, id);
                int rowsDeleted = contentResolver.delete(uri, null, null);
                cursor.close();
                return rowsDeleted > 0;
            }
            cursor.close();
        }
        return false;
    }
}