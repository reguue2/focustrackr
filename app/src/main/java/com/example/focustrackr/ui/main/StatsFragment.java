package com.example.focustrackr.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.focustrackr.R;

public class StatsFragment extends Fragment {

    private MainViewModel mainViewModel;
    private TextView tvTotalMinutes;
    private TextView tvAvgFocus;
    private TextView tvTotalSessions;

    public StatsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stats, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Asignar referencias UI
        tvTotalMinutes = view.findViewById(R.id.tvTotalMinutes);
        tvAvgFocus = view.findViewById(R.id.tvAvgFocus);
        tvTotalSessions = view.findViewById(R.id.tvTotalSessions);

        // ViewModel
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        // Observers - Total minutos
        mainViewModel.getTotalDuration().observe(getViewLifecycleOwner(), value -> {
            int total = value != null ? value : 0;
            tvTotalMinutes.setText("Minutos totales: " + total);
        });

        // Observers - Promedio de foco
        mainViewModel.getAvgFocus().observe(getViewLifecycleOwner(), value -> {
            float avg = value != null ? value : 0f;
            tvAvgFocus.setText("Promedio de foco: " + (int) avg + "%");
        });

        // Observers - Total sesiones
        mainViewModel.getTotalSessions().observe(getViewLifecycleOwner(), value -> {
            int total = value != null ? value : 0;
            tvTotalSessions.setText("Numero de sesiones: " + total);
        });
    }

}