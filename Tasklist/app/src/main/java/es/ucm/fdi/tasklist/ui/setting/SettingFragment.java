package es.ucm.fdi.tasklist.ui.setting;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import es.ucm.fdi.tasklist.R;

public class SettingFragment extends Fragment {

    View view;

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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}