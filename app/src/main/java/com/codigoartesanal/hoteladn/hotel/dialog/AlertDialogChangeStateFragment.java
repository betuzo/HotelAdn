package com.codigoartesanal.hoteladn.hotel.dialog;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codigoartesanal.hoteladn.hotel.R;
import com.codigoartesanal.hoteladn.hotel.model.SolicitudServicio;

/**
 * A simple {@link Fragment} subclass.
 */
public class AlertDialogChangeStateFragment extends DialogFragment {
    SolicitudServicio solicitudServicio;

    public static final String ARG_SOLICITUD = "argSolicitud";

    public AlertDialogChangeStateFragment() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        solicitudServicio = (SolicitudServicio) arguments.getSerializable(ARG_SOLICITUD);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder
            .setTitle(solicitudServicio.getHabitacionNo() + " "
                    + solicitudServicio.getServicioClave())
            .setItems(getValidStates(), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                }
            });
        return builder.create();
    }

    private String[] getValidStates() {
        String[] estadosValidos = new String[4];
        int iCount = 0;
        String[] estados = getResources().getStringArray(R.array.sol_list_state_values);
        for (String estado : estados) {
            if (!estado.equalsIgnoreCase(solicitudServicio.getEstadoSolicitud())){
                estadosValidos[iCount] = estado;
                iCount++;
            }
        }
        return estadosValidos;
    }
}
