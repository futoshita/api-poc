package com.futoshita.api.sdk.service;

import com.futoshita.api.sdk.ClientApp;
import com.futoshita.api.sdk.entity.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import java.net.URI;

public class UserServiceTest {

    private final static Logger LOGGER = LoggerFactory.getLogger(UserServiceTest.class);

    @Before
    public void before() {
        try {
            ClientApp.getInstance().loadProperties("src/test/resources/app.properties");
            //ClientApp.getInstance().initOAuth1Feature();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            Assert.fail();
        }
    }

    @Test
    public void get_Success() {
        Integer id = null;

        try {
            User user = new User("ident", "password", "email@test.com");

            URI uri = UserService.create(user);
            String[] split = uri.toString().split("/");
            id = Integer.parseInt(split[split.length - 1]);
        } catch (Exception e) {
            Assert.fail();
        }

        try {
            User user = UserService.get(id);

            Assert.assertEquals("email@test.com", user.getEmail());
        } catch (Exception e) {
            Assert.fail();
        }

        try {
            UserService.delete(id);
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void get_Failed_NotFound() {
        String errorMsg = null;

        ClientApp.getInstance().getClientAppProperties().setLocale("fr-FR");
        try {
            UserService.get(999);
        } catch (Exception e) {
            errorMsg = e.getMessage();
        }
        Assert.assertEquals("status code: 404, reason: Not Found, cause: L'utilisateur avec l'ID fourni en paramètre n'existe pas.", errorMsg);

        errorMsg = null;

        ClientApp.getInstance().getClientAppProperties().setLocale(null);
        try {
            UserService.get(999);
        } catch (Exception e) {
            errorMsg = e.getMessage();
        }
        Assert.assertEquals("status code: 404, reason: Not Found, cause: User with given ID does not exist.", errorMsg);

        ClientApp.getInstance().getClientAppProperties().setLocale("fr-FR");
        try {
            UserService.get(999, MediaType.APPLICATION_XML_TYPE);
        } catch (Exception e) {
            errorMsg = e.getMessage();
        }
        Assert.assertEquals("status code: 404, reason: Not Found, cause: L'utilisateur avec l'ID fourni en paramètre n'existe pas.", errorMsg);

        errorMsg = null;

        ClientApp.getInstance().getClientAppProperties().setLocale(null);
        try {
            UserService.get(999, MediaType.APPLICATION_XML_TYPE);
        } catch (Exception e) {
            errorMsg = e.getMessage();
        }
        Assert.assertEquals("status code: 404, reason: Not Found, cause: User with given ID does not exist.", errorMsg);
    }

    @Test
    public void create_Failed_NonUniqueEmail() {
        String errorMsg = null;
        Integer id = null;

        try {
            URI uri = UserService.create(new User("ident", "password", "email@test.com"));
            String[] split = uri.toString().split("/");
            id = Integer.parseInt(split[split.length - 1]);
        } catch (Exception e) {
            Assert.fail();
        }

        try {
            UserService.create(new User("ident1", "password", "email@test.com"));
        } catch (Exception e) {
            errorMsg = e.getMessage();
        }

        try {
            UserService.delete(id);
        } catch (Exception e) {
            Assert.fail();
        }

        Assert.assertEquals("status code: 409, reason: Conflict, cause: User with email 'email@test.com' already exists.", errorMsg);
    }

    @Test
    public void create_Failed_NonUniqueIdent() {
        String errorMsg = null;
        Integer id = null;

        try {
            URI uri = UserService.create(new User("ident", "password", "email@test.com"));
            String[] split = uri.toString().split("/");
            id = Integer.parseInt(split[split.length - 1]);
        } catch (Exception e) {
            Assert.fail();
        }

        try {
            UserService.create(new User("ident", "password", "email1@test.com"));
        } catch (Exception e) {
            errorMsg = e.getMessage();
        }

        try {
            UserService.delete(id);
        } catch (Exception e) {
            Assert.fail();
        }

        Assert.assertEquals("status code: 409, reason: Conflict, cause: User with ident 'ident' already exists.", errorMsg);
    }

    @Test
    public void create_Failed_NullIdent() {
        User user = new User(null, "password", null);

        String errorMsg = null;

        try {
            UserService.create(user);
        } catch (Exception e) {
            errorMsg = e.getMessage();
        }

        Assert.assertEquals("status code: 400, reason: Bad Request, cause: Ident of the user should not be null.", errorMsg);
    }

    @Test
    public void create_Failed_WrongIdent() {
        User user = new User("a", "password", null);

        String errorMsg = null;

        try {
            UserService.create(user);
        } catch (Exception e) {
            errorMsg = e.getMessage();
        }

        Assert.assertEquals("status code: 400, reason: Bad Request, cause: Ident of the user should contain at least 2 letters.", errorMsg);
    }
}
