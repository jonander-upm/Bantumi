package es.upm.miw.bantumi.model;

import android.app.Application;

public class PuntuacionRepository {
    IPuntuacionDAO iPuntuacionDAO;

    public PuntuacionRepository(Application application) {
        PuntuacionRoomDatabase db = PuntuacionRoomDatabase.getDatabase(application);
        iPuntuacionDAO = db.puntuacionDAO();
    }

    public long insert(PuntuacionEntity item) {
        return iPuntuacionDAO.insert(item);
    }

}
