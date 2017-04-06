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

package com.wso2telco.sp.builder;

import com.wso2telco.core.config.model.MobileConnectConfig;
import com.wso2telco.core.config.service.ConfigurationService;
import com.wso2telco.core.config.service.ConfigurationServiceImpl;
import com.wso2telco.core.spprovisionservice.external.admin.service.OauthAdminService;
import com.wso2telco.core.spprovisionservice.external.admin.service.SpAppManagementService;
import com.wso2telco.core.spprovisionservice.external.admin.service.dataTransform.TransformServiceProviderDto;
import com.wso2telco.core.spprovisionservice.external.admin.service.impl.OauthAdminServiceImpl;
import com.wso2telco.core.spprovisionservice.external.admin.service.impl.SpAppManagementServiceImpl;
import com.wso2telco.core.spprovisionservice.sp.entity.AdminServiceDto;
import com.wso2telco.core.spprovisionservice.sp.entity.ServiceProviderDto;
import com.wso2telco.core.spprovisionservice.sp.entity.SpProvisionConfig;
import com.wso2telco.core.spprovisionservice.sp.exception.SpProvisionServiceException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.application.common.model.xsd.ServiceProvider;

public class ServiceProviderBuilder {

    private OauthAdminService adminService = null;
    private SpAppManagementService spAppManagementService = null;
    private AdminServiceDto adminserviceDto = null;
    private ServiceProviderDto serviceProviderDto = null;
    private static Log log = LogFactory.getLog(ServiceProviderBuilder.class);
    private MobileConnectConfig mobileConnectConfig = null;
    private MobileConnectConfig.Config config = null;
    private ConfigurationService configurationService = new ConfigurationServiceImpl();

    public void buildServiceProvider(ServiceProviderDto serviceProviderDto, SpProvisionConfig spProvisionConfig) throws SpProvisionServiceException {
        mobileConnectConfig = configurationService.getDataHolder().getMobileConnectConfig();
        config = new MobileConnectConfig.Config();

        if (serviceProviderDto != null) {

            buildOauthDataStructure(serviceProviderDto.getAdminServiceDto());
            buildSpApplicationDataStructure(serviceProviderDto);
        } else {
            log.error("Service Provider details are empty");
        }
    }

    public void buildOauthDataStructure(AdminServiceDto adminServiceDto) throws SpProvisionServiceException {

        adminService = new OauthAdminServiceImpl();

        if (serviceProviderDto != null) {
            try {
                adminService.registerOAuthApplicationData(adminServiceDto);
            } catch (SpProvisionServiceException e) {
                throw new SpProvisionServiceException(e.getMessage());
            }
        } else {
            log.error("oAuth data object doesn't have data for the registration");
        }

    }

    public ServiceProvider buildSpApplicationDataStructure(ServiceProviderDto serviceProviderDto) throws SpProvisionServiceException {

        spAppManagementService = new SpAppManagementServiceImpl();
        String applicationName = serviceProviderDto.getApplicationName();
        ServiceProvider serviceProvider = null;

        if (serviceProviderDto != null) {

            spAppManagementService.createSpApplication(serviceProviderDto);
            serviceProvider = spAppManagementService.getSpApplicationData(applicationName);

            if (serviceProvider != null) {
                spAppManagementService.updateSpApplication(serviceProviderDto);
            }
            serviceProvider = spAppManagementService.getSpApplicationData(applicationName);
        }

        return serviceProvider;
    }

    public void reBuildOauthDataStructure(String oldConsumerKey, AdminServiceDto adminServiceDto) throws SpProvisionServiceException {

        if (adminServiceDto != null) {
            adminService = new OauthAdminServiceImpl();
            adminService.removeOAuthApplicationData(oldConsumerKey);
            adminService.registerOAuthApplicationData(adminServiceDto);
        }

    }

    public void revokeSpApplication(String oldConsumerKey, String applicationName) throws SpProvisionServiceException {


        adminService = new OauthAdminServiceImpl();
        spAppManagementService = new SpAppManagementServiceImpl();

        if (spAppManagementService.getSpApplicationData(applicationName) != null) {
            spAppManagementService.deleteSpApplication(applicationName);
        } else {
            log.error("Given service Provider is not available");
        }

        adminService.removeOAuthApplicationData(oldConsumerKey);

    }

    public ServiceProviderDto getServiceProviderDetails(String applicationName) throws SpProvisionServiceException {

        spAppManagementService = new SpAppManagementServiceImpl();
        TransformServiceProviderDto transformServiceProviderDto = new TransformServiceProviderDto();
        ServiceProvider serviceProvider = spAppManagementService.getSpApplicationData(applicationName);
        serviceProviderDto = transformServiceProviderDto.transformToServiceProviderDto(serviceProvider);
        return serviceProviderDto;
    }
}
