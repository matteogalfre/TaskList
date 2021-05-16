package es.ucm.fdi.tasklist.ui.setting;

import android.content.Context;
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

import java.util.ArrayList;

import es.ucm.fdi.tasklist.R;
import es.ucm.fdi.tasklist.db.Categories;
import es.ucm.fdi.tasklist.db.DataBaseTask;
import es.ucm.fdi.tasklist.db.TaskDetail;

public class TaskListCategoryAdapter extends ArrayAdapter<Categories> {
    private final Context mContext;

    public TaskListCategoryAdapter(Context context , ArrayList<Categories> objects) {
        super(context, -1, objects);
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String type = getItem(position).getType();
        int color = getItem(position).getColor();

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(R.layout.list_item_category,null);

        Button task_color = convertView.findViewById(R.id.category_color);
        TextView task_type = convertView.findViewById(R.id.category_name_list);
        CheckBox delete = convertView.findViewById(R.id.deleteCategory);

        delete.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                remove(getItem(position));
                notifyDataSetChanged();
                DataBaseTask.getInstance(getContext()).deleteCatgoryItem(type, color,DataBaseTask.getInstance(getContext()).getWritableDatabase());
            }
        });

        task_color.setBackgroundColor(color);
        task_type.setText(type);
        return convertView;
    }
}
