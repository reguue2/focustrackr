package com.example.focustrackr.ui.sessiondetail;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.focustrackr.data.local.entity.SessionEntity;
import com.example.focustrackr.data.repository.SessionRepository;

/**
 * ViewModel que proporciona los datos de una sesión
 * y permite su eliminación desde la base de datos.
 */
public class SessionDetailViewModel extends AndroidViewModel {

    private final SessionRepository sessionRepository;

    public SessionDetailViewModel(@NonNull Application application) {
        super(application);
        sessionRepository = new SessionRepository(application);
    }

    /**
     * Devuelve la sesión seleccionada según su ID.
     */
    public LiveData<SessionEntity> getSessionById(long id) {
        return sessionRepository.getSessionById(id);
    }

    /**
     * Elimina la sesión indicada.
     */
    public void deleteSession(SessionEntity session) {
        sessionRepository.deleteSession(session);
    }
}
