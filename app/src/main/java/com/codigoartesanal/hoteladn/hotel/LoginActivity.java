package com.codigoartesanal.hoteladn.hotel;


import android.content.Intent;
import android.os.AsyncTask;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.codigoartesanal.hoteladn.hotel.model.Session;
import com.codigoartesanal.hoteladn.hotel.model.SessionRepository;
import com.codigoartesanal.hoteladn.hotel.model.User;
import com.codigoartesanal.hoteladn.hotel.service.LoginService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpAuthentication;
import org.springframework.http.HttpBasicAuthentication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AbstractAsyncActivity {

    protected static final String TAG = LoginActivity.class.getSimpleName();
    protected static final String USERNAME = "username";
    protected static final String PASSWORD = "password";

    protected TipoLogin tipoLogin;

    LinearLayout lyLogin;
    LinearLayout lyInfoHabitacion;
    Switch swTipoLogin;
    TextView txtHabitacion;
    EditText txtUsername;
    EditText txtPassword;

    // ***************************************
    // Activity methods
    // ***************************************
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initiate the request to the protected service
        final Button submitButton = (Button) findViewById(R.id.submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (tipoLogin == TipoLogin.LOGIN && validateInfo()) {
                    new FetchSecuredResourceTask().execute();
                } else if(tipoLogin == TipoLogin.HABITACION) {
                    showSolicitud();
                }
            }
        });

        this.swTipoLogin = (Switch) findViewById(R.id.switch_modo);
        this.swTipoLogin .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Session session = SessionRepository.get(getApplicationContext());
                configureByTipo(session, !isChecked);
            }
        });

        this.lyLogin = (LinearLayout) findViewById(R.id.login);
        this.lyInfoHabitacion = (LinearLayout) findViewById(R.id.info_habitacion);

        this.txtHabitacion = (TextView) findViewById(R.id.label_habitacion);
        this.txtUsername = (EditText) findViewById(R.id.username);
        this.txtPassword = (EditText) findViewById(R.id.password);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Session session = SessionRepository.get(this);
        this.configureByTipo(session, (session==null || session.getToken().isEmpty()));
    }

    // ***************************************
    // Private methods
    // ***************************************
    private void displayResponse(String response) {
        Toast.makeText(this, response, Toast.LENGTH_LONG).show();
    }

    private boolean validateInfo(){
        String username = txtUsername.getText().toString();
        String password = txtPassword.getText().toString();
        String message;
        if (username == null || username.isEmpty()) {
            message = getString(R.string.message_username_invalid);
            txtUsername.setFocusable(true);
        }
        else if (password == null || password.isEmpty()) {
            message = getString(R.string.message_password_invalid);
            txtPassword.setFocusable(true);
        } else {
            return true;
        }

        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        return false;
    }
    private void configureByTipo(Session session, boolean isLogin) {
        if (isLogin) {
            this.swTipoLogin.setChecked(false);
            this.swTipoLogin.setEnabled((session != null && !session.getToken().isEmpty()));
            this.tipoLogin = TipoLogin.LOGIN;
            this.lyInfoHabitacion.setVisibility(LinearLayout.GONE);
            this.lyLogin.setVisibility(LinearLayout.VISIBLE);
        } else {
            this.swTipoLogin.setChecked(true);
            this.swTipoLogin.setEnabled(true);
            this.lyLogin.setVisibility(LinearLayout.GONE);
            this.lyInfoHabitacion.setVisibility(LinearLayout.VISIBLE);
            this.tipoLogin = TipoLogin.HABITACION;
            this.txtHabitacion.setText(
                    session.getNumeroHabitacion() + " - " +
                            session.getDescripcionHabitacion() + " - " +
                            session.getNombreOficial());

        }
    }

    private void showSettings(User user){
        Intent launch = new Intent(this, SettingActivity.class);
        if (user != null) {
            launch.putExtra("user", user);
        }

        this.startActivity(launch);
    }

    private void showSolicitud(){
        Intent launch = new Intent(this, SolicitudActivity.class);
        this.startActivity(launch);
    }

    private enum TipoLogin{
        LOGIN,HABITACION
    }

    // ***************************************
    // Private classes
    // ***************************************
    private class FetchSecuredResourceTask extends AsyncTask<Void, Void, String> {

        private String username;

        private String password;

        @Override
        protected void onPreExecute() {
            showLoadingProgressDialog();

            this.username = txtUsername.getText().toString();
            this.password = txtPassword.getText().toString();
        }

        @Override
        protected String doInBackground(Void... params) {
            final String url = getString(R.string.base_uri) + "/session/login";

            // Populate the HTTP Basic Authentitcation header with the username and password
            HttpAuthentication authHeader = new HttpBasicAuthentication(username, password);
            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.setAuthorization(authHeader);
            requestHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            requestHeaders.setContentType(MediaType.APPLICATION_JSON);

            RestTemplate restTemplate = new RestTemplate();
            JSONObject request = new JSONObject();
            try {
                request.put(USERNAME, username);
                request.put(PASSWORD, password);
            } catch (JSONException e) {
                e.printStackTrace();
                return e.getLocalizedMessage();
            }

            HttpEntity<String> httpEntity = new HttpEntity<String>(request.toString(), requestHeaders);
            List<HttpMessageConverter<?>> messageConverters = new ArrayList< HttpMessageConverter<?> >();
            messageConverters.add( new StringHttpMessageConverter() );
            restTemplate.setMessageConverters(messageConverters);

            try {
                ResponseEntity<String> loginResponse =
                        restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
                return loginResponse.getBody();
            } catch (HttpClientErrorException e) {
                Log.e(TAG, e.getLocalizedMessage(), e);
                return e.getLocalizedMessage();
            } catch (ResourceAccessException e) {
                Log.e(TAG, e.getLocalizedMessage(), e);
                return e.getLocalizedMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            dismissProgressDialog();
            showSettings(LoginService.convertStringToJSONObject(result));
        }

    }
}