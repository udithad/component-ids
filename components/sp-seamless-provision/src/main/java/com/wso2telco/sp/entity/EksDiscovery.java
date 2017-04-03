package com.wso2telco.sp.entity;

public class EksDiscovery {

    private Response response;

    private String ttl;

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public String getTtl() {
        return ttl;
    }

    public void setTtl(String ttl) {
        this.ttl = ttl;
    }

    @Override
    public String toString() {
        return "ClassPojo [response = " + response + ", ttl = " + ttl + "]";
    }

}
