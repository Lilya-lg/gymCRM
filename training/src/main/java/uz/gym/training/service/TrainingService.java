package uz.gym.training.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import uz.gym.training.domain.Trainer;
import uz.gym.training.domain.TrainingSession;
import uz.gym.training.dto.MonthSummaryDTO;
import uz.gym.training.dto.TrainerSummaryDTO;
import uz.gym.training.dto.TrainingSessionDTO;
import uz.gym.training.repository.TrainerRepository;
import uz.gym.training.repository.TrainingSessionRepository;

import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TrainingService {
    private final TrainingSessionRepository repository;
    private final TrainerRepository trainerRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(TrainingService.class);

    public TrainingService(TrainingSessionRepository repository, TrainerRepository trainerRepository) {
        this.repository = repository;
        this.trainerRepository = trainerRepository;
    }

    @CircuitBreaker(name = "trainingService", fallbackMethod = "fallbackGetTrainingSessions")
    public void addOrDeleteTraining(TrainingSessionDTO sessionDTO) {
        String transactionId = MDC.get("transactionId");
        LOGGER.info("Transaction ID: {} | Starting training session for trainer ID: {}", transactionId, sessionDTO.getUsername());
        Trainer trainer = trainerRepository.findByUsername(sessionDTO.getUsername()).orElseGet(() -> {
            Trainer newTrainer = new Trainer();
            newTrainer.setUsername(sessionDTO.getUsername());
            newTrainer.setFirstName(sessionDTO.getFirstName());
            newTrainer.setLastName(sessionDTO.getLastName());
            newTrainer.setActive(true);
            return trainerRepository.save(newTrainer);
        });

        if ("ADD".equalsIgnoreCase(sessionDTO.getActionType())) {
            TrainingSession session = new TrainingSession();
            session.setTrainer(trainer);
            session.setTrainingDate(sessionDTO.getTrainingDate());
            session.setDuration(sessionDTO.getDuration());
            repository.save(session);
        } else if ("DELETE".equalsIgnoreCase(sessionDTO.getActionType())) {
        }
    }

    public void fallbackGetTrainingSessions(TrainingSessionDTO trainingSession, Throwable t) {
        LOGGER.info("Training Service Circuit Breaker activated!");
        LOGGER.info("Error: " + t.getMessage());
    }

    public TrainerSummaryDTO getMonthlySummary(String trainerUsername) {
        Optional<Trainer> optionalTrainer = trainerRepository.findByUsername(trainerUsername);
        if (optionalTrainer.isEmpty()) {
            return null;
        }

        Trainer trainer = optionalTrainer.get();

        List<TrainingSession> sessions = repository.findByTrainer(trainer);


        Map<Integer, Map<Month, Integer>> trainingSummary = sessions.stream().filter(session -> session.getTrainingDate() != null).collect(Collectors.groupingBy(session -> session.getTrainingDate().getYear(), Collectors.groupingBy(session -> session.getTrainingDate().getMonth(), Collectors.summingInt(TrainingSession::getDuration))));


        List<Integer> years = new ArrayList<>(trainingSummary.keySet());
        Collections.sort(years);


        Map<Integer, List<MonthSummaryDTO>> monthlySummaries = new HashMap<>();
        for (var entry : trainingSummary.entrySet()) {
            Integer year = entry.getKey();
            List<MonthSummaryDTO> monthSummaries = entry.getValue().entrySet().stream().map(monthEntry -> new MonthSummaryDTO(monthEntry.getKey(), monthEntry.getValue())).sorted(Comparator.comparing(MonthSummaryDTO::getMonth)).collect(Collectors.toList());
            monthlySummaries.put(year, monthSummaries);
        }

        return new TrainerSummaryDTO(trainer.getUsername(), trainer.getFirstName(), trainer.getLastName(), String.valueOf(trainer.isActive()), years, monthlySummaries);
    }

}