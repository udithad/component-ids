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

package com.wso2telco.sp.provision;

import com.wso2telco.core.spprovisionservice.sp.entity.AdminServiceDto;
import com.wso2telco.core.spprovisionservice.sp.entity.ProvisionType;
import com.wso2telco.core.spprovisionservice.sp.entity.ServiceProviderDto;
import com.wso2telco.core.spprovisionservice.sp.exception.SpProvisionServiceException;
import com.wso2telco.sp.builder.ServiceProviderBuilder;

public class ServiceProviderProvisionFactory {

    private Provisioner provisioner = null;
    private ServiceProviderDto serviceProviderDto = null;
    private ServiceProviderBuilder serviceProviderBuilder = null;
    private AdminServiceDto adminServiceDto = null;

    public Provisioner getProvisioner(ProvisionType provisionType) {

        if (provisionType == ProvisionType.LOCAL) {
            provisioner = new LocalProvisioner();
        } else {
            provisioner = new RemoteProvisioner();
        }
        return provisioner;
    }

    public ServiceProviderDto getServiceApplicationDetails(String applicationName) throws SpProvisionServiceException {

        serviceProviderBuilder = new ServiceProviderBuilder();
        serviceProviderDto = serviceProviderBuilder.getServiceProviderDetails(applicationName);
        return serviceProviderDto;
    }

    public AdminServiceDto getOauthServiceProviderData(String consumerKey)throws SpProvisionServiceException {

        serviceProviderBuilder = new ServiceProviderBuilder();
        adminServiceDto = serviceProviderBuilder.getOauthServiceProviderData(consumerKey);
        return adminServiceDto;
    }

}
