package br.com.acalfortaleza.fikonmais.models.Credz;

import java.io.Serializable;

public class Proponent  implements Serializable {

   private String name;
   private String birthDate;
   private String email;
   private TaxId taxId;
   private Phone phone;

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getBirthDate() {
      return birthDate;
   }

   public void setBirthDate(String birthDate) {
      this.birthDate = birthDate;
   }

   public String getEmail() {
      return email;
   }

   public void setEmail(String email) {
      this.email = email;
   }

   public TaxId getTaxId() {
      return taxId;
   }

   public void setTaxId(TaxId taxId) {
      this.taxId = taxId;
   }

   public Phone getPhone() {
      return phone;
   }

   public void setPhone(Phone phone) {
      this.phone = phone;
   }
}
