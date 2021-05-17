package es.ucm.fdi.tasklist.ui;

import android.content.Context;
import android.content.res.ColorStateList;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.core.widget.CompoundButtonCompat;

import java.util.ArrayList;

import es.ucm.fdi.tasklist.R;
import es.ucm.fdi.tasklist.db.DataBaseTask;
import es.ucm.fdi.tasklist.db.TaskDetail;

public class TaskListAdapter extends ArrayAdapter<TaskDetail> {
    private final Context mContext;
    private final int colorScheme;
    private final boolean setHour;

    public static final int COLOR1 = 0;
    public static final int COLOR2 = 1;

    public TaskListAdapter(Context context , ArrayList<TaskDetail> objects, int colorScheme) {
        super(context, -1, objects);
        this.mContext = context;
        this.colorScheme = colorScheme;
        setHour = false;
    }

    public TaskListAdapter(Context context , ArrayList<TaskDetail> objects, int colorScheme, boolean setHour) {
        super(context, -1, objects);
        this.mContext = context;
        this.colorScheme = colorScheme;
        this.setHour = setHour;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TaskDetail taskDetail = getItem(position);
        String title = taskDetail.getTitle();
        String date = taskDetail.getDate();
        boolean fin = taskDetail.getFin();
        boolean imp = taskDetail.getImp();
        int color = taskDetail.getColor();
        String hora = taskDetail.getHora();

        ViewHolder holder;

        if(convertView == null){
            holder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.list_item_note, parent, false);

            holder.task_title = convertView.findViewById(R.id.task_title_list);
            holder.task_date = convertView.findViewById(R.id.task_date_list);
            holder.category = convertView.findViewById(R.id.task_category_list);
            holder.task_imp = convertView.findViewById(R.id.checkBoxImportant);
            holder.task_fin = convertView.findViewById(R.id.checkBoxFinish);

            int[][] states = {{android.R.attr.state_checked}, {}};
            int[] colors = null;
            if(colorScheme == COLOR1){
                colors = new int[]{Color.rgb(96, 200, 75), Color.GRAY};
            }
            else if(colorScheme == COLOR2){
                colors = new int[]{Color.rgb(255, 90, 80), Color.GRAY};
            }
            CompoundButtonCompat.setButtonTintList(holder.task_imp, new ColorStateList(states, colors));
            CompoundButtonCompat.setButtonTintList(holder.task_fin, new ColorStateList(states, colors));

            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.task_fin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TaskDetail td = getItem(position);
                td.setFin(holder.task_fin.isChecked());
                notifyDataSetChanged();
                DataBaseTask dataBaseTask = DataBaseTask.getInstance(getContext());
                SQLiteDatabase db = dataBaseTask.getWritableDatabase();
                dataBaseTask.updateTaskItem(td,db);
            }
        });

        holder.task_imp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TaskDetail td = getItem(position);
                td.setImp(holder.task_imp.isChecked());
                notifyDataSetChanged();
                DataBaseTask dataBaseTask = DataBaseTask.getInstance(getContext());
                SQLiteDatabase db = dataBaseTask.getWritableDatabase();
                dataBaseTask.updateTaskItem(td,db);
            }
        });

        holder.task_imp.setChecked(imp);
        holder.task_fin.setChecked(fin);

        holder.category.setBackgroundColor(color);

        holder.task_title.setText(title);
        if(setHour) holder.task_date.setText(getContext().getString(R.string.hora)+ " " + hora);
        else holder.task_date.setText(date);

        if(fin){
            if(colorScheme == COLOR1){
                convertView.setBackgroundColor(Color.argb(22, 200, 255, 200));
            }
            else if(colorScheme == COLOR2){
                convertView.setBackgroundColor(Color.argb(2,244,67,54));
            }

            holder.task_title.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        }
        else{
            convertView.setBackgroundColor(Color.WHITE);
            holder.task_title.setPaintFlags(0);
        }

        return convertView;
    }

    private static class ViewHolder{
        TextView task_title;
        TextView task_date;
        Button category;
        CheckBox task_imp;
        CheckBox task_fin;
    }
}
