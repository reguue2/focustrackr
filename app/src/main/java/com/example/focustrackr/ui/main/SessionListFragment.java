package com.example.focustrackr.ui.main;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.focustrackr.R;
import com.example.focustrackr.data.local.entity.SessionEntity;
import com.example.focustrackr.ui.adapter.SessionAdapter;
import com.example.focustrackr.ui.newsession.NewSessionActivity;
import com.example.focustrackr.ui.sessiondetail.SessionDetailActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class SessionListFragment extends Fragment implements SessionAdapter.OnSessionClickListener {

    private MainViewModel mainViewModel;
    private SessionAdapter adapter;

    public SessionListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_session_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerSessions);
        FloatingActionButton fab = view.findViewById(R.id.fabNewSession);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new SessionAdapter(this);
        recyclerView.setAdapter(adapter);

        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        mainViewModel.getSessions().observe(getViewLifecycleOwner(), this::updateList);

        fab.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), NewSessionActivity.class);
            startActivity(intent);
        });
    }

    private void updateList(List<SessionEntity> sessions) {
        adapter.setSessions(sessions);
    }

    @Override
    public void onSessionClick(SessionEntity session) {
        Intent intent = new Intent(getActivity(), SessionDetailActivity.class);
        intent.putExtra("SESSION_ID", session.getId());
        startActivity(intent);
    }
}
