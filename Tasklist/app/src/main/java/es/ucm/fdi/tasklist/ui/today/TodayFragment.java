package es.ucm.fdi.tasklist.ui.today;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import es.ucm.fdi.tasklist.R;
import es.ucm.fdi.tasklist.db.DataBaseTask;
import es.ucm.fdi.tasklist.db.ObserverDao;
import es.ucm.fdi.tasklist.db.TaskDetail;
import es.ucm.fdi.tasklist.ui.ViewTaskActivity;

public class TodayFragment extends Fragment implements ObserverDao {

    View view;

    private ArrayList<TaskDetail> todayTaskList = new ArrayList();
    private TaskTodayListAdapter arrayAdapter;
    private ListView taskTodayListView;

    SQLiteDatabase db;

    public TodayFragment(){ }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataBaseTask.getInstance(getContext()).addObserver(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_today,container,false);
        FloatingActionButton button = getActivity().findViewById(R.id.addNote);
        button.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(96, 200, 75)));

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(Color.rgb(96, 200, 75));

        Window window = getActivity().getWindow();
        window.setNavigationBarColor(Color.rgb(55, 140, 30));
        window.setStatusBarColor(Color.rgb(55, 140, 30));
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        taskTodayListView = view.findViewById(R.id.listTaskTodayView);
        ImageView emptyList = view.findViewById(R.id.emptyListToday);
        taskTodayListView.setEmptyView(emptyList);
        arrayAdapter = new TaskTodayListAdapter(getContext(), todayTaskList);
        taskTodayListView.setAdapter(arrayAdapter);
        initDataBase();
        execListener();
    }

    public void initDataBase(){
        todayTaskList.clear();
        arrayAdapter.notifyDataSetChanged();
        DataBaseTask dbHelper = DataBaseTask.getInstance(getContext());
        db = dbHelper.getWritableDatabase();

        if (db != null) {
            Cursor c = db.rawQuery("SELECT * FROM tasks ORDER BY fin, date ASC", null);
            if (c.moveToFirst()) {
                do {
                    updateList(c.getInt(0),
                            (c.isNull(1))? "" : c.getString(1),
                            (c.isNull(2))? "" : c.getString(2),
                            (c.isNull(3))? "" : c.getString(3),
                            c.getInt(4) == 0 ? false : true,
                            c.getInt(5) == 0 ? false : true,
                            (c.isNull(6))? "" : c.getString(6));
                } while (c.moveToNext());
            }
        }
    }

    public TaskDetail updateList(int _id,  String _title, String _desc, String _date, boolean _fin, boolean _imp, String _hora){
        TaskDetail detail = new TaskDetail(_id, _title, _desc, _date, _fin, _imp, _hora);

        if (todayTaskList.contains(detail)) todayTaskList.remove(detail);

        if(detail.getDate().equals(DataBaseTask.getInstance(getContext()).getDate())) {
            todayTaskList.add(detail);
        }

        arrayAdapter.notifyDataSetChanged();
        return detail;
    }

    private void execListener() {
        taskTodayListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openViewViewNotesActivity(todayTaskList.get(position).getId(), todayTaskList.get(position).getTitle(), todayTaskList.get(position).getDesc(),
                        todayTaskList.get(position).getDate(), todayTaskList.get(position).getFin(), todayTaskList.get(position).getImp(), todayTaskList.get(position).getHora());
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void openViewViewNotesActivity(int id, String title, String content, String date, boolean fin, boolean imp, String hora) {
        Intent notesActivityIntent = new Intent(getActivity(), ViewTaskActivity.class);
        notesActivityIntent.putExtra("CREATED",true);
        notesActivityIntent.putExtra("ID",id);
        notesActivityIntent.putExtra("TITLE",title);
        notesActivityIntent.putExtra("CONTENT",content);
        notesActivityIntent.putExtra("DATE",date);
        notesActivityIntent.putExtra("FINISH",fin);
        notesActivityIntent.putExtra("IMPORTANT",imp);
        notesActivityIntent.putExtra("HORA",hora);
        this.startActivityForResult(notesActivityIntent, 2);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        String title, content, date, hora;
        boolean finish;
        boolean important;
        int id;

        if(data != null) {
            title = data.getExtras().getString("title");
            content = data.getExtras().getString("content");
            date = data.getExtras().getString("date");
            hora = data.getExtras().getString("hora");
            finish = data.getExtras().getBoolean("finish");
            important = data.getExtras().getBoolean("important");

            if (requestCode == 2) {
                id = data.getExtras().getInt("id");
                if (resultCode == Activity.RESULT_OK) {
                    TaskDetail taskDetail = new TaskDetail(id, title, content, date, finish, important, hora);
                    DataBaseTask.getInstance(getContext()).updateItem(taskDetail, db);
                }

                if (resultCode == Activity.RESULT_CANCELED) {
                    TaskDetail taskDetail = new TaskDetail(id, title, content, date, finish, important, hora);
                    DataBaseTask.getInstance(getContext()).deleteItem(taskDetail, db);
                }
            }
        }
    }
}