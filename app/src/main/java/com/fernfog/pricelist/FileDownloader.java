package com.fernfog.pricelist;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.util.ArrayList;

public class FileDownloader {

    public void downloadImage(Context context, FileToDownload fileToDownload) {
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.getExternalStorageDirectory().getPath()), fileToDownload.name);
            if (file.exists()) {
                file.delete();
            }

            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(fileToDownload.url));

            request.setTitle(fileToDownload.name);

            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, "priceList/" + fileToDownload.name);

            DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            long downloadId = downloadManager.enqueue(request);

    }

    public void downloadPrice(Context context, FileToDownload fileToDownload) {
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.getExternalStorageDirectory().getPath()), fileToDownload.name);
            if (file.exists()) {
                file.delete();
            }

            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(fileToDownload.url));

            request.setTitle(fileToDownload.name);

            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOCUMENTS, "priceList/" + fileToDownload.name);

            DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            long downloadId = downloadManager.enqueue(request);


    }
}