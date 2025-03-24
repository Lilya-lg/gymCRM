package uz.gym.training.repository;

import org.springframework.stereotype.Repository;
import uz.gym.training.domain.Trainer;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
@Repository
public class TrainerRepository {
    private final Map<Long, Trainer> trainers = new ConcurrentHashMap<>();

    public Optional<Trainer> findById(Long id) {
        return trainers.values().stream().filter(t -> t.getId().equals(id)).findFirst();
    }

    public Optional<Trainer> findByUsername(String username) {
        if (username == null || username.isBlank()) {
            return Optional.empty();
        }
        String normalizedUsername = username.trim().toLowerCase();
        return trainers.values().stream()
                .filter(trainer -> trainer.getUsername().trim().toLowerCase().equals(normalizedUsername))
                .findFirst();
    }

    public List<Trainer> findAll() {
        return new ArrayList<>(trainers.values());
    }

    public Trainer save(Trainer trainer) {
        trainers.put(trainer.getId(), trainer);
        return trainer;
    }

    public void deleteById(Long id) {
        trainers.entrySet().removeIf(entry -> entry.getValue().getId().equals(id));
    }
}