package br.com.acalfortaleza.fikonmais.models;

import java.math.BigDecimal;

public class PixRequest {

    private BigDecimal value;
    private String message;
    private int partner_id;
    private int captura_id;
    private String usuario;
    private int usuario_id;
    private String user_email;

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getPartner_id() {
        return partner_id;
    }

    public void setPartner_id(int partner_id) {
        this.partner_id = partner_id;
    }

    public int getCaptura_id() {
        return captura_id;
    }

    public void setCaptura_id(int captura_id) {
        this.captura_id = captura_id;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public int getUsuario_id() {
        return usuario_id;
    }

    public void setUsuario_id(int usuario_id) {
        this.usuario_id = usuario_id;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }
}
