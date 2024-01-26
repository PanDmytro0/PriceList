package com.fernfog.pricelist;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DownloadActivity extends AppCompatActivity {

    FirebaseStorage storage = FirebaseStorage.getInstance("gs://somephingg.appspot.com");

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
                    materialButton.setActivated(false);
                    StorageReference storageRef = storage.getReference();

                    storageRef.child(link).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            FileDownloader fileDownloader = new FileDownloader();
                            fileDownloader.downloadFile(getApplicationContext(), uri.toString(), "price.xlsm");
                        }
                    });

                } else {
                    Toast.makeText(DownloadActivity.this, getString(R.string.toastLinkEmptyText), Toast.LENGTH_LONG).show();
                    materialButton.setEnabled(true);
                }
            }
        });
    }
}