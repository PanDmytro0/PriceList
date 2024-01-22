package com.fernfog.pricelist;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.net.MalformedURLException;
import java.net.UnknownHostException;

public class DownloadActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        TextInputLayout textInputLayout = findViewById(R.id.linkEditText);
        MaterialButton materialButton = findViewById(R.id.updatePriceButton);

        materialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String link = textInputLayout.getEditText().getText().toString().trim();
                if (!link.isEmpty()) {
                    new DownloadFileTask().execute(link);
                    materialButton.setActivated(false);
                } else {
                    Toast.makeText(DownloadActivity.this, getString(R.string.toastLinkEmptyText), Toast.LENGTH_LONG).show();
                    materialButton.setEnabled(true);
                }
            }
        });




    }

    private class DownloadFileTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String fileUrl = params[0];

            try {
                Document document = Jsoup.connect(fileUrl).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64)").get();

                FileDownloader fileDownloader = new FileDownloader();
                fileDownloader.downloadFile(getApplicationContext(), document.select("a.download-link").first().attr("href"), "price.xlsm");

                return getString(R.string.toastDownloadSuccesful);

            } catch (MalformedURLException e) {
                return getString(R.string.toastWrongLinkText);
            } catch (UnknownHostException e) {
                return getString(R.string.toastUnkownOrNoInternetText);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                Toast.makeText(DownloadActivity.this, result, Toast.LENGTH_LONG).show();
            }
        }
    }

}