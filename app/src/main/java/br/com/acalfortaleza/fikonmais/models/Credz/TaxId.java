package br.com.acalfortaleza.fikonmais.models.Credz;

import java.io.Serializable;

public class TaxId implements Serializable {

   private String type;
   private String number;

   public String getType() {
      return type;
   }

   public void setType(String type) {
      this.type = type;
   }

   public String getNumber() {
      return number;
   }

   public void setNumber(String number) {
      this.number = number;
   }

}
