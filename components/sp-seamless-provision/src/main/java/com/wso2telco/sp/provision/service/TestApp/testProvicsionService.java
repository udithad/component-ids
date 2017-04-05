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

package com.wso2telco.sp.provision.service.TestApp;

import com.wso2telco.core.spprovisionservice.sp.entity.*;
import com.wso2telco.core.spprovisionservice.sp.exception.SpProvisionServiceException;
import com.wso2telco.sp.provision.service.ProvisioningService;
import com.wso2telco.sp.provision.service.impl.ProvisioningServiceImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class testProvicsionService {

    private ServiceProviderDto serviceProviderDto = null;
    private SpProvisionConfig spProvisionConfig = null;
    SpProvisionDto spProvisionDto = new SpProvisionDto();
    DiscoveryServiceDto discoveryServiceDto = null;
    AdminServiceDto adminServiceDto = null;
    AdminServiceConfig adminServiceConfig = null;

    private static Log log = LogFactory.getLog(ProvisioningServiceImpl.class);

    public void tetsApp() {

        log.info("######################################## void tetsApp################################");

        serviceProviderDto = new ServiceProviderDto();
        //Service Provider Details
        serviceProviderDto.setApplicationName("WSO2Telco1012");
        serviceProviderDto.setDescription("App by Telco1012");
        serviceProviderDto.setAlwaysSendMappedLocalSubjectId(false);
        serviceProviderDto.setLocalClaimDialect(true);
        serviceProviderDto.setInboundAuthKey("customkeygenerationTelcoWSO21012");
        serviceProviderDto.setInboundAuthType("oauth2");
        serviceProviderDto.setConfidential(false);
        serviceProviderDto.setDefaultValue(null);
        serviceProviderDto.setPropertyName("oauthConsumerSecret");
        serviceProviderDto.setPropertyRequired(false);
        serviceProviderDto.setPropertyValue("secretkeygenerationTelcoWSO21012");
        serviceProviderDto.setProvisioningEnabled(false);
        serviceProviderDto.setProvisioningUserStore("PRIMARY");
        String idpRoles[] = {"WSO2Telco1012"};
        serviceProviderDto.setIdpRoles(idpRoles);
        serviceProviderDto.setSaasApp(true);
        serviceProviderDto.setLocalAuthenticatorConfigsDisplayName("LOA");
        serviceProviderDto.setLocalAuthenticatorConfigsEnabled(false);
        serviceProviderDto.setLocalAuthenticatorConfigsName("LOACompositeAuthenticator");
        serviceProviderDto.setLocalAuthenticatorConfigsValid(true);
        serviceProviderDto.setLocalAuthenticatorConfigsAuthenticationType("local");

        ServiceProviderConfig serviceProviderConfig = new ServiceProviderConfig();
        serviceProviderConfig.setPassword("admin");
        serviceProviderConfig.setUserName("admin");

        serviceProviderDto.setServiceProviderConfig(serviceProviderConfig);

        //Set values for spProvisionConfig

        adminServiceDto = new AdminServiceDto();
        adminServiceDto.setApplicationName("WSO2Telco1012");
        adminServiceDto.setOauthVersion("OAuth-2.0");
        adminServiceDto.setCallbackUrl("https://localhost:9443/playground2/oauth2.jsp");
        adminServiceDto.setGrantTypes("authorization_code implicit password client_credentials refresh_token urn:ietf:params:oauth:grant-type:saml2-bearer iwa:ntlm");
        adminServiceDto.setOauthConsumerKey("customkeygenerationTelcoWSO21012");
        adminServiceDto.setOauthConsumerSecret("secretkeygenerationTelcoWSO21012");
        adminServiceDto.setPkceMandatory(true);
        adminServiceDto.setPkceSupportPlain(false);

        serviceProviderDto.setAdminServiceDto(adminServiceDto);

        adminServiceConfig = new AdminServiceConfig();
        adminServiceConfig.setPassword("admin");
        adminServiceConfig.setUserName("admin");


        //Set values for SP Provision Config
        spProvisionConfig = new SpProvisionConfig();
        spProvisionConfig.setAdminServiceConfig(adminServiceConfig);

        //Set Values for SpProvisionDTO
        spProvisionDto.setServiceProviderDto(serviceProviderDto);
        spProvisionDto.setProvisionType(ProvisionType.LOCAL);
        spProvisionDto.setSpProvisionConfig(spProvisionConfig);
        spProvisionDto.setDiscoveryServiceDto(null);

        ProvisioningService provisioningService = new ProvisioningServiceImpl();
        try {
            provisioningService.provisionServiceProvider(spProvisionDto);
        } catch (SpProvisionServiceException e) {
            log.error("Error occurred in provisioning a Service Provider "+e.getMessage());
        }
    }
}
