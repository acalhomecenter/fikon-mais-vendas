package br.com.acalfortaleza.fikonmais.models.Credz;

import java.io.Serializable;
import java.util.Date;

public class Qualification implements Serializable {

    private String state;
    private String transactionId;
    private String dateTime;
    private String free;
    private Merchant merchant;
    private PreQualificationRequest preQualificationRequest;
    private PreQualificationResponse preQualificationResponse;
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

    public PreQualificationRequest getPreQualificationRequest() {
        return preQualificationRequest;
    }

    public void setPreQualificationRequest(PreQualificationRequest preQualificationRequest) {
        this.preQualificationRequest = preQualificationRequest;
    }

    public PreQualificationResponse getPreQualificationResponse() {
        return preQualificationResponse;
    }

    public void setPreQualificationResponse(PreQualificationResponse preQualificationResponse) {
        this.preQualificationResponse = preQualificationResponse;
    }

    public ErrorResponse getErrorResponse() {
        return errorResponse;
    }

    public void setErrorResponse(ErrorResponse errorResponse) {
        this.errorResponse = errorResponse;
    }














     class ErrorResponse implements Serializable
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

     class Error implements Serializable
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
