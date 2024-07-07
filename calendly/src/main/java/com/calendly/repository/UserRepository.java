package com.calendly.repository;

import com.calendly.model.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserRepository {
    private List<User> users = new ArrayList<>();

    public Optional<User> findById(String id) {
        return users.stream().filter(user -> user.getId().equals(id)).findFirst();
    }

    public User save(User user) {
        users.add(user);
        return user;
    }

    public void deleteById(String id) {
        users.removeIf(user -> user.getId().equals(id));
    }

    public List<User> findAll() {
        return new ArrayList<>(users);
    }
}