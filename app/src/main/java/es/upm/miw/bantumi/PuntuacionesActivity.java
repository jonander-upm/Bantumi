package es.upm.miw.bantumi;

import android.os.Bundle;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.viewmodel.CreationExtras;

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
        puntuacionesList.setAdapter(new PuntuacionListAdapter(getApplicationContext(),
                puntuacionViewModel.findTopTen()));
    }
}
