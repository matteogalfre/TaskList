package es.ucm.fdi.tasklist.ui.category;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

import es.ucm.fdi.tasklist.R;
import es.ucm.fdi.tasklist.db.Categories;
import es.ucm.fdi.tasklist.db.DataBaseTask;

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

        ViewHolder holder;

        if(convertView == null){
            holder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.list_item_category, parent, false);

            holder.task_color = convertView.findViewById(R.id.category_color);
            holder.task_type = convertView.findViewById(R.id.category_name_list);
            holder.delete = convertView.findViewById(R.id.deleteCategory);

            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remove(getItem(position));
                notifyDataSetChanged();
                DataBaseTask dataBaseTask = DataBaseTask.getInstance(getContext());
                SQLiteDatabase db = dataBaseTask.getWritableDatabase();
                dataBaseTask.deleteCatgoryItem(type, color, db);
            }
        });

        holder.task_color.setBackgroundColor(color);
        holder.task_type.setText(type);
        return convertView;
    }

    private static class ViewHolder{
        Button task_color;
        TextView task_type;
        CheckBox delete;
    }
}
