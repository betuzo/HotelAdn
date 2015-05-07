package com.codigoartesanal.hoteladn.hotel.model;

import java.io.Serializable;

/**
 * Created by betuzo on 6/05/15.
 */
public class Categoria implements Serializable {
    private long id;
    private String claveCategoria;
    private String descripcion;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getClaveCategoria() {
        return claveCategoria;
    }

    public void setClaveCategoria(String claveCategoria) {
        this.claveCategoria = claveCategoria;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return claveCategoria;
    }
}
