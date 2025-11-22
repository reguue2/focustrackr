package com.example.focustrackr.ui.main;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.focustrackr.R;
import com.example.focustrackr.data.local.entity.SessionEntity;
import com.example.focustrackr.databinding.FragmentSessionListBinding;
import com.example.focustrackr.ui.adapter.SessionAdapter;
import com.example.focustrackr.ui.newsession.NewSessionActivity;
import com.example.focustrackr.ui.sessiondetail.SessionDetailActivity;

import java.util.List;
import java.util.Random;

public class SessionListFragment extends Fragment implements SessionAdapter.OnSessionClickListener {

    private FragmentSessionListBinding binding;
    private MainViewModel mainViewModel;
    private SessionAdapter adapter;

    // Frases motivacionales locales
    private final String[] motivationalPhrases = {
            "Lo que haces hoy determina donde estaras manana.",
            "La disciplina vence al talento cuando el talento no se disciplina.",
            "Cada minuto bien usado te acerca a tu meta.",
            "Si esperas a estar listo, nunca empezaras.",
            "Progreso, no perfeccion. Avanza."
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSessionListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupMotivation();

        binding.recyclerSessions.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new SessionAdapter(this);
        binding.recyclerSessions.setAdapter(adapter);

        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(
                getContext(), R.anim.layout_fade_in
        );
        binding.recyclerSessions.setLayoutAnimation(controller);

        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        mainViewModel.getSessions().observe(getViewLifecycleOwner(), this::updateList);

        binding.fabAddSession.setOnClickListener(v -> {
            binding.fabAddSession.animate().scaleX(0.85f).scaleY(0.85f).setDuration(80)
                    .withEndAction(() -> {
                        Intent intent = new Intent(getActivity(), NewSessionActivity.class);
                        startActivity(intent);
                        requireActivity().overridePendingTransition(
                                android.R.anim.fade_in, android.R.anim.fade_out
                        );
                        binding.fabAddSession.setScaleX(1f);
                        binding.fabAddSession.setScaleY(1f);
                    }).start();
        });
    }

    private void setupMotivation() {
        String phrase = motivationalPhrases[new Random().nextInt(motivationalPhrases.length)];
        boolean isConnected = isInternetAvailable();

        if (!isConnected) {
            phrase = phrase + "\n(Sin conexion)";
        }

        binding.tvMotivation.setAlpha(0f);
        binding.tvMotivation.setText(phrase);
        binding.tvMotivation.animate().alpha(1f).setDuration(600).start();
    }

    private boolean isInternetAvailable() {
        try {
            ConnectivityManager cm = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm == null) return false;
            NetworkCapabilities capabilities = cm.getNetworkCapabilities(cm.getActiveNetwork());
            return capabilities != null &&
                    (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR));
        } catch (Exception ignored) {
            return false;
        }
    }

    private void updateList(List<SessionEntity> sessions) {
        adapter.setSessions(sessions);

        if (sessions == null || sessions.isEmpty()) {
            binding.recyclerSessions.setVisibility(View.GONE);
            binding.tvEmpty.setAlpha(0f);
            binding.tvEmpty.setVisibility(View.VISIBLE);
            binding.tvEmpty.animate().alpha(1f).setDuration(300).start();
        } else {
            binding.tvEmpty.setVisibility(View.GONE);
            binding.recyclerSessions.setVisibility(View.VISIBLE);
            binding.recyclerSessions.scheduleLayoutAnimation();
        }
    }

    @Override
    public void onSessionClick(SessionEntity session) {
        Intent intent = new Intent(getActivity(), SessionDetailActivity.class);
        intent.putExtra("SESSION_ID", session.getId());
        startActivity(intent);
        requireActivity().overridePendingTransition(
                android.R.anim.slide_in_left, android.R.anim.slide_out_right
        );
    }
}
