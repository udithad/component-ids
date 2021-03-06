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
package com.wso2telco.gsma.authenticators;

import com.wso2telco.core.config.service.ConfigurationService;
import com.wso2telco.core.config.service.ConfigurationServiceImpl;
import com.wso2telco.gsma.authenticators.util.AdminServiceUtil;
import com.wso2telco.gsma.authenticators.util.AuthenticationContextHelper;
import com.wso2telco.gsma.manager.client.ClaimManagementClient;
import com.wso2telco.gsma.manager.client.LoginAdminServiceClient;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.authenticator.stub.LoginAuthenticationExceptionException;
import org.wso2.carbon.identity.application.authentication.framework.AbstractApplicationAuthenticator;
import org.wso2.carbon.identity.application.authentication.framework.AuthenticatorFlowStatus;
import org.wso2.carbon.identity.application.authentication.framework.LocalApplicationAuthenticator;
import org.wso2.carbon.identity.application.authentication.framework.config.ConfigurationFacade;
import org.wso2.carbon.identity.application.authentication.framework.context.AuthenticationContext;
import org.wso2.carbon.identity.application.authentication.framework.exception.AuthenticationFailedException;
import org.wso2.carbon.identity.application.authentication.framework.exception.LogoutFailedException;
import org.wso2.carbon.identity.application.authentication.framework.util.FrameworkUtils;
import org.wso2.carbon.identity.oauth.cache.CacheKey;
import org.wso2.carbon.identity.oauth.cache.SessionDataCache;
import org.wso2.carbon.identity.oauth.cache.SessionDataCacheEntry;
import org.wso2.carbon.identity.oauth.cache.SessionDataCacheKey;
import org.wso2.carbon.identity.oauth.common.OAuthConstants;
import org.wso2.carbon.um.ws.api.stub.RemoteUserStoreManagerServiceUserStoreExceptionException;
import org.wso2.carbon.user.api.UserStoreException;

import javax.crypto.Cipher;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.RemoteException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.sql.SQLException;

// TODO: Auto-generated Javadoc

/**
 * The Class MSISDNAuthenticator.
 */
