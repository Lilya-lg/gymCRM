package uz.gym.training.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.gym.training.dto.TrainerSummaryDTO;
import uz.gym.training.dto.TrainingSessionDTO;
import uz.gym.training.service.TrainingService;

@RestController
@RequestMapping("/api/trainings")
public class TrainerWorkloadController {
    private final TrainingService service;

    public TrainerWorkloadController(TrainingService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<String> processTraining(@RequestBody TrainingSessionDTO sessionDTO) {
        service.addOrDeleteTraining(sessionDTO);
        return ResponseEntity.ok("Processed");
    }

    @GetMapping("/{trainerUsername}/summary")
    public ResponseEntity<TrainerSummaryDTO> getTrainerSummary(@PathVariable String trainerUsername) {
         try {
            TrainerSummaryDTO summary = service.getMonthlySummary(trainerUsername);

            if (summary == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(summary);

        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 🔥 Now returns 500 properly
        }
    }
}
