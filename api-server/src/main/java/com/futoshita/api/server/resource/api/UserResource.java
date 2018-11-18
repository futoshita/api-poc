package com.futoshita.api.server.resource.api;

import com.futoshita.api.server.entity.User;
import com.futoshita.api.server.entity.constraint.HasId;
import com.futoshita.api.server.entity.constraint.UniqueUser;
import com.futoshita.api.server.service.StorageService;
import com.futoshita.api.server.service.exception.NonUniqueException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@Path("/users")
public class UserResource {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    //@HasId
    public Response addUser(@NotNull @Valid @UniqueUser User user) throws URISyntaxException, NonUniqueException {
        URI uri;

        try {
            User newUser = StorageService.addUser(user);
            uri = new URI("/api/users/" + newUser.getId());
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        }

        return Response.created(uri).build();
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
    @NotNull(message = "{user.not.found}")
    @HasId
    public User getUser(
            @DecimalMin(value = "0", message = "{user.id.wrong.format}") @PathParam("id") final Long id) {
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
    @NotNull(message = "{user.not.found}")
    @HasId
    public User deleteUser(
            @DecimalMin(value = "0", message = "{user.id.wrong.format}") @PathParam("id") final Long id) {
        return StorageService.remove(id);
    }

}
