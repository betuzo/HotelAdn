package com.codigoartesanal.hoteladn.hotel;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


import com.codigoartesanal.hoteladn.hotel.dialog.AlertDialogChangeStateFragment;
import com.codigoartesanal.hoteladn.hotel.listener.OnStateSelectedListener;
import com.codigoartesanal.hoteladn.hotel.listener.OnFragmentInteractionSolicitudListener;
import com.codigoartesanal.hoteladn.hotel.model.Session;
import com.codigoartesanal.hoteladn.hotel.model.SessionRepository;
import com.codigoartesanal.hoteladn.hotel.model.SolicitudServicio;

/**
 * A fragment with a Google +1 button.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionSolicitudListener} interface
 * to handle interaction events.
 * Use the {@link SolicitudServicioDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SolicitudServicioDetailFragment extends Fragment
        implements OnStateSelectedListener {
    private static final String ARG_SOLICITUD_SERVICIO = "solicitudServicio";
    private static final String TAG_CHANGE_STATE_DIALOG_FRAGMENT = "tagChangeStateDialogFragment";

    private SolicitudServicio solicitudServicio;

    private OnFragmentInteractionSolicitudListener mListener;
    private Session session;

    TextView txtEstado;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param solicitud Parameter 1.
     * @return A new instance of fragment SolicitudServicioNuevaFragment.
     */
    public static SolicitudServicioDetailFragment newInstance(SolicitudServicio solicitud) {
        SolicitudServicioDetailFragment fragment = new SolicitudServicioDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_SOLICITUD_SERVICIO, solicitud);
        fragment.setArguments(args);
        return fragment;
    }

    public SolicitudServicioDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = SessionRepository.get(getActivity());

        if (getArguments() != null) {
            solicitudServicio = (SolicitudServicio) getArguments().getSerializable(ARG_SOLICITUD_SERVICIO);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_solicitud_servicio_nueva, container, false);
        final Button btnChangeState = (Button) view.findViewById(R.id.btn_change_state);
        final Button btnViewComments = (Button) view.findViewById(R.id.btn_view_comments);

        if(solicitudServicio.getId()<0) {
            btnChangeState.setVisibility(View.GONE);
            btnViewComments.setVisibility(View.GONE);
            return view;
        }

        btnChangeState.setVisibility(View.VISIBLE);
        btnViewComments.setVisibility(View.VISIBLE);

        TextView txtHabitacion = (TextView) view.findViewById(R.id.txt_habitacion);
        txtHabitacion.setText(solicitudServicio.getHabitacionNo() + " "
                + solicitudServicio.getHabitacionDesc());
        TextView txtServicio = (TextView) view.findViewById(R.id.txt_servicio);
        txtServicio.setText(solicitudServicio.getServicioDesc());
        TextView txtFecha = (TextView) view.findViewById(R.id.txt_fecha);
        txtFecha.setText(solicitudServicio.getFechaSolicitud().toString());
        txtEstado = (TextView) view.findViewById(R.id.txt_estado);
        txtEstado.setText(solicitudServicio.getEstadoSolicitud());

        btnChangeState.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DialogFragment fragment = new AlertDialogChangeStateFragment();
                Bundle args = new Bundle();
                args.putSerializable(AlertDialogChangeStateFragment.ARG_SOLICITUD,
                        solicitudServicio);
                args.putSerializable(AlertDialogChangeStateFragment.ARG_USER,
                        session);
                fragment.setArguments(args);
                fragment.show(getFragmentManager(), TAG_CHANGE_STATE_DIALOG_FRAGMENT);
            }
        });

        btnViewComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launch = new Intent(getActivity(), CommentActivity.class);
                if (solicitudServicio != null) {
                    launch.putExtra("solicitudServicio", solicitudServicio);
                }
                getActivity().startActivity(launch);
            }
        });

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(solicitudServicio);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionSolicitudListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onArticleSelected(SolicitudServicio solicitudServicio) {
        txtEstado.setText(solicitudServicio.getEstadoSolicitud());
    }
}
