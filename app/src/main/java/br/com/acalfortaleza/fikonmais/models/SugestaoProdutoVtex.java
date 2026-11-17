package br.com.acalfortaleza.fikonmais.models;

import androidx.annotation.Nullable;

import java.util.List;

public class SugestaoProdutoVtex {

    public class Image{

        public @Nullable String imageId;
        public @Nullable String imageLabel;
        public @Nullable String imageTag;
        public @Nullable String imageUrl;
        public @Nullable String imageText;

    }

    public class Item{

        public @Nullable String itemId;
        public @Nullable String name;
        public @Nullable String nameComplete;
        public @Nullable String complementName;
        public @Nullable String ean;
        public @Nullable List<Image> images;

    }

    //----------------------------------------------------------------------------------------------

    public @Nullable String productId;
    public @Nullable String productName;
    public @Nullable String brand;
    public @Nullable Long brandId;
    public @Nullable String brandImageUrl;
    public @Nullable String linkText;
    public @Nullable String productReference;
    public @Nullable String productReferenceCode;
    public @Nullable String categoryId;
    public @Nullable String productTitle;
    public @Nullable String metaTagDescription;
    public @Nullable String releaseDate;
    public @Nullable List<Item> items;

}
