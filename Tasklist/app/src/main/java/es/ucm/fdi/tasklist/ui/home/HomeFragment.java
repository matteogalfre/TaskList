package es.ucm.fdi.tasklist.ui.home;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
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
import java.util.Collections;

import es.ucm.fdi.tasklist.R;
import es.ucm.fdi.tasklist.db.DataBaseTask;
import es.ucm.fdi.tasklist.db.ObserverDao;
import es.ucm.fdi.tasklist.db.TaskDetail;
import es.ucm.fdi.tasklist.ui.ViewTaskActivity;

public class HomeFragment extends Fragment implements ObserverDao {

    View view;

    private ArrayList<TaskDetail> taskList = new ArrayList();
    private TaskListAdapter arrayAdapter;
    private ListView taskListView;

    SQLiteDatabase db;

    public HomeFragment(){ }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home,container,false);
        FloatingActionButton button = getActivity().findViewById(R.id.addNote);
        button.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(96, 200, 75)));

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(Color.rgb(96, 200, 75));

        /* Ponemos un color determinado de la aplicacion para esta vista. */
        Window window = getActivity().getWindow();
        window.setNavigationBarColor(Color.rgb(55, 140, 30));
        window.setStatusBarColor(Color.rgb(55, 140, 30));

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        taskListView = view.findViewById(R.id.listTaskView);
        ImageView emptyList = view.findViewById(R.id.emptyListHome);
        taskListView.setEmptyView(emptyList);

        arrayAdapter = new TaskListAdapter(getContext(), taskList);
        taskListView.setAdapter(arrayAdapter);

        execListener();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(taskList.isEmpty()){
            initDataBase();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("taskList", taskList);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if(savedInstanceState != null)
            taskList = savedInstanceState.getParcelableArrayList("taskList");
    }

    public void initDataBase(){
        DataBaseTask dbHelper = DataBaseTask.getInstance(getContext());
        db = dbHelper.getWritableDatabase();

        Log.e("prueba", "Init Database");

        if (db != null) {
            Cursor c = db.rawQuery("SELECT * FROM tasks ORDER BY fin, date ASC", null);
            if (c.moveToFirst()) {
                do {
                    updateList(false, c.getInt(0),
                            (c.isNull(1))? "" : c.getString(1),
                            (c.isNull(2))? "" : c.getString(2),
                            (c.isNull(3))? "" : c.getString(3),
                            c.getInt(4) == 0 ? false : true,
                            c.getInt(5) == 0 ? false : true,
                            (c.isNull(6))? "" : c.getString(6),
                            c.getInt(7),
                            (c.isNull(8))? "" : c.getString(8));
                } while (c.moveToNext());
            }
        }
    }

    public TaskDetail updateList(boolean remove, long _id,  String _title, String _desc, String _date, boolean _fin, boolean _imp, String _hora, int _color, String _type){
        TaskDetail detail = new TaskDetail(_id, _title, _desc, _date, _fin, _imp, _hora, _color, _type);
        if(remove)
            taskList.remove(detail);
        else{
            if (!taskList.contains(detail)) {
                taskList.add(detail);
                Log.e("prueba", "ADD Task ALL -> ID:" + _id + " TITLE:" + _title + " DESC:" + _desc +
                        " DATE:" + _date + " FIN:" + _fin + " IMPORTANT:" + _imp+ " HORA:" + _hora +
                        " COLOR:" + _color+ " TYPE:" + _type);
            }
            else{
                taskList.remove(detail);
                taskList.add(detail);
                Log.e("prueba", "UPDATE Task ALL -> ID:" + _id + " TITLE:" + _title + " DESC:" + _desc +
                        " DATE:" + _date + " FIN:" + _fin + " IMPORTANT:" + _imp+ " HORA:" + _hora +
                        " COLOR:" + _color+ " TYPE:" + _type);
            }
        }
        arrayAdapter.notifyDataSetChanged();
        return detail;
    }

    private void execListener() {
        taskListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openViewViewNotesActivity(taskList.get(position).getId(), taskList.get(position).getTitle(), taskList.get(position).getDesc(),
                        taskList.get(position).getDate(), taskList.get(position).getFin(), taskList.get(position).getImp(), taskList.get(position).getHora(),
                        taskList.get(position).getColor(), taskList.get(position).getType());
            }
        });

        FloatingActionButton fab = getActivity().findViewById(R.id.addNote);
        fab.setVisibility(View.VISIBLE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openViewViewNotesActivity();
            }
        });
    }

    private void openViewViewNotesActivity() {
        Intent notesActivityIntent = new Intent(getActivity(), ViewTaskActivity.class);
        notesActivityIntent.putExtra("CREATED",false);
        this.startActivityForResult(notesActivityIntent, 1);
    }

    public void openViewViewNotesActivity(long id, String title, String content, String date, boolean fin, boolean imp, String hora, int color, String type) {
        Intent notesActivityIntent = new Intent(getActivity(), ViewTaskActivity.class);
        notesActivityIntent.putExtra("CREATED",true);
        notesActivityIntent.putExtra("ID",id);
        notesActivityIntent.putExtra("TITLE",title);
        notesActivityIntent.putExtra("CONTENT",content);
        notesActivityIntent.putExtra("DATE",date);
        notesActivityIntent.putExtra("FINISH",fin);
        notesActivityIntent.putExtra("IMPORTANT",imp);
        notesActivityIntent.putExtra("HORA",hora);
        notesActivityIntent.putExtra("COLOR",color);
        notesActivityIntent.putExtra("TYPE",type);
        this.startActivityForResult(notesActivityIntent, 2);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        String title, content, date, hora, type;
        boolean finish;
        boolean important;
        long id;
        int color;

        if(data != null) {
            if (requestCode == 1 && resultCode == Activity.RESULT_CANCELED) return;

            title = data.getExtras().getString("title");
            content = data.getExtras().getString("content");
            date = data.getExtras().getString("date");
            hora = data.getExtras().getString("hora");
            finish = data.getExtras().getBoolean("finish");
            important = data.getExtras().getBoolean("important");
            color = data.getExtras().getInt("color");
            type = data.getExtras().getString("type");

            if (requestCode == 1) {
                if (resultCode == Activity.RESULT_OK) {
                    long newId = DataBaseTask.getInstance(getContext()).addTaskItem(new TaskDetail(-1, title, content, date, finish, important, hora, color, type), db);
                    updateList(false, newId, title, content, date, finish, important, hora, color, type);
                    Collections.sort(taskList);
                    arrayAdapter.notifyDataSetChanged();
                }
            }
            else if (requestCode == 2) {
                id = data.getExtras().getLong("id");
                if (resultCode == Activity.RESULT_OK) {
                    TaskDetail td = updateList(false, id, title, content, date, finish, important, hora, color, type);
                    Collections.sort(taskList);
                    arrayAdapter.notifyDataSetChanged();
                    DataBaseTask.getInstance(getContext()).updateTaskItem(td, db);
                }
                else if (resultCode == Activity.RESULT_CANCELED) {
                    DataBaseTask.getInstance(getContext()).deleteTaskItem(new TaskDetail(id, title, content, date, finish, important, hora, color, type), db);
                    updateList(true, id, title, content, date, finish, important, hora, color, type);
                    Collections.sort(taskList);
                    arrayAdapter.notifyDataSetChanged();
                }
            }
        }
    }
}