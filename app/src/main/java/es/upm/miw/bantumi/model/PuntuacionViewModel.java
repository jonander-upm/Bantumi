package es.upm.miw.bantumi.model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class PuntuacionViewModel extends AndroidViewModel {
    private PuntuacionRepository puntuacionRepository;

    public PuntuacionViewModel(@NonNull Application application) {
        super(application);
        puntuacionRepository = new PuntuacionRepository(application);
    }

    public LiveData<List<PuntuacionEntity>> findTopTen() {
        return puntuacionRepository.findTopTen();
    }

    public void insert(PuntuacionEntity item) {
        puntuacionRepository.insert(item);
    }

    public void deleteAll() {
        puntuacionRepository.deleteAll();
    }

}
