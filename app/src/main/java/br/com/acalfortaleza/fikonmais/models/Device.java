package br.com.acalfortaleza.fikonmais.models;

public class Device{

    public String Cpf;
    public String Colaborador;
    public String Android_id;
    public String Marca;
    public String Modelo;
    public String Tipo;
    public String Status;
    public String Aprovador;

    public String getAprovador() {
        return Aprovador;
    }

    public void setAprovador(String aprovador) {
        Aprovador = aprovador;
    }

    public String getCpf() {
        return Cpf;
    }

    public void setCpf(String cpf) {
        Cpf = cpf;
    }

    public String getColaborador() {
        return Colaborador;
    }

    public void setColaborador(String colaborador) {
        Colaborador = colaborador;
    }

    public String getAndroid_id() {
        return Android_id;
    }

    public void setAndroid_id(String android_id) {
        Android_id = android_id;
    }

    public String getMarca() {
        return Marca;
    }

    public void setMarca(String marca) {
        Marca = marca;
    }

    public String getModelo() {
        return Modelo;
    }

    public void setModelo(String modelo) {
        Modelo = modelo;
    }

    public String getTipo() {
        return Tipo;
    }

    public void setTipo(String tipo) {
        Tipo = tipo;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }
}
