package es.ucm.fdi.tasklist.ui;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.RemoteInput;

import es.ucm.fdi.tasklist.R;
import es.ucm.fdi.tasklist.db.DataBaseTask;

public class ViewTaskActivity extends AppCompatActivity {

    private static final String TAG = "ViewTaskActivity";
    private static final String CHANNEL_ID = "notification";
    EditText title;
    EditText description;
    EditText date;
    EditText hora;
    boolean finish;
    boolean important;
    ImageView calendar;
    ImageView clock;
    boolean created;

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

            title.setText(t);
            description.setText(c);
            date.setText(d);
            hora.setText(h);

        }
        else{
            toolbar.setTitle(getString(R.string.nuevaTarea));
            setSupportActionBar(toolbar);
            title.requestFocus();
            date.setText(DataBaseTask.getInstance(this).getDate());
            hora.setText(DataBaseTask.getInstance(this).getHourAndMin());
        }
        execListener();
    }

    @Override
    public void onBackPressed() {
        String t = title.getText().toString();
        String c = description.getText().toString();
        String d = date.getText().toString();
        String h = hora.getText().toString();

        Intent returnIntent = new Intent();
        returnIntent.putExtra("title",t);
        returnIntent.putExtra("content",c);
        returnIntent.putExtra("finish",finish);
        returnIntent.putExtra("date",d);
        returnIntent.putExtra("important",important);
        returnIntent.putExtra("hora",h);

        if(created){
            int id = getIntent().getExtras().getInt("ID");
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
                    int id = getIntent().getExtras().getInt("ID");
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

    private void dialogDate(){
        DatePickerDialog dialogoFecha = new DatePickerDialog(ViewTaskActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                date.setText(DataBaseTask.getInstance(getApplicationContext()).getFormatDate(dayOfMonth, month, year));
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

}