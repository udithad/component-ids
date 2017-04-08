/*******************************************************************************
 * Copyright  (c) 2015-2017, WSO2.Telco Inc. (http://www.wso2telco.com) All Rights Reserved.
 *
 * WSO2.Telco Inc. licences this file to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.wso2telco.carbon.identity.oauth2.token.handlers.clientauth;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.oauth.IdentityOAuthAdminException;
import org.wso2.carbon.identity.oauth.common.exception.InvalidOAuthClientException;
import org.wso2.carbon.identity.oauth2.IdentityOAuth2Exception;
import org.wso2.carbon.identity.oauth2.dto.OAuth2AccessTokenReqDTO;
import org.wso2.carbon.identity.oauth2.token.OAuthTokenReqMessageContext;
import org.wso2.carbon.identity.oauth2.token.handlers.clientauth.AbstractClientAuthHandler;
import org.wso2.carbon.identity.oauth2.util.OAuth2Util;

import com.wso2telco.core.config.model.MobileConnectConfig;
import com.wso2telco.core.config.model.MobileConnectConfig.DiscoveryConfig;
import com.wso2telco.core.config.service.ConfigurationService;
import com.wso2telco.core.config.service.ConfigurationServiceImpl;
import com.wso2telco.core.pcrservice.util.SectorUtil;
import com.wso2telco.core.spprovisionservice.sp.entity.DiscoveryServiceConfig;
import com.wso2telco.core.spprovisionservice.sp.entity.DiscoveryServiceDto;
import com.wso2telco.core.spprovisionservice.sp.entity.ProvisionType;
import com.wso2telco.core.spprovisionservice.sp.entity.ServiceProviderDto;
import com.wso2telco.sp.discovery.service.DiscoveryService;
import com.wso2telco.sp.discovery.service.impl.DiscoveryServiceImpl;
import com.wso2telco.sp.util.TransformUtil;

public class SeamlessProvisionClientAuthHandler extends AbstractClientAuthHandler {

    private static Log log = LogFactory.getLog(SeamlessProvisionClientAuthHandler.class);
    private DiscoveryService discoveryService;
    private static MobileConnectConfig mobileConnectConfigs = null;
    private static ConfigurationService configurationService = new ConfigurationServiceImpl();

    public SeamlessProvisionClientAuthHandler() {
        discoveryService = new DiscoveryServiceImpl();
    }

    @Override
    public boolean authenticateClient(OAuthTokenReqMessageContext tokReqMsgCtx) throws IdentityOAuth2Exception {

        seamlessProvisioning(tokReqMsgCtx);
        boolean isAuthenticated = super.authenticateClient(tokReqMsgCtx);

        if (!isAuthenticated) {
            OAuth2AccessTokenReqDTO oAuth2AccessTokenReqDTO = tokReqMsgCtx.getOauth2AccessTokenReqDTO();
            try {
                return OAuth2Util.authenticateClient(oAuth2AccessTokenReqDTO.getClientId(),
                        oAuth2AccessTokenReqDTO.getClientSecret());
            } catch (IdentityOAuthAdminException e) {
                throw new IdentityOAuth2Exception("Error while authenticating client", e);
            } catch (InvalidOAuthClientException e) {
                throw new IdentityOAuth2Exception("Invalid Client : " + oAuth2AccessTokenReqDTO.getClientId(), e);
            }
        } else {
            return true;
        }
    }

    private void seamlessProvisioning(OAuthTokenReqMessageContext tokReqMsgCtx) throws IdentityOAuth2Exception {
        log.info("Initiating seamless provisioning procees");
        if (mobileConnectConfigs.isSeamlessProvisioningEnabled()) {
            ServiceProviderDto serviceProviderDto = discoverServiceProvider(tokReqMsgCtx.getOauth2AccessTokenReqDTO());
            if (serviceProviderDto != null && !serviceProviderDto.getExistance().equals(ProvisionType.LOCAL)) {
                log.info("Service Provider does not contain same credentials. Provisioning new credentials...");
                // UPDATE SERVICE PROVIDER CREDENTIALS
            } else {
                log.info("Service Provider does not found LOCALLY OR REMOTELY... Auth token creation failed...");
                throw new IdentityOAuth2Exception("Service Provider Not Found");
            }
        }
    }

    private ServiceProviderDto discoverServiceProvider(OAuth2AccessTokenReqDTO oAuth2AccessTokenReqDTO)
            throws IdentityOAuth2Exception {
        ServiceProviderDto serviceProviderDto = null;
        try {
            serviceProviderDto = discoveryService.servceProviderEksDiscovery(readDiscoveryConfigs(),
                    getDiscoveryServiceDto(oAuth2AccessTokenReqDTO));
        } catch (Exception e) {
            log.error("" + e.getMessage());
        }
        return serviceProviderDto;
    }

    private DiscoveryServiceConfig readDiscoveryConfigs() {
        return TransformUtil.transformDiscoveryConfig(getDiscoveryConfig(), getMobileConnectConfig());
    }

    private DiscoveryServiceDto getDiscoveryServiceDto(OAuth2AccessTokenReqDTO oAuth2AccessTokenReqDTO) {
        DiscoveryServiceDto discoveryServiceDto = new DiscoveryServiceDto();
        discoveryServiceDto.setClientId(oAuth2AccessTokenReqDTO.getClientId());
        discoveryServiceDto.setClientSecret(oAuth2AccessTokenReqDTO.getClientSecret());
        String sectorId = SectorUtil.getSectorIdFromUrl(oAuth2AccessTokenReqDTO.getCallbackURI());
        discoveryServiceDto.setSectorId(sectorId);
        return discoveryServiceDto;
    }

    private DiscoveryConfig getDiscoveryConfig() {
        mobileConnectConfigs = configurationService.getDataHolder().getMobileConnectConfig();
        return mobileConnectConfigs.getDiscoveryConfig();
    }

    private MobileConnectConfig getMobileConnectConfig() {
        mobileConnectConfigs = configurationService.getDataHolder().getMobileConnectConfig();
        return mobileConnectConfigs;
    }

}
