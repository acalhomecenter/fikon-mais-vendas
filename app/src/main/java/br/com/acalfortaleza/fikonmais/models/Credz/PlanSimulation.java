package br.com.acalfortaleza.fikonmais.models.Credz;

//import java.time.DateTime;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class PlanSimulation implements Serializable {

    private String state;
    private String transactionId;
    private String dateTime;
    private String free;
    private Merchant merchant;
    private Customer customer;
    private Paymentplansimulationrequest paymentPlanSimulationRequest;
    private Paymentplansimulationresponse paymentPlanSimulationResponse;
    private ErrorResponse errorResponse;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getFree() {
        return free;
    }

    public void setFree(String free) {
        this.free = free;
    }

    public Merchant getMerchant() {
        return merchant;
    }

    public void setMerchant(Merchant merchant) {
        this.merchant = merchant;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Paymentplansimulationrequest getPaymentPlanSimulationRequest() {
        return paymentPlanSimulationRequest;
    }

    public void setPaymentPlanSimulationRequest(Paymentplansimulationrequest paymentPlanSimulationRequest) {
        this.paymentPlanSimulationRequest = paymentPlanSimulationRequest;
    }

    public Paymentplansimulationresponse getPaymentPlanSimulationResponse() {
        return paymentPlanSimulationResponse;
    }

    public void setPaymentPlanSimulationResponse(Paymentplansimulationresponse paymentPlanSimulationResponse) {
        this.paymentPlanSimulationResponse = paymentPlanSimulationResponse;
    }

    public ErrorResponse getErrorResponse() {
        return errorResponse;
    }

    public void setErrorResponse(ErrorResponse errorResponse) {
        this.errorResponse = errorResponse;
    }

    //----------------------------------------------------------------------------------------------


    public class Merchant implements Serializable
    {
        private String terminalId;

        public String getTerminalId() {
            return terminalId;
        }

        public void setTerminalId(String terminalId) {
            this.terminalId = terminalId;
        }
    }

    public class Customer implements Serializable
    {
        private Taxid taxId;

        public Taxid getTaxId() {
            return taxId;
        }

        public void setTaxId(Taxid taxId) {
            this.taxId = taxId;
        }
    }

    public class Taxid implements Serializable
    {
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

    public class Paymentplansimulationrequest implements Serializable
    {
        private Credzinstallment credzInstallment;

        public Credzinstallment getCredzInstallment() {
            return credzInstallment;
        }

        public void setCredzInstallment(Credzinstallment credzInstallment) {
            this.credzInstallment = credzInstallment;
        }
    }

    public class Credzinstallment implements Serializable
    {
        private String installmentType;
        private Customer1 customer;
        private Purchaseamount purchaseAmount;

        public String getInstallmentType() {
            return installmentType;
        }

        public void setInstallmentType(String installmentType) {
            this.installmentType = installmentType;
        }

        public Customer1 getCustomer() {
            return customer;
        }

        public void setCustomer(Customer1 customer) {
            this.customer = customer;
        }

        public Purchaseamount getPurchaseAmount() {
            return purchaseAmount;
        }

        public void setPurchaseAmount(Purchaseamount purchaseAmount) {
            this.purchaseAmount = purchaseAmount;
        }
    }

    public class Customer1 implements Serializable
    {
        private Taxid1 taxId;

        public Taxid1 getTaxId() {
            return taxId;
        }

        public void setTaxId(Taxid1 taxId) {
            this.taxId = taxId;
        }
    }

    public class Taxid1 implements Serializable
    {
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

    public class Purchaseamount implements Serializable
    {
        private String currency;
        private float total;
        private float downPayment;

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public float getTotal() {
            return total;
        }

        public void setTotal(float total) {
            this.total = total;
        }

        public float getDownPayment() {
            return downPayment;
        }

        public void setDownPayment(float downPayment) {
            this.downPayment = downPayment;
        }
    }

    public class Paymentplansimulationresponse implements Serializable
    {
        private Credzinstallment1 credzInstallment;

        public Credzinstallment1 getCredzInstallment() {
            return credzInstallment;
        }

        public void setCredzInstallment(Credzinstallment1 credzInstallment) {
            this.credzInstallment = credzInstallment;
        }
    }

    public class Credzinstallment1 implements Serializable
    {
        private String responseCode;
        private String displayMessage;
        private String hostDateTime;
        private Plans plans;

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

        public Plans getPlans() {
            return plans;
        }

        public void setPlans(Plans plans) {
            this.plans = plans;
        }
    }

    public class Plans implements Serializable
    {
        private Straight[] straight;
        private Merchant1[] merchant;
        private Issuer[] issuer;

        public Straight[] getStraight() {
            return straight;
        }

        public void setStraight(Straight[] straight) {
            this.straight = straight;
        }

        public Merchant1[] getMerchant() {
            return merchant;
        }

        public void setMerchant(Merchant1[] merchant) {
            this.merchant = merchant;
        }

        public Issuer[] getIssuer() {
            return issuer;
        }

        public void setIssuer(Issuer[] issuer) {
            this.issuer = issuer;
        }
    }

    public class Straight implements Serializable
    {
        private long planId;
        private String description;
        private int installments;
        private Amount amount;
        private Rates rates;

        public long getPlanId() {
            return planId;
        }

        public void setPlanId(long planId) {
            this.planId = planId;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public int getInstallments() {
            return installments;
        }

        public void setInstallments(int installments) {
            this.installments = installments;
        }

        public Amount getAmount() {
            return amount;
        }

        public void setAmount(Amount amount) {
            this.amount = amount;
        }

        public Rates getRates() {
            return rates;
        }

        public void setRates(Rates rates) {
            this.rates = rates;
        }
    }

    public class Amount implements Serializable
    {
        private String currency;
        private float downPayment;
        private float installment;
        private float basePurchase;
        private float total;
        private float maxPurchase;

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public float getDownPayment() {
            return downPayment;
        }

        public void setDownPayment(float downPayment) {
            this.downPayment = downPayment;
        }

        public float getInstallment() {
            return installment;
        }

        public void setInstallment(float installment) {
            this.installment = installment;
        }

        public float getBasePurchase() {
            return basePurchase;
        }

        public void setBasePurchase(float basePurchase) {
            this.basePurchase = basePurchase;
        }

        public float getTotal() {
            return total;
        }

        public void setTotal(float total) {
            this.total = total;
        }

        public float getMaxPurchase() {
            return maxPurchase;
        }

        public void setMaxPurchase(float maxPurchase) {
            this.maxPurchase = maxPurchase;
        }
    }

    public class Rates implements Serializable
    {
        private float monthlyInterest;
        private float effectiveTotalCost;

        public float getMonthlyInterest() {
            return monthlyInterest;
        }

        public void setMonthlyInterest(float monthlyInterest) {
            this.monthlyInterest = monthlyInterest;
        }

        public float getEffectiveTotalCost() {
            return effectiveTotalCost;
        }

        public void setEffectiveTotalCost(float effectiveTotalCost) {
            this.effectiveTotalCost = effectiveTotalCost;
        }
    }

    public class Merchant1 implements Serializable
    {
        private long planId;
        private String description;
        private int installments;
        private Amount1 amount;
        private Rates1 rates;

        public long getPlanId() {
            return planId;
        }

        public void setPlanId(long planId) {
            this.planId = planId;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public int getInstallments() {
            return installments;
        }

        public void setInstallments(int installments) {
            this.installments = installments;
        }

        public Amount1 getAmount() {
            return amount;
        }

        public void setAmount(Amount1 amount) {
            this.amount = amount;
        }

        public Rates1 getRates() {
            return rates;
        }

        public void setRates(Rates1 rates) {
            this.rates = rates;
        }
    }

    public class Amount1 implements Serializable
    {
        private String currency;
        private float downPayment;
        private float installment;
        private float basePurchase;
        private float total;
        private float maxPurchase;

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public float getDownPayment() {
            return downPayment;
        }

        public void setDownPayment(float downPayment) {
            this.downPayment = downPayment;
        }

        public float getInstallment() {
            return installment;
        }

        public void setInstallment(float installment) {
            this.installment = installment;
        }

        public float getBasePurchase() {
            return basePurchase;
        }

        public void setBasePurchase(float basePurchase) {
            this.basePurchase = basePurchase;
        }

        public float getTotal() {
            return total;
        }

        public void setTotal(float total) {
            this.total = total;
        }

        public float getMaxPurchase() {
            return maxPurchase;
        }

        public void setMaxPurchase(float maxPurchase) {
            this.maxPurchase = maxPurchase;
        }
    }

    public class Rates1 implements Serializable
    {
        private float monthlyInterest;
        private float effectiveTotalCost;

        public float getMonthlyInterest() {
            return monthlyInterest;
        }

        public void setMonthlyInterest(float monthlyInterest) {
            this.monthlyInterest = monthlyInterest;
        }

        public float getEffectiveTotalCost() {
            return effectiveTotalCost;
        }

        public void setEffectiveTotalCost(float effectiveTotalCost) {
            this.effectiveTotalCost = effectiveTotalCost;
        }
    }

    public class Issuer implements Serializable
    {
        private long planId;
        private String description;
        private int installments;
        private Amount2 amount;
        private Rates2 rates;

        public long getPlanId() {
            return planId;
        }

        public void setPlanId(long planId) {
            this.planId = planId;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public int getInstallments() {
            return installments;
        }

        public void setInstallments(int installments) {
            this.installments = installments;
        }

        public Amount2 getAmount() {
            return amount;
        }

        public void setAmount(Amount2 amount) {
            this.amount = amount;
        }

        public Rates2 getRates() {
            return rates;
        }

        public void setRates(Rates2 rates) {
            this.rates = rates;
        }
    }

    public class Amount2 implements Serializable
    {
        private String currency;
        private float downPayment;
        private float installment;
        private float basePurchase;
        private float total;
        private float maxPurchase;

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public float getDownPayment() {
            return downPayment;
        }

        public void setDownPayment(float downPayment) {
            this.downPayment = downPayment;
        }

        public float getInstallment() {
            return installment;
        }

        public void setInstallment(float installment) {
            this.installment = installment;
        }

        public float getBasePurchase() {
            return basePurchase;
        }

        public void setBasePurchase(float basePurchase) {
            this.basePurchase = basePurchase;
        }

        public float getTotal() {
            return total;
        }

        public void setTotal(float total) {
            this.total = total;
        }

        public float getMaxPurchase() {
            return maxPurchase;
        }

        public void setMaxPurchase(float maxPurchase) {
            this.maxPurchase = maxPurchase;
        }
    }

    public class Rates2 implements Serializable
    {
        private float monthlyInterest;
        private float effectiveTotalCost;

        public float getMonthlyInterest() {
            return monthlyInterest;
        }

        public void setMonthlyInterest(float monthlyInterest) {
            this.monthlyInterest = monthlyInterest;
        }

        public float getEffectiveTotalCost() {
            return effectiveTotalCost;
        }

        public void setEffectiveTotalCost(float effectiveTotalCost) {
            this.effectiveTotalCost = effectiveTotalCost;
        }
    }

    public class ErrorResponse implements Serializable
    {
        private String responseCode;
        private String hostDateTime;
        private Error[] errors;

        public String getResponseCode() {
            return responseCode;
        }

        public void setResponseCode(String responseCode) {
            this.responseCode = responseCode;
        }

        public String getHostDateTime() {
            return hostDateTime;
        }

        public void setHostDateTime(String hostDateTime) {
            this.hostDateTime = hostDateTime;
        }

        public Error[] getErrors() {
            return errors;
        }

        public void setErrors(Error[] errors) {
            this.errors = errors;
        }
    }

    public class Error implements Serializable
    {
        private String displayMessage;
        private String technicalMessage;

        public String getDisplayMessage() {
            return displayMessage;
        }

        public void setDisplayMessage(String displayMessage) {
            this.displayMessage = displayMessage;
        }

        public String getTechnicalMessage() {
            return technicalMessage;
        }

        public void setTechnicalMessage(String technicalMessage) {
            this.technicalMessage = technicalMessage;
        }

    }

}
