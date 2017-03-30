package com.wso2telco.carbon.identity.oauth2.token.handlers.clientauth;

import org.wso2.carbon.identity.oauth.IdentityOAuthAdminException;
import org.wso2.carbon.identity.oauth.common.exception.InvalidOAuthClientException;
import org.wso2.carbon.identity.oauth2.IdentityOAuth2Exception;
import org.wso2.carbon.identity.oauth2.dto.OAuth2AccessTokenReqDTO;
import org.wso2.carbon.identity.oauth2.token.OAuthTokenReqMessageContext;
import org.wso2.carbon.identity.oauth2.token.handlers.clientauth.AbstractClientAuthHandler;
import org.wso2.carbon.identity.oauth2.util.OAuth2Util;

public class SeemlessProvisionClientAuthHandler extends AbstractClientAuthHandler {

    @Override
    public boolean authenticateClient(OAuthTokenReqMessageContext tokReqMsgCtx) throws IdentityOAuth2Exception {

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

}
