package com.wso2telco.sp.entity;

public class Response {
    private String serving_operator;

    private String client_secret;

    private Apis apis;

    private String client_id;

    private String currency;

    private String country;

    public String getServing_operator() {
        return serving_operator;
    }

    public void setServing_operator(String serving_operator) {
        this.serving_operator = serving_operator;
    }

    public String getClient_secret() {
        return client_secret;
    }

    public void setClient_secret(String client_secret) {
        this.client_secret = client_secret;
    }

    public Apis getApis() {
        return apis;
    }

    public void setApis(Apis apis) {
        this.apis = apis;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public String toString() {
        return "ClassPojo [serving_operator = " + serving_operator + ", client_secret = " + client_secret + ", apis = "
                + apis + ", client_id = " + client_id + ", currency = " + currency + ", country = " + country + "]";
    }
}
