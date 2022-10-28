package es.upm.miw.bantumi.model;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class PuntuacionRepository {
    IPuntuacionDAO iPuntuacionDAO;
    LiveData<List<PuntuacionEntity>> topPuntuacionesList;

    public PuntuacionRepository(Application application) {
        PuntuacionRoomDatabase db = PuntuacionRoomDatabase.getDatabase(application);
        iPuntuacionDAO = db.puntuacionDAO();
        topPuntuacionesList = iPuntuacionDAO.findTopTen();
    }

    public LiveData<List<PuntuacionEntity>> findTopTen() {
        return topPuntuacionesList;
    }

    public long insert(PuntuacionEntity item) {
        return iPuntuacionDAO.insert(item);
    }

    public void deleteAll() {
        iPuntuacionDAO.deleteAll();
    }

}
