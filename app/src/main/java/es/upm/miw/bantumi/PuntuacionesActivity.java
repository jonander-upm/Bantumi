package es.upm.miw.bantumi;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.viewmodel.CreationExtras;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import es.upm.miw.bantumi.model.PuntuacionEntity;
import es.upm.miw.bantumi.model.PuntuacionViewModel;
import es.upm.miw.bantumi.view.PuntuacionListAdapter;

public class PuntuacionesActivity extends AppCompatActivity {

    PuntuacionViewModel puntuacionViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoreboard);
        ListView puntuacionesList = findViewById(R.id.lvPuntuaciones);
        puntuacionViewModel = new ViewModelProvider(this).get(PuntuacionViewModel.class);
        puntuacionViewModel.findTopTen().observe(this, new Observer<List<PuntuacionEntity>>() {
            @Override
            public void onChanged(List<PuntuacionEntity> puntuacionEntities) {
                puntuacionesList.setAdapter(new PuntuacionListAdapter(getApplicationContext(),
                        puntuacionEntities));
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.puntuaciones_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.opcEliminarPuntuaciones:
                new DeleteScoreboardAlertDialog().show(getSupportFragmentManager(), "DELETE_DIALOG");
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

    public void eliminarPuntuaciones() {
        puntuacionViewModel.deleteAll();
        Snackbar.make(
                findViewById(android.R.id.content),
                getString(R.string.txtPuntuacionesEliminadas),
                Snackbar.LENGTH_LONG
        ).show();
    }
}
