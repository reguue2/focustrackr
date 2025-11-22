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
@Database(entities = {SessionEntity.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    public abstract SessionDao sessionDao();

    // Executor para operaciones iniciales en segundo plano
    private static final ExecutorService dbExecutor = Executors.newSingleThreadExecutor();

    /**
     * Migración de versión 1 a 2: añade la columna 'distractionsCount'.
     */
    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE sessions ADD COLUMN distractionsCount INTEGER NOT NULL DEFAULT 0");
        }
    };

    /**
     * Obtiene la instancia única de la base de datos.
     */
    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "focus_db"
                            )
                            .addMigrations(MIGRATION_1_2)
                            .addCallback(roomCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Callback de creación inicial de base de datos.
     * Se añaden tres sesiones de ejemplo.
     */
    private static final RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            dbExecutor.execute(() -> {
                SessionDao dao = INSTANCE.sessionDao();

                // Inserción de datos de ejemplo para probar la aplicación
                dao.insert(new SessionEntity("Estudio Matemáticas", 45, 72f, 40.4168, -3.7038, System.currentTimeMillis() - 86400000, 0));
                dao.insert(new SessionEntity("Trabajo Geografía", 30, 68f, 40.4175, -3.7039, System.currentTimeMillis() - 172800000, 0));
                dao.insert(new SessionEntity("Estudio Lengua", 55, 81f, 40.4180, -3.7040, System.currentTimeMillis() - 3600000, 3));
            });
        }
    };
}
