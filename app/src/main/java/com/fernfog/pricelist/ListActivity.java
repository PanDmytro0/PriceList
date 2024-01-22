package com.fernfog.pricelist;

import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;

import com.bumptech.glide.Glide;


import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class ListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        try {
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "price.xlsm");
            FileInputStream fileInputStream = new FileInputStream(file);

            Workbook workbook = WorkbookFactory.create(fileInputStream);
            Sheet sheet = workbook.getSheetAt(1);

            // Iterate through rows
            for (Row row : sheet) {
                // Get data from specific columns E, G, A, C
                String dataE = getCellValueAsString(row.getCell(4)); // Column E (0-based index)
                String dataG = getCellValueAsString(row.getCell(6)); // Column G (0-based index)
                String dataA = getCellValueAsString(row.getCell(0)); // Column A (0-based index)

                // Get image data from column C
                byte[] imageData = getImageData(row.getCell(2)); // Column C (0-based index)

                // Save the image data to a file
                String imagePath = saveImageToFile(imageData);

                // Create a URI for the saved image file
                Uri imageUri = Uri.parse("file://" + imagePath);

                // Process the extracted data as needed (e.g., display in UI)
                Log.d("ExcelReader", "Data E: " + dataE);
                Log.d("ExcelReader", "Data G: " + dataG);
                Log.d("ExcelReader", "Data A: " + dataA);
                Log.d("ExcelReader", "Image URI: " + imageUri);
            }

            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

//    public void addCardToView(String shortText, String image, LinearLayout parentLayout, double latitude, double longitude, String emailText_, String date) {
//        CardView mCard = new CardView(ListActivity.this);
//        LinearLayout.LayoutParams mCardParams = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT
//        );
//        mCardParams.setMargins(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16));
//        mCard.setLayoutParams(mCardParams);
//        mCard.setRadius(dpToPx(22));
//        mCard.setCardElevation(dpToPx(4));
//        mCard.setContentPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16));
//
//        LinearLayout insideCardLayout = new LinearLayout(ListActivity.this);
//        insideCardLayout.setLayoutParams(new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT
//        ));
//        insideCardLayout.setOrientation(LinearLayout.VERTICAL);
//
//        ImageView mImageView = new ImageView(ListActivity.this);
//        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                dpToPx(200)
//        );
//        imageParams.setMargins(dpToPx(20), dpToPx(16), dpToPx(20), dpToPx(16));
//        mImageView.setScaleType(ImageView.ScaleType.CENTER);
//        mImageView.setLayoutParams(imageParams);
//
//        Glide.with(ListActivity.this).load(uri).into(mImageView);
//
//
//        TextView mText = new TextView(ListActivity.this);
//        mText.setTextSize(dpToPx(8));
//        mText.setText(shortText);
//        Typeface customFont = ResourcesCompat.getFont(ListActivity.this, R.font.marmelad);
//        mText.setTypeface(customFont);
//        mText.setGravity(Gravity.CENTER);
//
//        TextView emailText = new TextView(ListActivity.this);
//        emailText.setTextColor(Color.parseColor("#777a80"));
//        emailText.setTextSize(dpToPx(5));
//        emailText.setText(emailText_);
//        emailText.setTypeface(customFont);
//        emailText.setGravity(Gravity.LEFT);
//
//        TextView dateText = new TextView(ListActivity.this);
//        dateText.setTextColor(Color.parseColor("#777a80"));
//        dateText.setTextSize(dpToPx(5));
//        dateText.setText(date);
//        dateText.setTypeface(customFont);
//        dateText.setGravity(Gravity.LEFT);
//
//        insideCardLayout.addView(emailText);
//        insideCardLayout.addView(dateText);
//        insideCardLayout.addView(mImageView);
//        insideCardLayout.addView(mText);
//        mCard.addView(insideCardLayout);
//        parentLayout.addView(mCard);
//    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    // Helper method to get cell value as a string
    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return ""; // Return an empty string for null cells
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return Double.toString(cell.getNumericCellValue());
            // Add additional cases for other cell types if needed
            default:
                return "";
        }
    }

    // Helper method to get image data as a byte array
    private byte[] getImageData(Cell cell) {
        if (cell != null && cell.getCellType() == CellType.ERROR) {
            // Handle error cells (e.g., formula errors) if needed
            return new byte[0];
        }

        if (cell != null && cell.getHyperlink() != null) {
            // If there is a hyperlink, you can extract image data from the link (if it's an image)
            // Note: This is just a basic example and may not work in all cases
            // You might need to explore more complex scenarios based on your specific use case
            String hyperlink = cell.getHyperlink().getAddress();
            // Extract image data from the hyperlink and return as a byte array
            // (This part may need to be customized based on your specific requirements)
            return getImageDataFromLink(hyperlink);
        }

        return new byte[0];
    }

    // Helper method to save image data to a file and return the file path
    private String saveImageToFile(byte[] imageData) {
        try {
            File cacheDir = getCacheDir();
            File imageFile = new File(cacheDir, "output_image.png");

            try (FileOutputStream fileOutputStream = new FileOutputStream(imageFile)) {
                fileOutputStream.write(imageData);
            }

            return imageFile.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    // Helper method to get image data from a hyperlink (example)
    private byte[] getImageDataFromLink(String link) {
        // This is a placeholder method; you might need to implement your logic
        // based on the structure of the hyperlink or use external libraries to download images
        // For simplicity, this example returns an empty byte array
        return new byte[0];
    }
}