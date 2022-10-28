package es.upm.miw.bantumi.model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface IPuntuacionDAO {
    @Query("SELECT * FROM " + PuntuacionEntity.TABLA + " ORDER BY playerSeeds DESC, opponentSeeds ASC LIMIT 10")
    LiveData<List<PuntuacionEntity>> findTopTen();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(PuntuacionEntity puntuacion);

    @Query("DELETE FROM " + PuntuacionEntity.TABLA)
    void deleteAll();
}
