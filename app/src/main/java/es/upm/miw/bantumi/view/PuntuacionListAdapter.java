package es.upm.miw.bantumi.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import es.upm.miw.bantumi.R;
import es.upm.miw.bantumi.model.PuntuacionEntity;

public class PuntuacionListAdapter extends BaseAdapter {
    Context context;
    List<PuntuacionEntity> puntuaciones;
    LayoutInflater inflater;

    public PuntuacionListAdapter(Context applicationContext, List<PuntuacionEntity> puntuaciones) {
        this.context = context;
        this.puntuaciones = puntuaciones;
        inflater = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return puntuaciones.size();
    }

    @Override
    public Object getItem(int i) {
        return puntuaciones.get(i);
    }

    @Override
    public long getItemId(int i) {
        return puntuaciones.get(i).getUsername().hashCode();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.puntuacion_item, null);
        TextView tvUsername = view.findViewById(R.id.tvUsername);
        TextView tvPlayerScore = view.findViewById(R.id.tvPlayerScore);
        TextView tvComputerScore = view.findViewById(R.id.tvComputerScore);
        tvUsername.setText(puntuaciones.get(i).getUsername());
        tvPlayerScore.setText(String.valueOf(puntuaciones.get(i).getPlayerSeeds()));
        tvComputerScore.setText(String.valueOf(puntuaciones.get(i).getOpponentSeeds()));
        return view;
    }
}
