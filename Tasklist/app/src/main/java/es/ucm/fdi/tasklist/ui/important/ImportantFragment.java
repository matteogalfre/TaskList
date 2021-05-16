package es.ucm.fdi.tasklist.ui.important;

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

public class ImportantFragment extends Fragment implements ObserverDao {

    View view;

    private ArrayList<TaskDetail> importantTaskList = new ArrayList();
    private TaskImportantListAdapter arrayAdapter;
    private ListView taskimportantlistview;

    SQLiteDatabase db;

    public ImportantFragment(){ }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FloatingActionButton button = getActivity().findViewById(R.id.addNote);
        button.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(255, 90, 80)));

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(Color.rgb(255, 90, 80));

        /* Ponemos un color determinado de la aplicacion para esta vista. */
        Window window = getActivity().getWindow();
        window.setNavigationBarColor(Color.rgb(195, 45, 35));
        window.setStatusBarColor(Color.rgb(195, 45, 35));

        return inflater.inflate(R.layout.fragment_important,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        taskimportantlistview = view.findViewById(R.id.listTaskImportantView);
        ImageView emptyList = view.findViewById(R.id.emptyListImportant);
        taskimportantlistview.setEmptyView(emptyList);

        arrayAdapter = new TaskImportantListAdapter(getContext(), importantTaskList);
        taskimportantlistview.setAdapter(arrayAdapter);

        execListener();
    }

    @Override
    public void onStart() {
        super.onStart();
        if(importantTaskList.isEmpty()){
            initDataBase();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
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
        outState.putParcelableArrayList("importantTaskList", importantTaskList);

    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if(savedInstanceState != null)
            importantTaskList = savedInstanceState.getParcelableArrayList("importantTaskList");

    }

    public void initDataBase(){
        DataBaseTask dbHelper = DataBaseTask.getInstance(getContext());
        db = dbHelper.getWritableDatabase();

        Log.e("prueba", "Init Database");

        if (db != null) {
            Cursor c = db.rawQuery("SELECT * FROM tasks ORDER BY fin, date ASC", null);
            if (c.moveToFirst()) {
                do {
                    TaskDetail td = TaskDetail.parseTaskDetail(c);
                    if(!td.isHidden()) updateList(false, td);
                } while (c.moveToNext());
            }
        }
    }

    public void updateList(boolean remove, TaskDetail detail){
        if(remove) importantTaskList.remove(detail);
        else{
            if (!importantTaskList.contains(detail)) {
                if(detail.getImp()) {
                    importantTaskList.add(detail);
                    //Log.e("prueba", "Add Task IMPORTANT -> ID:" + _id + " TITLE:" + _title + " DESC:" + _desc + " DATE:" + _date + " FIN:" + _fin + " IMPORTANT:" + _imp+ " HORA:" + _hora);
                }
            }
            else{
                importantTaskList.remove(detail);
                if(detail.getImp()) importantTaskList.add(detail);
            }
        }
        arrayAdapter.notifyDataSetChanged();
    }

    private void execListener() {
        taskimportantlistview.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openViewViewNotesActivity(importantTaskList.get(position).getId(), importantTaskList.get(position).getTitle(), importantTaskList.get(position).getDesc(),
                        importantTaskList.get(position).getDate(), importantTaskList.get(position).getFin(), importantTaskList.get(position).getImp(),
                        importantTaskList.get(position).getHora(), importantTaskList.get(position).getColor(), importantTaskList.get(position).getType());
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
        notesActivityIntent.putExtra("CREATED_IMPORTANT",true);
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
                    TaskDetail td = new TaskDetail(-1, title, content, date, finish, important, hora, color, type);
                    long newId = DataBaseTask.getInstance(getContext()).addTaskItem(td, db);
                    td.setId(newId);
                    updateList(false, td);
                    Collections.sort(importantTaskList);
                    arrayAdapter.notifyDataSetChanged();
                }
            }
            else if (requestCode == 2) {
                id = data.getExtras().getLong("id");
                if (resultCode == Activity.RESULT_OK) {
                    TaskDetail td = new TaskDetail(id, title, content, date, finish, important, hora, color, type);
                    updateList(false, td);
                    Collections.sort(importantTaskList);
                    arrayAdapter.notifyDataSetChanged();
                    DataBaseTask.getInstance(getContext()).updateTaskItem(td, db);
                }
                else if (resultCode == Activity.RESULT_CANCELED) {
                    TaskDetail td = new TaskDetail(id, title, content, date, finish, important, hora, color, type);
                    DataBaseTask.getInstance(getContext()).deleteTaskItem(td, db);
                    updateList(true, td);
                    Collections.sort(importantTaskList);
                    arrayAdapter.notifyDataSetChanged();
                }
            }
        }
    }
}