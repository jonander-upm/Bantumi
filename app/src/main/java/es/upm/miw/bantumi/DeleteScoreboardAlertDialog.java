package es.upm.miw.bantumi;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

public class DeleteScoreboardAlertDialog extends AppCompatDialogFragment {
    @NonNull
    @Override
	public AppCompatDialog onCreateDialog(Bundle savedInstanceState) {
		final PuntuacionesActivity main = (PuntuacionesActivity) getActivity();

        assert main != null;
        AlertDialog.Builder builder = new AlertDialog.Builder(main);
        builder
                .setTitle(R.string.txtDialogoEliminarPuntuacionesTitulo)
                .setMessage(R.string.txtDialogoEliminarPuntuacionesPregunta)
                .setPositiveButton(
                        getString(R.string.txtDialogoEliminarPuntuacionesAfirmativo),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                main.eliminarPuntuaciones();
                            }
                        }
                )
                .setNegativeButton(
                        getString(R.string.txtDialogoEliminarPuntuacionesNegativo),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }
                );

		return builder.create();
	}
}
