package com.codigoartesanal.hoteladn.hotel.model;

import java.util.Date;

/**
 * Created by betuzo on 29/04/15.
 */
public class SolicitudServicio {
    private long id;
    private String habitacionNo;
    private String estadoSolicitud;
    private String servicioDesc;
    private String servicioClave;
    private long servicioId;
    private String habitacionDesc;
    private long habitacionId;
    private Date fechaSolicitud;
    private long hotelId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getHabitacionNo() {
        return habitacionNo;
    }

    public void setHabitacionNo(String habitacionNo) {
        this.habitacionNo = habitacionNo;
    }

    public String getEstadoSolicitud() {
        return estadoSolicitud;
    }

    public void setEstadoSolicitud(String estadoSolicitud) {
        this.estadoSolicitud = estadoSolicitud;
    }

    public String getServicioDesc() {
        return servicioDesc;
    }

    public void setServicioDesc(String servicioDesc) {
        this.servicioDesc = servicioDesc;
    }

    public String getServicioClave() {
        return servicioClave;
    }

    public void setServicioClave(String servicioClave) {
        this.servicioClave = servicioClave;
    }

    public long getServicioId() {
        return servicioId;
    }

    public void setServicioId(long servicioId) {
        this.servicioId = servicioId;
    }

    public String getHabitacionDesc() {
        return habitacionDesc;
    }

    public void setHabitacionDesc(String habitacionDesc) {
        this.habitacionDesc = habitacionDesc;
    }

    public long getHabitacionId() {
        return habitacionId;
    }

    public void setHabitacionId(long habitacionId) {
        this.habitacionId = habitacionId;
    }

    public Date getFechaSolicitud() {
        return fechaSolicitud;
    }

    public void setFechaSolicitud(Date fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }

    public long getHotelId() {
        return hotelId;
    }

    public void setHotelId(long hotelId) {
        this.hotelId = hotelId;
    }
}