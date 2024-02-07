package com.fernfog.pricelist;

import android.content.res.Configuration;
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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;


import org.apache.commons.io.FileUtils;
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
import java.io.OutputStream;
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
            Workbook workbook = WorkbookFactory.create(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "price.xlsm"));
            Sheet sheet = workbook.getSheetAt(0);

            viewPager = findViewById(R.id.viewPager);
            adapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), getLifecycle());
            viewPager.setAdapter(adapter);

            ArrayList<MyData> arrayList = new ArrayList<>();
            int itemCount = 0;

            for (Row row : sheet) {
                String name = getCellValueAsString(row.getCell(0));
                String dataN = getCellValueAsString(row.getCell(13));

                if (name.equals("") && itemCount > 1) {
                        addFragmentAndUpdateAdapter(arrayList, false);
                        arrayList.clear();
                        itemCount = 0;
                }

                if (!name.contains("Назва") && !name.contains("Прайс") && !name.equals("")) {
                    String id = getCellValueAsString(row.getCell(3));
                    String count = getCellValueAsString(row.getCell(4));
                    String inPack = getCellValueAsString(row.getCell(6));
                    String description = getCellValueAsString(row.getCell(12));
                    String check = getCellValueAsString(row.getCell(10));
                    String check__ = getCellValueAsString(row.getCell(11));

                    if (check__.equals("+")) {
                        MyData myData = new MyData(name, id, count, inPack, description, false, true);
                        arrayList.add((MyData) myData.clone());
                        itemCount++;
                    } else if (check.equals("+")){
                        MyData myData = new MyData(name, id, count, inPack, description, true, false);
                        arrayList.add((MyData) myData.clone());
                        itemCount++;
                    } else {
                        MyData myData = new MyData(name, id, count, inPack, description);
                        arrayList.add((MyData) myData.clone());
                        itemCount++;
                    }


                    if (itemCount == MAX_ELEMENTS_PER_FRAGMENT) {
                        addFragmentAndUpdateAdapter(arrayList, false);
                        arrayList.clear();
                        itemCount = 0;
                    }
                }

                if (!dataN.equals("")) {
                    arrayList.add(new MyData(dataN));
                    addFragmentAndUpdateAdapter(arrayList, true);
                    arrayList.clear();
                    itemCount = 0;
                }
            }

            if (!arrayList.isEmpty()) {
                addFragmentAndUpdateAdapter(arrayList, false);
            }

            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        } catch (OutOfMemoryError error) {
            Toast.makeText(ListActivity.this, getString(R.string.toastOutOfMemory), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void addFragmentAndUpdateAdapter(ArrayList<MyData> arrayList, boolean a) throws Exception {
        adapter.addFragment(new ListFragment(new ArrayList<>(arrayList), a)); // Create a new ArrayList to avoid modifying the existing one
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
                double numericValue = cell.getNumericCellValue();
                // Check if the numeric value is a whole number (no decimal part)
                if (numericValue == (long) numericValue) {
                    return String.valueOf((long) numericValue); // Convert to long to remove decimal if present
                } else {
                    return String.valueOf(numericValue);
                }
            default:
                return "";
        }
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Handle configuration changes here
    }

}
