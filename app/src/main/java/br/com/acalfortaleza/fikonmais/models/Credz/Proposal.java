package br.com.acalfortaleza.fikonmais.models.Credz;

import java.io.Serializable;

public class Proposal implements Serializable {

   private Attendant attendant;
   private Proponent proponent;
   private String responseCode;
   private String displayMessage;
   private String hostDateTime;

   public String getResponseCode() {
      return responseCode;
   }

   public void setResponseCode(String responseCode) {
      this.responseCode = responseCode;
   }

   public String getDisplayMessage() {
      return displayMessage;
   }

   public void setDisplayMessage(String displayMessage) {
      this.displayMessage = displayMessage;
   }

   public String getHostDateTime() {
      return hostDateTime;
   }

   public void setHostDateTime(String hostDateTime) {
      this.hostDateTime = hostDateTime;
   }


   public Attendant getAttendant() {
      return attendant;
   }

   public void setAttendant(Attendant attendant) {
      this.attendant = attendant;
   }

   public Proponent getProponent() {
      return proponent;
   }

   public void setProponent(Proponent proponent) {
      this.proponent = proponent;
   }

}
