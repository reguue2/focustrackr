package com.example.focustrackr.ui.adapter;

import android.content.Context;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.focustrackr.R;
import com.example.focustrackr.data.local.entity.SessionEntity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SessionAdapter extends RecyclerView.Adapter<SessionAdapter.SessionViewHolder> {

    public interface OnSessionClickListener {
        void onSessionClick(SessionEntity session);
    }

    private final List<SessionEntity> sessions = new ArrayList<>();
    private final OnSessionClickListener listener;
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM HH:mm", Locale.getDefault());

    public SessionAdapter(OnSessionClickListener listener) {
        this.listener = listener;
    }

    public void setSessions(List<SessionEntity> newSessions) {
        sessions.clear();
        if (newSessions != null) {
            sessions.addAll(newSessions);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SessionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.item_session, parent, false); // mantener así

        // Pero aseguramos esto, justo después:
        if (v.getLayoutParams() == null) {
            v.setLayoutParams(new RecyclerView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
        }
        return new SessionViewHolder(v);
    }



    @Override
    public void onBindViewHolder(@NonNull SessionViewHolder holder, int position) {
        SessionEntity session = sessions.get(position);
        holder.bind(session);
    }

    @Override
    public int getItemCount() {
        return sessions.size();
    }

    class SessionViewHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvDuration, tvDate;
        ProgressBar pbFocus;
        CardView card;

        SessionViewHolder(@NonNull View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.cardSession);
            tvName = itemView.findViewById(R.id.tvSessionName);
            tvDuration = itemView.findViewById(R.id.tvSessionDuration);
            tvDate = itemView.findViewById(R.id.tvSessionDate);
            pbFocus = itemView.findViewById(R.id.pbItemFocus);
        }

        void bind(final SessionEntity session) {
            tvName.setText(session.getName());
            tvDuration.setText(session.getDurationMinutes() + " min");
            tvDate.setText(sdf.format(new Date(session.getTimestamp())));

            pbFocus.setProgress((int) session.getFocusPercentage());

            card.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onSessionClick(session);
                }
            });
        }
    }

}
