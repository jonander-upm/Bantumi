package es.upm.miw.bantumi.model;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;

@Dao
public interface IPuntuacionDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(PuntuacionEntity puntuacion);
}
