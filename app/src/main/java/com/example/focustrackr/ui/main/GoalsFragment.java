package com.example.focustrackr.ui.main;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.focustrackr.R;
import com.example.focustrackr.data.local.dao.SessionDao;

import java.util.List;

/**
 * Fragmento de objetivos semanales de enfoque.
 * Muestra progreso actual y racha activa.
 */
public class GoalsFragment extends Fragment {

    private GoalsViewModel viewModel;
    private TextView tvWeeklyGoal, tvWeeklyStatus, tvCurrentStreak;
    private ProgressBar progressWeekly;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_goals, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvWeeklyGoal = view.findViewById(R.id.tvWeeklyGoal);
        tvWeeklyStatus = view.findViewById(R.id.tvWeeklyStatus);
        tvCurrentStreak = view.findViewById(R.id.tvCurrentStreak);
        progressWeekly = view.findViewById(R.id.progressWeekly);

        viewModel = new ViewModelProvider(this).get(GoalsViewModel.class);

        MainViewModel mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        viewModel.connectMainViewModel(mainViewModel);

        view.findViewById(R.id.btnChangeGoal).setOnClickListener(v -> showGoalDialog());

        observeData();
    }

    /**
     * Observa los datos expuestos por el ViewModel.
     */
    private void observeData() {
        viewModel.getWeeklyGoal().observe(getViewLifecycleOwner(), goal -> {
            tvWeeklyGoal.setText("Objetivo semanal: " + goal + " min");
            Integer progress = viewModel.getWeeklyProgress().getValue();
            int percent = goal > 0 && progress != null ? (progress * 100 / goal) : 0;
            tvWeeklyStatus.setText("Progreso actual: " + (progress != null ? progress : 0) + " / " + goal + " min (" + percent + "%)");
            progressWeekly.setProgress(percent);
        });

        viewModel.getWeeklyProgress().observe(getViewLifecycleOwner(), progress -> {
            Integer goal = viewModel.getWeeklyGoal().getValue();
            int percent = (goal != null && goal > 0) ? (progress * 100 / goal) : 0;
            tvWeeklyStatus.setText("Progreso actual: " + progress + (goal != null ? " / " + goal + " min (" + percent + "%)" : " min"));
            progressWeekly.setProgress(percent);
        });

        viewModel.getCurrentStreak().observe(getViewLifecycleOwner(), streak ->
                tvCurrentStreak.setText("Racha actual: " + streak + " días"));
    }

    /**
     * Fuerza actualización al volver a esta pestaña.
     */
    @Override
    public void onResume() {
        super.onResume();
        MainViewModel mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        Integer progress = mainViewModel.getWeeklyDuration().getValue();
        if (progress != null) {
            viewModel.setWeeklyProgress(progress);
        }

        List<SessionDao.DayDuration> dailyData = mainViewModel.getDailyDurations().getValue();
        if (dailyData != null) {
            viewModel.forceRecalculateStreak(dailyData);
        }
    }

    /**
     * Muestra un diálogo para cambiar el objetivo semanal.
     */
    private void showGoalDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Nuevo objetivo semanal (min)");

        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        builder.setPositiveButton("Guardar", (dialog, which) -> {
            String value = input.getText().toString();
            if (!value.isEmpty()) {
                int newGoal = Integer.parseInt(value);
                viewModel.setWeeklyGoal(newGoal);
            }
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());
        builder.show();
    }
}
