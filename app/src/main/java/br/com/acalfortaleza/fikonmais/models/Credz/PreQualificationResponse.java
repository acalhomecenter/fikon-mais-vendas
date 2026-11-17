package br.com.acalfortaleza.fikonmais.models.Credz;

import java.io.Serializable;

public class PreQualificationResponse implements Serializable {

   private Proposal proposal;


   public Proposal getProposal() {
      return proposal;
   }

   public void setProposal(Proposal proposal) {
      this.proposal = proposal;
   }
}