public class MSISDNAuthenticator extends AbstractApplicationAuthenticator
        implements LocalApplicationAuthenticator {

    /**
     * The Constant serialVersionUID.
     */
    private static final long serialVersionUID = 6817280268460894001L;
    private static final int LOA_3 = 3;

    /**
     * The log.
     */
    private static Log log = LogFactory.getLog(MSISDNAuthenticator.class);

    /**
     * The Constant LOGIN_HINT_ENCRYPTED_PREFIX.
     */
    private static final String LOGIN_HINT_ENCRYPTED_PREFIX = "ENCR_MSISDN:";

    /**
     * The Constant LOGIN_HINT_NOENCRYPTED_PREFIX.
     */
    private static final String LOGIN_HINT_NOENCRYPTED_PREFIX = "MSISDN:";

    /**
     * The Constant LOGIN_HINT_SEPARATOR.
     */
    private static final String LOGIN_HINT_SEPARATOR = "|";

    /**
     * The Constant ENCRYPTION_ALGORITHM.
     */
    private static final String ENCRYPTION_ALGORITHM = "RSA";

    /**
     * The Configuration service
     */
    private static ConfigurationService configurationService = new ConfigurationServiceImpl();

    /* (non-Javadoc)
     * @see org.wso2.carbon.identity.application.authentication.framework.ApplicationAuthenticator#canHandle(javax.servlet.http.HttpServletRequest)
     */
    @Override
    public boolean canHandle(HttpServletRequest request) {
        if (log.isDebugEnabled()) {
            log.debug("MSISDN Authenticator canHandle invoked");
        }

        if ((request.getParameter("msisdn") != null) || (getLoginHintValues(request) != null)
                || ((request.getParameter("msisdn_header") != null) && (request.getParameter("msisdn_header") != ""))) {
            log.info("msisdn forwarding ");
            return true;
        }
        return false;
    }

    /* (non-Javadoc)
     * @see org.wso2.carbon.identity.application.authentication.framework.AbstractApplicationAuthenticator#process(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, org.wso2.carbon.identity.application.authentication.framework.context.AuthenticationContext)
     */
    @Override
    public AuthenticatorFlowStatus process(HttpServletRequest request,
                                           HttpServletResponse response, AuthenticationContext context)
            throws AuthenticationFailedException, LogoutFailedException {

        if (context.isLogoutRequest()) {
            return AuthenticatorFlowStatus.SUCCESS_COMPLETED;
        } else {
            return super.process(request, response, context);
        }
    }

    /* (non-Javadoc)
     * @see org.wso2.carbon.identity.application.authentication.framework.AbstractApplicationAuthenticator#initiateAuthenticationRequest(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, org.wso2.carbon.identity.application.authentication.framework.context.AuthenticationContext)
     */
    @Override
    protected void initiateAuthenticationRequest(HttpServletRequest request,
                                                 HttpServletResponse response, AuthenticationContext context)
            throws AuthenticationFailedException {


        boolean isProfileUpgrade = false;
        String loginPage;
        try {

            String msisdn = request.getParameter(Constants.MSISDN);
            int currentLoa = getAcr(request, context);

            if (context.isRetrying()) {
                isProfileUpgrade = (boolean) context.getProperty(Constants.IS_PROFILE_UPGRADE);
            }
            context.setProperty(Constants.ACR, currentLoa);

            loginPage = getAuthEndpointUrl(msisdn, isProfileUpgrade);

            String queryParams = FrameworkUtils
                    .getQueryStringWithFrameworkContextId(context.getQueryParams(),
                            context.getCallerSessionKey(),
                            context.getContextIdentifier());
            String retryParam = "";

            if (context.isRetrying()) {
                retryParam = "&authFailure=true&authFailureMsg=login.fail.message";
            }
            log.info("Query params: " + queryParams);

            response.sendRedirect(response.encodeRedirectURL(loginPage + ("?" + queryParams)) + "&redirect_uri=" + request.getParameter("redirect_uri") + "&authenticators="
                    + getName() + ":" + "LOCAL" + retryParam);
        } catch (UserStoreException e) {
            e.printStackTrace();
        } catch (IOException e) {
            log.error("Error occurred while redirecting request", e);
            throw new AuthenticationFailedException(e.getMessage(), e);
        } catch (RemoteUserStoreManagerServiceUserStoreExceptionException | LoginAuthenticationExceptionException e) {
            log.error("Error occurred while accessing admin services", e);
            throw new AuthenticationFailedException(e.getMessage(), e);
        }
    }

    private int getAcr(HttpServletRequest request, AuthenticationContext context) {
        int acr;

        if (!context.isRetrying()) {
            acr = Integer.parseInt(request.getParameter(Constants.PARAM_ACR));
        } else {
            acr = (int) context.getProperty(Constants.ACR);
        }
        return acr;
    }

    /* (non-Javadoc)
     * @see org.wso2.carbon.identity.application.authentication.framework.AbstractApplicationAuthenticator#processAuthenticationResponse(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, org.wso2.carbon.identity.application.authentication.framework.context.AuthenticationContext)
     */
    @Override
    protected void processAuthenticationResponse(HttpServletRequest request,
                                                 HttpServletResponse response, AuthenticationContext context)
            throws AuthenticationFailedException {

        String msisdn = getMsisdn(request, context);
        String operator = request.getParameter(Constants.OPERATOR);

        try {
            boolean isUserExists = AdminServiceUtil.isUserExists(msisdn);
            int currentLoa = (int) context.getProperty(Constants.ACR);
            boolean isProfileUpgrade = isProfileUpgrade(msisdn, currentLoa, isUserExists);

            setPropertiesToContext(context, msisdn, operator, isUserExists, currentLoa, isProfileUpgrade);

            handleFlow(request, context, msisdn, isUserExists, isProfileUpgrade);

            AuthenticationContextHelper.setSubject(context, msisdn);
            String rememberMe = request.getParameter("chkRemember");

            if (rememberMe != null && "eon".equals(rememberMe)) {
                context.setRememberMe(true);
            }
        } catch (org.wso2.carbon.user.api.UserStoreException e) {
            log.error("MSISDN Authentication failed while trying to authenticate", e);
            throw new AuthenticationFailedException(e.getMessage(), e);
        } catch (AuthenticatorException e) {
            log.error("Error occurred while saving request type");
        } catch (SQLException | NamingException e) {
            log.error("Error occurred while saving data", e);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (RemoteUserStoreManagerServiceUserStoreExceptionException e) {
            e.printStackTrace();
        } catch (LoginAuthenticationExceptionException e) {
            e.printStackTrace();
        }
    }

    private void handleFlow(HttpServletRequest request, AuthenticationContext context, String msisdn, boolean isUserExists,
                            boolean isProfileUpgrade) throws AuthenticationFailedException, SQLException, NamingException,
            AuthenticatorException {

        if (isUserRegistration(context, isUserExists)) {

            retryAuthenticatorToGetConsent(context, msisdn);

        } else if (isProfileUpgrade(context, isUserExists, isProfileUpgrade)) {

            retryAuthenticatorToUpdateProfile(context);

        } else if (isRegistrationCompletion(context, isUserExists)) {

            updateDatabaseForRegirationPending(request, context, msisdn);

        } else {

            handleLogin(context);
        }
    }

    private void retryAuthenticatorToUpdateProfile(AuthenticationContext context) throws AuthenticationFailedException {
        context.setProperty(Constants.IS_REGISTERING, false);
        throw new AuthenticationFailedException("User exists. Moving for profile updating");
    }

    private void retryAuthenticatorToGetConsent(AuthenticationContext context, String msisdn) throws AuthenticationFailedException {
        context.setProperty("faileduser", msisdn);
        context.setProperty(Constants.IS_REGISTERING, true);
        if (log.isDebugEnabled()) {
            log.debug("User authentication failed due to not existing user MSISDN.");
        }
        throw new AuthenticationFailedException("User does not exist. Moving for registration");
    }

    private void handleLogin(AuthenticationContext context) throws AuthenticatorException {
        context.setProperty(Constants.IS_REGISTERING, false);
        DBUtils.insertLoginStatus(context.getContextIdentifier(), String.valueOf(Constants.STATUS_PENDING));
    }

    private void setPropertiesToContext(AuthenticationContext context, String msisdn, String operator, boolean isUserExists, int currentLoa, boolean isProfileUpgrade) {
        context.setProperty(Constants.IS_USER_EXISTS, isUserExists);
        context.setProperty(Constants.MSISDN, msisdn);
        context.setProperty(Constants.OPERATOR, operator);
        context.setProperty(Constants.ACR, currentLoa);
        context.setProperty(Constants.IS_PROFILE_UPGRADE, isProfileUpgrade);
        context.setProperty(Constants.IS_PIN_RESET, false);
    }

    private boolean isProfileUpgrade(AuthenticationContext context, boolean isUserExists, boolean isProfileUpgrade) {
        return isUserExists && isProfileUpgrade && !context.isRetrying();
    }

    private boolean isRegistrationCompletion(AuthenticationContext context, boolean isUserExists) {
        return !isUserExists && context.isRetrying();
    }

    private boolean isUserRegistration(AuthenticationContext context, boolean isUserExists) {
        return !isUserExists && !context.isRetrying();
    }

    private void setPropertiesToContext(AuthenticationContext context, String msisdn, boolean isProfileUpgrade) throws UserStoreException,
            AuthenticationFailedException, RemoteUserStoreManagerServiceUserStoreExceptionException, RemoteException,
            LoginAuthenticationExceptionException {

        if (msisdn != null) {

            boolean isUserExists = AdminServiceUtil.isUserExists(msisdn);

            if (!isUserExists) {
                context.setProperty(Constants.IS_REGISTERING, true);
                context.setProperty(Constants.IS_PROFILE_UPGRADE, false);
            } else {
                context.setProperty(Constants.IS_REGISTERING, false);
                if (isProfileUpgrade) {
                    context.setProperty(Constants.IS_PROFILE_UPGRADE, true);
                } else {
                    context.setProperty(Constants.IS_PROFILE_UPGRADE, false);
                }
            }
        }
    }

    private boolean isProfileUpgrade(String msisdn, int currentLoa, boolean isUserExits) throws RemoteException, LoginAuthenticationExceptionException, RemoteUserStoreManagerServiceUserStoreExceptionException, AuthenticationFailedException, UserStoreException {

        if (msisdn != null && isUserExits) {
            String adminURL = configurationService.getDataHolder().getMobileConnectConfig().getAdminUrl();
            LoginAdminServiceClient lAdmin = new LoginAdminServiceClient(adminURL);
            String sessionCookie = lAdmin.authenticate(configurationService.getDataHolder().getMobileConnectConfig().getAdminUsername(),
                    configurationService.getDataHolder().getMobileConnectConfig().getAdminPassword());
            ClaimManagementClient claimManager = new ClaimManagementClient(adminURL, sessionCookie);
            int registeredLoa = Integer.parseInt(claimManager.getRegisteredLOA(msisdn));

            return currentLoa > registeredLoa;
        } else {
            return false;
        }

    }

    private String getAuthEndpointUrl(String msisdn, boolean isProfileUpgrade) throws UserStoreException,
            AuthenticationFailedException, RemoteException, LoginAuthenticationExceptionException,
            RemoteUserStoreManagerServiceUserStoreExceptionException {

        String loginPage;
        if (msisdn != null && !AdminServiceUtil.isUserExists(msisdn)) {

            loginPage = configurationService.getDataHolder().getMobileConnectConfig().getAuthEndpointUrl() + Constants.CONSENT_JSP;
        } else {

            if (isProfileUpgrade) {

                loginPage = configurationService.getDataHolder().getMobileConnectConfig().getAuthEndpointUrl()
                        + Constants.PROFILE_UPGRADE_JSP;
            } else {
                loginPage = ConfigurationFacade.getInstance().getAuthenticationEndpointURL();
            }
        }
        return loginPage;
    }

    private void updateDatabaseForRegirationPending(HttpServletRequest request, AuthenticationContext context, String msisdn) throws SQLException, NamingException, AuthenticatorException {
        DBUtils.saveRequestType(msisdn, 1);
        DBUtils.insertRegistrationStatus(msisdn, Constants.STATUS_PENDING, context.getContextIdentifier());
        if (request.getParameter("isRegistration") != null) {
            context.setProperty("isRegistration", request.getParameter("isRegistration"));
        }
    }

    private String getMsisdn(HttpServletRequest request, AuthenticationContext context) {
        String msisdn;
        msisdn = request.getParameter(Constants.MSISDN);
        if (msisdn == null) {
            msisdn = (String) context.getProperty(Constants.MSISDN);
        }
        return msisdn;
    }

    /**
     * Decrypt data.
     *
     * @param data the data
     * @return the string
     * @throws Exception the exception
     */
    public String decryptData(String data) throws Exception {
        byte[] bytes = hexStringToByteArray(data);
        String filename = configurationService.getDataHolder().getMobileConnectConfig().getKeyfile();
        PrivateKey key = getPrivateKey(filename);
        return decrypt(bytes, key);
    }

    /**
     * Decrypt.
     *
     * @param text the text
     * @param key  the key
     * @return the string
     */
    public static String decrypt(byte[] text, PrivateKey key) {
        try {
            // get an RSA cipher object and print the provider
            final Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);

            // decrypt the text using the private key
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] dectyptedText = cipher.doFinal(text);

            return new String(dectyptedText);

        } catch (Exception ex) {
            log.error("Exception encrypting data " + ex.getClass().getName() + ": " + ex.getMessage());
            return null;
        }
    }

    /**
     * Gets the private key.
     *
     * @param filename the filename
     * @return the private key
     * @throws Exception the exception
     */
    public static PrivateKey getPrivateKey(String filename) throws Exception {

        try {

            String publicK = readStringKey(filename);
            //byte[] keyBytes = new BASE64Decoder().decodeBuffer(publicK);
            byte[] keyBytes = Base64.decodeBase64(publicK.getBytes());
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory fact = KeyFactory.getInstance("RSA");

            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance(ENCRYPTION_ALGORITHM);
            return kf.generatePrivate(spec);

        } catch (Exception ex) {
            log.error("Exception reading private key:" + ex.getMessage());
            return null;
        }

    }

    /**
     * Read string key.
     *
     * @param fileName the file name
     * @return the string
     */
    public static String readStringKey(String fileName) {

        BufferedReader reader = null;
        StringBuffer fileData = null;
        try {

            fileData = new StringBuffer(2048);
            reader = new BufferedReader(new FileReader(fileName));
            char[] buf = new char[1024];
            int numRead = 0;
            while ((numRead = reader.read(buf)) != -1) {
                String readData = String.valueOf(buf, 0, numRead);
                fileData.append(readData);
                buf = new char[1024];
            }

            reader.close();

        } catch (Exception e) {
        } finally {
            if (reader != null) {
                reader = null;
            }
        }
        return fileData.toString();

    }


    /**
     * Hex string to byte array.
     *
     * @param s the s
     * @return the byte[]
     */
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    /**
     * Validate msisdn.
     *
     * @param msisdn the msisdn
     * @return true, if successful
     */
    protected boolean validateMsisdn(String msisdn) {
        boolean isvalid = false;
        if (msisdn != null && ((msisdn.length() == 11 && msisdn.indexOf('+') < 0) || (msisdn.length() == 12 && msisdn.matches("[0-9]+")))) {
            isvalid = true;
        }
        return isvalid;
    }

    /**
     * Gets the login hint values.
     *
     * @param request the request
     * @return the login hint values
     */
    private String getLoginHintValues(HttpServletRequest request) {
        String loginHintValues = null;

        try {
            String sdk = request.getParameter(OAuthConstants.SESSION_DATA_KEY);
            CacheKey ck = new SessionDataCacheKey(sdk);
            SessionDataCacheKey sessionDataCacheKey = new SessionDataCacheKey(sdk);
            SessionDataCacheEntry sdce =
                    (SessionDataCacheEntry) SessionDataCache.getInstance().getValueFromCache(sessionDataCacheKey);
            loginHintValues = sdce.getoAuth2Parameters().getLoginHint();
        } catch (Exception e) {
            log.error("Exception Getting the login hint values " + e);
        }

        return loginHintValues;
    }

    /* (non-Javadoc)
     * @see org.wso2.carbon.identity.application.authentication.framework.AbstractApplicationAuthenticator#retryAuthenticationEnabled()
     */
    @Override
    protected boolean retryAuthenticationEnabled() {
        // Setting retry to true as we need the correct MSISDN to continue
        return true;
    }

    /* (non-Javadoc)
     * @see org.wso2.carbon.identity.application.authentication.framework.ApplicationAuthenticator#getContextIdentifier(javax.servlet.http.HttpServletRequest)
     */
    @Override
    public String getContextIdentifier(HttpServletRequest request) {
        return request.getParameter("sessionDataKey");
    }

    /* (non-Javadoc)
     * @see org.wso2.carbon.identity.application.authentication.framework.ApplicationAuthenticator#getFriendlyName()
     */
    @Override
    public String getFriendlyName() {
        return Constants.MSISDN_AUTHENTICATOR_FRIENDLY_NAME;
    }

    /**
     * Gets the private key file.
     *
     * @return the private key file
     */
    private String getPrivateKeyFile() {
        return Constants.PRIVATE_KEYFILE;
    }

    /* (non-Javadoc)
     * @see org.wso2.carbon.identity.application.authentication.framework.ApplicationAuthenticator#getName()
     */
    @Override
    public String getName() {
        return Constants.MSISDN_AUTHENTICATOR_NAME;
    }
}
