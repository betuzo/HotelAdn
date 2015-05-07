package com.codigoartesanal.hoteladn.hotel;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;


import com.codigoartesanal.hoteladn.hotel.adapter.SolicitudServicioArrayAdapter;
import com.codigoartesanal.hoteladn.hotel.listener.OnFragmentInteractionSolicitudListener;
import com.codigoartesanal.hoteladn.hotel.listener.OnStateSelectedListener;
import com.codigoartesanal.hoteladn.hotel.model.Session;
import com.codigoartesanal.hoteladn.hotel.model.SessionRepository;
import com.codigoartesanal.hoteladn.hotel.model.SolicitudServicio;
import com.codigoartesanal.hoteladn.hotel.service.LoginService;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionSolicitudListener}
 * interface.
 */
public class SolicitudServicioFragment extends Fragment
        implements AbsListView.OnItemClickListener, OnStateSelectedListener {

    protected static final String TAG = SolicitudServicioFragment.class.getSimpleName();

    private List<SolicitudServicio> solicitudes;
    private Session session;

    private OnFragmentInteractionSolicitudListener mListener;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    private View viewRowSelected;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private SolicitudServicioArrayAdapter mAdapter;

    public static SolicitudServicioFragment newInstance() {
        SolicitudServicioFragment fragment = new SolicitudServicioFragment();

        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SolicitudServicioFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        session = SessionRepository.get(getActivity());
        mAdapter = new SolicitudServicioArrayAdapter(getActivity());
        new DownloadSolicitudServicioTask().execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_solicitudservicio, container, false);
        getActivity().setTitle(session.getNumeroHabitacion());
        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);
        Button sendBtn = (Button) view.findViewById(R.id.btn_new_sol);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launch = new Intent(getActivity(), SolicitudNewActivity.class);
                getActivity().startActivity(launch);
            }
        });

        return view;
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            mListener.onFragmentInteraction(solicitudes.get(position));
        }
        viewRowSelected = view;
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }

    @Override
    public void onArticleSelected(SolicitudServicio solicitudServicio) {
        ((TextView)viewRowSelected.findViewById(R.id.solicitud_state)).setText(
                solicitudServicio.getEstadoSolicitud());
    }

    // ***************************************
    // Private classes
    // ***************************************
    private class DownloadSolicitudServicioTask extends AsyncTask<Void, Void, List<SolicitudServicio>> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected List<SolicitudServicio> doInBackground(Void... params) {
            try {
                // The URL for making the GET request
                final String url = getString(R.string.base_uri) + "/hotel/" + session.getIdHotel() + "/solicitud";

                // Set the Accept header for "application/json"
                HttpHeaders requestHeaders = new HttpHeaders();
                List<MediaType> acceptableMediaTypes = new ArrayList<MediaType>();
                acceptableMediaTypes.add(MediaType.APPLICATION_JSON);
                requestHeaders.setAccept(acceptableMediaTypes);
                requestHeaders.add("X-Auth-Token", session.getToken());

                // Populate the headers in an HttpEntity object to use for the request
                HttpEntity<?> requestEntity = new HttpEntity<Object>(requestHeaders);

                // Create a new RestTemplate instance
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

                // Perform the HTTP GET request
                ResponseEntity<SolicitudServicio[]> responseEntity
                        = restTemplate.exchange(url, HttpMethod.GET, requestEntity, SolicitudServicio[].class);
                Log.d(TAG, String.valueOf(responseEntity.getBody()));

                // convert the array to a list and return it
                return Arrays.asList(responseEntity.getBody());
            } catch (HttpClientErrorException e) {
                Log.e(TAG, e.getLocalizedMessage(), e);
                LoginService.handlerError(e, getActivity());
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<SolicitudServicio> results) {
            solicitudes = results;
            mAdapter.setData(results);
        }

    }

}
