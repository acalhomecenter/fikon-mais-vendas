package br.com.acalfortaleza.fikonmais.models;

import java.io.Serializable;

public class Regulamento implements Serializable {

    public int Position;
    public String Url_Img;

    public String getUrl_Img() {
        return Url_Img;
    }

    public void setUrl_Img(String url_Img) {
        Url_Img = url_Img;
    }

    public int getPosition() {
        return Position;
    }

    public void setPosition(int position) {
        Position = position;
    }


}
