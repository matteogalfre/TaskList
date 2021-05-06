package es.ucm.fdi.tasklist.ui.setting;

import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.GnssAntennaInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.function.Function;

import dev.sasikanth.colorsheet.ColorSheet;
import es.ucm.fdi.tasklist.R;
import es.ucm.fdi.tasklist.db.Categories;
import es.ucm.fdi.tasklist.db.DataBaseTask;
import es.ucm.fdi.tasklist.db.TaskDetail;
import es.ucm.fdi.tasklist.ui.ViewTaskActivity;
import es.ucm.fdi.tasklist.ui.calendar.TaskListCalendarAdapter;
import kotlin.Unit;
import petrov.kristiyan.colorpicker.ColorPicker;

public class SettingFragment extends Fragment {

    View view;
    EditText category_name;
    Button category_color, save;
    ColorPicker colorPicker;
    private ArrayList<Categories> categoryList = new ArrayList();
    private TaskListCategoryAdapter arrayAdapter;
    private ListView categoryListView;

    private Color color;

    SQLiteDatabase db;

    public SettingFragment(){ }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_setting,container,false);
        FloatingActionButton button = getActivity().findViewById(R.id.addNote);
        button.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(96, 200, 75)));

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(Color.rgb(96, 200, 75));

        Window window = getActivity().getWindow();
        window.setNavigationBarColor(Color.rgb(55, 140, 30));
        window.setStatusBarColor(Color.rgb(55, 140, 30));
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        category_name = view.findViewById(R.id.category_name);
        category_color = view.findViewById(R.id.category_color);
        save = view.findViewById(R.id.add_category);
        color = Color.valueOf(Color.GRAY);

        updateCategories();

        categoryListView = view.findViewById(R.id.categoryListView);
        arrayAdapter = new TaskListCategoryAdapter(getContext(), categoryList);
        categoryListView.setAdapter(arrayAdapter);

        execListener();
    }

    private void execListener() {
        FloatingActionButton fab = getActivity().findViewById(R.id.addNote);
        fab.setVisibility(View.INVISIBLE);

        category_color.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                putDialog();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                String name = category_name.getText().toString();
                if(!name.equals("")){
                    db =  DataBaseTask.getInstance(getContext()).getWritableDatabase();
                    DataBaseTask.getInstance(getContext()).addCategoryItem(name, Color.rgb(color.red(), color.green(), color.blue()), db);
                    Categories c = new Categories(name, Color.rgb(color.red(), color.green(), color.blue()));
                    if(!categoryList.contains(c)){
                        categoryList.add(c);
                        arrayAdapter.notifyDataSetChanged();
                    }
                    else{
                        Toast.makeText(getActivity().getApplicationContext(), "La categoría introducida ya existe", Toast.LENGTH_SHORT);
                    }

                }
            }
        });
    }

    public void updateCategories(){
        categoryList = new ArrayList<>();
        DataBaseTask dbHelper = DataBaseTask.getInstance(getContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Log.e("prueba", "Init categroy Database");

        if (db != null) {
            Cursor c = db.rawQuery("SELECT * FROM category ORDER BY name  ASC", null);
            if (c.moveToFirst()) {
                do {
                    if(!c.getString(0).equals(getString(R.string.categoryDefault)))
                        categoryList.add(new Categories((c.isNull(0))? "" : c.getString(0), c.getInt(1)));
                } while (c.moveToNext());
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("categoryList", categoryList);

    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if(savedInstanceState != null)
            categoryList = savedInstanceState.getParcelableArrayList("categoryList");
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void putDialog(){
        colorPicker = new ColorPicker(getActivity());
        colorPicker.addListenerButton("GUARDAR", new ColorPicker.OnButtonListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v, int position, int color) {
                setColor(Color.valueOf(color));
                category_color.setBackgroundColor(Color.rgb(getColor().red(), getColor().green(), getColor().blue()));
                colorPicker.dismissDialog();
                Log.e("ChooseColor", "Guardado color");
            }
        }).disableDefaultButtons(true)
                .disableDefaultButtons(true)
                .setColumns(5)
                .setTitle("Elige un color")
                .setRoundColorButton(true)
                .setDefaultColorButton (Color.rgb(96, 200, 75))
                .show();
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}