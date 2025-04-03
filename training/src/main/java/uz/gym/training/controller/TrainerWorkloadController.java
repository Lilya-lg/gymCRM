package uz.gym.training.controller;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.gym.training.dto.TrainerSummaryDTO;
import uz.gym.crm.dto.TrainingSessionDTO;
import uz.gym.training.service.TrainingService;

import java.util.Map;

@RestController
@RequestMapping("/api/trainings")
public class TrainerWorkloadController {
  private final TrainingService service;
  private static final String ACTION_ADD = "ADD";
  private static final String ACTION_DELETE = "DELETE";

  public TrainerWorkloadController(TrainingService service) {
    this.service = service;
  }

  @PostMapping
  public ResponseEntity<Map<String, String>> processTraining(
      @RequestBody TrainingSessionDTO sessionDTO) {
    if (ACTION_ADD.equalsIgnoreCase(sessionDTO.getActionType())) {
      service.addTraining(sessionDTO);
      return ResponseEntity.ok(Map.of("status", "success", "message", "Training session added"));
    } else if (ACTION_DELETE.equalsIgnoreCase(sessionDTO.getActionType())) {
      service.deleteTraining(sessionDTO);
      return ResponseEntity.ok(Map.of("status", "success", "message", "Training session deleted"));
    } else {
      return ResponseEntity.badRequest()
          .body(Map.of("status", "error", "message", "Invalid action type"));
    }
  }

  @GetMapping("/{trainerUsername}/summary")
  public ResponseEntity<TrainerSummaryDTO> getTrainerSummary(@PathVariable String trainerUsername) {
    TrainerSummaryDTO summary = service.getMonthlySummary(trainerUsername);

    if (summary == null) {
      throw new EntityNotFoundException("Trainer not found: " + trainerUsername);
    }
    return ResponseEntity.ok(summary);
  }
}
