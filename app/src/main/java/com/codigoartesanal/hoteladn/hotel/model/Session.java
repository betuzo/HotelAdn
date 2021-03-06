package com.codigoartesanal.hoteladn.hotel.model;

import java.io.Serializable;

import io.realm.RealmObject;

/**
 * Created by betuzo on 25/04/15.
 */
public class Session extends RealmObject implements Serializable {

    private long            idHabitacion;
    private String          numeroHabitacion;
    private String          descripcionHabitacion;
    private long            idHotel;
    private String          nombreOficial;
    private String          token;
    private String          keyHabitacion;

    public long getIdHabitacion() {
        return idHabitacion;
    }

    public void setIdHabitacion(long idHabitacion) {
        this.idHabitacion = idHabitacion;
    }

    public String getNumeroHabitacion() {
        return numeroHabitacion;
    }

    public void setNumeroHabitacion(String numeroHabitacion) {
        this.numeroHabitacion = numeroHabitacion;
    }

    public String getDescripcionHabitacion() {
        return descripcionHabitacion;
    }

    public void setDescripcionHabitacion(String descripcionHabitacion) {
        this.descripcionHabitacion = descripcionHabitacion;
    }

    public long getIdHotel() {
        return idHotel;
    }

    public void setIdHotel(long idHotel) {
        this.idHotel = idHotel;
    }

    public String getNombreOficial() {
        return nombreOficial;
    }

    public void setNombreOficial(String nombreOficial) {
        this.nombreOficial = nombreOficial;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getKeyHabitacion() {
        return keyHabitacion;
    }

    public void setKeyHabitacion(String keyHabitacion) {
        this.keyHabitacion = keyHabitacion;
    }
}
