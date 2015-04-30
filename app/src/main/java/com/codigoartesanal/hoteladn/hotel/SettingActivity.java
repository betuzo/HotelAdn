package com.codigoartesanal.hoteladn.hotel;

import android.content.Context;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.util.Log;

import com.codigoartesanal.hoteladn.hotel.model.Habitacion;
import com.codigoartesanal.hoteladn.hotel.model.Session;
import com.codigoartesanal.hoteladn.hotel.model.User;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.realm.Realm;


public class SettingActivity extends PreferenceActivity {

    private static final boolean ALWAYS_SIMPLE_PREFS = false;
    protected static final String TAG = SettingActivity.class.getSimpleName();
    protected User user = null;
    protected List<Habitacion> habitaciones;

    @Override
    public void onStart() {
        super.onStart();
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            this.user = (User) extras.getSerializable("user");
        }
        if (user!=null) {
            new DownloadHabitacionTask().execute();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        setupSimplePreferencesScreen();
    }

    private void setupSimplePreferencesScreen() {
        if (!isSimplePreferences(this)) {
            return;
        }

        addPreferencesFromResource(R.xml.pref_general);
    }

    private void addPreferenceHabitacion(List<Habitacion> result){
        this.habitaciones = result;
        PreferenceCategory fakeHeader = new PreferenceCategory(this);
        fakeHeader.setTitle(R.string.pref_title_general);
        getPreferenceScreen().addPreference(fakeHeader);
        addPreferencesFromResource(R.xml.pref_list_habitacion);
        Preference preference = findPreference("list_habitacion");
        bindValuesToListPreference((ListPreference) preference, result);
        bindPreferenceSummaryToValue(preference);
    }

    private void bindValuesToListPreference(
            ListPreference listPreference, List<Habitacion> habitaciones){
        String[] entry = new String[habitaciones.size()];
        String[] values = new String[habitaciones.size()];
        int iCount = 0;
        for (Habitacion hab : habitaciones){
            entry[iCount] = hab.getNumeroHabitacion() + " " + hab.getDescripcionHabitacion();
            values[iCount] = String.valueOf(iCount);
            iCount++;
        }
        listPreference.setEntries(entry);
        listPreference.setEntryValues(values);
    }

    private void saveSession(int location) {
        Habitacion hab = this.habitaciones.get(location);
        Realm realm = Realm.getInstance(this);

        realm.beginTransaction();

        Session session = realm.createObject(Session.class);
        session.setIdHotel(this.user.getIdHotel());
        session.setNombreOficial(this.user.getHotel());
        session.setIdHabitacion(hab.getId());
        session.setNumeroHabitacion(hab.getNumeroHabitacion());
        session.setDescripcionHabitacion(hab.getDescripcionHabitacion());
        session.setToken(this.user.getToken());

        realm.commitTransaction();
    }

    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    private static boolean isSimplePreferences(Context context) {
        return ALWAYS_SIMPLE_PREFS
                || Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB
                || !isXLargeTablet(context);
    }

    private void bindPreferenceSummaryToValue(Preference preference) {
        preference.setOnPreferenceChangeListener(this.sBindPreferenceSummaryToValueListener);

        this.sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    private Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);
                if (index >= 0) {
                    saveSession(index);
                }
            } else {
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    // ***************************************
    // Private classes
    // ***************************************
    private class DownloadHabitacionTask extends AsyncTask<Void, Void, List<Habitacion>> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected List<Habitacion> doInBackground(Void... params) {
            try {
                // The URL for making the GET request
                final String url = getString(R.string.base_uri) + "/hotel/" + user.getIdHotel() + "/habitacion";

                // Set the Accept header for "application/json"
                HttpHeaders requestHeaders = new HttpHeaders();
                List<MediaType> acceptableMediaTypes = new ArrayList<MediaType>();
                acceptableMediaTypes.add(MediaType.APPLICATION_JSON);
                requestHeaders.setAccept(acceptableMediaTypes);
                requestHeaders.add("X-Auth-Token", user.getToken());

                // Populate the headers in an HttpEntity object to use for the request
                HttpEntity<?> requestEntity = new HttpEntity<Object>(requestHeaders);

                // Create a new RestTemplate instance
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

                // Perform the HTTP GET request
                ResponseEntity<Habitacion[]> responseEntity
                        = restTemplate.exchange(url, HttpMethod.GET, requestEntity, Habitacion[].class);
                Log.d(TAG, String.valueOf(responseEntity.getBody()));

                // convert the array to a list and return it
                return Arrays.asList(responseEntity.getBody());
            } catch (Exception e) {
                Log.e(TAG, e.getLocalizedMessage(), e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<Habitacion> result) {
            addPreferenceHabitacion(result);
        }

    }
}
