package com.futoshita.api.server.resource.api;

import com.futoshita.api.server.oauth.AccessTokenRequired;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;


@Path("/greeting")
public class GreetingResource {

    @GET
    @Path("/hello")
    @Produces("text/plain")
    public String hello() {
        return "Hello, world!";
    }


    @GET
    @Path("/hello/secured")
    @Produces("text/plain")
    @AccessTokenRequired
    public String helloSecured() {
        return "Hello, world! (secured)";
    }

}
