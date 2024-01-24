package com.fernfog.pricelist;

import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.Map;

public class ListActivity extends AppCompatActivity {

    private static final int MAX_ELEMENTS_PER_ROW = 5;
    private int currentElementCount = 0;
    private LinearLayout currentRowLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        try {
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "price.xlsm");
            FileInputStream fileInputStream = new FileInputStream(file);

            Workbook workbook = WorkbookFactory.create(fileInputStream);
            Sheet sheet = workbook.getSheetAt(1);

            LinearLayout parentLayout = findViewById(R.id.parentLayout);

            createNewRow(parentLayout);

            for (Row row : sheet) {
                String name = getCellValueAsString(row.getCell(0));
                String dataN = getCellValueAsString(row.getCell(13));

                if (!name.contains("Назва") && !name.contains("Прайс") && !name.equals("")) {
                    String photoLink = getCellValueAsString(row.getCell(1));
                    String count = getCellValueAsString(row.getCell(4));
                    String inPack = getCellValueAsString(row.getCell(6));
                    String description = getCellValueAsString(row.getCell(12));

                    addCardToView(name, photoLink, count, inPack, parentLayout, description);

                    if (++currentElementCount >= MAX_ELEMENTS_PER_ROW) {
                        createNewRow(parentLayout);
                    }
                }

                if (!dataN.equals("")) {
                    addCardToView(dataN, parentLayout);
                }
            }

            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createNewRow(LinearLayout parentLayout) {
        currentRowLayout = new LinearLayout(ListActivity.this);
        currentRowLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        currentRowLayout.setOrientation(LinearLayout.VERTICAL);
        parentLayout.addView(currentRowLayout);

        // Reset the counter for the new row
        currentElementCount = 0;
    }


    public void addCardToView(String photoLink, LinearLayout parentLayout) {
        CardView mCard = new CardView(ListActivity.this);
        LinearLayout.LayoutParams mCardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        mCardParams.setMargins(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16));
        mCard.setLayoutParams(mCardParams);
        mCard.setRadius(dpToPx(22));
        mCard.setCardElevation(dpToPx(4));
        mCard.setContentPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16));

        LinearLayout insideCardLayout = new LinearLayout(ListActivity.this);
        insideCardLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        insideCardLayout.setOrientation(LinearLayout.VERTICAL);

        ImageView mImageView = new ImageView(ListActivity.this);
        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dpToPx(200)
        );
        imageParams.setMargins(dpToPx(20), dpToPx(16), dpToPx(20), dpToPx(16));
        mImageView.setScaleType(ImageView.ScaleType.CENTER);
        mImageView.setLayoutParams(imageParams);

        Log.d("ImageLink", photoLink);
        Glide.with(ListActivity.this).load(photoLink).override(600, 600).into(mImageView);

        insideCardLayout.addView(mImageView);
        mCard.addView(insideCardLayout);

        currentRowLayout.addView(mCard);
    }


    public void addCardToView(String name, String photoLink, String count, String inPack, LinearLayout parentLayout, String description) {
        CardView mCard = new CardView(ListActivity.this);
        LinearLayout.LayoutParams mCardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        mCardParams.setMargins(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16));
        mCard.setLayoutParams(mCardParams);
        mCard.setRadius(dpToPx(22));
        mCard.setCardElevation(dpToPx(4));
        mCard.setContentPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16));

        LinearLayout insideCardLayout = new LinearLayout(ListActivity.this);
        insideCardLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        insideCardLayout.setOrientation(LinearLayout.VERTICAL);

        ImageView mImageView = new ImageView(ListActivity.this);
        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dpToPx(200)
        );
        imageParams.setMargins(dpToPx(20), dpToPx(16), dpToPx(20), dpToPx(16));
        mImageView.setScaleType(ImageView.ScaleType.CENTER);
        mImageView.setLayoutParams(imageParams);

        Glide.with(ListActivity.this).load(photoLink).override(600, 600).into(mImageView);

        TextView mText = new TextView(ListActivity.this);
        mText.setTextSize(dpToPx(10));
        mText.setText(name);
        Typeface customFont = ResourcesCompat.getFont(ListActivity.this, R.font.marmelad);
        mText.setTypeface(customFont);
        mText.setGravity(Gravity.CENTER);

        TextView emailText = new TextView(ListActivity.this);
        emailText.setTextColor(getResources().getColor(R.color.textColor));
        emailText.setTextSize(dpToPx(10));
        emailText.setText(count);
        emailText.setTypeface(customFont);
        emailText.setGravity(Gravity.CENTER);

        TextView dateText = new TextView(ListActivity.this);
        dateText.setTextColor(getResources().getColor(R.color.textColor));
        dateText.setTextSize(dpToPx(10));
        dateText.setText(inPack);
        dateText.setTypeface(customFont);
        dateText.setGravity(Gravity.CENTER);

        TextView descriptionText = new TextView(ListActivity.this);
        descriptionText.setTextSize(dpToPx(10));
        descriptionText.setText(description);
        descriptionText.setTypeface(customFont);
        descriptionText.setGravity(Gravity.CENTER);

        insideCardLayout.addView(mImageView);
        insideCardLayout.addView(emailText);
        insideCardLayout.addView(dateText);
        insideCardLayout.addView(mText);
        insideCardLayout.addView(descriptionText);
        mCard.addView(insideCardLayout);

        // Add the card to the current row layout
        currentRowLayout.addView(mCard);
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return Double.toString(cell.getNumericCellValue());
            default:
                return "";
        }
    }
}
