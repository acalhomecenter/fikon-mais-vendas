package br.com.acalfortaleza.fikonmais.models.Credz;

import java.io.Serializable;

public class Merchant implements Serializable {

   private String terminalId;

   public String getTerminalId() {
      return terminalId;
   }

   public void setTerminalId(String terminalId) {
      this.terminalId = terminalId;
   }

}
