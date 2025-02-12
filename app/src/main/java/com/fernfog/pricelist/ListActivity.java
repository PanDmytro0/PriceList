package com.fernfog.pricelist;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    private static final int MAX_ELEMENTS_PER_FRAGMENT = 10;
    private ViewPager2 viewPager;
    private MyFragmentPagerAdapter defadapter;
    public static final String ACTION_CUSTOM_BROADCAST = "com.example.ACTION_CUSTOM_BROADCAST";
    public HashSet<String> groupSet = new HashSet<>();
    SharedPreferences sharedPreferences;
    Workbook workbook;
    Sheet sheet;

    public ArrayList<Integer> changes = new ArrayList<>();
    public Integer index = 0;

    private DatabaseReference mDatabase;

    HashSet<String[]> skladi = new HashSet<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ListActivity.this);

        Toolbar myToolbar = findViewById(R.id.topBar);
        setSupportActionBar(myToolbar);

        Uri file = getIntent().getParcelableExtra("file");

        mDatabase = FirebaseDatabase.getInstance().getReference();

        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();

        mDatabase.child("розмір_шрифту").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                sharedPreferencesEditor.putString("fontSize", task.getResult().getValue().toString());
                sharedPreferencesEditor.apply();

            }
        });

        mDatabase.child("розмір_шрифту_2").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                sharedPreferencesEditor.putString("fontSize2", task.getResult().getValue().toString());
                sharedPreferencesEditor.apply();

            }
        });

        mDatabase.child("висота_картинки").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                sharedPreferencesEditor.putString("imageSizeH", task.getResult().getValue().toString());
                sharedPreferencesEditor.apply();

            }
        });

        mDatabase.child("ширина_картинки").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                sharedPreferencesEditor.putString("imageSizeW", task.getResult().getValue().toString());
                sharedPreferencesEditor.apply();

            }
        });

        mDatabase.child("ширина_малої_картки").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                sharedPreferencesEditor.putString("cardPreviewSizeW", task.getResult().getValue().toString());
                sharedPreferencesEditor.apply();

            }
        });

        mDatabase.child("висота_малої_картки").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                sharedPreferencesEditor.putString("cardPreviewSizeH", task.getResult().getValue().toString());
                sharedPreferencesEditor.apply();

            }
        });

        mDatabase.child("розміри_повної_картки").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                sharedPreferencesEditor.putString("cardFullSize", task.getResult().getValue().toString());
                sharedPreferencesEditor.apply();

            }
        });

        mDatabase.child("пароль_налаштувань").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                sharedPreferencesEditor.putString("pass", task.getResult().getValue().toString());
                sharedPreferencesEditor.apply();

            }
        });

        mDatabase.child("пароль_зміни_товарів").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                sharedPreferencesEditor.putString("passChange", task.getResult().getValue().toString());
                sharedPreferencesEditor.apply();

            }
        });

        mDatabase.child("пароль_зміни_груп").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                sharedPreferencesEditor.putString("passGroups", task.getResult().getValue().toString());
                sharedPreferencesEditor.apply();
            }
        });

        mDatabase.child("затримка_перелистування").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                sharedPreferencesEditor.putString("delayOfViewPager", task.getResult().getValue().toString());
                sharedPreferencesEditor.apply();

            }
        });

        mDatabase.child("відступи_між_картками").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                sharedPreferencesEditor.putString("marginsOfCards", task.getResult().getValue().toString());
                sharedPreferencesEditor.apply();

            }
        });

        mDatabase.child("відступ_назви_зверху").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                sharedPreferencesEditor.putString("marginsTextTop", task.getResult().getValue().toString());
                sharedPreferencesEditor.apply();

            }
        });

        mDatabase.child("відступ_назви_знизу").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                sharedPreferencesEditor.putString("marginsTextBottom", task.getResult().getValue().toString());
                sharedPreferencesEditor.apply();

            }
        });

        mDatabase.child("відступ_кількості_зверху").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                sharedPreferencesEditor.putString("marginsCountTextTop", task.getResult().getValue().toString());
                sharedPreferencesEditor.apply();

            }
        });

        mDatabase.child("відступ_кількості_знизу").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                sharedPreferencesEditor.putString("marginsCountTextBottom", task.getResult().getValue().toString());
                sharedPreferencesEditor.apply();

            }
        });

        try {
            workbook = WorkbookFactory.create(getContentResolver().openInputStream(file));
            sheet = workbook.getSheetAt(0);

            viewPager = findViewById(R.id.viewPager);
            defadapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), getLifecycle());
            viewPager.setAdapter(defadapter);

            viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    int delay = Integer.parseInt(sharedPreferences.getString("delayOfViewPager", "1")) * 100;

                    viewPager.setUserInputEnabled(false);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            viewPager.setUserInputEnabled(true);
                        }
                    }, delay);
                }
            });

            ArrayList<MyData> arrayList = new ArrayList<>();
            final int[] itemCount = {0};
            final String[] dataN = {""};

            for (Row row : sheet) {

                try {
                    String name = getCellValueAsString(row.getCell(0));
                    dataN[0] = getCellValueAsString(row.getCell(13));

                    String sklad = getCellValueAsString(row.getCell(23));

                    boolean skladState = true;

                    for (String i: sklad.split(",")) {
                        if (sklad.isEmpty()) {
                            skladState = false;
                        }
                        else if (i.trim().contains(sharedPreferences.getString("sklad", ""))) {
                            skladState = false;
                        }
                    }

                    if (skladState) {
                        continue;
                    }

                    String[] groups = getCellValueAsString(row.getCell(18)).split(",");

                    Collections.addAll(groupSet, groups);

                    if (name.equals("") && itemCount[0] > 1) {
                        addFragmentAndUpdateAdapter(arrayList, false);
                        arrayList.clear();
                        itemCount[0] = 0;
                    }

                    if (!name.contains("Назва") && !name.contains("Прайс") && !name.equals("")) {
                        String id = getCellValueAsString(row.getCell(3));
                        String count = getCellValueAsString(row.getCell(4));
                        String inPack = getCellValueAsString(row.getCell(6));
                        String description = getCellValueAsString(row.getCell(12));
                        String check = getCellValueAsString(row.getCell(10));
                        String check__ = getCellValueAsString(row.getCell(11));
                        String nameOfGroup = getCellValueAsString(row.getCell(19));
                        String video = getCellValueAsString(row.getCell(20));
                        String sale  = getCellValueAsString(row.getCell(29));

                        if (check__.equals("+")) {
                            MyData myData = new MyData(name, id, count, inPack, description, false, true, nameOfGroup);
                            myData.setVideo(video);
                            myData.setSale(sale);
                            arrayList.add((MyData) myData.clone());
                            itemCount[0]++;
                        } else if (check.equals("+")){
                            MyData myData = new MyData(name, id, count, inPack, description, true, false, nameOfGroup);
                            myData.setVideo(video);
                            myData.setSale(sale);
                            arrayList.add((MyData) myData.clone());
                            itemCount[0]++;
                        } else {
                            MyData myData = new MyData(name, id, count, inPack, description, nameOfGroup);
                            myData.setVideo(video);
                            myData.setSale(sale);
                            arrayList.add((MyData) myData.clone());
                            itemCount[0]++;
                        }

                        if (itemCount[0] == MAX_ELEMENTS_PER_FRAGMENT) {
                            addFragmentAndUpdateAdapter(arrayList, false);
                            arrayList.clear();
                            itemCount[0] = 0;
                        }
                    }

                    if (!dataN[0].equals("")) {
                        boolean dataSkladState = true;

                        for (String i: sklad.split(",")) {
                            if (sklad.isEmpty()) {
                                dataSkladState = false;
                            }
                            else if (i.trim().contains(sharedPreferences.getString("sklad", ""))) {
                                dataSkladState = false;
                            }
                        }

                        if (dataSkladState) {
                            continue;
                        } else {
                            ArrayList<MyData> arrayList_ = new ArrayList<>();
                            arrayList_.add(new MyData(dataN[0]));
                            addFragmentAndUpdateAdapter(arrayList_, true);
                        }
                    }

                } catch (Exception e) {

                }

            }

            workbook.close();

            if (!arrayList.isEmpty()) {
                addFragmentAndUpdateAdapter(arrayList, false);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        for (final ListFragment mm : defadapter.getAll()) {
            for (final MyData mmm : mm.getMyDataArrayList()) {
                if (mmm.nameOfGroup != null && !mmm.nameOfGroup.isEmpty()) {
                    changes.add(defadapter.getAll().indexOf(mm));
                }
            }
        }

        Log.d("changes", "" + changes);

        Log.wtf("data", skladi.toString());
    }

    private void addFragmentAndUpdateAdapter(ArrayList<MyData> arrayList, boolean a) {
        defadapter.addFragment(new ListFragment(new ArrayList<>(arrayList), a));
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

                if (numericValue == (long) numericValue) {
                    return String.valueOf((long) numericValue);
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
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.toFirst:
                viewPager.setCurrentItem(0);
                index = 0;
                return true;
            case R.id.toLast:
                viewPager.setCurrentItem(changes.get(index), true);
                index += 1;
                return true;
            case R.id.toGroups:
                if (sharedPreferences.getString("passGroups", "").isEmpty()) {
                    new GroupDialog(groupSet).show(getSupportFragmentManager(), "");
                } else {
                    Intent intent = new Intent(ListActivity.this, PassActivity.class);
                    intent.putExtra("group", "send");
                    startActivityForResult(intent, 1);
                }
                return true;
            case R.id.changeGroup:
                if (sharedPreferences.getString("passChange", "").isEmpty()) {
                    new ChangeDialog(defadapter).show(getSupportFragmentManager(), "");
                } else {
                    Intent intenttt = new Intent(ListActivity.this, PassActivity.class);
                    intenttt.putExtra("group", "change");
                    startActivityForResult(intenttt, 1);
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private final BroadcastReceiver customBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_CUSTOM_BROADCAST)) {
                if (intent.getStringExtra("group") != null) {
                    defadapter.removeAll();
                    defadapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), getLifecycle());
                    viewPager.setAdapter(defadapter);

                    try {
                        ArrayList<MyData> arrayList = new ArrayList<>();
                        int itemCount = 0;

                        for (Row row : sheet) {
                            String name = getCellValueAsString(row.getCell(0));

                            if (name.equals("") && itemCount > 1) {
                                addFragmentAndUpdateAdapter(arrayList, false);
                                arrayList.clear();
                                itemCount = 0;
                            }

                            if (!name.contains("Назва") && !name.contains("Прайс") && !name.equals("") && getCellValueAsString(row.getCell(18)).contains(intent.getStringExtra("group"))) {
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
                        }

                        if (!arrayList.isEmpty()) {
                            addFragmentAndUpdateAdapter(arrayList, false);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (intent.getIntExtra("change", 0) != 0) {
                    viewPager.setCurrentItem(intent.getIntExtra("change", 0), true);
                }


            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter(ACTION_CUSTOM_BROADCAST);
        registerReceiver(customBroadcastReceiver, intentFilter, RECEIVER_EXPORTED);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(customBroadcastReceiver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                String result = data.getStringExtra("result_key");
                if (result.equals("send")) {
                    new GroupDialog(groupSet).show(getSupportFragmentManager(), "");
                }

                if (result.equals("change")) {
                    new ChangeDialog(defadapter).show(getSupportFragmentManager(), "");
                }
            }
        }
    }
}
