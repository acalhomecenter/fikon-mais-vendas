package br.com.acalfortaleza.fikonmais.models.Credz;

import java.io.Serializable;

public class Phone implements Serializable {

   private String type;
   private String countryCallingCode;
   private String areaCallingCode;
   private String number;

   public String getType() {
      return type;
   }

   public void setType(String type) {
      this.type = type;
   }

   public String getCountryCallingCode() {
      return countryCallingCode;
   }

   public void setCountryCallingCode(String countryCallingCode) {
      this.countryCallingCode = countryCallingCode;
   }

   public String getAreaCallingCode() {
      return areaCallingCode;
   }

   public void setAreaCallingCode(String areaCallingCode) {
      this.areaCallingCode = areaCallingCode;
   }

   public String getNumber() {
      return number;
   }

   public void setNumber(String number) {
      this.number = number;
   }

}
