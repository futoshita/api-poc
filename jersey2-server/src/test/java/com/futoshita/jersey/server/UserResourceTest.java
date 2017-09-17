package com.futoshita.jersey.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.eclipse.persistence.jaxb.BeanValidationMode;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.moxy.json.MoxyJsonConfig;
import org.glassfish.jersey.moxy.json.MoxyJsonFeature;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.server.validation.ValidationError;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.glassfish.jersey.test.external.ExternalTestContainerFactory;
import org.junit.Test;

import com.futoshita.jersey.server.entity.User;

/**
 * @author Michal Gajdos
 */
public class UserResourceTest extends JerseyTest {
  
  private static final User USER_1;
  
  static {
    USER_1 = new User("ident1", "123456789", "email1@test.com");
  }
  
  @Override
  protected Application configure() {
    enable(TestProperties.LOG_TRAFFIC);
    enable(TestProperties.DUMP_ENTITY);
    
    return new ServerApp().property(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true);
  }
  
  @Override
  protected void configureClient(final ClientConfig config) {
    super.configureClient(config);
    
    config.register(MoxyJsonFeature.class);
    // Turn off BV otherwise the entities on client would be validated as well.
    config.register(
        new MoxyJsonConfig().property(MarshallerProperties.BEAN_VALIDATION_MODE, BeanValidationMode.NONE).resolver());
  }
  
  @Override
  protected URI getBaseUri() {
    final UriBuilder baseUriBuilder = UriBuilder.fromUri(super.getBaseUri()).path("jersey2-server");
    final boolean externalFactoryInUse = getTestContainerFactory() instanceof ExternalTestContainerFactory;
    return externalFactoryInUse ? baseUriBuilder.path("api").build() : baseUriBuilder.build();
  }
  
  @Test
  public void testAddUser() throws Exception {
    final WebTarget target = target().path("users");
    final Response response = target.request(MediaType.APPLICATION_JSON_TYPE)
        .post(Entity.entity(USER_1, MediaType.APPLICATION_JSON_TYPE));
    
    final User user = response.readEntity(User.class);
    
    assertEquals(200, response.getStatus());
    assertNotNull(user.getId());
    
    final Response invalidResponse = target.request(MediaType.APPLICATION_JSON_TYPE)
        .post(Entity.entity(USER_1, MediaType.APPLICATION_JSON_TYPE));
    assertEquals(500, invalidResponse.getStatus());
    assertTrue(getValidationMessageTemplates(invalidResponse).contains("{user.already.exist}"));
    
    assertEquals(200, target.path("" + user.getId()).request(MediaType.APPLICATION_JSON_TYPE).delete().getStatus());
  }
  
  @Test
  public void testUserDoesNotExist() throws Exception {
    final WebTarget target = target().path("users");
    
    // GET
    Response response = target.path("1").request(MediaType.APPLICATION_JSON_TYPE).get();
    
    assertEquals(500, response.getStatus());
    
    Set<String> violationsMessageTemplates = getValidationMessageTemplates(response);
    assertEquals(1, violationsMessageTemplates.size());
    assertTrue(violationsMessageTemplates.contains("{user.does.not.exist}"));
    
    // DELETE
    response = target.path("1").request(MediaType.APPLICATION_JSON_TYPE).delete();
    
    assertEquals(500, response.getStatus());
    
    violationsMessageTemplates = getValidationMessageTemplates(response);
    assertEquals(1, violationsMessageTemplates.size());
    assertTrue(violationsMessageTemplates.contains("{user.does.not.exist}"));
  }
  
  @Test
  public void testUserWrongId() throws Exception {
    final WebTarget target = target().path("users");
    
    // GET
    Response response = target.path("-1").request(MediaType.APPLICATION_JSON_TYPE).get();
    
    assertEquals(400, response.getStatus());
    
    Set<String> violationsMessageTemplates = getValidationMessageTemplates(response);
    assertEquals(1, violationsMessageTemplates.size());
    assertTrue(violationsMessageTemplates.contains("{user.wrong.id}"));
    
    // DELETE
    response = target.path("-2").request(MediaType.APPLICATION_JSON_TYPE).delete();
    
    assertEquals(400, response.getStatus());
    
    violationsMessageTemplates = getValidationMessageTemplates(response);
    assertEquals(1, violationsMessageTemplates.size());
    assertTrue(violationsMessageTemplates.contains("{user.wrong.id}"));
  }
  
  @Test
  public void testAddInvalidUser() throws Exception {
    final User entity = new User();
    entity.setEmail("email");
    
    final Response response = target().path("users").request(MediaType.APPLICATION_JSON_TYPE)
        .post(Entity.entity(entity, MediaType.APPLICATION_JSON_TYPE));
    
    assertEquals(400, response.getStatus());
    
    final List<ValidationError> validationErrorList = getValidationErrorList(response);
    for (final ValidationError validationError : validationErrorList) {
      assertTrue(validationError.getPath().contains("UserResource.addUser.user."));
    }
    
    final Set<String> messageTemplates = getValidationMessageTemplates(validationErrorList);
    assertEquals(2, messageTemplates.size());
    assertTrue(messageTemplates.contains("{user.wrong.ident}"));
    assertTrue(messageTemplates.contains("{user.wrong.email}"));
  }
  
  private List<ValidationError> getValidationErrorList(final Response response) {
    return response.readEntity(new GenericType<List<ValidationError>>() {
    });
  }
  
  private Set<String> getValidationMessageTemplates(final Response response) {
    return getValidationMessageTemplates(getValidationErrorList(response));
  }
  
  private Set<String> getValidationMessageTemplates(final List<ValidationError> errors) {
    final Set<String> templates = new HashSet<>();
    for (final ValidationError error : errors) {
      templates.add(error.getMessageTemplate());
    }
    return templates;
  }
}
