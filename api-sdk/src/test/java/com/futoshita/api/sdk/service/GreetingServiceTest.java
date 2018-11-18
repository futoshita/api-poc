package com.futoshita.api.sdk.service;

import com.futoshita.api.sdk.ClientApp;
import com.futoshita.api.sdk.entity.ClientAppProperties;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;

public class GreetingServiceTest {

    private final static Logger LOGGER = LoggerFactory.getLogger(GreetingServiceTest.class);

    @Before
    public void before() {
        try {
            ClientApp.getInstance().loadProperties("src/test/resources/app.properties");
            ClientApp.getInstance().initOAuth1Feature();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            Assert.fail();
        }
    }

    @Test
    public void getHello_Success() {
        try {
            String result = GreetingService.getHello(MediaType.TEXT_PLAIN_TYPE);

            Assert.assertEquals("Hello, world!", result);
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void getHello_Failed_WrongMediaType() {
        String errorMsg = null;
        try {
            GreetingService.getHello();
        } catch (Exception e) {
            errorMsg = e.getMessage();
        }

        Assert.assertEquals("status code: 406, reason: Not Acceptable, cause: the requested resource is not available for content types: [application/json].", errorMsg);
    }

    @Test
    public void getHelloSecured_Success() {
        try {
            String result = GreetingService.getHelloSecured(MediaType.TEXT_PLAIN_TYPE);
            Assert.assertEquals("Hello, world! (secured)", result);
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void getHelloSecured_Failed_Unauthorized() {
        ClientAppProperties properties = ClientApp.getInstance().getClientAppProperties();
        properties.setAccessTokenKey("foo");
        properties.setAccessTokenSecret("bar");
        ClientApp.getInstance().setClientAppProperties(properties);
        try {
            ClientApp.getInstance().initOAuth1Feature();
        } catch (Exception e) {
            Assert.fail();
        }

        ClientApp.SecuredClientConfigBuidler.getInstance().resetClientConfig();

        String errorMsg = null;
        try {
            GreetingService.getHelloSecured(MediaType.TEXT_PLAIN_TYPE);
        } catch (Exception e) {
            errorMsg = e.getMessage();
        }

        Assert.assertEquals("status code: 401, reason: Unauthorized, cause: HTTP 401 Unauthorized", errorMsg);
    }
}
