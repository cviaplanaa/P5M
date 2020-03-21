package com.p5m.puzzledroid.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

/**
 * Class that represents the SQLite database.
 */
@Database(entities = Score.class, exportSchema = false, version = 5)
@TypeConverters({Converters.class})
public abstract class ScoreDatabase extends RoomDatabase {
    public abstract ScoreDao scoreDao();
    private static final String DB_NAME = "score_db";
    private static ScoreDatabase instance;

    public static synchronized ScoreDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    ScoreDatabase.class, DB_NAME)
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
