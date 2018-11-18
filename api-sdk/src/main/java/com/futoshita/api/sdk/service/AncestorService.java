package com.futoshita.api.sdk.service;

import com.futoshita.api.sdk.ClientApp;
import com.futoshita.api.sdk.entity.ErrorRepresentation;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;

public abstract class AncestorService {

    protected static <T> T get(String uri, String path, Class<T> clazz, MediaType... mediaTypes) throws Exception {
        MediaType mt = mediaTypes.length != 0 ? mediaTypes[0] : MediaType.APPLICATION_JSON_TYPE;

        WebTarget webTarget = ClientBuilder.newClient(ClientApp.ClientConfigBuidler.getInstance().getClientConfig())
                .target(uri);

        webTarget = path != null ? webTarget.path(path) : webTarget;

        Invocation.Builder invocationBuilder = webTarget.request(mt);

        if (ClientApp.getInstance().getClientAppProperties().getLocale() != null)
            invocationBuilder.acceptLanguage(ClientApp.getInstance().getClientAppProperties().getLocale());

        Response response = invocationBuilder.get();

        checkStatus(response, 200);

        return response.readEntity(clazz);
    }

    protected static <T> T getSecured(String uri, String path, Class<T> clazz, MediaType... mediaTypes) throws Exception {
        MediaType mt = mediaTypes.length != 0 ? mediaTypes[0] : MediaType.APPLICATION_JSON_TYPE;

        WebTarget webTarget = ClientBuilder.newClient(ClientApp.SecuredClientConfigBuidler.getInstance().getClientConfig())
                .target(uri);

        webTarget = path != null ? webTarget.path(path) : webTarget;

        Response response = webTarget
                .request()
                .accept(mt)
                .get();

        checkStatus(response, 200);

        return response.readEntity(clazz);
    }

    protected static <T> URI post(String uri, String path, T entity, MediaType... mediaTypes) throws Exception {
        MediaType mt = mediaTypes.length != 0 ? mediaTypes[0] : MediaType.APPLICATION_JSON_TYPE;

        WebTarget webTarget = ClientBuilder.newClient(ClientApp.ClientConfigBuidler.getInstance().getClientConfig())
                .target(uri);

        webTarget = path != null ? webTarget.path(path) : webTarget;

        Response response = webTarget
                .request()
                .accept(mt)
                .post(Entity.entity(entity, mt));

        checkStatus(response, 201);

        return response.getLocation();
    }

    protected static <T> T put(String uri, String path, T entity, Class<T> clazz, MediaType... mediaTypes) throws Exception {
        MediaType mt = mediaTypes.length != 0 ? mediaTypes[0] : MediaType.APPLICATION_JSON_TYPE;

        WebTarget webTarget = ClientBuilder.newClient(ClientApp.ClientConfigBuidler.getInstance().getClientConfig())
                .target(uri);

        webTarget = path != null ? webTarget.path(path) : webTarget;

        Response response = webTarget
                .request()
                .accept(mt)
                .put(Entity.entity(entity, mt));

        checkStatus(response, 200);

        return response.readEntity(clazz);
    }

    protected static <T> void delete(String uri, String path, MediaType... mediaTypes) throws Exception {
        MediaType mt = mediaTypes.length != 0 ? mediaTypes[0] : MediaType.APPLICATION_JSON_TYPE;

        WebTarget webTarget = ClientBuilder.newClient(ClientApp.ClientConfigBuidler.getInstance().getClientConfig())
                .target(uri);

        webTarget = path != null ? webTarget.path(path) : webTarget;

        Response response = webTarget
                .request()
                .accept(mt)
                .delete();

        checkStatus(response, 200);
    }

    private static void checkStatus(Response response, int expectedStatus) throws Exception {
        if (response.getStatus() != expectedStatus) {
            String errorMessage = null;

            if (response.hasEntity()) {
                try {
                    if (response.getMediaType().equals(MediaType.APPLICATION_JSON_TYPE) || response.getMediaType().equals(MediaType.APPLICATION_XML_TYPE)) {
                        List<ErrorRepresentation> errors = response.readEntity(new GenericType<List<ErrorRepresentation>>() {

                        });
                        errorMessage = errors.get(0).getMessage();
                    } else {
                        errorMessage = response.readEntity(String.class);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                // TODO
            }

            throw new Exception("status code: " + response.getStatus() + ", reason: "
                    + response.getStatusInfo().getReasonPhrase() + ", cause: " + errorMessage);
        }
    }
}
