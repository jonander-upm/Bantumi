package es.upm.miw.bantumi;

import android.util.Log;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import es.upm.miw.bantumi.model.BantumiViewModel;
import es.upm.miw.bantumi.util.FileManager;

public class JuegoBantumi {

    public static final int NUM_POSICIONES = 14;
    // Posiciones 0-5: campo jugador 1
    // Posición 6: depósito jugador 1
    // Posiciones 7-12: campo jugador 2
    // Posición 13: depósito jugador 2
    public static final int DEPOSITO_JUGADOR = 6;
    public static final int DEPOSITO_OPONENTE = 13;

    public static final String SEPARADOR_CLAVE_VALOR = ":";
    public static final String SEPARADOR_LINEA = "\n";

    private final BantumiViewModel bantumiVM;

    public Integer getSemillasJugador() {
        return this.getSemillas(DEPOSITO_JUGADOR);
    }

    public Integer getSemillasOponente() {
        return this.getSemillas(DEPOSITO_OPONENTE);
    }

    public void setNumInicialSemillas(int numInicialSemillas) {
        this.numInicialSemillas = numInicialSemillas;
    }

    // Turno juego
    public enum Turno {
        turnoJ1, turnoJ2, Turno_TERMINADO
    }

    // Número inicial de semillas
    private int numInicialSemillas;

    private Turno turnoInicial;

    /**
     * Constructor
     *
     * Inicializa el modelo sólo si éste está vacío
     *
     * @param turno especifica el turno inicial <code>[Turno.turnoJ1 || Turno.turnoJ2]</code>
     * @param numInicialSemillas Número de semillas al inicio del juego
     */
    public JuegoBantumi(BantumiViewModel bantumiVM, Turno turno, int numInicialSemillas) {
        this.bantumiVM = bantumiVM;
        this.numInicialSemillas = numInicialSemillas;
        turnoInicial = turno;
        if (campoVacio(Turno.turnoJ1) && campoVacio(Turno.turnoJ2)) { // Inicializa sólo si está vacío!!!
            inicializar();
        }
    }

    /**
     * @param pos posición
     * @return Número de semillas en el hueco <i>pos</i>
     */
    public int getSemillas(int pos) {
        return bantumiVM.getNumSemillas(pos).getValue();
    }

    /**
     * Asigna el número de semillas a una posición
     *
     * @param pos posición
     * @param valor número de semillas
     */
    public void setSemillas(int pos, int valor) {
        bantumiVM.setNumSemillas(pos, valor);
    }

    /**
     * Inicializa el estado del juego (almacenes vacíos y campos con semillas)
     *
     */
    public void inicializar() {
        setTurno(turnoInicial);
        for (int i = 0; i < NUM_POSICIONES; i++)
            setSemillas(
                    i,
                    (i == 6 || i == 13) // Almacén??
                            ? 0
                            : numInicialSemillas
            );
        bantumiVM.restartTimer();
    }

    /**
     * Recoge las semillas en <i>pos</i> y realiza la siembra
     *
     * @param pos posición escogida [0..13]
     */
    public void jugar(int pos) {
        if (pos < 0 || pos >= NUM_POSICIONES)
            throw new IndexOutOfBoundsException(String.format("Posición (%d) fuera de límites", pos));
        if (getSemillas(pos) == 0
                || (pos < 6 && turnoActual() != Turno.turnoJ1)
                || (pos > 6 && turnoActual() != Turno.turnoJ2)
        )
            return;
        Log.i("MiW", String.format("jugar(%02d)", pos));

        // Recoger semillas en posición pos
        int nSemillasHueco, numSemillas = getSemillas(pos);
        setSemillas(pos, 0);

        // Realizar la siembra
        int nextPos = pos;
        while (numSemillas > 0) {
            nextPos = (nextPos + 1) % NUM_POSICIONES;
            if (turnoActual() == Turno.turnoJ1 && nextPos == 13) // J1 salta depósito jugador 2
                nextPos = 0;
            if (turnoActual() == Turno.turnoJ2 && nextPos == 6) // J2 salta depósito jugador 1
                nextPos = 7;
            nSemillasHueco = getSemillas(nextPos);
            setSemillas(nextPos, nSemillasHueco + 1);
            numSemillas--;
        }

        // Si acaba en hueco vacío en propio campo -> recoger propio + contrario
        if (getSemillas(nextPos) == 1
                && ((turnoActual() == Turno.turnoJ1 && nextPos < 6)
                    || (turnoActual() == Turno.turnoJ2 && nextPos > 6 && nextPos < 13))
        ) {
            int posContrario = 12 - nextPos;
            Log.i("MiW", "\trecoger: turno=" + turnoActual() + ", pos=" + nextPos + ", contrario=" + posContrario);
            int miAlmacen = (turnoActual() == Turno.turnoJ1) ? 6 : 13;
            setSemillas(
                    miAlmacen,
                    1 + getSemillas(miAlmacen) + getSemillas(posContrario)
            );
            setSemillas(nextPos, 0);
            setSemillas(posContrario, 0);
        }

        // Si es fin -> recolectar
        if (campoVacio(Turno.turnoJ1) || campoVacio(Turno.turnoJ2)) {
            recolectar(0);
            recolectar(7);
            setTurno(Turno.Turno_TERMINADO);
        }

        // Determinar turno siguiente (si es depósito propio -> repite turno)
        if (turnoActual() == Turno.turnoJ1 && nextPos != 6)
            setTurno(Turno.turnoJ2);
        else if (turnoActual() == Turno.turnoJ2 && nextPos != 13)
            setTurno(Turno.turnoJ1);
        Log.i("MiW", "\t turno = " + turnoActual());
    }

