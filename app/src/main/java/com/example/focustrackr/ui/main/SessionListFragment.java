package com.example.focustrackr.ui.main;

import android.content.Intent;
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

public class SessionListFragment extends Fragment implements SessionAdapter.OnSessionClickListener {

    private FragmentSessionListBinding binding;
    private MainViewModel mainViewModel;
    private SessionAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSessionListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // RecyclerView con diseño profesional
        binding.recyclerSessions.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new SessionAdapter(this);
        binding.recyclerSessions.setAdapter(adapter);

        // Animación de entrada de items
        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(
                getContext(), R.anim.layout_fade_in
        );
        binding.recyclerSessions.setLayoutAnimation(controller);

        // ViewModel
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        mainViewModel.getSessions().observe(getViewLifecycleOwner(), this::updateList);

        // FAB con mini animación de feedback
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

    private void updateList(List<SessionEntity> sessions) {
        adapter.setSessions(sessions);

        if (sessions == null || sessions.isEmpty()) {
            // Mostrar mensaje con fade-in
            binding.recyclerSessions.setVisibility(View.GONE);
            binding.tvEmpty.setAlpha(0f);
            binding.tvEmpty.setVisibility(View.VISIBLE);
            binding.tvEmpty.animate().alpha(1f).setDuration(300).start();
        } else {
            // Mostrar lista con animación
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
