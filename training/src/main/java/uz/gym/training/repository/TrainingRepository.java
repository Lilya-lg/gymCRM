package uz.gym.training.repository;

import org.springframework.stereotype.Repository;
import uz.gym.training.domain.TrainingSession;
import uz.gym.training.repository.abstr.TrainingRepositoryInterface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class TrainingRepository implements TrainingRepositoryInterface {
  private final Map<String, List<TrainingSession>> trainingData = new ConcurrentHashMap<>();

  public void addTraining(String trainerUsername, TrainingSession session) {
    trainingData.computeIfAbsent(trainerUsername, k -> new ArrayList<>()).add(session);
  }

  public void removeTraining(String trainerUsername, TrainingSession session) {
    trainingData.getOrDefault(trainerUsername, new ArrayList<>()).remove(session);
  }

  public List<TrainingSession> getTrainingsByTrainer(String trainerUsername) {
    return trainingData.getOrDefault(trainerUsername, Collections.emptyList());
  }

  public boolean trainerExists(String trainerUsername) {

    return trainingData.containsKey(trainerUsername);
  }
}
