package es.ucm.fdi.tasklist.ui.today;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Paint;
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

public class TaskTodayListAdapter extends ArrayAdapter<TaskDetail> {
    private Context mContext;

    public TaskTodayListAdapter(Context context , ArrayList<TaskDetail> objects) {
        super(context, -1, objects);
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        long id = getItem(position).getId();
        String title = getItem(position).getTitle();
        String desc = getItem(position).getDesc();
        String date = getItem(position).getDate();
        boolean fin = getItem(position).getFin();
        boolean imp = getItem(position).getImp();
        String hour = getItem(position).getHora();
        int color = getItem(position).getColor();
        String type = getItem(position).getType();

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(R.layout.list_item_note,null);

        TextView task_title = convertView.findViewById(R.id.task_title_list);
        TextView task_date = convertView.findViewById(R.id.task_date_list);
        Button category = convertView.findViewById(R.id.task_category_list);
        CheckBox task_imp = convertView.findViewById(R.id.checkBoxImportant);
        CheckBox task_fin = convertView.findViewById(R.id.checkBoxFinish);

        int states[][] = {{android.R.attr.state_checked}, {}};
        int colors[] = {Color.rgb(96, 200, 75), Color.GRAY};
        CompoundButtonCompat.setButtonTintList(task_imp, new ColorStateList(states, colors));
        CompoundButtonCompat.setButtonTintList(task_fin, new ColorStateList(states, colors));

        task_fin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                getItem(position).setFin(buttonView.isChecked());
                notifyDataSetChanged();
                DataBaseTask.getInstance(getContext()).updateTaskItem(new TaskDetail(id, title, desc, date, buttonView.isChecked(), imp, hour, color, type),DataBaseTask.getInstance(getContext()).getWritableDatabase());
            }
        });

        task_imp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                getItem(position).setImp(buttonView.isChecked());
                notifyDataSetChanged();
                DataBaseTask.getInstance(getContext()).updateTaskItem(new TaskDetail(id, title, desc, date, fin, buttonView.isChecked(), hour, color, type),DataBaseTask.getInstance(getContext()).getWritableDatabase());
            }
        });

        task_imp.setChecked(imp);
        task_fin.setChecked(fin);

        category.setBackgroundColor(color);

        convertView.setBackgroundColor(Color.WHITE);
        if(fin) {
            convertView.setBackgroundColor(Color.argb(22, 200, 255, 200));
            task_title.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        }

        task_title.setText(title);

        task_date.setText(getContext().getString(R.string.hora)+ " " + hour + "   |  "+ getContext().getString(R.string.description) + "  " + desc);

        return convertView;
    }
}
