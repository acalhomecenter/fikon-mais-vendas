package br.com.acalfortaleza.fikonmais.models;

import java.io.Serializable;

public class ImagemCiashop implements Serializable {

    public int sortOrder;
    public String imagePath;

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
