package es.ucm.fdi.tasklist.ui;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import java.util.Calendar;
import android.content.Intent;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.RemoteInput;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

import es.ucm.fdi.tasklist.DrawActivity;
import es.ucm.fdi.tasklist.R;
import es.ucm.fdi.tasklist.db.Categories;
import es.ucm.fdi.tasklist.db.DataBaseTask;

public class ViewTaskActivity extends AppCompatActivity {

    private EditText title;
    private EditText description;
    private  EditText date;
    private EditText hora;
    private  boolean finish;
    private boolean important;
    private int color;
    private  ImageView calendar;
    private ImageView clock;
    private Spinner spinnerCategory;
    private ArrayList<Categories> categories;
    private boolean created;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_edit_note);

        Toolbar toolbar = findViewById(R.id.toolbar_edit);
        toolbar.setBackgroundColor(Color.rgb(25, 200, 175));
        getWindow().setNavigationBarColor(Color.rgb(20, 140, 116));
        getWindow().setStatusBarColor(Color.rgb(20, 140, 116));

        title = findViewById(R.id.task_title_edit);
        description = findViewById(R.id.task_description_edit);
        date = findViewById(R.id.task_date_edit);
        hora = findViewById(R.id.task_hour_edit);
        calendar = findViewById(R.id.imageViewCalendar);
        clock = findViewById(R.id.imageViewHour);
        spinnerCategory = findViewById(R.id.spinnerCategory);

        updateCategories();

        ArrayAdapter<Categories> adapter = new ArrayAdapter<>(getApplication(), android.R.layout.simple_spinner_item, categories);
        spinnerCategory.setAdapter(adapter);

        created = getIntent().getExtras().getBoolean("CREATED");
        if(created){
            toolbar.setTitle(getString(R.string.editarTarea));
            setSupportActionBar(toolbar);
            String t = getIntent().getExtras().getString("TITLE");
            String c = getIntent().getExtras().getString("CONTENT");
            String d = getIntent().getExtras().getString("DATE");
            String h = getIntent().getExtras().getString("HORA");
            finish = getIntent().getExtras().getBoolean("FINISH");
            important = getIntent().getExtras().getBoolean("IMPORTANT");
            color = getIntent().getExtras().getInt("COLOR");
            String type = getIntent().getExtras().getString("TYPE");

            title.setText(t);
            description.setText(c);
            date.setText(d);
            hora.setText(h);

            int pos = adapter.getPosition(new Categories(type, color));
            if(pos < 0 ) pos = 0;
            spinnerCategory.setSelection(pos);
        }
        else{
            toolbar.setTitle(getString(R.string.nuevaTarea));
            setSupportActionBar(toolbar);
            title.requestFocus();
            date.setText(DataBaseTask.getInstance(this).getDate());
            hora.setText(DataBaseTask.getInstance(this).getHourAndMin());
            if(getIntent().getExtras().getBoolean("CREATED_IMPORTANT")){
                important = true;
            }
            spinnerCategory.setSelection(0);
        }
        execListener();
    }

    public void updateCategories(){
        categories = new ArrayList<Categories>();
        DataBaseTask dbHelper = DataBaseTask.getInstance(getApplication());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Log.e("prueba", "Init categroy Database");

        if (db != null) {
            Cursor c = db.rawQuery("SELECT * FROM category ORDER BY name  ASC", null);
            if (c.moveToFirst()) {
                do {
                    categories.add(new Categories((c.isNull(0))? "" : c.getString(0), c.getInt(1)));
                } while (c.moveToNext());
            }
        }
    }

    @Override
    public void onBackPressed() {
        String t = title.getText().toString();
        String c = description.getText().toString();
        String d = date.getText().toString();
        String h = hora.getText().toString();
        Categories category = (Categories) spinnerCategory.getSelectedItem();

        Intent returnIntent = new Intent();
        returnIntent.putExtra("title",t);
        returnIntent.putExtra("content",c);
        returnIntent.putExtra("finish",finish);
        returnIntent.putExtra("date",d);
        returnIntent.putExtra("important",important);
        returnIntent.putExtra("hora",h);
        returnIntent.putExtra("color", category.getColor());
        returnIntent.putExtra("type", category.getType());

        if(created){
            long id = getIntent().getExtras().getLong("ID");
            returnIntent.putExtra("id",id);
        }
        setResult(Activity.RESULT_OK,returnIntent);
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btn_delete:
                Intent returnIntent = new Intent();
                if(created){
                    long id = getIntent().getExtras().getLong("ID");
                    returnIntent.putExtra("id", id);
                }
                setResult(Activity.RESULT_CANCELED,returnIntent);
                finish();
                return (true);
        }
        return (super.onOptionsItemSelected(item));
    }

    private void execListener(){

        calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogDate();
            }
        });

        clock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogHour();
            }
        });

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogDate();
            }
        });

        hora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogHour();
            }
        });
    }

    private void dialogHour(){
        TimePickerDialog dialogoHora = new TimePickerDialog(ViewTaskActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                hora.setText(DataBaseTask.getInstance(getApplicationContext()).getFormatHour(hourOfDay, minute));
            }
        },DataBaseTask.getInstance(getApplicationContext()).getHour(),
                DataBaseTask.getInstance(getApplicationContext()).getMin(),
                true);

        dialogoHora.show();
    }

    public void startDraw(View view) {
        Intent intent = new Intent(this, DrawActivity.class);
        startActivity(intent);
        finish();
    }

    private void dialogDate(){
        DatePickerDialog dialogoFecha = new DatePickerDialog(ViewTaskActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                date.setText(DataBaseTask.getInstance(getApplicationContext()).getFormatDate(dayOfMonth, month+1, year));
            }
        }, DataBaseTask.getInstance(getApplicationContext()).getAnio(),
                DataBaseTask.getInstance(getApplicationContext()).getMes(),
                DataBaseTask.getInstance(getApplicationContext()).getDia());

        dialogoFecha.show();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void addCalendar(View view) {
        if (!title.getText().toString().isEmpty() && !date.getText().toString().isEmpty() && !description
                .getText().toString().isEmpty() && !hora.getText().toString().isEmpty()) {

            String[] d = date.getText().toString().split("/");
            String[] h = hora.getText().toString().split(":");
            int Year = Integer.parseInt(d[2]);
            int Month = Integer.parseInt(d[1]);
            int Day = Integer.parseInt(d[0]);
            int Hour = Integer.parseInt(h[0]);
            int Min = Integer.parseInt(h[1]);

            long startMillis = 0;
            Calendar beginTime = Calendar.getInstance();
            beginTime.set(Year, Month, Day, Hour, Min);
            startMillis = beginTime.getTimeInMillis();


            Calendar cal = Calendar.getInstance();
            Intent intent = new Intent(Intent.ACTION_INSERT);
            intent.setType("vnd.android.cursor.item/event");
            intent.putExtra("beginTime", startMillis);
            intent.putExtra("allDay", false);
            intent.putExtra("endTime", startMillis+60*60*1000);
            intent.putExtra("title", title.getText().toString());
            intent.putExtra("description", description.getText().toString());

            startActivity(intent);

        }else{
            Toast.makeText(ViewTaskActivity.this, "Por favor, rellene todos los campos",
                    Toast.LENGTH_SHORT).show();
        }
    }

}