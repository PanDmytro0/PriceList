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
import androidx.viewpager2.widget.ViewPager2;

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
import java.util.ArrayList;
import java.util.Map;

public class ListActivity extends AppCompatActivity {

    private static final int MAX_ELEMENTS_PER_FRAGMENT = 10;
    private ViewPager2 viewPager;
    private MyFragmentPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        try {
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "price.xlsm");
            FileInputStream fileInputStream = new FileInputStream(file);

            Workbook workbook = WorkbookFactory.create(fileInputStream);
            Sheet sheet = workbook.getSheetAt(1);

            viewPager = findViewById(R.id.viewPager);
            adapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), getLifecycle());
            viewPager.setAdapter(adapter);

            ArrayList<MyData> arrayList = new ArrayList<>();
            int itemCount = 0;

            for (Row row : sheet) {
                String name = getCellValueAsString(row.getCell(0));
                String dataN = getCellValueAsString(row.getCell(13));

                if (name.equals("") && itemCount > 1) {
                        addFragmentAndUpdateAdapter(arrayList);
                        arrayList.clear();
                        itemCount = 0;
                }

                if (!name.contains("Назва") && !name.contains("Прайс") && !name.equals("")) {
                    String photoLink = getCellValueAsString(row.getCell(1));
                    String count = getCellValueAsString(row.getCell(4));
                    String inPack = getCellValueAsString(row.getCell(6));
                    String description = getCellValueAsString(row.getCell(12));

                    MyData myData = new MyData(name, photoLink, count, inPack, description);
                    arrayList.add((MyData) myData.clone());
                    itemCount++;

                    if (itemCount == MAX_ELEMENTS_PER_FRAGMENT) {
                        addFragmentAndUpdateAdapter(arrayList);
                        arrayList.clear();
                        itemCount = 0;
                    }
                }

                if (!dataN.equals("")) {
                    arrayList.add(new MyData(dataN));
                    itemCount++;

                    if (itemCount == MAX_ELEMENTS_PER_FRAGMENT) {
                        addFragmentAndUpdateAdapter(arrayList);
                        arrayList.clear();
                        itemCount = 0;
                    }
                }
            }

            if (!arrayList.isEmpty()) {
                addFragmentAndUpdateAdapter(arrayList);
            }

            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    private void addFragmentAndUpdateAdapter(ArrayList<MyData> arrayList) {
        adapter.addFragment(new ListFragment(new ArrayList<>(arrayList))); // Create a new ArrayList to avoid modifying the existing one
        Log.d("log", "added the fragment");
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
