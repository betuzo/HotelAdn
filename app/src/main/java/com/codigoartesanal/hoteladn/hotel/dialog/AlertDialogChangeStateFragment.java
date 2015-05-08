package com.codigoartesanal.hoteladn.hotel.dialog;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.DialogFragment;
import android.util.Log;

import com.codigoartesanal.hoteladn.hotel.R;
import com.codigoartesanal.hoteladn.hotel.listener.OnStateSelectedListener;
import com.codigoartesanal.hoteladn.hotel.model.Response;
import com.codigoartesanal.hoteladn.hotel.model.Session;
import com.codigoartesanal.hoteladn.hotel.model.SolicitudServicio;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class AlertDialogChangeStateFragment extends DialogFragment {
    protected static final String TAG = AlertDialogChangeStateFragment.class.getSimpleName();

    public static final String ARG_SOLICITUD = "argSolicitud";
    public static final String ARG_USER = "argUser";

    SolicitudServicio solicitudServicio;
    Session session;

    String[] validStates;

    OnStateSelectedListener mListener;

    public AlertDialogChangeStateFragment() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        solicitudServicio = (SolicitudServicio) arguments.getSerializable(ARG_SOLICITUD);
        session = (Session) arguments.getSerializable(ARG_USER);
        validStates = getValidStates();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder
            .setTitle(solicitudServicio.getHabitacionNo() + " "
                    + solicitudServicio.getServicioClave())
            .setItems(validStates, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    solicitudServicio.setEstadoSolicitud(validStates[which]);
                    new ChangeStateTask().execute();
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

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnStateSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnArticleSelectedListener");
        }
    }

    private class ChangeStateTask extends AsyncTask<Void, Void, Response> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Response doInBackground(Void... params) {
            final String url = getString(R.string.base_uri) + "/solicitud/" + solicitudServicio.getId();
            // Set the Accept header for "application/json"
            HttpHeaders requestHeaders = new HttpHeaders();
            List<MediaType> acceptableMediaTypes = new ArrayList<MediaType>();
            acceptableMediaTypes.add(MediaType.APPLICATION_JSON);
            requestHeaders.setAccept(acceptableMediaTypes);
            requestHeaders.add("X-Auth-Token", session.getKeyHabitacion());

            // Populate the headers in an HttpEntity object to use for the request
            HttpEntity<SolicitudServicio> requestEntity =
                    new HttpEntity<SolicitudServicio>(solicitudServicio ,requestHeaders);
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            try {
                ResponseEntity<Response> loginResponse =
                        restTemplate.exchange(url, HttpMethod.PUT   , requestEntity, Response.class);
                return loginResponse.getBody();
            } catch (HttpClientErrorException e) {
                Log.e(TAG, e.getLocalizedMessage(), e);
                return new Response(Response.CODE_ERROR, e.getLocalizedMessage());
            } catch (ResourceAccessException e) {
                Log.e(TAG, e.getLocalizedMessage(), e);
                return new Response(Response.CODE_ERROR, e.getLocalizedMessage());
            }
        }

        @Override
        protected void onPostExecute(Response result) {
            mListener.onArticleSelected(solicitudServicio);
        }

    }
}
