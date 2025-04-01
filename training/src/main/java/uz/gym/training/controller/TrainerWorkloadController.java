package uz.gym.training.controller;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.gym.training.dto.TrainerSummaryDTO;
import uz.gym.training.dto.TrainingSessionDTO;
import uz.gym.training.service.TrainingService;

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
  public ResponseEntity<String> processTraining(@RequestBody TrainingSessionDTO sessionDTO) {
    if (ACTION_ADD.equalsIgnoreCase(sessionDTO.getActionType())) {
      service.addTraining(sessionDTO);
    } else if (ACTION_DELETE.equalsIgnoreCase(sessionDTO.getActionType())) {
      service.deleteTraining(sessionDTO);
    } else {
      return ResponseEntity.badRequest().body("Invalid action type");
    }
    return ResponseEntity.ok().build();
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
