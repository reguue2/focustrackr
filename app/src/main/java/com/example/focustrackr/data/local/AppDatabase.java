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

@Database(entities = {SessionEntity.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    public abstract SessionDao sessionDao();

    private static final ExecutorService dbExecutor = Executors.newSingleThreadExecutor();

    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE sessions ADD COLUMN distractionsCount INTEGER NOT NULL DEFAULT 0");
        }
    };

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "focus_db")
                            .addMigrations(MIGRATION_1_2)
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
                SessionDao dao = INSTANCE.sessionDao();

                // Sesiones de ejemplo
                dao.insert(new SessionEntity("Estudio Java", 45, 72f, 40.4168, -3.7038, System.currentTimeMillis() - 86400000, 0));
                dao.insert(new SessionEntity("Planificacion semanal", 30, 68f, 40.4175, -3.7039, System.currentTimeMillis() - 172800000, 0));
                dao.insert(new SessionEntity("Android Layouts", 55, 81f, 40.4180, -3.7040, System.currentTimeMillis() - 3600000, 3));
            });
        }
    };
}
