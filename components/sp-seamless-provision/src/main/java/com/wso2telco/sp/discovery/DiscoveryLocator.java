package com.wso2telco.sp.discovery;

import com.wso2telco.core.spprovisionservice.sp.entity.DiscoveryServiceConfig;
import com.wso2telco.core.spprovisionservice.sp.entity.DiscoveryServiceDto;
import com.wso2telco.core.spprovisionservice.sp.entity.ServiceProviderDto;
import com.wso2telco.sp.discovery.exception.DicoveryException;

public abstract class DiscoveryLocator {

    private DiscoveryLocator nextDiscovery;

    public abstract ServiceProviderDto servceProviderDiscovery(DiscoveryServiceConfig discoveryServiceConfig,
            DiscoveryServiceDto discoveryServiceDto) throws DicoveryException;


    public DiscoveryLocator getNextDiscovery() {
        return nextDiscovery;
    }

    public void setNextDiscovery(DiscoveryLocator nextDiscovery) {
        this.nextDiscovery = nextDiscovery;
    }

}
