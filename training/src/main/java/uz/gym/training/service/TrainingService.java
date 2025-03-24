package uz.gym.training.service;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import uz.gym.training.domain.Trainer;
import uz.gym.training.domain.TrainingSession;
import uz.gym.training.dto.MonthSummaryDTO;
import uz.gym.training.dto.TrainerSummaryDTO;
import uz.gym.training.dto.TrainingSessionDTO;
import uz.gym.training.repository.TrainerRepository;
import uz.gym.training.repository.TrainingSessionRepository;
import uz.gym.training.service.abstr.BaseService;

import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TrainingService implements BaseService {
    private final TrainingSessionRepository repository;
    private final TrainerRepository trainerRepository;

    public TrainingService(TrainingSessionRepository repository, TrainerRepository trainerRepository) {
        this.repository = repository;
        this.trainerRepository = trainerRepository;
    }


    public void addTraining(TrainingSessionDTO sessionDTO) {
        String transactionId = MDC.get("transactionId");
        LOGGER.info("Transaction ID: {} | Starting training session for trainer ID: {}", transactionId, sessionDTO.getUsername());

        Trainer trainer = trainerRepository.findByUsername(sessionDTO.getUsername()).orElse(null);
        if (trainer == null) {
            Trainer newTrainer = new Trainer();
            newTrainer.setId(trainerIdCounter.getAndIncrement()); // Assign a unique ID
            newTrainer.setUsername(sessionDTO.getUsername().trim().toLowerCase());
            newTrainer.setFirstName(sessionDTO.getFirstName());
            newTrainer.setLastName(sessionDTO.getLastName());
            newTrainer.setActive(true);
            trainer = trainerRepository.save(newTrainer);
            LOGGER.info("Trainer saved: {}", newTrainer.getUsername());
        }
        TrainingSession session = new TrainingSession();
        session.setId(sessionIdCounter.getAndIncrement()); // Assign a unique session ID
        session.setTrainer(trainer);
        session.setTrainingDate(sessionDTO.getTrainingDate());
        session.setDuration(sessionDTO.getDuration());
        LOGGER.info("Saved trainer session");
        repository.save(session);
        LOGGER.info("Training session saved for trainer: {}", trainer.getUsername());
    }

    public void deleteTraining(TrainingSessionDTO sessionDTO) {
        String transactionId = MDC.get("transactionId");
        LOGGER.info("Transaction ID: {} | Deleting training session for trainer: {}", transactionId, sessionDTO.getUsername());

        Trainer trainer = trainerRepository.findByUsername(sessionDTO.getUsername()).orElseThrow(() -> new EntityNotFoundException("Trainer not found: " + sessionDTO.getUsername()));
        repository.findAll().stream().filter(session -> session.getTrainer().equals(trainer)).findFirst().ifPresentOrElse(session -> {
            repository.deleteById(session.getId());
            LOGGER.info("Training session deleted for trainer: {}", trainer.getUsername());
        }, () -> {
            throw new EntityNotFoundException("Training session not found for trainer: " + trainer.getUsername());
        });
    }


    public TrainerSummaryDTO getMonthlySummary(String trainerUsername) {
        LOGGER.info("Fetching monthly summary for trainer: {}", trainerUsername);
        List<Trainer> allTrainers = trainerRepository.findAll();
        LOGGER.info("All stored trainers: {}", allTrainers.stream().map(Trainer::getUsername).toList());

        Optional<Trainer> optionalTrainer = trainerRepository.findByUsername(trainerUsername);

        if (optionalTrainer.isEmpty()) {
            LOGGER.warn("Trainer not found: {}", trainerUsername);
            return null;
        }

        Trainer trainer = optionalTrainer.get();
        List<TrainingSession> sessions = repository.findByTrainer(trainer);

        if (sessions.isEmpty()) {
            LOGGER.warn("No training sessions found for trainer: {}", trainerUsername);
            return new TrainerSummaryDTO(trainer.getUsername(), trainer.getFirstName(), trainer.getLastName(), String.valueOf(trainer.isActive()), Collections.emptyList(), Collections.emptyMap());
        }

        Map<Integer, Map<Month, Integer>> trainingSummary = sessions.stream().filter(session -> session.getTrainingDate() != null).collect(Collectors.groupingBy(session -> session.getTrainingDate().getYear(), Collectors.groupingBy(session -> session.getTrainingDate().getMonth(), Collectors.summingInt(TrainingSession::getDuration))));

        List<Integer> years = new ArrayList<>(trainingSummary.keySet());
        Collections.sort(years);

        Map<Integer, List<MonthSummaryDTO>> monthlySummaries = new HashMap<>();
        for (var entry : trainingSummary.entrySet()) {
            Integer year = entry.getKey();
            List<MonthSummaryDTO> monthSummaries = entry.getValue().entrySet().stream().map(monthEntry -> new MonthSummaryDTO(monthEntry.getKey(), monthEntry.getValue())).sorted(Comparator.comparing(MonthSummaryDTO::getMonth)).collect(Collectors.toList());
            monthlySummaries.put(year, monthSummaries);
        }

        LOGGER.info("Monthly summary generated successfully for trainer: {}", trainerUsername);
        return new TrainerSummaryDTO(trainer.getUsername(), trainer.getFirstName(), trainer.getLastName(), String.valueOf(trainer.isActive()), years, monthlySummaries);
    }
}