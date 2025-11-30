package com.example.focustrackr.data.local;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.focustrackr.data.local.dao.SessionDao;
import com.example.focustrackr.data.local.entity.SessionEntity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Base de datos Room principal de la aplicación.
 * Versión 2 con migración desde versión 1 para añadir el campo distractionsCount.
 */
@Database(entities = {SessionEntity.class}, version = 4, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    public abstract SessionDao sessionDao();

    private static final ExecutorService dbExecutor = Executors.newSingleThreadExecutor();

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "focus_db"
                            )
                            .fallbackToDestructiveMigration()
                            .addCallback(roomCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static final RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            dbExecutor.execute(() -> {
                AppDatabase database = INSTANCE;
                if (database == null) return;

                SessionDao dao = database.sessionDao();

                long now = System.currentTimeMillis();
                long monday = now - (4 * 24 * 60 * 60 * 1000);
                long wednesday = now - (2 * 24 * 60 * 60 * 1000);
                long today = now - (2 * 60 * 60 * 1000);

                dao.insert(new SessionEntity(
                        "Estudio Matemáticas",
                        50,
                        75f,
                        40.4168,   // Madrid
                        -3.7038,
                        monday,
                        1
                ));

                dao.insert(new SessionEntity(
                        "Trabajo Historia",
                        35,
                        70f,
                        41.3879,   // Barcelona
                        2.16992,
                        wednesday,
                        0
                ));

                dao.insert(new SessionEntity(
                        "Estudio Programación",
                        60,
                        85f,
                        39.4699,   // Valencia
                        -0.3763,
                        today,
                        2
                ));

            });
        }
    };
}

