package com.wso2telco.sp.util;

import com.wso2telco.core.config.model.MobileConnectConfig;
import com.wso2telco.core.config.model.MobileConnectConfig.CrValidateDiscoveryConfig;
import com.wso2telco.core.config.model.MobileConnectConfig.DiscoveryConfig;
import com.wso2telco.core.config.model.MobileConnectConfig.EksDiscoveryConfig;
import com.wso2telco.core.pcrservice.util.SectorUtil;
import com.wso2telco.core.spprovisionservice.sp.entity.DiscoveryServiceConfig;
import com.wso2telco.core.spprovisionservice.sp.entity.DiscoveryServiceDto;
import com.wso2telco.core.spprovisionservice.sp.entity.EksDisConfig;

public class TransformUtil {

    public static DiscoveryServiceConfig transformDiscoveryConfig(DiscoveryConfig discoveryConfig,
            MobileConnectConfig mobileConnectConfig) {
        DiscoveryServiceConfig config = new DiscoveryServiceConfig();

        config.setEksDiscoveryConfig(transformEksDiscoveryConfig(discoveryConfig.getEksDiscoveryConfig()));
        config.setCrValidateDiscoveryConfig(
                transoformCrValidateDiscoveryConfig(discoveryConfig.getCrValidateDiscoveryConfig()));
        config.setPcrServiceEnabled(mobileConnectConfig.isPcrServiceEnabled());
        return config;
    }

    public static com.wso2telco.core.spprovisionservice.sp.entity.CrValidateDiscoveryConfig transoformCrValidateDiscoveryConfig(
            CrValidateDiscoveryConfig discoveryConf) {
        com.wso2telco.core.spprovisionservice.sp.entity.CrValidateDiscoveryConfig crValidateDiscoveryConfig = new com.wso2telco.core.spprovisionservice.sp.entity.CrValidateDiscoveryConfig();
        if (discoveryConf != null) {
            crValidateDiscoveryConfig.setServiceUrl(discoveryConf.getServiceUrl());
        }
        return crValidateDiscoveryConfig;
    }

    public static EksDisConfig transformEksDiscoveryConfig(EksDiscoveryConfig discoveryConf) {
        EksDisConfig eksDiscoveryConfig = new EksDisConfig();
        if (discoveryConf != null) {
            eksDiscoveryConfig.setRedirectUrl(discoveryConf.getRedirectUrl());
            eksDiscoveryConfig.setServiceUrl(discoveryConf.getServiceUrl());
            eksDiscoveryConfig.setMsisdn(discoveryConf.getMsisdn());
        }
        return eksDiscoveryConfig;
    }

    public static DiscoveryServiceDto transofrmDiscoveryDto(String clientId, String callbackUrl) {
        DiscoveryServiceDto discoveryServiceDto = new DiscoveryServiceDto();
        discoveryServiceDto.setClientId(clientId);
        String sectorId = null;
        if (callbackUrl != null && !callbackUrl.isEmpty()) {
            sectorId = SectorUtil.getSectorIdFromUrl(callbackUrl);
        }
        discoveryServiceDto.setSectorId(sectorId);
        return discoveryServiceDto;
    }

}
