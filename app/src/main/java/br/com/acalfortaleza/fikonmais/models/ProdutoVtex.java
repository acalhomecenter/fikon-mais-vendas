package br.com.acalfortaleza.fikonmais.models;

import java.io.Serializable;
import java.util.List;

public class ProdutoVtex  implements Serializable {


   public int Id;
   public String NameComplete;
   public String ImagemUrl;
   public String Detailurl;
   public List<Images> Images;
   public List<ProductSpecifications> ProductSpecifications;
   public Integer ProductId;



   public static class Images implements Serializable{
       public String ImageUrl;
       public boolean Principal;

   }


   public class ProductSpecifications implements Serializable {
       public int FieldId;
       public String FieldName;
       public List<String> FieldValueIds;
       public List<String> FieldValues;
   }





}
