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
        tvTotalMinutes = view.findViewById(R.id.tvTotalMinutes);
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        mainViewModel.getTotalDuration().observe(getViewLifecycleOwner(), integer -> {
            int total = integer != null ? integer : 0;
            tvTotalMinutes.setText("Minutos totales: " + total);
        });
    }
}