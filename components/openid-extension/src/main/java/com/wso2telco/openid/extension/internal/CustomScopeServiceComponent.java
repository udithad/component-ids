/*******************************************************************************
 * Copyright (c) 2015-2016, WSO2.Telco Inc. (http://www.wso2telco.com)
 *
 * All Rights Reserved. WSO2.Telco Inc. licences this file to you under the Apache License, Version 2.0 (the "License");
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
package com.wso2telco.openid.extension.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;

/**
 * @scr.component name="com.wso2telco.openid.extension.internal.CustomScopeServiceComponent" immediate="true"
 */
public class CustomScopeServiceComponent {

    private static BundleContext bundleContext;
    private static Log log = LogFactory.getLog(CustomScopeServiceComponent.class);

    protected void activate(ComponentContext context) {
        if(log.isDebugEnabled()){
            log.debug("CustomScopeServiceComponent activated");
        }
    }

    protected void deactivate(ComponentContext context) {
        if(log.isDebugEnabled()){
            log.debug("CustomScopeServiceComponent deactivated");
        }
    }

    public static BundleContext getBundleContext() {
        return bundleContext;
    }

    public static void setBundleContext(BundleContext bundleContext) {
        CustomScopeServiceComponent.bundleContext = bundleContext;
    }
}
