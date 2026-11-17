package br.com.acalfortaleza.fikonmais.models.Credz;

import java.io.Serializable;

public class Attendant implements Serializable {

    public TaxId taxId;

    public TaxId getTaxId() {
        return taxId;
    }

    public void setTaxId(TaxId taxId) {
        this.taxId = taxId;
    }
}
