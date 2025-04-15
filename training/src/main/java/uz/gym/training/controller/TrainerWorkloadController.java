package uz.gym.training.controller;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.gym.training.dto.TrainerSummaryDTO;

import uz.gym.training.service.TrainerSummaryService;

@RestController
@RequestMapping("/api/trainings")
public class TrainerWorkloadController {
  private final TrainerSummaryService trainerSummaryService;
  private static final String ACTION_ADD = "ADD";
  private static final String ACTION_DELETE = "DELETE";

  public TrainerWorkloadController(TrainerSummaryService trainerSummaryService) {
    this.trainerSummaryService = trainerSummaryService;
  }

  @GetMapping("/{trainerUsername}/summary")
  public ResponseEntity<TrainerSummaryDTO> getTrainerSummary(@PathVariable String trainerUsername) {
    TrainerSummaryDTO summary = trainerSummaryService.getTrainerSummary(trainerUsername);

    if (summary == null) {
      throw new EntityNotFoundException("Trainer not found: " + trainerUsername);
    }
    return ResponseEntity.ok(summary);
  }
}
