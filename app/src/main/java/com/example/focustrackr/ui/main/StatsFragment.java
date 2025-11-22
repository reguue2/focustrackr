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
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment que muestra estadísticas generales de las sesiones mediante
 * texto y gráfico de barras. Se personaliza el eje X para mostrar el nombre
 * de cada sesión y se elimina la descripción inferior de la gráfica.
 */
public class StatsFragment extends Fragment {

    private MainViewModel mainViewModel;
    private TextView tvTotalMinutes;
    private TextView tvAvgFocus;
    private TextView tvTotalSessions;
    private TextView tvBestSession;
    private TextView tvWorstSession;
    private BarChart barChart;

    public StatsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stats, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvTotalMinutes = view.findViewById(R.id.tvTotalMinutes);
        tvAvgFocus = view.findViewById(R.id.tvAvgFocus);
        tvTotalSessions = view.findViewById(R.id.tvTotalSessions);
        tvBestSession = view.findViewById(R.id.tvBestSession);
        tvWorstSession = view.findViewById(R.id.tvWorstSession);
        barChart = view.findViewById(R.id.barChart);

        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        mainViewModel.getTotalDuration().observe(getViewLifecycleOwner(), value -> {
            tvTotalMinutes.setText("Minutos totales: " + (value != null ? value : 0));
        });

        mainViewModel.getAvgFocus().observe(getViewLifecycleOwner(), value -> {
            tvAvgFocus.setText("Promedio de foco: " + (int) (value != null ? value : 0f) + "%");
        });

        mainViewModel.getTotalSessions().observe(getViewLifecycleOwner(), value -> {
            tvTotalSessions.setText("Número de sesiones: " + (value != null ? value : 0));
        });

        mainViewModel.getSessions().observe(getViewLifecycleOwner(), this::updateChartAndBenchmarks);

        setupChartAppearance();
    }

    /**
     * Configura el aspecto gráfico base:
     * sin descripción, sin zoom, leyenda desactivada, etc.
     */
    private void setupChartAppearance() {
        if (barChart == null) return;

        barChart.setDrawGridBackground(false);
        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(true);
        barChart.setPinchZoom(false);
        barChart.setScaleEnabled(false);

        Description desc = new Description();
        desc.setText("");
        desc.setEnabled(false);
        barChart.setDescription(desc);

        barChart.getLegend().setEnabled(false);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);

        barChart.getAxisRight().setEnabled(false);
    }

    /**
     * Actualiza grafica y estadísticas. Se añaden nombres de sesiones en eje X.
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

        List<BarEntry> entries = new ArrayList<>();
        final List<String> labels = new ArrayList<>();

        for (int i = 0; i < sessions.size(); i++) {
            SessionEntity s = sessions.get(i);
            entries.add(new BarEntry(i, s.getDurationMinutes()));
            labels.add(s.getName());
        }

        BarDataSet dataSet = new BarDataSet(entries, "Duración (min)");
        dataSet.setColor(ContextCompat.getColor(requireContext(), R.color.focus_primary));
        dataSet.setValueTextColor(ContextCompat.getColor(requireContext(), R.color.focus_text_primary));
        dataSet.setValueTextSize(10f);

        BarData data = new BarData(dataSet);
        data.setBarWidth(0.6f);
        barChart.setData(data);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setGranularity(1f);
        xAxis.setLabelRotationAngle(45f);

        barChart.animateY(600);
        barChart.invalidate();

        SessionEntity longest = sessions.get(0);
        SessionEntity shortest = sessions.get(0);

        for (SessionEntity s : sessions) {
            if (s.getDurationMinutes() > longest.getDurationMinutes()) longest = s;
            if (s.getDurationMinutes() < shortest.getDurationMinutes()) shortest = s;
        }

        tvBestSession.setText(
                "Mejor sesión: " + longest.getName() + " (" + longest.getDurationMinutes() + " min)"
        );

        tvWorstSession.setText(
                "Sesión más corta: " + shortest.getName() + " (" + shortest.getDurationMinutes() + " min)"
        );
    }
}
