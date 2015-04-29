package com.codigoartesanal.hoteladn.hotel.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by betuzo on 28/04/15.
 */
public class Habitacion {

    @JsonProperty("id")
    private long id;
    @JsonProperty("numeroHabitacion")
    private String numeroHabitacion;
    @JsonProperty("descripcionHabitacion")
    private String descripcionHabitacion;
    @JsonProperty("precioReferencia")
    private double precioReferencia;
    @JsonProperty("idTipoHabitacion")
    private long idTipoHabitacion;
    @JsonProperty("tipoHabitacion")
    private String tipoHabitacion;
    @JsonProperty("hotelId")
    private long hotelId;
    @JsonProperty("hotelNombre")
    private String hotelNombre;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public double getPrecioReferencia() {
        return precioReferencia;
    }

    public void setPrecioReferencia(double precioReferencia) {
        this.precioReferencia = precioReferencia;
    }

    public long getIdTipoHabitacion() {
        return idTipoHabitacion;
    }

    public void setIdTipoHabitacion(long idTipoHabitacion) {
        this.idTipoHabitacion = idTipoHabitacion;
    }

    public String getTipoHabitacion() {
        return tipoHabitacion;
    }

    public void setTipoHabitacion(String tipoHabitacion) {
        this.tipoHabitacion = tipoHabitacion;
    }

    public long getHotelId() {
        return hotelId;
    }

    public void setHotelId(long hotelId) {
        this.hotelId = hotelId;
    }

    public String getHotelNombre() {
        return hotelNombre;
    }

    public void setHotelNombre(String hotelNombre) {
        this.hotelNombre = hotelNombre;
    }
}
