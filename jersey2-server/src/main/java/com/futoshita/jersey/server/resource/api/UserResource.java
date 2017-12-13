package com.futoshita.jersey.server.resource.api;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.futoshita.jersey.server.entity.User;
import com.futoshita.jersey.server.entity.constraint.HasId;
import com.futoshita.jersey.server.service.StorageService;
import com.futoshita.jersey.server.service.exception.NonUniqueException;

@Path("/users")
public class UserResource {
  
  private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
  
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @HasId
  public User addUser(@NotNull @Valid User user) throws NonUniqueException {
    return StorageService.addUser(user);
  }
  
  @GET
  @Path("/json")
  @NotNull
  @HasId
  @Produces(MediaType.APPLICATION_JSON)
  public List<User> getUsersJson() {
    return StorageService.findByIdent("");
  }
  
  @GET
  @Path("/xml")
  @NotNull
  @HasId
  @Produces(MediaType.APPLICATION_XML)
  public List<User> getUsersXml() {
    return StorageService.findByIdent("");
  }
  
  @GET
  @Path("{id}")
  @NotNull(message = "{user.does.not.exist}")
  @HasId
  public User getUser(
      @DecimalMin(value = "0", message = "{user.wrong.id}") @PathParam("id") final Long id) {
    return StorageService.get(id);
  }
  
  @DELETE
  @NotNull
  @HasId
  public List<User> deleteUsers() {
    return StorageService.clear();
  }
  
  @DELETE
  @Path("{id}")
  @NotNull(message = "{user.does.not.exist}")
  @HasId
  public User deleteUser(
      @DecimalMin(value = "0", message = "{user.wrong.id}") @PathParam("id") final Long id) {
    return StorageService.remove(id);
  }
  
}
