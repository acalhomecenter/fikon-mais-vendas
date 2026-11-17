package br.com.acalfortaleza.fikonmais.models.Credz;

import java.io.Serializable;

public class Invitation implements Serializable {

    private String state;
    private String transactionId;
    private String dateTime;
    private String free;
    private Merchant merchant;
    private InvitationRequest invitationRequest;
    private InvitationResponse invitationResponse;
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

    public InvitationRequest getInvitationRequest() {
        return invitationRequest;
    }

    public void setInvitationRequest(InvitationRequest invitationRequest) {
        this.invitationRequest = invitationRequest;
    }

    public InvitationResponse getInvitationResponse() {
        return invitationResponse;
    }

    public void setInvitationResponse(InvitationResponse invitationResponse) {
        this.invitationResponse = invitationResponse;
    }

    public ErrorResponse getErrorResponse() {
        return errorResponse;
    }

    public void setErrorResponse(ErrorResponse errorResponse) {
        this.errorResponse = errorResponse;
    }

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

    public class InvitationRequest implements Serializable
    {
        private Card card;

        public Card getCard() {
            return card;
        }

        public void setCard(Card card) {
            this.card = card;
        }
    }

    public class Card implements Serializable
    {
        private String productId;
        private Proponent proponent;

        public String getProductId() {
            return productId;
        }

        public void setProductId(String productId) {
            this.productId = productId;
        }

        public Proponent getProponent() {
            return proponent;
        }

        public void setProponent(Proponent proponent) {
            this.proponent = proponent;
        }
    }

    public class Proponent implements Serializable
    {
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

    public class TaxId implements Serializable
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

    public class Phone implements Serializable
    {
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

    public class InvitationResponse implements Serializable
    {
        private Card1 card;

        public Card1 getCard() {
            return card;
        }

        public void setCard(Card1 card) {
            this.card = card;
        }
    }

    public class Card1 implements Serializable
    {
        private String responseCode;
        private String displayMessage;
        private String code;
        private String url;
        private String expirationDateTime;
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

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getExpirationDateTime() {
            return expirationDateTime;
        }

        public void setExpirationDateTime(String expirationDateTime) {
            this.expirationDateTime = expirationDateTime;
        }

        public String getHostDateTime() {
            return hostDateTime;
        }

        public void setHostDateTime(String hostDateTime) {
            this.hostDateTime = hostDateTime;
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
