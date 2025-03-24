package uz.gym.training.repository;

import org.springframework.stereotype.Repository;
import uz.gym.training.domain.Trainer;
import uz.gym.training.domain.TrainingSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class TrainingSessionRepository{
    private final Map<Long, TrainingSession> sessions = new ConcurrentHashMap<>();

    public Optional<TrainingSession> findById(Long id) {
        return Optional.ofNullable(sessions.get(id));
    }

    public List<TrainingSession> findAll() {
        return new ArrayList<>(sessions.values());
    }

    public List<TrainingSession> findByTrainer(Trainer trainer) {
        return sessions.values().stream().filter(s -> s.getTrainer().equals(trainer)).collect(Collectors.toList());
    }

    public TrainingSession save(TrainingSession session) {
        sessions.put(session.getId(), session);
        return session;
    }

    public void deleteById(Long id) {
        sessions.remove(id);
    }
}
