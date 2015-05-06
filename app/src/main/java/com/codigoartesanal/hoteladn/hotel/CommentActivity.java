package com.codigoartesanal.hoteladn.hotel;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codigoartesanal.hoteladn.hotel.adapter.CommentAdapter;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class CommentActivity extends ActionBarActivity {

    private EditText messageET;
    private ListView messagesContainer;
    private Button sendBtn;
    private CommentAdapter adapter;
    private ArrayList<Map<String, Object>> chatHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        initControls();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_comment, menu);
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

    private void initControls() {
        messagesContainer = (ListView) findViewById(R.id.messagesContainer);
        messageET = (EditText) findViewById(R.id.messageEdit);
        sendBtn = (Button) findViewById(R.id.chatSendButton);

        TextView meLabel = (TextView) findViewById(R.id.meLbl);
        TextView companionLabel = (TextView) findViewById(R.id.friendLabel);
        RelativeLayout container = (RelativeLayout) findViewById(R.id.container);
        companionLabel.setText("My Buddy");

        loadDummyHistory();

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageET.getText().toString();
                if (TextUtils.isEmpty(messageText)) {
                    return;
                }

                Map<String, Object> comment = new HashMap<String, Object>();
                comment.put(CommentAdapter.PROPERTY_ID, 122);//dummy
                comment.put(CommentAdapter.PROPERTY_COMENTARIO, messageText);
                comment.put(CommentAdapter.PROPERTY_FECHA_COMENTARIO,
                        DateFormat.getDateTimeInstance().format(new Date()));
                comment.put(CommentAdapter.PROPERTY_TIPO_COMENTARIO, "HABITACION");

                messageET.setText("");

                displayMessage(comment);
            }
        });


    }

    public void displayMessage(Map<String, Object> comment) {
        adapter.add(comment);
        adapter.notifyDataSetChanged();
        scroll();
    }

    private void scroll() {
        messagesContainer.setSelection(messagesContainer.getCount() - 1);
    }

    private void loadDummyHistory(){

        chatHistory = new ArrayList<Map<String, Object>>();

        Map<String, Object> msg = new HashMap<String, Object>();
        msg.put(CommentAdapter.PROPERTY_ID, 1);
        msg.put(CommentAdapter.PROPERTY_TIPO_COMENTARIO, "EMPLEADO");
        msg.put(CommentAdapter.PROPERTY_COMENTARIO, "Hi");
        msg.put(CommentAdapter.PROPERTY_FECHA_COMENTARIO,
                DateFormat.getDateTimeInstance().format(new Date()));
        chatHistory.add(msg);
        Map<String, Object> msg1 = new HashMap<String, Object>();
        msg1.put(CommentAdapter.PROPERTY_ID, 2);
        msg1.put(CommentAdapter.PROPERTY_TIPO_COMENTARIO, "EMPLEADO");
        msg1.put(CommentAdapter.PROPERTY_COMENTARIO, "How r u doing???");
        msg1.put(CommentAdapter.PROPERTY_FECHA_COMENTARIO,
                DateFormat.getDateTimeInstance().format(new Date()));
        chatHistory.add(msg1);

        adapter = new CommentAdapter(CommentActivity.this, new ArrayList<Map<String, Object>>());
        messagesContainer.setAdapter(adapter);

        for(int i=0; i<chatHistory.size(); i++) {
            Map<String, Object> message = chatHistory.get(i);
            displayMessage(message);
        }

    }
}
