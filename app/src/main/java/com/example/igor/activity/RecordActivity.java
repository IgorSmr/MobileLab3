package com.example.igor.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.igor.model.Photo;
import com.example.igor.model.Record;
import com.example.igor.utils.DataBaseHelper;
import com.example.vladislav.mobilelab3.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class RecordActivity extends AppCompatActivity {

    public static final String STATE = "STATE";
    public static final String RECORD = "RECORD";
    public static final String PHOTO = "PHOTO";


    private String dateString;
    private Calendar myCalendar = Calendar.getInstance();

    private DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }

    };

    private TimePickerDialog.OnTimeSetListener start = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int i, int i1) {
            myCalendar.set(Calendar.HOUR_OF_DAY, i);
            myCalendar.set(Calendar.MINUTE, i1);
            updateLabelStart();
        }
    };

    private TimePickerDialog.OnTimeSetListener finish = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int i, int i1) {
            myCalendar.set(Calendar.HOUR_OF_DAY, i);
            myCalendar.set(Calendar.MINUTE, i1);
            updateLabelFinish();
        }
    };

    private Record record;
    private EditText editDescription;
    private Spinner spinner;
    private Context context;
    private Intent intent;
    private int startHour;
    private int startMinute;
    private int endHour;
    private int endMinute;
    private List<Photo> photo = new ArrayList<>();
    private AlertDialog al;
    private AlertDialog.Builder ad;
    private View layoutView;
    private TextView inputData;
    private TextView startTime;
    private TextView finishTime;

    private LinearLayout linear;
    private List<TextView> textViews = new ArrayList<>();

    private void updateLabel() {

        String myFormat = "dd.MM.yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        inputData.setText(sdf.format(myCalendar.getTime()));
    }

    private void updateLabelStart() {
        startTime.setText(myCalendar.get(Calendar.HOUR_OF_DAY) + ":" + myCalendar.get(Calendar.MINUTE));
        startHour = myCalendar.get(Calendar.HOUR_OF_DAY);
        startMinute = myCalendar.get(Calendar.MINUTE);
    }

    private void updateLabelFinish() {
        finishTime.setText(myCalendar.get(Calendar.HOUR_OF_DAY) + ":" + myCalendar.get(Calendar.MINUTE));
        endHour = myCalendar.get(Calendar.HOUR_OF_DAY);
        endMinute = myCalendar.get(Calendar.MINUTE);
    }

    private void initComponents() {
        inputData = (TextView) findViewById(R.id.inputData);
        startTime = (TextView) findViewById(R.id.inputTimeStart);
        finishTime = (TextView) findViewById(R.id.inputTimeFinish);
        inputData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(context, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new TimePickerDialog(context, start, myCalendar.get(Calendar.HOUR_OF_DAY),
                        myCalendar.get(Calendar.MINUTE), true).show();
            }
        });

        finishTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new TimePickerDialog(context, finish, myCalendar.get(Calendar.HOUR_OF_DAY),
                        myCalendar.get(Calendar.MINUTE), true).show();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record_layout);
        context = RecordActivity.this;

        intent = getIntent();
        photo.clear();

        initComponents();

        editDescription = (EditText) findViewById(R.id.edit_description);

        List<String> category = new ArrayList<>();
        SQLiteDatabase db = DataBaseHelper.getInstance().getReadableDatabase();
        String query = "SELECT * FROM Category";
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            category.add(cursor.getString(1));
        }
        cursor.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, category);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner = (Spinner) findViewById(R.id.spinner_category);
        spinner.setAdapter(adapter);

        if (intent.getSerializableExtra(STATE).equals("open")) {
            record = (Record) intent.getSerializableExtra(RECORD);

            String name = "";
            query = "SELECT name FROM Category where _id='" + record.getCategoryId() + "'";
            cursor = db.rawQuery(query, null);
            while (cursor.moveToNext()) {
                name = cursor.getString(0);
            }
            cursor.close();

            editDescription.setText(record.getDescription());
            inputData.setText(record.getDate());
            startTime.setText(record.getTimeStart());
            finishTime.setText(record.getTimeEnd());
            editDescription.setText(record.getDescription());
            String time;
            String[] t;
            time = record.getTimeStart();
            t = time.split(":");
            startHour = Integer.valueOf(t[0]);
            startMinute = Integer.valueOf(t[1]);
            time = record.getTimeEnd();
            t = time.split(":");
            endHour = Integer.valueOf(t[0]);
            endMinute = Integer.valueOf(t[1]);

            spinner.setSelection(category.indexOf(name));

            if (record.getPhotoIdList().length() > 0) {
                if (record.getPhotoIdList().contains(",")) {
                    String ph[] = record.getPhotoIdList().split(",");

                    query = "SELECT * FROM Photo";
                    cursor = db.rawQuery(query, null);
                    while (cursor.moveToNext()) {
                        for (String aPh : ph) {
                            if (cursor.getInt(0) == Integer.valueOf(aPh)) {
                                photo.add(new Photo(cursor.getInt(0), cursor.getString(1)));
                            }
                        }
                    }
                    cursor.close();
                } else {
                    query = "SELECT * FROM Photo WHERE _id='" + record.getPhotoIdList() + "'";
                    cursor = db.rawQuery(query, null);
                    while (cursor.moveToNext()) {
                        photo.add(new Photo(cursor.getInt(0), cursor.getString(1)));
                    }
                    cursor.close();
                }
            }
        }

        String f = intent.getStringExtra(PHOTO);
        if (f.length() > 1) {
            query = "SELECT * FROM Photo WHERE name='" + f + "'";
            cursor = db.rawQuery(query, null);
            while (cursor.moveToNext()) {
                photo.add(new Photo(cursor.getInt(0), cursor.getString(1)));
            }
            cursor.close();
        }
        linear = (LinearLayout) findViewById(R.id.linear_photo);
        addTextView();
    }

    public void addTextView() {
        textViews.clear();
        linear.removeAllViews();

        textViews.clear();
        linear.removeAllViews();
        TextView cb;
        for (int i = 0; i < photo.size(); i++) {
            cb = new TextView(context);
            cb.setId(i);
            cb.setOnClickListener(textClickListener);
            cb.setText(photo.get(i).getTitle());
            textViews.add(cb);
        }

        for (TextView c : textViews)
            linear.addView(c);
    }

    int id;

    View.OnClickListener textClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            LayoutInflater li = LayoutInflater.from(context);
            layoutView = li.inflate(R.layout.photo_layout, null);

            ad = new AlertDialog.Builder(context);
            ad.setView(layoutView);
            ad.setTitle("Edit category");
            ad.setCancelable(false);

            id = v.getId();
            ImageView im = (ImageView) layoutView.findViewById(R.id.image_view);
            Uri uri = Uri.fromFile(new File(photo.get(v.getId()).getName()));
            im.setImageURI(uri);

            al = ad.create();
            al.show();
        }
    };

    public void onClickAddPhoto(View view) {
        String category, time, descr, catId = "", photoList = " ";

        category = spinner.getSelectedItem().toString();
        SQLiteDatabase db = DataBaseHelper.getInstance().getReadableDatabase();
        String query = "SELECT _id FROM Category where name='" + category + "'";
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            catId = cursor.getString(0);
        }
        cursor.close();
        //////////////////////////////////
        String start = String.valueOf(startTime.getText());
        String end = String.valueOf(finishTime.getText());
        time = getTime(startHour, startMinute, endHour, endMinute);
        //////////////////////////////////////
        descr = editDescription.getText().toString();

        String date = String.valueOf(inputData.getText());

        if (photo.size() > 0) {
            photoList = String.valueOf(photo.get(0).getId());
            for (int i = 1; i < photo.size(); i++)
                photoList += "," + photo.get(i).getId();
        }

        Intent in = new Intent(RecordActivity.this, CameraActivity.class);

        if (intent.getStringExtra(STATE).equals("open")) {
            Record rec = new Record(record.getId(), Integer.valueOf(catId), date, descr, start, end, time, photoList);
            in.putExtra(RecordActivity.STATE, "open");
            in.putExtra(RECORD, rec);
        } else {
            in.putExtra(RecordActivity.STATE, "new");
        }

        startActivity(in);
    }

    public void onClickDelete(View view) {
        if (intent.getStringExtra(STATE).equals("open")) {
            DataBaseHelper.getInstance().getWritableDatabase()
                    .delete("Record", "_id = ?", new String[]{String.valueOf(record.getId())});
        }
        Intent intent = new Intent(RecordActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public void onClickSave(View view) {
        String category, time, start, end, descr, catId = "", photoList = " ";

        category = spinner.getSelectedItem().toString();
        SQLiteDatabase db = DataBaseHelper.getInstance().getReadableDatabase();
        String query = "SELECT _id FROM Category where name='" + category + "'";
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            catId = cursor.getString(0);
        }
        cursor.close();

        start = startHour + ":" + startMinute;
        end = endHour + ":" + endMinute;
        time = getTime(startHour, startMinute, endHour, endMinute);

        descr = editDescription.getText().toString();

        String date = String.valueOf(inputData.getText());

        if (photo.size() > 0) {
            photoList = String.valueOf(photo.get(0).getId());
            for (int i = 1; i < photo.size(); i++) {
                photoList += "," + photo.get(i).getId();
            }
        }

        if (intent.getStringExtra(STATE).equals("open")) {
            ContentValues cv = new ContentValues();
            cv.put("category_id", catId);
            cv.put("date", date);
            cv.put("description", descr);
            cv.put("time_start", start);
            cv.put("time_end", end);
            cv.put("time", time);
            cv.put("photo", photoList);
            DataBaseHelper.getInstance().getWritableDatabase()
                    .update("Record", cv, "_id = ?", new String[]{String.valueOf(record.getId())});
        } else {
            query = "INSERT INTO Record "
                    + "(category_id,date,description,time_start,time_end,time,photo)"
                    + " VALUES ('" + catId + "','" + date + "','" + descr + "','" + start + "','" + end + "','" + time + "','" + photoList + "')";
            DataBaseHelper.getInstance().getWritableDatabase().execSQL(query);
        }

        Intent intent = new Intent(RecordActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public String getTime(int h1, int m1, int h2, int m2) {
        int h, m;

        if (h2 > h1)
            h = h2 - h1;
        else if (h2 < h1) {
            h = 24 - h1 + h2;
        } else h = h1;

        if (m2 > m1)
            m = m2 - m1;
        else if (m2 < m1) {
            m = 60 - m1 + m2;
            h--;
        } else m = m1;
        return String.valueOf(h + ":" + m);
    }

    public void onClickCancel(View view) {
        Intent intent = new Intent(RecordActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public void onClickDeleteImage(View view) {
        DataBaseHelper.getInstance().getWritableDatabase()
                .delete("Photo", "_id = ?", new String[]{String.valueOf(id)});
        photo.remove(id);
        al.cancel();
        addTextView();
    }

    public void onClickCancelImage(View view) {
        al.cancel();
    }
}
