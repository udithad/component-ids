package com.wso2telco.sp.entity;

public class CrValidateRes {
    private String orgId;

    private Application application;

    private String x_client_id;

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public String getX_client_id() {
        return x_client_id;
    }

    public void setX_client_id(String x_client_id) {
        this.x_client_id = x_client_id;
    }

    @Override
    public String toString() {
        return "ClassPojo [orgId = " + orgId + ", application = " + application + ", x_client_id = " + x_client_id
                + "]";
    }
}
