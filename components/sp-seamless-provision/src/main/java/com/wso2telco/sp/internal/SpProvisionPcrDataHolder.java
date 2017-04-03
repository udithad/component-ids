package com.wso2telco.sp.internal;

import com.wso2telco.core.pcrservice.PCRGeneratable;

public class SpProvisionPcrDataHolder {

    private static SpProvisionPcrDataHolder spProvisionPcrDataHolder = new SpProvisionPcrDataHolder();

    PCRGeneratable pcrGeneratable;

    private SpProvisionPcrDataHolder() {

    }

    public static SpProvisionPcrDataHolder getInstance() {
        return spProvisionPcrDataHolder;
    }

    public PCRGeneratable getPcrGeneratable() {
        return pcrGeneratable;
    }

    public void setPcrGeneratable(PCRGeneratable pcrGeneratable) {
        this.pcrGeneratable = pcrGeneratable;
    }

}
