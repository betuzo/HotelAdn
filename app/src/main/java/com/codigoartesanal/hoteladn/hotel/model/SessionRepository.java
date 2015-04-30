package com.codigoartesanal.hoteladn.hotel.model;

import android.content.Context;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by betuzo on 30/04/15.
 */
public class SessionRepository {
    public static Session checkData(Context context) {
        Realm realm = Realm.getInstance(context);
        RealmQuery<Session> query = realm.where(Session.class);
        RealmResults<Session> result = query.findAll();

        for (Session session : result) {
            return session;
        }
        return null;
    }
}
