package com.codigoartesanal.hoteladn.hotel.model;

import java.io.Serializable;

/**
 * Created by betuzo on 9/04/15.
 */
public class Response implements Serializable {
    public static final String CODE_SUCCESS                  = "success";
    public static final String CODE_ERROR                    = "error";

    private String code;
    private String message;

    public Response(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public Response(){

    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
