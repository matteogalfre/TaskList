package es.ucm.fdi.tasklist.db;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class DataBaseTask extends SQLiteOpenHelper {


    private static final String TASK_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS tasks(id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, description TEXT, date TEXT, fin INTEGER, important INTEGER, hora TEXT)";
    private static final String DB_NAME = "task.sqlite";
    private static final int DB_VERSION = 1;

    private static DataBaseTask INSTANCE;

    final Calendar calendario = Calendar.getInstance();

    int anio = calendario.get(Calendar.YEAR);
    int mes = calendario.get(Calendar.MONTH);
    int dia = calendario.get(Calendar.DAY_OF_MONTH);

    int hour = calendario.get(Calendar.HOUR);
    int min = calendario.get(Calendar.MINUTE);

    private DataBaseTask(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    private ArrayList<ObserverDao> observers = new ArrayList<ObserverDao>();

    public void addObserver(ObserverDao o){
        if(!observers.contains(o)){
            observers.add(o);
        }
    }

    public void notifyObservers(){
        for(ObserverDao o : observers){
            o.initDataBase();
        }
    }

    public static DataBaseTask getInstance(Context context){
        if(INSTANCE == null) INSTANCE = new DataBaseTask(context);
        return INSTANCE;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TASK_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        notifyObservers();
    }

    public void addItem(TaskDetail td, SQLiteDatabase db) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", td.getTitle().isEmpty()? null:td.getTitle());
        contentValues.put("description", td.getDesc().isEmpty()? null:td.getDesc());
        contentValues.put("date", td.getDate().isEmpty()? null:td.getDate());
        contentValues.put("hora", td.getHora().isEmpty()? null:td.getHora());
        contentValues.put("fin", td.getFin()? 1:0);
        contentValues.put("important", td.getImp()? 1:0);

        db.insert("tasks", null, contentValues);
        notifyObservers();
    }

    public void updateItem(TaskDetail td, SQLiteDatabase db) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", td.getTitle().isEmpty()? null:td.getTitle());
        contentValues.put("description", td.getDesc().isEmpty()? null:td.getDesc());
        contentValues.put("date", td.getDate().isEmpty()? null:td.getDate());
        contentValues.put("hora", td.getHora().isEmpty()? null:td.getHora());
        contentValues.put("fin", td.getFin()? 1:0);
        contentValues.put("important", td.getImp()? 1:0);

        String whereClause = "id=?";
        String whereArgs[] = {String.valueOf(td.getId())};
        db.update("tasks", contentValues, whereClause, whereArgs);
        notifyObservers();
    }

    public void deleteItem(TaskDetail td, SQLiteDatabase db) {
        String whereClause = "id=?";
        String whereArgs[] = {String.valueOf(td.getId())};
        db.delete("tasks", whereClause, whereArgs);
        notifyObservers();
    }

    public String getFormatDate(int dia, int mes, int anio){
        return String.format(Locale.getDefault(), "%02d/%02d/%02d", dia, mes+1, anio);
    }

    public String getDate(){
        return getFormatDate(dia, mes, anio);
    }

    public String getHourAndMin(){
        return getFormatHour(getHour(), getMin());
    }

    public String getFormatHour(int hora, int min){
        return String.format(Locale.getDefault(), "%02d:%02d", hora, min);
    }

    public int getAnio() {
        return anio;
    }

    public int getMes() {
        return mes;
    }

    public int getDia() {
        return dia;
    }

    public int getHour() {
        hour = calendario.get(Calendar.HOUR_OF_DAY);
        return hour;
    }

    public int getMin() {
        min = calendario.get(Calendar.MINUTE);
        return min;
    }

}

