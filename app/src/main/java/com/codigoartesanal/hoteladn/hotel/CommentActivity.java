package com.codigoartesanal.hoteladn.hotel;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codigoartesanal.hoteladn.hotel.adapter.CommentAdapter;
import com.codigoartesanal.hoteladn.hotel.model.Response;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CommentActivity extends ActionBarActivity {
    protected static final String TAG = LoginActivity.class.getSimpleName();

    Session session;
    SolicitudServicio solicitudServicio;

    private EditText messageET;
    private ListView messagesContainer;
    private Button sendBtn;
    private CommentAdapter adapter;
    private ArrayList<Map<String, Object>> chatHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            this.solicitudServicio = (SolicitudServicio) extras.getSerializable("solicitudServicio");
        }
        session = SessionRepository.get(getApplicationContext());
        initControls();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_solicitud, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initControls() {
        messagesContainer = (ListView) findViewById(R.id.messagesContainer);
        messageET = (EditText) findViewById(R.id.messageEdit);
        sendBtn = (Button) findViewById(R.id.chatSendButton);

        TextView meLabel = (TextView) findViewById(R.id.meLbl);
        TextView companionLabel = (TextView) findViewById(R.id.friendLabel);
        RelativeLayout container = (RelativeLayout) findViewById(R.id.container);
        meLabel.setText(session.getNumeroHabitacion());

        this.setTitle(R.string.com_title);

        adapter = new CommentAdapter(CommentActivity.this, new ArrayList<Map<String, Object>>());
        messagesContainer.setAdapter(adapter);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageET.getText().toString();
                if (TextUtils.isEmpty(messageText)) {
                    return;
                }
                new SendCommentServiceTask().execute(messageText);
            }
        });
        resetMessages();
    }

    public void resetMessages() {
        adapter.setChatMessages(new ArrayList<Map<String, Object>>());
        adapter.notifyDataSetChanged();
        scroll();
        new DownloadCommentServiceTask().execute();
    }

    public void displayMessage(Map<String, Object> comment) {
        adapter.add(comment);
        adapter.notifyDataSetChanged();
        scroll();
    }

    private void scroll() {
        messagesContainer.setSelection(messagesContainer.getCount() - 1);
    }

    private void loadChatHistory(List<Map<String, Object>> comments){
        for(Map<String, Object> comment : comments) {
            displayMessage(comment);
        }
    }

    private class DownloadCommentServiceTask extends AsyncTask<Void, Void, List<Map<String, Object>>> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected List<Map<String, Object>> doInBackground(Void... params) {
            try {
                // The URL for making the GET request
                final String url = getString(R.string.base_uri) + "/solicitud/" + solicitudServicio.getId() + "/comentario";

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
                ResponseEntity<List> responseEntity
                        = restTemplate.exchange(url, HttpMethod.GET, requestEntity, List.class);
                Log.d(TAG, String.valueOf(responseEntity.getBody()));

                // convert the array to a list and return it
                return responseEntity.getBody();
            } catch (HttpClientErrorException e) {
                Log.e(TAG, e.getLocalizedMessage(), e);
                LoginService.handlerError(e, getApplicationContext());
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<Map<String, Object>> results) {
            loadChatHistory(results);
        }

    }


    private class SendCommentServiceTask extends AsyncTask<String, Void, Response> {
        Map<String, String> comment;

        @Override
        protected void onPreExecute() {
            comment = new HashMap<String, String>();
            comment.put(CommentAdapter.PROPERTY_FECHA_COMENTARIO, String.valueOf(new Date().getTime()));
            comment.put(CommentAdapter.PROPERTY_TIPO_COMENTARIO, "HABITACION");
            comment.put(CommentAdapter.PROPERTY_SOLICITUD_ID, String.valueOf(solicitudServicio.getId()));
        }

        @Override
        protected Response doInBackground(String... messageText) {
            final String url = getString(R.string.base_uri) + "/comentario";
            // Set the Accept header for "application/json"
            HttpHeaders requestHeaders = new HttpHeaders();
            List<MediaType> acceptableMediaTypes = new ArrayList<MediaType>();
            acceptableMediaTypes.add(MediaType.APPLICATION_JSON);
            requestHeaders.setAccept(acceptableMediaTypes);
            requestHeaders.add("X-Auth-Token", session.getKeyHabitacion());

            // Populate the headers in an HttpEntity object to use for the request
            comment.put(CommentAdapter.PROPERTY_COMENTARIO, messageText[0]);
            HttpEntity<Map<String, String>> requestEntity =
                    new HttpEntity<Map<String, String>>(comment ,requestHeaders);
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            try {
                ResponseEntity<Response> commentResponse =
                        restTemplate.exchange(url, HttpMethod.POST, requestEntity, Response.class);
                return commentResponse.getBody();
            } catch (HttpClientErrorException e) {
                Log.e(TAG, e.getLocalizedMessage(), e);
                return new Response(Response.CODE_ERROR, e.getLocalizedMessage());
            } catch (ResourceAccessException e) {
                Log.e(TAG, e.getLocalizedMessage(), e);
                return new Response(Response.CODE_ERROR, e.getLocalizedMessage());
            }
        }

        @Override
        protected void onPostExecute(Response response) {
            messageET.setText("");
            resetMessages();;
        }

    }
}
