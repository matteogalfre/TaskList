package es.ucm.fdi.tasklist.ui.statistics;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Objects;

import es.ucm.fdi.tasklist.R;

public class StatisticsFragment extends Fragment {
    ViewPager2 viewPager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_statistics, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        requireActivity().findViewById(R.id.addNote).setVisibility(View.INVISIBLE);

        FragmentStateAdapter fragmentAdapter = new FragmentStateAdapter(this) {

            @NonNull
            @Override
            public Fragment createFragment(int position) {
                if(position == 0) return new CompletedTasksFragment();
                else if(position == 1) return new ProductivityFragment();
                return new CompletedTasksFragment();
            }

            @Override
            public int getItemCount() {
                return 2;
            }
        };
        viewPager = view.findViewById(R.id.pager);
        viewPager.setAdapter(fragmentAdapter);
        TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
        }).attach();
    }

}
