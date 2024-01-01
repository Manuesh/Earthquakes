package it.serverbooster.app.earthquakes.database;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import it.serverbooster.app.earthquakes.model.Earthquake;

@Dao
public interface EarthquakesDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insert(List<Earthquake> data);

    @Query("DELETE FROM earthquakes")
    public void deleteAll();

    @Query("SELECT * FROM earthquakes")
    public List<Earthquake> findAll();

}
