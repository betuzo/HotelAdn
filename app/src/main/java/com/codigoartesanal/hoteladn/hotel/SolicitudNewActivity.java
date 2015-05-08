package com.codigoartesanal.hoteladn.hotel;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.codigoartesanal.hoteladn.hotel.model.Categoria;
import com.codigoartesanal.hoteladn.hotel.model.Response;
import com.codigoartesanal.hoteladn.hotel.model.Servicio;
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
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


public class SolicitudNewActivity extends ActionBarActivity {

    protected static final String TAG = SolicitudNewActivity.class.getSimpleName();

    ArrayAdapter<Categoria> dataAdapter;

    private Session session;

    private Spinner spnCategoria;
    private Spinner spnServicio;
    private EditText txtHabitacion;
    private EditText txtComentario;
    private Button btnSolSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solicitud_new);
        session = SessionRepository.get(getApplicationContext());

        spnCategoria = (Spinner) findViewById(R.id.spn_sol_categoria);
        spnServicio = (Spinner) findViewById(R.id.spn_sol_servicio);
        txtHabitacion = (EditText) findViewById(R.id.txt_sol_habitacion);
        txtComentario = (EditText) findViewById(R.id.txt_sol_comentario);

        txtHabitacion.setText(session.getNumeroHabitacion() + " "
                + session.getDescripcionHabitacion());
        txtHabitacion.setEnabled(false);

        btnSolSave = (Button) findViewById(R.id.btn_sol_save);

        btnSolSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SaveSolicitudTask().execute();
            }
        });
        spnCategoria.setOnItemSelectedListener(new CustomOnItemSelectedListener());

        new DownloadCategoriaTask().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_solicitud_new, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void displayResponse(Response response) {
        Toast.makeText(this, response.getMessage(), Toast.LENGTH_LONG).show();
    }

    private class DownloadCategoriaTask extends AsyncTask<Void, Void, List<Categoria>> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected List<Categoria> doInBackground(Void... params) {
            try {
                // The URL for making the GET request
                final String url = getString(R.string.base_uri) + "/hotel/" + session.getIdHotel() + "/categoria";

                // Set the Accept header for "application/json"
                HttpHeaders requestHeaders = new HttpHeaders();
                List<MediaType> acceptableMediaTypes = new ArrayList<MediaType>();
                acceptableMediaTypes.add(MediaType.APPLICATION_JSON);
                requestHeaders.setAccept(acceptableMediaTypes);
                requestHeaders.add("X-Auth-Token", session.getKeyHabitacion());

                // Populate the headers in an HttpEntity object to use for the request
                HttpEntity<?> requestEntity = new HttpEntity<Object>(requestHeaders);

                // Create a new RestTemplate instance
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

                // Perform the HTTP GET request
                ResponseEntity<Categoria[]> responseEntity
                        = restTemplate.exchange(url, HttpMethod.GET, requestEntity, Categoria[].class);
                Log.d(TAG, String.valueOf(responseEntity.getBody()));

                // convert the array to a list and return it
                return Arrays.asList(responseEntity.getBody());
            } catch (HttpClientErrorException e) {
                Log.e(TAG, e.getLocalizedMessage(), e);
                LoginService.handlerError(e, getApplicationContext());
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<Categoria> results) {
            dataAdapter = new ArrayAdapter<Categoria>(
                            getApplicationContext(), R.layout.simple_spinner_item, results);
            dataAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
            spnCategoria.setAdapter(dataAdapter);
        }

    }

    private class DownloadServicioTask extends AsyncTask<String, Void, List<Servicio>> {
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected List<Servicio> doInBackground(String... params) {
            try {
                // The URL for making the GET request
                final String url = getString(R.string.base_uri) + "/hotel/" + session.getIdHotel()
                        + "/categoria/" + params[0] + "/servicio";

                // Set the Accept header for "application/json"
                HttpHeaders requestHeaders = new HttpHeaders();
                List<MediaType> acceptableMediaTypes = new ArrayList<MediaType>();
                acceptableMediaTypes.add(MediaType.APPLICATION_JSON);
                requestHeaders.setAccept(acceptableMediaTypes);
                requestHeaders.add("X-Auth-Token", session.getKeyHabitacion());

                // Populate the headers in an HttpEntity object to use for the request
                HttpEntity<?> requestEntity = new HttpEntity<Object>(requestHeaders);

                // Create a new RestTemplate instance
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

                // Perform the HTTP GET request
                ResponseEntity<Servicio[]> responseEntity
                        = restTemplate.exchange(url, HttpMethod.GET, requestEntity, Servicio[].class);
                Log.d(TAG, String.valueOf(responseEntity.getBody()));

                // convert the array to a list and return it
                return Arrays.asList(responseEntity.getBody());
            } catch (HttpClientErrorException e) {
                Log.e(TAG, e.getLocalizedMessage(), e);
                LoginService.handlerError(e, getApplicationContext());
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<Servicio> results) {
            ArrayAdapter<Servicio> dataAdapter = new ArrayAdapter<Servicio>(
                    getApplicationContext(), R.layout.simple_spinner_item, results);
            dataAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
            spnServicio.setAdapter(dataAdapter);
        }

    }
    private class CustomOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {
            Categoria categoria = dataAdapter.getItem(pos);
            new DownloadServicioTask().execute(String.valueOf(categoria.getId()));
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }

    private class SaveSolicitudTask extends AsyncTask<Void, Void, Response> {
        SolicitudServicio solicitudServicio;
        boolean isValid = true;

        @Override
        protected void onPreExecute() {
            long idHabitacion = session.getIdHabitacion();
            Servicio servicio = (Servicio) spnServicio.getSelectedItem();
            String comentario = txtComentario.getText().toString();
            if (servicio == null || comentario == null
                    || idHabitacion <= 0 || comentario.isEmpty()) {
                isValid = false;
                return;
            }
            solicitudServicio = new SolicitudServicio();
            solicitudServicio.setHabitacionId(idHabitacion);
            solicitudServicio.setServicioId(servicio.getId());
            solicitudServicio.setEstadoSolicitud("SOLICITADA");
            solicitudServicio.setFechaSolicitud(new Date());
            solicitudServicio.setComentario(comentario);
            solicitudServicio.setTipoComentario("HABITACION");
            solicitudServicio.setFechaComentario(new Date());
        }

        @Override
        protected Response doInBackground(Void... params) {
            if (!isValid) {
                return new Response(Response.CODE_ERROR, "Datos incompletos");
            }

            final String url = getString(R.string.base_uri) + "/solicitud";
            // Set the Accept header for "application/json"
            HttpHeaders requestHeaders = new HttpHeaders();
            List<MediaType> acceptableMediaTypes = new ArrayList<>();
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
                        restTemplate.exchange(url, HttpMethod.POST   , requestEntity, Response.class);
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
            displayResponse(result);
        }
    }
}
