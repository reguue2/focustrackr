package com.example.focustrackr.data.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.focustrackr.data.local.dao.SessionDao;
import com.example.focustrackr.data.local.entity.SessionEntity;

@Database(entities = {SessionEntity.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    public abstract SessionDao sessionDao();

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "focus_trackr_db"
                    ).build();
                }
            }
        }
        return INSTANCE;
    }
}
