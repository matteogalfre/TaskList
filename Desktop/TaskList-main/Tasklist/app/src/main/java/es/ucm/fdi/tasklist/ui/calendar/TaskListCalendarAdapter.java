package es.ucm.fdi.tasklist.ui.calendar;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

import es.ucm.fdi.tasklist.R;
import es.ucm.fdi.tasklist.db.DataBaseTask;
import es.ucm.fdi.tasklist.db.TaskDetail;
import es.ucm.fdi.tasklist.ui.TaskListAdapter;

public class TaskListCalendarAdapter extends ArrayAdapter<TaskDetail> {
    private final Context mContext;

    public TaskListCalendarAdapter(Context context , ArrayList<TaskDetail> objects) {
        super(context, -1, objects);
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String title = getItem(position).getTitle();
        String hour = getItem(position).getHora();
        boolean fin = getItem(position).getFin();
        int color = getItem(position).getColor();

        ViewHolder holder;

        if(convertView == null){
            holder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.list_item_calendar_note, parent, false);

            holder.task_title = convertView.findViewById(R.id.task_title_calendar_list);
            holder.task_hour = convertView.findViewById(R.id.task_hour_calendar_list);
            holder.category = convertView.findViewById(R.id.task_category_calendar_list);

            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.category.setBackgroundColor(color);

        DataBaseTask db = DataBaseTask.getInstance(getContext());
        String actualHour = db.getFormatHour(db.getHour(), db.getMin());

        if(hour.compareTo(actualHour) < 0){
            holder.task_hour.setTextColor(Color.RED);
        }


        if(fin) {
            convertView.setBackgroundColor(Color.argb(22, 200, 255, 200));
            holder.task_title.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            holder.task_hour.setTextColor(Color.GRAY);
        }
        else{
            convertView.setBackgroundColor(Color.WHITE);
            holder.task_title.setPaintFlags(0);
        }

        holder.task_title.setText(title);
        holder.task_hour.setText(hour);
        return convertView;
    }


    private static class ViewHolder{
        TextView task_title;
        TextView task_hour;
        Button category;
    }
}
