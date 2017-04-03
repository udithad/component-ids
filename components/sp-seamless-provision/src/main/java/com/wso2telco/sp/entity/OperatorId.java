package com.wso2telco.sp.entity;

public class OperatorId {
    
    private Link[] link;

    public Link[] getLink() {
        return link;
    }

    public void setLink(Link[] link) {
        this.link = link;
    }

    @Override
    public String toString() {
        return "ClassPojo [link = " + link + "]";
    }
    
}
