package es.ucm.fdi.tasklist.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DataBaseTask extends SQLiteOpenHelper {


    private static final String TASK_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS tasks(id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, description TEXT, date TEXT, fin INTEGER, important INTEGER, hora TEXT, color INTEGER, type TEXT)";
    private static final String DB_TASK_NAME = "task.sqlite";
    private static final int DB_VERSION = 1;

    private static final String  CATEGORY_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS category(name TEXT PRIMARY KEY, color INTEGER)";

    private static DataBaseTask INSTANCE;

    private DataBaseTask(Context context) {
        super(context, DB_TASK_NAME, null, DB_VERSION);
    }

    private final ArrayList<ObserverDao> observers = new ArrayList<ObserverDao>();

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
        db.execSQL(CATEGORY_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public long addTaskItem(TaskDetail td, SQLiteDatabase db) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", td.getTitle().isEmpty()? null:td.getTitle());
        contentValues.put("description", td.getDesc().isEmpty()? null:td.getDesc());
        contentValues.put("date", td.getDate().isEmpty()? null:normalizeDateString(td.getDate()));
        contentValues.put("hora", td.getHora().isEmpty()? null:td.getHora());
        contentValues.put("fin", td.getFin()? 1:0);
        contentValues.put("important", td.getImp()? 1:0);
        contentValues.put("color", td.getColor());
        contentValues.put("type", td.getType().isEmpty()? null:td.getType());

        long i = db.insert("tasks", null, contentValues);

        return i;
    }

    public long addCategoryItem(String name, int color, SQLiteDatabase db) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name.isEmpty()? null:name);
        contentValues.put("color", color);

        long i = db.insert("category", null, contentValues);
        return i;
    }

    public void deleteCatgoryItem(String name, int color, SQLiteDatabase db) {
        String whereClause = "name=?";
        String[] whereArgs = {name};
        db.delete("category", whereClause, whereArgs);
    }

    public void updateTaskItem(TaskDetail td, SQLiteDatabase db) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", td.getTitle().isEmpty()? null:td.getTitle());
        contentValues.put("description", td.getDesc().isEmpty()? null:td.getDesc());
        contentValues.put("date", td.getDate().isEmpty()? null:normalizeDateString(td.getDate()));
        contentValues.put("hora", td.getHora().isEmpty()? null:td.getHora());
        contentValues.put("fin", td.getFin()? 1:0);
        contentValues.put("important", td.getImp()? 1:0);
        contentValues.put("color", td.getColor());
        contentValues.put("type", td.getType().isEmpty()? null:td.getType());

        String whereClause = "id=?";
        String[] whereArgs = {String.valueOf(td.getId())};
        db.update("tasks", contentValues, whereClause, whereArgs);
    }

    public void deleteTaskItem(TaskDetail td, SQLiteDatabase db) {
        String whereClause = "id=?";
        String[] whereArgs = {String.valueOf(td.getId())};
        db.delete("tasks", whereClause, whereArgs);
    }

    public String getFormatDate(int dia, int mes, int anio){
        return String.format(Locale.getDefault(), "%02d/%02d/%02d", dia, mes, anio);
    }

    public String getDate(){
        return getFormatDate(getDia(), getMes(), getAnio());
    }

    public String getHourAndMin(){
        return getFormatHour(getHour(), getMin());
    }

    public String getFormatHour(int hora, int min){
        return String.format(Locale.getDefault(), "%02d:%02d", hora, min);
    }

    public int getAnio() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    public int getMes() {
        return Calendar.getInstance().get(Calendar.MONTH)+1;
    }

    public int getDia() {
        return Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    }

    public int getHour() {
        return Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
    }

    public int getMin() {
         return Calendar.getInstance().get(Calendar.MINUTE);
    }

    public static String normalizeDateString(String strDate){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date date = sdf1.parse(strDate);
            return sdf.format(date);
        } catch (ParseException ignored) {}
       return strDate;
    }

    public static String formatDateString(String strDate){
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date date = sdf1.parse(strDate);
            return sdf.format(date);
        } catch (ParseException ignored) {}
        return strDate;
    }

}

