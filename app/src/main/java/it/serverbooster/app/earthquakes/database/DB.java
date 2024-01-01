package it.serverbooster.app.earthquakes.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import it.serverbooster.app.earthquakes.model.Earthquake;

@Database(entities = { Earthquake.class }, version = 3)
public abstract class DB extends RoomDatabase {

    public abstract EarthquakesDAO getEarthquakesDAO();

    private volatile static DB instance = null;

    public static synchronized DB getInstance(Context context) {
        if(instance == null) {
            synchronized (DB.class) {
                if(instance == null) {
                    instance = Room.databaseBuilder(context, DB.class, "database,db")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }

        return instance;
    }


}
