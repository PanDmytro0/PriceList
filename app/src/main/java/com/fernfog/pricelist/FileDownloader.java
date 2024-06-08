package com.fernfog.pricelist;

import android.app.DownloadManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.util.ArrayList;

public class FileDownloader {

    public void downloadImage(Context context, FileToDownload fileToDownload) {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "priceList/" + fileToDownload.name);

        if (file.exists()) {
        } else {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(fileToDownload.url));

            request.setTitle(fileToDownload.name);

            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "priceList/" + fileToDownload.name);

            DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            long downloadId = downloadManager.enqueue(request);
        }
    }

    public void downloadPromotion(Context context, FileToDownload fileToDownload, boolean isVideo) {
        File file;
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
            long downloadId = downloadManager.enqueue(request);
        }
    }

    public void downloadVideo(Context context, FileToDownload fileToDownload) {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "priceList/videos/" + fileToDownload.name);

        if (file.exists()) {
        } else {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(fileToDownload.url));

            request.setTitle(fileToDownload.name);

            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "priceList/videos/" + fileToDownload.name);

            DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            long downloadId = downloadManager.enqueue(request);
        }
    }

    public void downloadFile(Context context, String fileUrl, String fileName) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(fileUrl));

        request.setTitle(fileName);

        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOCUMENTS, "priceList/" + fileName);

        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        long downloadId = downloadManager.enqueue(request);
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