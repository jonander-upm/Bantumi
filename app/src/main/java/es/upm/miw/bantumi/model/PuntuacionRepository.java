package es.upm.miw.bantumi.model;

import android.app.Application;

import java.util.List;

public class PuntuacionRepository {
    IPuntuacionDAO iPuntuacionDAO;

    public PuntuacionRepository(Application application) {
        PuntuacionRoomDatabase db = PuntuacionRoomDatabase.getDatabase(application);
        iPuntuacionDAO = db.puntuacionDAO();
    }

    public List<PuntuacionEntity> findTopTen() {
        return iPuntuacionDAO.findTopTen();
    }

    public long insert(PuntuacionEntity item) {
        return iPuntuacionDAO.insert(item);
    }

}
