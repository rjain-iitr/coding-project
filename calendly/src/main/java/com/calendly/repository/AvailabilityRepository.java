package com.calendly.repository;

import com.calendly.model.Availability;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AvailabilityRepository {
    private List<Availability> availabilities = new ArrayList<>();

    public Optional<Availability> findById(String id) {
        return availabilities.stream().filter(a -> a.getId().equals(id)).findFirst();
    }

    public List<Availability> findByUserId(String userId) {
        return availabilities.stream().filter(a -> a.getUserId().equals(userId)).collect(Collectors.toList());
    }

    public Availability save(Availability availability) {
        availabilities.add(availability);
        return availability;
    }

    public void deleteById(String id) {
        availabilities.removeIf(a -> a.getId().equals(id));
    }

    public List<Availability> findAll() {
        return new ArrayList<>(availabilities);
    }
}