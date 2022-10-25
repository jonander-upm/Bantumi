package es.upm.miw.bantumi;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

public class OverwriteAlertDialog extends AppCompatDialogFragment {
    @NonNull
    @Override
	public AppCompatDialog onCreateDialog(Bundle savedInstanceState) {
		final MainActivity main = (MainActivity) getActivity();

        assert main != null;
        AlertDialog.Builder builder = new AlertDialog.Builder(main);
        builder
                .setTitle(R.string.txtDialogoSobreescribirTitulo)
                .setMessage(R.string.txtDialogoSobreescribirPregunta)
                .setPositiveButton(
                        getString(R.string.txtDialogoSobreescribirAfirmativo),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                main.cargarPartida();
                            }
                        }
                )
                .setNegativeButton(
                        getString(R.string.txtDialogoSobreescribirNegativo),
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
