package es.ucm.fdi.tasklist.ui.calendar;

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
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.vivekkaushik.datepicker.DatePickerTimeline;
import com.vivekkaushik.datepicker.OnDateSelectedListener;

import java.util.ArrayList;
import java.util.Calendar;

import es.ucm.fdi.tasklist.R;
import es.ucm.fdi.tasklist.db.DataBaseTask;
import es.ucm.fdi.tasklist.db.ObserverDao;
import es.ucm.fdi.tasklist.db.TaskDetail;

public class CalendarFragment extends Fragment implements ObserverDao {

    View view;

    private ArrayList<TaskDetail> taskList = new ArrayList();
    private ArrayList<TaskDetail> filterDayTaskList = new ArrayList();
    private TaskListCalendarAdapter arrayAdapter;
    private ListView taskListCalendarView;

    private SQLiteDatabase db;
    private String dateSelect;

    public CalendarFragment() { }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_calendar,container,false);

        FloatingActionButton button = getActivity().findViewById(R.id.addNote);
        button.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(96, 200, 75)));

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(Color.rgb(96, 200, 75));

        Window window = getActivity().getWindow();
        window.setNavigationBarColor(Color.rgb(55, 140, 30));
        window.setStatusBarColor(Color.rgb(55, 140, 30));

        initDataBase();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        taskListCalendarView = view.findViewById(R.id.calendarListView);
        ImageView emptyList = view.findViewById(R.id.emptyList);
        taskListCalendarView.setEmptyView(emptyList);
        arrayAdapter = new TaskListCalendarAdapter(getContext(), filterDayTaskList);
        taskListCalendarView.setAdapter(arrayAdapter);

        dateSelect = DataBaseTask.getInstance(getContext()).getFormatDate(
                DataBaseTask.getInstance(getContext()).getDia(),
                DataBaseTask.getInstance(getContext()).getMes(),
                DataBaseTask.getInstance(getContext()).getAnio());

        filterList();

        DatePickerTimeline datePickerTimeline = view.findViewById(R.id.datePickerTimeline);
        datePickerTimeline.setInitialDate(DataBaseTask.getInstance(getContext()).getAnio(),
                DataBaseTask.getInstance(getContext()).getMes()-1,
                DataBaseTask.getInstance(getContext()).getDia());

        datePickerTimeline.setActiveDate(Calendar.getInstance());

        datePickerTimeline.setOnDateSelectedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(int year, int month, int day, int dayOfWeek) {
                dateSelect = DataBaseTask.getInstance(getContext()).getFormatDate(day, month+1, year);
                filterList();
            }

            @Override
            public void onDisabledDateSelected(int year, int month, int day, int dayOfWeek, boolean isDisabled) {
                //Do Something
            }
        });

        FloatingActionButton fab = getActivity().findViewById(R.id.addNote);
        fab.setVisibility(View.INVISIBLE);
    }

    private void filterList() {
        filterDayTaskList.clear();
        for(TaskDetail dt : taskList){
            if(dt.getDate().equals(dateSelect)){
                filterDayTaskList.add(dt);
            }
        }
        arrayAdapter.notifyDataSetChanged();
    }

    public void initDataBase(){
        taskList.clear();
        DataBaseTask dbHelper = DataBaseTask.getInstance(getContext());
        db = dbHelper.getWritableDatabase();

        if (db != null) {
            Cursor c = db.rawQuery("SELECT * FROM tasks ORDER BY fin, hora ASC", null);
            if (c.moveToFirst()) {
                do {
                    updateList(c.getInt(0),
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

    public TaskDetail updateList(int _id,  String _title, String _desc, String _date, boolean _fin, boolean _imp, String _hora, int _color, String _type){
        TaskDetail detail = new TaskDetail(_id, _title, _desc, _date, _fin, _imp, _hora, _color, _type);
        if (!taskList.contains(detail))
            taskList.add(detail);
        return detail;
    }
}
