package br.com.acalfortaleza.fikonmais.models.Credz;

import java.io.Serializable;

public class Plan implements Serializable {

    private long planId;
    private String description;
    private int installments;
    private Amount amount;
    private Rates rates;
    private String type;

    // Getter Methods

    public long getPlanId() {
        return planId;
    }

    public String getDescription() {
        return description;
    }

    public int getInstallments() {
        return installments;
    }

    public Amount getAmount() {
        return amount;
    }

    public Rates getRates() {
        return rates;
    }

    public String getType(){
        return type;
    }

    // Setter Methods

    public void setPlanId(long planId) {
        this.planId = planId;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setInstallments(int installments) {
        this.installments = installments;
    }

    public void setAmount(Amount amount) {
        this.amount = amount;
    }

    public void setRates(Rates rates) {
        this.rates = rates;
    }

    public void setType(String type) {
        this.type = type;
    }

    public class Rates  implements Serializable{

        private float monthlyInterest;
        private float effectiveTotalCost;

        // Getter Methods

        public float getMonthlyInterest() {
            return monthlyInterest;
        }

        public float getEffectiveTotalCost() {
            return effectiveTotalCost;
        }

        // Setter Methods

        public void setMonthlyInterest(float monthlyInterest) {
            this.monthlyInterest = monthlyInterest;
        }

        public void setEffectiveTotalCost(float effectiveTotalCost) {
            this.effectiveTotalCost = effectiveTotalCost;
        }

    }


    public class Amount implements Serializable{

        private String currency;
        private float downPayment;
        private float installment;
        private float basePurchase;
        private float total;
        private float maxPurchase;

        // Getter Methods

        public String getCurrency() {
            return currency;
        }

        public float getDownPayment() {
            return downPayment;
        }

        public float getInstallment() {
            return installment;
        }

        public float getBasePurchase() {
            return basePurchase;
        }

        public float getTotal() {
            return total;
        }

        public float getMaxPurchase() {
            return maxPurchase;
        }

        // Setter Methods

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public void setDownPayment(float downPayment) {
            this.downPayment = downPayment;
        }

        public void setInstallment(float installment) {
            this.installment = installment;
        }

        public void setBasePurchase(float basePurchase) {
            this.basePurchase = basePurchase;
        }

        public void setTotal(float total) {
            this.total = total;
        }

        public void setMaxPurchase(float maxPurchase) {
            this.maxPurchase = maxPurchase;
        }

    }

}
