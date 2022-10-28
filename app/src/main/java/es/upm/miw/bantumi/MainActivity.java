package es.upm.miw.bantumi;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.PreferenceManager;

import com.google.android.material.snackbar.Snackbar;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;

import es.upm.miw.bantumi.model.BantumiViewModel;
import es.upm.miw.bantumi.model.PuntuacionEntity;
import es.upm.miw.bantumi.model.PuntuacionViewModel;
import es.upm.miw.bantumi.util.FileManager;

public class MainActivity extends AppCompatActivity {

    protected final String LOG_TAG = "MiW";
    protected final String NOMBRE_FICHERO = "partida.txt";
    SharedPreferences preferencias;
    String username;
    TextView tvJugador1;
    TextView tvTimer;
    JuegoBantumi juegoBantumi;
    BantumiViewModel bantumiVM;
    FileManager fileManager;
    PuntuacionViewModel puntuacionViewModel;
    int numInicialSemillas;
    Long duracionPartidaSegundos;
    JuegoBantumi.Turno turnoInicial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preferencias = PreferenceManager.getDefaultSharedPreferences(this);
        tvJugador1 = findViewById(R.id.tvPlayer1);
        tvTimer = findViewById(R.id.tvTimer);
        duracionPartidaSegundos = 0L;
        // Instancia el ViewModel y el juego, y asigna observadores a los huecos
        bantumiVM = new ViewModelProvider(this).get(BantumiViewModel.class);
        loadGamePreferences();
        juegoBantumi = new JuegoBantumi(bantumiVM, turnoInicial, numInicialSemillas);
        puntuacionViewModel = new ViewModelProvider(this).get(PuntuacionViewModel.class);
        crearObservadores();
        fileManager = FileManager.builder()
                .applicationContext(this)
                .fileName(NOMBRE_FICHERO)
                .storageSystem(FileManager.StorageSystem.FILE_SYSTEM)
                .build();
    }

    @Override
    protected void onPause() {
        super.onPause();
        bantumiVM.pauseTimer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        preferencias = PreferenceManager.getDefaultSharedPreferences(this);
        loadUserPreferences();
        loadGamePreferences();
        bantumiVM.resumeTimer();
    }

    public void loadUserPreferences() {
        username = preferencias.getString(
                getString(R.string.keyUsername),
                getString(R.string.usernameDefault));
        if(username.equals("")) {
            username = getString(R.string.usernameDefault);
        }
        tvJugador1.setText(username);
    }

    public void loadGamePreferences() {
        numInicialSemillas = Integer.parseInt(preferencias.getString(
                getString(R.string.keySeedNumber),
                String.valueOf(R.integer.intNumInicialSemillas)));
        boolean switchJugadorInicial = preferencias.getBoolean(getString(R.string.keyFirstPlayer), false);
        turnoInicial = (!switchJugadorInicial)
                ? JuegoBantumi.Turno.turnoJ1
                : JuegoBantumi.Turno.turnoJ2;
        if(juegoBantumi != null) {
            juegoBantumi.setNumInicialSemillas(numInicialSemillas);
            juegoBantumi.setTurnoInicial(turnoInicial);
        }
    }

    /**
     * Crea y subscribe los observadores asignados a las posiciones del tablero.
     * Si se modifica el contenido del tablero -> se actualiza la vista.
     */
    private void crearObservadores() {
        for (int i = 0; i < JuegoBantumi.NUM_POSICIONES; i++) {
            int finalI = i;
            bantumiVM.getNumSemillas(i).observe(    // Huecos y almacenes
                    this,
                    new Observer<Integer>() {
                        @Override
                        public void onChanged(Integer integer) {
                            mostrarValor(finalI, juegoBantumi.getSemillas(finalI));
                        }
                    });
        }
        bantumiVM.getTurno().observe(   // Turno
                this,
                new Observer<JuegoBantumi.Turno>() {
                    @Override
                    public void onChanged(JuegoBantumi.Turno turno) {
                        marcarTurno(juegoBantumi.turnoActual());
                    }
                }
        );
        bantumiVM.getTimerMillis().observe(
                this,
                new Observer<Long>() {
                    @Override
                    public void onChanged(Long millis) {
                        duracionPartidaSegundos = millis / 1000;
                        int minutes = duracionPartidaSegundos.intValue() / 60;
                        int seconds = duracionPartidaSegundos.intValue() % 60;
                        String timeText = String.format("%d:%02d", minutes, seconds);
                        tvTimer.setText(timeText);
                    }
                }
        );
    }

    /**
     * Indica el turno actual cambiando el color del texto
     *
     * @param turnoActual turno actual
     */
    private void marcarTurno(@NonNull JuegoBantumi.Turno turnoActual) {
        TextView tvJugador2 = findViewById(R.id.tvPlayer2);
        switch (turnoActual) {
            case turnoJ1:
                tvJugador1.setTextColor(getColor(R.color.white));
                tvJugador1.setBackgroundColor(getColor(android.R.color.holo_blue_light));
                tvJugador2.setTextColor(getColor(R.color.black));
                tvJugador2.setBackgroundColor(getColor(R.color.white));
                break;
            case turnoJ2:
                tvJugador1.setTextColor(getColor(R.color.black));
                tvJugador1.setBackgroundColor(getColor(R.color.white));
                tvJugador2.setTextColor(getColor(R.color.white));
                tvJugador2.setBackgroundColor(getColor(android.R.color.holo_blue_light));
                break;
            default:
                tvJugador1.setTextColor(getColor(R.color.black));
                tvJugador2.setTextColor(getColor(R.color.black));
        }
    }

    /**
     * Muestra el valor <i>valor</i> en la posición <i>pos</i>
     *
     * @param pos posición a actualizar
     * @param valor valor a mostrar
     */
    private void mostrarValor(int pos, int valor) {
        String num2digitos = String.format(Locale.getDefault(), "%02d", pos);
        // Los identificadores de los huecos tienen el formato casilla_XX
        int idBoton = getResources().getIdentifier("casilla_" + num2digitos, "id", getPackageName());
        if (0 != idBoton) {
            TextView viewHueco = findViewById(idBoton);
            viewHueco.setText(String.valueOf(valor));
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.opciones_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
//            case R.id.opcAjustes: // @todo Preferencias
//                startActivity(new Intent(this, BantumiPrefs.class));
//                return true;
            case R.id.opcAcercaDe:
                new AlertDialog.Builder(this)
                        .setTitle(R.string.aboutTitle)
                        .setMessage(R.string.aboutMessage)
                        .setPositiveButton(android.R.string.ok, null)
                        .show();
                return true;
            case R.id.opcReiniciarPartida:
                new RestartAlertDialog().show(getSupportFragmentManager(), "RESTART_DIALOG");
                break;
            case R.id.opcGuardarPartida:
                juegoBantumi.guardar(fileManager);
                Snackbar.make(
                        findViewById(android.R.id.content),
                        getString(R.string.txtPartidaGuardada),
                        Snackbar.LENGTH_LONG
                ).show();
                break;
            case R.id.opcRecuperarPartida:
                if(!this.juegoBantumi.juegoTerminado()) {
                    new OverwriteAlertDialog().show(getSupportFragmentManager(), "OVERWRITE_DIALOG");
                } else {
                    this.cargarPartida();
                }
                break;
            case R.id.opcAjustes:
                Intent intentAjustes = new Intent(this, PreferenciasActivity.class);
                startActivity(intentAjustes);
                break;
            case R.id.opcMejoresResultados:
                Intent intentPuntuaciones = new Intent(this, PuntuacionesActivity.class);
                startActivity(intentPuntuaciones);
                break;
            default:
                Snackbar.make(
                        findViewById(android.R.id.content),
                        getString(R.string.txtSinImplementar),
                        Snackbar.LENGTH_LONG
                ).show();
        }
        return true;
    }

    /**
     * Acción que se ejecuta al pulsar sobre un hueco
     *
     * @param v Vista pulsada (hueco)
     */
    public void huecoPulsado(@NonNull View v) {
        String resourceName = getResources().getResourceEntryName(v.getId()); // pXY
        int num = Integer.parseInt(resourceName.substring(resourceName.length() - 2));
        Log.i(LOG_TAG, "huecoPulsado(" + resourceName + ") num=" + num);
        switch (juegoBantumi.turnoActual()) {
            case turnoJ1:
                juegoBantumi.jugar(num);
                break;
            case turnoJ2:
                juegaComputador();
                break;
            default:    // JUEGO TERMINADO
                finJuego();
        }
        if (juegoBantumi.juegoTerminado()) {
            finJuego();
        }
    }

    /**
     * Elige una posición aleatoria del campo del jugador2 y realiza la siembra
     * Si mantiene turno -> vuelve a jugar
     */
    void juegaComputador() {
        while (juegoBantumi.turnoActual() == JuegoBantumi.Turno.turnoJ2) {
            int pos = 7 + (int) (Math.random() * 6);    // posición aleatoria
            Log.i(LOG_TAG, "juegaComputador(), pos=" + pos);
            if (juegoBantumi.getSemillas(pos) != 0 && (pos < 13)) {
                juegoBantumi.jugar(pos);
            } else {
                Log.i(LOG_TAG, "\t posición vacía");
            }
        }
    }

    /**
     * El juego ha terminado. Volver a jugar?
     */
    private void finJuego() {
        bantumiVM.pauseTimer();
        String texto = (juegoBantumi.getSemillas(6) > 6 * numInicialSemillas)
                ? "Gana Jugador 1"
                : "Gana Jugador 2";
        Snackbar.make(
                findViewById(android.R.id.content),
                texto,
                Snackbar.LENGTH_LONG
        )
        .show();
        PuntuacionEntity puntuacionEntity = new PuntuacionEntity(username, new Date(), juegoBantumi.getSemillasJugador(), juegoBantumi.getSemillasOponente(), duracionPartidaSegundos);
        this.puntuacionViewModel.insert(puntuacionEntity);

        new FinalAlertDialog().show(getSupportFragmentManager(), "ALERT_DIALOG");
    }

    public void cargarPartida() {
        juegoBantumi.recuperar(fileManager);
        Snackbar.make(
                findViewById(android.R.id.content),
                getString(R.string.txtPartidaRecuperada),
                Snackbar.LENGTH_LONG
        ).show();
    }
}