package es.upm.miw.bantumi.model;

import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import es.upm.miw.bantumi.JuegoBantumi;

public class BantumiViewModel extends ViewModel {
    public static final String SEPARADOR_LISTA_VALORES = ";";

    private ArrayList<MutableLiveData<Integer>> tablero;

    private MutableLiveData<JuegoBantumi.Turno> turno;

    private MutableLiveData<Long> timerMillis;
    long startTime;

    Handler timerHandler;
    Runnable timerRunnable;

    public BantumiViewModel() {
        turno = new MutableLiveData<>(JuegoBantumi.Turno.turnoJ1);
        tablero = new ArrayList<>(JuegoBantumi.NUM_POSICIONES);
        for (int i = 0; i < JuegoBantumi.NUM_POSICIONES; i++) {
            tablero.add(i, new MutableLiveData<>(0));
        }
        startTime = System.currentTimeMillis();
        timerMillis = new MutableLiveData<>(startTime);
        timerHandler = new Handler();
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                timerMillis.setValue(System.currentTimeMillis() - startTime);
                timerHandler.postDelayed(this, 500);
            }
        };
    }

    /**
     * @return Devuelve el turno actual
     */
    public LiveData<JuegoBantumi.Turno> getTurno() {
        return turno;
    }

    /**
     * Establece el valor para turno
     * @param turno valor
     */
    public void setTurno(JuegoBantumi.Turno turno) {
        this.turno.setValue(turno);
    }

    /**
     * Recupera el valor de una determinada posición
     *
     * @param pos posición
     * @return contenido de la posición <i>pos</i>
     */
    @NonNull
    public LiveData<Integer> getNumSemillas(int pos) {
        if (pos < 0 || pos >= JuegoBantumi.NUM_POSICIONES) {
            throw new ArrayIndexOutOfBoundsException();
        }

        return tablero.get(pos);
    }

    /**
     * Asigna el valor v a la posición pos del tablero
     *
     * @param pos índice
     * @param v valor
     */
    public void setNumSemillas(int pos, int v) {
        if (pos < 0 || pos >= JuegoBantumi.NUM_POSICIONES) {
            throw new ArrayIndexOutOfBoundsException();
        }
        tablero.get(pos).setValue(v);
    }

    /**
     * Devuelve el tablero
     *
     * @return el estado actual del tablero
     */
    public ArrayList<MutableLiveData<Integer>> getTablero() {
        return this.tablero;
    }

    /**
     * Devuelve el tablero serializado
     *
     * @return el estado actual del tablero
     */
    public String serializa() {
        StringBuilder tableroSerializado = new StringBuilder();
        for(int i = 0; i < tablero.size(); i++) {
            Integer semillas = Objects.requireNonNull(tablero.get(i).getValue());
            tableroSerializado.append(semillas);
            if(i < tablero.size()-1) {
                tableroSerializado.append(SEPARADOR_LISTA_VALORES);
            }
        }
        return tableroSerializado.toString();
    }

    public List<Integer> deserializa(String tableroSerializado) {
        List<Integer> semillas = new ArrayList<>();
        String[] semillasString = tableroSerializado.split(SEPARADOR_LISTA_VALORES);
        for (String semillaStr : semillasString) {
            semillas.add(Integer.parseInt(semillaStr));
        }
        return semillas;
    }

    public void pauseTimer() {
        timerHandler.removeCallbacks(timerRunnable);
    }

    public void resumeTimer() {
        timerHandler.postDelayed(timerRunnable, 0);
    }

    public void restartTimer() {
        startTime = System.currentTimeMillis();
        resumeTimer();
    }

    public MutableLiveData<Long> getTimerMillis() {
        return timerMillis;
    }
}