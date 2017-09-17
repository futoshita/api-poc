package com.futoshita.jersey.server.resource.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.futoshita.jersey.server.oauth.AccessTokenRequired;


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
