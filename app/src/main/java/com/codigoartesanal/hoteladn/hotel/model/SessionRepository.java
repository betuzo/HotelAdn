package com.codigoartesanal.hoteladn.hotel.model;

import android.content.Context;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by betuzo on 30/04/15.
 */
public class SessionRepository {
    public static Session get(Context context) {
        Realm realm = Realm.getInstance(context);
        RealmQuery<Session> query = realm.where(Session.class);
        RealmResults<Session> result = query.findAll();

        for (Session session : result) {
            return session;
        }
        return null;
    }

    public static void validate(Session ses, Context context) {
        Session session = get(context);
        if (session != null) {
            update(ses, context);
        } else {
            save(ses, context);
        }
    }

    private static void update(Session ses, Context context) {
        Realm realm = Realm.getInstance(context);

        realm.beginTransaction();

        RealmQuery<Session> query = realm.where(Session.class);
        RealmResults<Session> result = query.findAll();

        for (Session session : result) {
            session.setIdHotel(ses.getIdHotel());
            session.setNombreOficial(ses.getNombreOficial());
            session.setIdHabitacion(ses.getIdHabitacion());
            session.setNumeroHabitacion(ses.getNumeroHabitacion());
            session.setDescripcionHabitacion(ses.getDescripcionHabitacion());
            session.setToken(ses.getToken());
        }

        realm.commitTransaction();
    }

    private static void save(Session ses, Context context) {
        Realm realm = Realm.getInstance(context);

        realm.beginTransaction();

        Session session = realm.createObject(Session.class);
        session.setIdHotel(ses.getIdHotel());
        session.setNombreOficial(ses.getNombreOficial());
        session.setIdHabitacion(ses.getIdHabitacion());
        session.setNumeroHabitacion(ses.getNumeroHabitacion());
        session.setDescripcionHabitacion(ses.getDescripcionHabitacion());
        session.setToken(ses.getToken());

        realm.commitTransaction();
    }
}