    /**
     * @return Indica si el juego ha finalizado (todas las semillas están en los depósitos)
     */
    public boolean juegoTerminado() {
        return (turnoActual() == Turno.Turno_TERMINADO);
    }

    /**
     * @param turno Turno
     * @return Determina si el campo del jugador especificado por <i>turno</i> está vacío
     */
    private boolean campoVacio(Turno turno) {
        boolean vacio = true;
        int inicioCampo = (turno == Turno.turnoJ1) ? 0 : 7;
        for (int i = inicioCampo; i < inicioCampo + 6; i++)
            vacio = vacio && (getSemillas(i) == 0);

        return vacio;
    }

    /**
     * Recolecta las semillas del campo que empieza en <i>pos</i> en su depósito (<i>pos + 6</i>)
     *
     * @param pos Posición de inicio del campo
     */
    private void recolectar(int pos) {
        int semillasAlmacen = getSemillas(pos + 6);
        for (int i = pos; i < pos + 6; i++) {
            semillasAlmacen += getSemillas(i);
            setSemillas(i, 0);
        }
        setSemillas(pos + 6, semillasAlmacen);
        Log.i("MiW", "\tRecolectar - " + pos);
    }

    /**
     * @return turno actual
     */
    public Turno turnoActual() {
        return bantumiVM.getTurno().getValue();
    }

    /**
     * Establece el turno
     *
     * @param turno
     */
    public void setTurno(Turno turno) {
        bantumiVM.setTurno(turno);
    }

    /**
     * Devuelve una cadena que representa el estado completo del juego
     *
     * @return juego serializado
     */
    public String serializa() {
        String datos = "";
        datos = datos.concat("fecha" + SEPARADOR_CLAVE_VALOR + new Date().getTime() + SEPARADOR_LINEA);
        datos = datos.concat("tablero" + SEPARADOR_CLAVE_VALOR + this.bantumiVM.serializa() + SEPARADOR_LINEA);
        datos = datos.concat("turno" + SEPARADOR_CLAVE_VALOR + Objects.requireNonNull(this.bantumiVM.getTurno().getValue()).toString());
        return datos;
    }

    /**
     * Recupera el estado del juego a partir de su representación
     *
     * @param juegoSerializado cadena que representa el estado completo del juego
     */
    public void deserializa(String juegoSerializado) {
        Map<String, String> datos = new HashMap<>();
        String[] lineas = juegoSerializado.split(SEPARADOR_LINEA);
        for(String linea : lineas) {
            String[] claveValor = linea.split(SEPARADOR_CLAVE_VALOR);
            datos.put(claveValor[0], claveValor[1]);
        }
        cargarDatos(datos);
    }

    public void cargarDatos(Map<String, String> datos) {
        this.setTurno((Objects.equals(datos.get("turno"), "turnoJ1"))
                ? Turno.turnoJ1
                :Turno.turnoJ2);
        List<Integer> semillasTablero = this.bantumiVM.deserializa(Objects.requireNonNull(datos.get("tablero")));
        for(int i = 0; i < semillasTablero.size(); i++) {
            this.bantumiVM.setNumSemillas(i, semillasTablero.get(i));
        }
    }

    public void guardar(FileManager fileManager) {
        String datos = this.serializa();
        fileManager.save(datos);
    }

    public void recuperar(FileManager fileManager) {
        String datos = fileManager.load();
        this.deserializa(datos);
    }

    public void setTurnoInicial(Turno turnoInicial) {
        this.turnoInicial = turnoInicial;
    }
}
