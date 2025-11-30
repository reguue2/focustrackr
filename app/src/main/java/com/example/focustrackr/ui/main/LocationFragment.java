package com.example.focustrackr.ui.main;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.focustrackr.R;
import com.example.focustrackr.data.local.dao.SessionDao;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * Fragmento que muestra estadisticas por ubicacion.
 * Convierte coordenadas a ubicaciones legibles, ordena por productividad
 * y destaca el lugar mas eficiente.
 */
public class LocationFragment extends Fragment {

    private LocationViewModel viewModel;

    private TextView tvNoLocations;
    private LinearLayout layoutLocationContainer;
    private TextView tvBestLocation;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_location, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvNoLocations = view.findViewById(R.id.tvNoLocations);
        layoutLocationContainer = view.findViewById(R.id.layoutLocationContainer);
        tvBestLocation = view.findViewById(R.id.tvBestLocation);

        viewModel = new ViewModelProvider(this).get(LocationViewModel.class);

        observeData();
    }

    /**
     * Observa las estadisticas de ubicacion.
     */
    private void observeData() {
        viewModel.getLocationStats().observe(getViewLifecycleOwner(), stats -> {
            if (stats == null || stats.isEmpty()) {
                tvNoLocations.setVisibility(View.VISIBLE);
                layoutLocationContainer.setVisibility(View.GONE);
                tvBestLocation.setVisibility(View.GONE);
            } else {
                tvNoLocations.setVisibility(View.GONE);
                layoutLocationContainer.setVisibility(View.VISIBLE);
                tvBestLocation.setVisibility(View.VISIBLE);
                processLocations(stats);
            }
        });
    }

    /**
     * Procesa ubicaciones: convierte coordenadas, ordena y muestra.
     */
    private void processLocations(List<SessionDao.LocationStats> statsList) {
        // Ordenar por mayor productividad
        Collections.sort(statsList, (a, b) -> Float.compare(b.avgFocus, a.avgFocus));

        // Mejor ubicacion
        SessionDao.LocationStats best = statsList.get(0);
        String bestLocationName = getLocationName(best.latitude, best.longitude);
        tvBestLocation.setText("Lugar mas productivo: " + bestLocationName +
                " (" + Math.round(best.avgFocus) + "% enfoque)");

        updateLocationUI(statsList);
    }

    /**
     * Genera visual de cada ubicacion en tarjetas.
     */
    private void updateLocationUI(List<SessionDao.LocationStats> statsList) {
        layoutLocationContainer.removeAllViews();

        for (SessionDao.LocationStats stats : statsList) {
            CardView card = new CardView(getContext());
            card.setCardElevation(4);
            card.setUseCompatPadding(true);

            TextView tv = new TextView(getContext());
            String locationName = getLocationName(stats.latitude, stats.longitude);
            tv.setText(locationName +
                    "\nTiempo total: " + stats.totalMinutes + " min" +
                    "\nEnfoque medio: " + Math.round(stats.avgFocus) + "%");
            tv.setPadding(24, 24, 24, 24);

            card.addView(tv);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 24, 0, 0);
            card.setLayoutParams(params);

            layoutLocationContainer.addView(card);
        }
    }

    /**
     * Convierte coordenadas en una direccion legible.
     */
    private String getLocationName(double lat, double lon) {
        if (lat == 0 && lon == 0) {
            return "Ubicacion no disponible";
        }

        try {
            Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);
            if (addresses != null && !addresses.isEmpty()) {
                return addresses.get(0).getLocality() != null ?
                        addresses.get(0).getLocality() : addresses.get(0).getAddressLine(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Ubicacion (" + lat + ", " + lon + ")";
    }

}
