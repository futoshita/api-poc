package com.futoshita.jersey.server.oauth;

import org.glassfish.jersey.oauth1.signature.OAuth1Parameters;
import org.glassfish.jersey.oauth1.signature.OAuth1Secrets;
import org.glassfish.jersey.oauth1.signature.OAuth1Signature;
import org.glassfish.jersey.oauth1.signature.OAuth1SignatureException;
import org.glassfish.jersey.server.oauth1.OAuth1Consumer;
import org.glassfish.jersey.server.oauth1.OAuth1Exception;
import org.glassfish.jersey.server.oauth1.OAuth1Provider;
import org.glassfish.jersey.server.oauth1.OAuth1Token;
import org.glassfish.jersey.server.oauth1.internal.OAuthServerRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;

@Priority(Priorities.AUTHORIZATION)
public class AccessTokenFilter implements ContainerRequestFilter {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Inject
    private OAuth1Provider oauth1Provider;
    @Inject
    private OAuth1Signature oauth1Signature;

    @Inject
    public AccessTokenFilter() {
    }

    @Override
    public void filter(ContainerRequestContext requestContext) {
        String authHeader = requestContext.getHeaderString(OAuth1Parameters.AUTHORIZATION_HEADER);

        if (authHeader == null || !authHeader.toUpperCase().startsWith(OAuth1Parameters.SCHEME.toUpperCase())) {
            throw new OAuth1Exception(Response.Status.BAD_REQUEST, "OAuth parameters not found.");
        } else {
            OAuthServerRequest osr = new OAuthServerRequest(requestContext);
            OAuth1Parameters params = new OAuth1Parameters().readRequest(osr);

            String consumerKey = params.getConsumerKey();
            String token = params.getToken();

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("consumerKey: {}", consumerKey);
            }

            OAuth1Consumer consumer = oauth1Provider.getConsumer(consumerKey);
            OAuth1Token accessToken = oauth1Provider.getAccessToken(token);

            if (consumer == null || accessToken == null) {
                throw new OAuth1Exception(Response.Status.BAD_REQUEST, null);
            }

            OAuth1Secrets secrets = new OAuth1Secrets().consumerSecret(consumer.getSecret())
                    .tokenSecret(accessToken.getSecret());

            boolean sigIsOk = false;
            try {
                sigIsOk = oauth1Signature.verify(osr, params, secrets);
            } catch (OAuth1SignatureException e) {
                LOGGER.error(e.getMessage(), e);
            }

            if (!sigIsOk) {
                // signature invalid
                throw new OAuth1Exception(Response.Status.UNAUTHORIZED, "invalid OAuth1 parameters.");
            }
        }
    }

}
