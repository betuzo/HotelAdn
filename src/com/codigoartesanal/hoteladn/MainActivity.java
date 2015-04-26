/*
 * Copyright 2010-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.codigoartesanal.hoteladn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.codigoartesanal.hoteladn.model.Session;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * @author rolguin
 */
public class MainActivity extends AbstractAsyncActivity {

	protected static final String TAG = MainActivity.class.getSimpleName();
    protected static final String USERNAME = "username";
    protected static final String PASSWORD = "password";

	// ***************************************
	// Activity methods
	// ***************************************
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity_layout);

		// Initiate the request to the protected service
		final Button submitButton = (Button) findViewById(R.id.submit);
		submitButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				new FetchSecuredResourceTask().execute();
			}
		});

        if (this.checkData() == null) {

        }
	}
	
	// ***************************************
	// Private methods
	// ***************************************
	private void displayResponse(String response) {
		Toast.makeText(this, response, Toast.LENGTH_LONG).show();
	}

    private Session checkData() {
        Realm realm = Realm.getInstance(this);
        RealmQuery<Session> query = realm.where(Session.class);
        RealmResults<Session> result = query.findAll();

        for (Session session : result) {
            return session;
        }
        return null;
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

			// build the message object
			EditText editText = (EditText) findViewById(R.id.username);
			this.username = editText.getText().toString();

			editText = (EditText) findViewById(R.id.password);
			this.password = editText.getText().toString();
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
			displayResponse(result);
		}

	}
	
}
