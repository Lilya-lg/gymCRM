package uz.gym.crm.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.gym.crm.domain.Trainer;
import uz.gym.crm.dto.*;
import uz.gym.crm.dto.abstr.BaseTrainerDTO;
import uz.gym.crm.mapper.Mapper;
import uz.gym.crm.service.abstr.TrainerService;
import java.util.List;

@RestController
@RequestMapping(value = "/api/trainers", produces = {"application/json", "application/xml"})
public class TrainerController {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrainerController.class);

    private final TrainerService trainerService;
    private final Mapper mapper;

    public TrainerController(TrainerService trainerService, Mapper mapper) {
        this.trainerService = trainerService;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<BaseUserDTO> createTrainer(@Valid @RequestBody TrainerDTO trainerDTO) {
        Trainer trainer = mapper.toTrainer(trainerDTO);
        trainerService.create(trainer);
        BaseUserDTO userDTO = new BaseUserDTO(trainer.getUser().getUsername(), trainer.getUser().getPassword());
        return ResponseEntity.ok(userDTO);
    }

    @PutMapping(value = "/update-profile", consumes = "application/json", produces = "application/json")
    public ResponseEntity<TrainerProfileResponseDTO> updateTrainerProfile(@Valid @RequestBody TrainerProfileDTO trainerDTO) {
        trainerService.updateTrainerProfile(trainerDTO.getUsername(), trainerDTO);
        TrainerProfileResponseDTO trainerProfile = trainerService.getTrainerProfileResponse(trainerDTO.getUsername());
        return ResponseEntity.ok(trainerProfile);
    }

    @GetMapping("/profiles/{username}")
    public ResponseEntity<BaseTrainerDTO> getTrainerProfile(@PathVariable String username) {
        BaseTrainerDTO trainerProfile = trainerService.getTrainerProfile(username);
        return ResponseEntity.ok(trainerProfile);
    }

    @GetMapping(value = "/unassigned-active-trainers")
    public ResponseEntity<List<TrainerDTO>> getUnassignedTrainee(@RequestParam String username) {
        List<TrainerDTO> trainers = mapper.mapTrainersToProfileDTOs(trainerService.getUnassignedTrainersForTrainee(username));
        return ResponseEntity.ok(trainers);
    }
}
