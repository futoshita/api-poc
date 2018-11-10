package com.futoshita.api.server.service;

import com.futoshita.api.server.entity.User;
import com.futoshita.api.server.service.exception.NonUniqueException;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class StorageService {

    private static final AtomicLong userCounter = new AtomicLong(0);
    private static final Map<Long, User> users = new HashMap<Long, User>();

    /**
     * Adds a user into the storage. If a user with given data already exist
     * {@code null} value is returned.
     *
     * @param user user to be added.
     * @return user with pre-filled {@code id} field, {@code null} if the user
     * already exist in the storage.
     * @throws NonUniqueException
     */
    public static User addUser(final User user) throws NonUniqueException {
        if (users.containsValue(user)) {
            throw new NonUniqueException("User with email " + user.getEmail() + " already exists.");
        }

        user.setId(userCounter.incrementAndGet());
        users.put(user.getId(), user);

        return user;
    }

    /**
     * Removes all users from the storage.
     *
     * @return list of all removed users.
     */
    public static List<User> clear() {
        final Collection<User> values = users.values();
        users.clear();
        return new ArrayList<User>(values);
    }

    /**
     * Removes user with given {@code id}.
     *
     * @param id id of the user to be removed.
     * @return removed user or {@code null} if the user is not present in the
     * storage.
     */
    public static User remove(final Long id) {
        return users.remove(id);
    }

    /**
     * Retrieves user with given {@code id}.
     *
     * @param id id of the user to be retrieved.
     * @return user or {@code null} if the user is not present in the storage.
     */
    public static User get(final Long id) {
        return users.get(id);
    }

    /**
     * Finds users whose email contains {@code emailPart} as a substring.
     *
     * @param emailPart search phrase.
     * @return list of matched users or an empty list.
     */
    public static List<User> findByEmail(final String emailPart) {
        final List<User> results = new ArrayList<User>();

        for (final User user : users.values()) {
            final String email = user.getEmail();
            if (email != null && email.contains(emailPart)) {
                results.add(user);
            }
        }

        return results;
    }

    /**
     * Finds users whose ident contains {@code identPart} as a substring.
     *
     * @param identPart search phrase.
     * @return list of matched users or an empty list.
     */
    public static List<User> findByIdent(final String identPart) {
        final List<User> results = new ArrayList<User>();

        for (final User user : users.values()) {
            if (user.getIdent().contains(identPart)) {
                results.add(user);
            }
        }

        return results;
    }
}
