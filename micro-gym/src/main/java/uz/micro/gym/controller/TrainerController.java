package uz.micro.gym.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.micro.gym.domain.Trainer;
import uz.micro.gym.dto.BaseUserDTO;
import uz.micro.gym.dto.TrainerDTO;
import uz.micro.gym.dto.TrainerProfileDTO;
import uz.micro.gym.dto.TrainerProfileResponseDTO;
import uz.micro.gym.dto.abstr.BaseTrainerDTO;
import uz.micro.gym.mapper.Mapper;
import uz.micro.gym.metrics.MetricsService;
import uz.micro.gym.service.abstr.TrainerService;
import uz.micro.gym.metrics.MetricsService;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping(value = "/api/trainers", produces = {"application/json", "application/xml"})
public class TrainerController {

    private final TrainerService trainerService;
    private final Mapper mapper;
    private final MetricsService metricsService;
    private final AtomicInteger activeRequests = new AtomicInteger(0);

    public TrainerController(TrainerService trainerService, Mapper mapper, MetricsService metricsService) {
        this.trainerService = trainerService;
        this.mapper = mapper;
        this.metricsService = metricsService;
        metricsService.createGauge("trainer_active_requests", activeRequests, "Number of active trainer requests");
    }

    @PostMapping
    public ResponseEntity<BaseUserDTO> createTrainer(@Valid @RequestBody TrainerDTO trainerDTO) {
        Trainer trainer = mapper.toTrainer(trainerDTO);
        trainerService.create(trainer);
        String password = trainerService.putPassword(trainer.getUser());
        BaseUserDTO userDTO = new BaseUserDTO(trainer.getUser().getUsername(), password);
        return ResponseEntity.ok(userDTO);
    }

    @PutMapping(value = "/update-profile", consumes = "application/json", produces = "application/json")
    public ResponseEntity<TrainerProfileResponseDTO> updateTrainerProfile(@Valid @RequestBody TrainerProfileDTO trainerDTO) {
        activeRequests.incrementAndGet();
        try {
            trainerService.updateTrainerProfile(trainerDTO.getUsername(), trainerDTO);
            TrainerProfileResponseDTO trainerProfile = trainerService.getTrainerProfileResponse(trainerDTO.getUsername());
            return ResponseEntity.ok(trainerProfile);
        } finally {
            activeRequests.decrementAndGet();
        }

    }

    @GetMapping("/profiles/{username}")
    public ResponseEntity<BaseTrainerDTO> getTrainerProfile(@PathVariable String username) {
        BaseTrainerDTO trainerProfile = trainerService.getTrainerProfile(username);
        return ResponseEntity.ok(trainerProfile);
    }

    @GetMapping(value = "/unassigned-active-trainers")
    public ResponseEntity<List<TrainerDTO>> getUnassignedTrainee(@RequestParam String username) {
        activeRequests.incrementAndGet();
        try {
            List<TrainerDTO> trainers = mapper.mapTrainersToProfileDTOs(trainerService.getUnassignedTrainersForTrainee(username));
            return ResponseEntity.ok(trainers);
        } finally {
            activeRequests.decrementAndGet();
        }
    }
}
