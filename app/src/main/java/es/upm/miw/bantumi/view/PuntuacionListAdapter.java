package es.upm.miw.bantumi.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.Locale;

import es.upm.miw.bantumi.R;
import es.upm.miw.bantumi.model.PuntuacionEntity;

public class PuntuacionListAdapter extends BaseAdapter {
    Context context;
    List<PuntuacionEntity> puntuaciones;
    LayoutInflater inflater;

    public PuntuacionListAdapter(Context applicationContext, List<PuntuacionEntity>  puntuaciones) {
        this.context = applicationContext;
        this.puntuaciones = puntuaciones;
        inflater = (LayoutInflater.from(applicationContext));
    }

    static class PuntuacionViewHolder {
        TextView tvUsername;
        TextView tvPlayerScore;
        TextView tvComputerScore;
        TextView durationScore;
    }

    @Override
    public int getCount() {
        return puntuaciones != null ?
                puntuaciones.size()
                : 0;
    }

    @Override
    public Object getItem(int i) {
        return puntuaciones != null ?
                puntuaciones.get(i)
                : null;
    }

    @Override
    public long getItemId(int i) {
        return puntuaciones != null ?
                puntuaciones.get(i).getUsername().hashCode()
                : -1;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        PuntuacionViewHolder viewHolder;
        if(view == null) {
            viewHolder = new PuntuacionViewHolder();
            view = inflater.inflate(R.layout.puntuacion_item, null);
            viewHolder.tvUsername = view.findViewById(R.id.tvUsername);
            viewHolder.tvPlayerScore = view.findViewById(R.id.tvPlayerScore);
            viewHolder.tvComputerScore = view.findViewById(R.id.tvComputerScore);
            viewHolder.durationScore = view.findViewById(R.id.tvDurationScore);
        } else {
            viewHolder = (PuntuacionViewHolder) view.getTag();
        }
        if(puntuaciones != null) {
            int pos = i + 1;
            int minutes = puntuaciones.get(i).getGameDurationSecs().intValue() / 60;
            int seconds = puntuaciones.get(i).getGameDurationSecs().intValue() % 60;
            String usernamePosition = pos + ". - " + puntuaciones.get(i).getUsername();
            viewHolder.tvUsername.setText(usernamePosition);
            viewHolder.tvPlayerScore.setText(String.valueOf(puntuaciones.get(i).getPlayerSeeds()));
            viewHolder.tvComputerScore.setText(String.valueOf(puntuaciones.get(i).getOpponentSeeds()));
            viewHolder.durationScore.setText(String.format(Locale.getDefault(), "%d:%02d", minutes, seconds));
        }
        return view;
    }
}
