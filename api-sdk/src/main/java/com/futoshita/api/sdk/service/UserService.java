package com.futoshita.api.sdk.service;

import com.futoshita.api.sdk.ClientApp;
import com.futoshita.api.sdk.entity.User;

import javax.ws.rs.core.MediaType;
import java.net.URI;
import java.util.List;

public class UserService extends AncestorService {

    private final static String RESOURCE_URI = "/users";

    public static User get(Integer id, MediaType... mediaTypes) throws Exception {
        return get(ClientApp.getInstance().getClientAppProperties().getApiBaseUrl(), RESOURCE_URI + "/" + id, User.class, mediaTypes);
    }

    public static List<User> getList() {
        return null;
    }

    public static URI create(User user) throws Exception {
        return post(ClientApp.getInstance().getClientAppProperties().getApiBaseUrl(), RESOURCE_URI, user);
    }

    public static User update(User user) throws Exception {
        return put(ClientApp.getInstance().getClientAppProperties().getApiBaseUrl(), RESOURCE_URI, user, User.class);
    }

    public static void delete(Integer id) throws Exception {
        delete(ClientApp.getInstance().getClientAppProperties().getApiBaseUrl(), RESOURCE_URI + "/" + id);;
    }
}
