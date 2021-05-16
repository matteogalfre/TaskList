package es.ucm.fdi.tasklist.db;

import android.database.Cursor;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.RequiresApi;

import java.text.SimpleDateFormat;

public class TaskDetail implements Parcelable, Comparable<TaskDetail> {

    private long id;
    private String title;
    private String desc;
    private String date;
    private String hora;
    private boolean fin;
    private boolean imp;
    private int color;
    private String type;

    public TaskDetail(long id, String title, String desc, String date, boolean fin,  boolean imp, String hora, int color, String type){
        this.id = id;
        this.title = title;
        this.desc = desc;
        this.date = date;
        this.fin = fin;
        this.imp = imp;
        this.hora = hora;
        this.color = color;
        this.type = type;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    protected TaskDetail(Parcel in) {
        id = in.readLong();
        title = in.readString();
        desc = in.readString();
        date = in.readString();
        hora = in.readString();
        fin = in.readBoolean();
        imp = in.readBoolean();
        color = in.readInt();
        type = in.readString();
    }

    public static final Creator<TaskDetail> CREATOR = new Creator<TaskDetail>() {
        @RequiresApi(api = Build.VERSION_CODES.Q)
        @Override
        public TaskDetail createFromParcel(Parcel in) {
            return new TaskDetail(in);
        }

        @Override
        public TaskDetail[] newArray(int size) {
            return new TaskDetail[size];
        }
    };

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public Boolean getFin() {
        return fin;
    }

    public void setFin(Boolean fin) {
        this.fin = fin;
    }

    public Boolean getImp() {
        return imp;
    }

    public void setImp(Boolean imp) {
        this.imp = imp;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if(o instanceof TaskDetail) {
            TaskDetail taskDetail = (TaskDetail) o;
            return this.id == taskDetail.getId();
        }
        return false;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(title);
        dest.writeString(desc);
        dest.writeString(date);
        dest.writeString(hora);
        dest.writeBoolean(fin);
        dest.writeBoolean(imp);
        dest.writeInt(color);
        dest.writeString(type);
    }


    @Override
    public int compareTo(TaskDetail o) {
        if(Boolean.compare(fin, o.fin)==0){
            if(date.compareTo(o.date) == 0){
                return hora.compareTo(o.hora);
            }
            else{
                return date.compareTo(o.date);
            }
        }
        else{
            return Boolean.compare(fin, o.fin);
        }
    }

    public static TaskDetail parseTaskDetail(Cursor c){
        long _id = c.getInt(0);
        String _title = (c.isNull(1))? "" : c.getString(1);
        String _desc = (c.isNull(2))? "" : c.getString(2);
        String _date = (c.isNull(3))? "" : c.getString(3);
        _date = DataBaseTask.formatDateString(_date);
        boolean _fin = c.getInt(4) != 0;
        boolean _imp = c.getInt(5) != 0;
        String _hora = (c.isNull(6))? "" : c.getString(6);
        int _color = c.getInt(7);
        String _type = (c.isNull(8))? "" : c.getString(8);

        return new TaskDetail(_id, _title, _desc, _date, _fin, _imp, _hora, _color, _type);
    }
}
