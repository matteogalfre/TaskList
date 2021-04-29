package es.ucm.fdi.tasklist.ui.calendar;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;

import es.ucm.fdi.tasklist.R;
import es.ucm.fdi.tasklist.db.DataBaseTask;
import es.ucm.fdi.tasklist.db.TaskDetail;

public class TaskListCalendarAdapter extends ArrayAdapter<TaskDetail> {
    private Context mContext;

    public TaskListCalendarAdapter(Context context , ArrayList<TaskDetail> objects) {
        super(context, -1, objects);
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String title = getItem(position).getTitle();
        String hour = getItem(position).getHora();
        boolean fin = getItem(position).getFin();

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(R.layout.list_item_calendar_note,null);

        TextView task_title = convertView.findViewById(R.id.task_title_calendar_list);
        TextView task_hour = convertView.findViewById(R.id.task_hour_calendar_list);
        Button category = convertView.findViewById(R.id.task_category_calendar_list);

        category.setBackgroundColor(Color.rgb(96, 200, 75));

        convertView.setBackgroundColor(Color.WHITE);
        if(fin) {
            convertView.setBackgroundColor(Color.argb(22, 200, 255, 200));
            task_title.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        }

        task_title.setText(title);
        task_hour.setText(hour);
        return convertView;
    }
}
