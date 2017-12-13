package com.futoshita.jersey.client;

import javax.ws.rs.core.MediaType;

import org.junit.Assert;
import org.junit.Test;

import com.futoshita.jersey.client.entity.User;

public class ClientAppTest {
  
  private final String GET_RESOURCE_URI = "http://localhost:8080/jersey2-server/api/greeting/hello";
  private final String GET_SECURED_RESOURCE_URI = "http://localhost:8080/jersey2-server/api/greeting/hello/secured";
  
  private final String POST_USER_URI = "http://localhost:8080/jersey2-server/api/users";
  
  @Test
  public void get_Success() {
    ClientApp client = new ClientApp();
    try {
      String entity = client.get(GET_RESOURCE_URI, String.class);
      Assert.assertEquals("Hello, world!", entity);
    } catch (Exception e) {
      Assert.fail(e.toString());
    }
  }
  
  @Test
  public void getSecured_Success() {
    ClientApp client = new ClientApp();
    try {
      client.initOAuth1Feature("src/test/resources/app.properties");
    } catch (Exception e) {
      Assert.fail(e.toString());
    }
    
    try {
      String entity = client.getSecured(GET_SECURED_RESOURCE_URI, String.class);
      Assert.assertEquals("Hello, world! (secured)", entity);
    } catch (Exception e) {
      Assert.fail(e.toString());
    }
  }
  
  @Test
  public void getSecured_Failed_Unauthorized() {
    ClientApp client = new ClientApp();
    
    String errorMsg = null;
    
    try {
      client.get(GET_SECURED_RESOURCE_URI, String.class);
    } catch (Exception e) {
      errorMsg = e.getMessage();
    }
    
    Assert.assertEquals("status code: 400, reason: Bad Request, cause: ", errorMsg);
  }
  
  @Test
  public void checkNonUniqueException_Success() {
    ClientApp client = new ClientApp();
    
    try {
      client.post(POST_USER_URI, new User("ident", "password", "email@test.com"), MediaType.APPLICATION_JSON_TYPE);
    } catch (Exception e) {
      Assert.fail(e.toString());
    }
    
    String errorMsg = null;
    try {
      client.post(POST_USER_URI, new User("ident", "password", "email@test.com"), MediaType.APPLICATION_JSON_TYPE);
    } catch (Exception e) {
      errorMsg = e.getMessage();
    }
    
    Assert.assertEquals("status code: 409, reason: Conflict, cause: User with email email@test.com already exists.", errorMsg);
  }
  
  @Test
  public void checkBadParameter_Failed_NullField() {
    ClientApp client = new ClientApp();
    
    String errorMsg = null;
    
    try {
      client.post(POST_USER_URI, new User(null, "password", null), MediaType.APPLICATION_JSON_TYPE);
    } catch (Exception e) {
      errorMsg  = e.getMessage();
    }
    
    Assert.assertEquals("status code: 400, reason: Bad Request, cause: ", errorMsg);
  }
}
