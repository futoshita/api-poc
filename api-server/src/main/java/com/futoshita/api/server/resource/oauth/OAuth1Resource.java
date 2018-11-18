package com.futoshita.api.server.resource.oauth;

import org.glassfish.jersey.server.mvc.Viewable;
import org.glassfish.jersey.server.oauth1.DefaultOAuth1Provider;
import org.glassfish.jersey.server.oauth1.OAuth1Provider;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.security.Principal;
import java.util.*;

@Path("/oauth")
public class OAuth1Resource {

    @Inject
    private OAuth1Provider provider;

    @GET
    @Path("/authorize")
    public String authorize(@QueryParam("oauth_token") String token) {
        DefaultOAuth1Provider dp = (DefaultOAuth1Provider) provider;

        Set<String> dummyRoles = new HashSet<>(Arrays.asList(new String[]{"my-role-1", "my-role-2"}));
        DefaultOAuth1Provider.Token tok1 = dp.getRequestToken(token);
        String verifier = dp.authorizeToken(tok1, new Principal() {
            public String getName() {
                return "my-user";
            }
        }, dummyRoles);

        return verifier;
    }

    @GET
    @Path("/signin")
    @Produces(MediaType.TEXT_HTML)
    public Viewable getSignin() {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("postSigninUrl", "http://localhost:8080/api-server/api/oauth/check-signin");
        parameters.put("oauthToken", "token");
        parameters.put("forgotPasswordUrl", "http://localhost:8080/api-server/api/password/forgot-password");

        return new Viewable("/signin.jsp", parameters);
    }

    @POST
    @Path("/check-signin")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public Viewable postSignin(@FormParam("authorize") String authorization, @FormParam("oauth_token") String token, @FormParam("username") String ident, @FormParam("password") String password) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("authorization", authorization);
        parameters.put("token", token);
        parameters.put("ident", ident);
        parameters.put("password", password);

        return new Viewable("/check-signin.jsp", parameters);
    }

}
