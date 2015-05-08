package com.codigoartesanal.hoteladn.hotel.service;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.codigoartesanal.hoteladn.hotel.LoginActivity;
import com.codigoartesanal.hoteladn.hotel.model.Session;
import com.codigoartesanal.hoteladn.hotel.model.SessionRepository;
import com.codigoartesanal.hoteladn.hotel.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

/**
 * Created by betuzo on 4/05/15.
 */
public class LoginService {
    protected static final String TAG = LoginService.class.getSimpleName();

    public static User convertStringToJSONObject(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            User user = new User();
            user.setId(jsonObject.getLong("id"));
            user.setName((String) jsonObject.get("name"));
            user.setUsername((String) jsonObject.get("username"));
            user.setToken((String) jsonObject.get("token"));
            user.setIdHotel(jsonObject.getLong("idHotel"));
            user.setHotel(jsonObject.getString("hotel"));
            JSONArray jsonArray = (JSONArray) jsonObject.get("roles");
            List<String> list = new ArrayList<String>();
            for (int i=0; i<jsonArray.length(); i++) {
                list.add( jsonArray.getString(i) );
            }
            user.setRoles(list);
            return user;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void handlerError(HttpClientErrorException e, Context context) {
        Log.e(TAG, e.getLocalizedMessage(), e);
        switch (e.getStatusCode()) {
            case UNAUTHORIZED:
                error401(context);
                break;
            default:
        }
    }

    private static void error401(Context context){
        Session session = new Session();
        session.setToken("");
        session.setKeyHabitacion("");
        SessionRepository.validate(session, context);

        Intent launch = new Intent(context, LoginActivity.class);
        context.startActivity(launch);
    }
}
