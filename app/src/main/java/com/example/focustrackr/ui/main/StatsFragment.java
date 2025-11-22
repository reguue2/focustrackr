package com.example.focustrackr.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.focustrackr.R;
import com.example.focustrackr.data.local.entity.SessionEntity;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment que muestra estadísticas generales de las sesiones mediante texto y gráfico de barras.
 */
public class StatsFragment extends Fragment {

    private MainViewModel mainViewModel;
    private TextView tvTotalMinutes;
    private TextView tvAvgFocus;
    private TextView tvTotalSessions;
    private TextView tvBestSession;
    private TextView tvWorstSession;
    private BarChart barChart;

    public StatsFragment() {
        // Constructor vacío requerido por Fragment
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Infla el layout asociado al fragment
        return inflater.inflate(R.layout.fragment_stats, container, false);
    }

    /**
     * Se llama una vez creada la vista. Se inicializa UI y se observan datos del ViewModel.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Referencias UI
        tvTotalMinutes = view.findViewById(R.id.tvTotalMinutes);
        tvAvgFocus = view.findViewById(R.id.tvAvgFocus);
        tvTotalSessions = view.findViewById(R.id.tvTotalSessions);
        tvBestSession = view.findViewById(R.id.tvBestSession);
        tvWorstSession = view.findViewById(R.id.tvWorstSession);
        barChart = view.findViewById(R.id.barChart);

        // Configuración ViewModel
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        // Observador de minutos totales
        mainViewModel.getTotalDuration().observe(getViewLifecycleOwner(), value -> {
            int total = value != null ? value : 0;
            tvTotalMinutes.setText("Minutos totales: " + total);
        });

        // Observador de promedio de enfoque
        mainViewModel.getAvgFocus().observe(getViewLifecycleOwner(), value -> {
            float avg = value != null ? value : 0f;
            tvAvgFocus.setText("Promedio de foco: " + (int) avg + "%");
        });

        // Observador número total de sesiones
        mainViewModel.getTotalSessions().observe(getViewLifecycleOwner(), value -> {
            int total = value != null ? value : 0;
            tvTotalSessions.setText("Número de sesiones: " + total);
        });

        // Observador de sesiones para el gráfico y estadísticas adicionales
        mainViewModel.getSessions().observe(getViewLifecycleOwner(), this::updateChartAndBenchmarks);

        // Configuración visual básica del gráfico
        setupChartAppearance();
    }

    /**
     * Configura el aspecto del gráfico de barras.
     */
    private void setupChartAppearance() {
        if (barChart == null) return;

        barChart.setDrawGridBackground(false);
        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(true);
        barChart.setPinchZoom(false);
        barChart.setScaleEnabled(false);

        Description desc = new Description();
        desc.setText("Duración de cada sesión (minutos)");
        desc.setTextSize(10f);
        barChart.setDescription(desc);

        barChart.getLegend().setEnabled(false);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);

        barChart.getAxisRight().setEnabled(false);
    }

    /**
     * Actualiza el gráfico y muestra la mejor y peor sesión según duración.
     */
    private void updateChartAndBenchmarks(List<SessionEntity> sessions) {
        if (barChart == null) return;

        if (sessions == null || sessions.isEmpty()) {
            barChart.clear();
            barChart.setNoDataText("Aún no tienes sesiones guardadas");
            tvBestSession.setText("Mejor sesión: -");
            tvWorstSession.setText("Sesión más corta: -");
            return;
        }

        // Crear entradas para gráfico: eje X = índice de la sesión, eje Y = duración (minutos)
        List<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < sessions.size(); i++) {
            SessionEntity s = sessions.get(i);
            entries.add(new BarEntry(i, s.getDurationMinutes()));
        }

        // Dataset configurado con colores personalizados
        BarDataSet dataSet = new BarDataSet(entries, "Duración (min)");
        dataSet.setColor(ContextCompat.getColor(requireContext(), R.color.focus_primary));
        dataSet.setValueTextColor(ContextCompat.getColor(requireContext(), R.color.focus_text_primary));
        dataSet.setValueTextSize(10f);

        BarData data = new BarData(dataSet);
        data.setBarWidth(0.6f);

        barChart.setData(data);
        barChart.animateY(600);
        barChart.invalidate();

        // Identificación de la sesión con mayor y menor duración
        SessionEntity longest = sessions.get(0);
        SessionEntity shortest = sessions.get(0);

        for (SessionEntity s : sessions) {
            if (s.getDurationMinutes() > longest.getDurationMinutes()) {
                longest = s;
            }
            if (s.getDurationMinutes() < shortest.getDurationMinutes()) {
                shortest = s;
            }
        }

        // Actualización de datos estadísticos
        tvBestSession.setText(
                "Mejor sesión: " + longest.getName() +
                        " (" + longest.getDurationMinutes() + " min)"
        );

        tvWorstSession.setText(
                "Sesión más corta: " + shortest.getName() +
                        " (" + shortest.getDurationMinutes() + " min)"
        );
    }
}
