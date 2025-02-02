package uz.gym.crm.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.gym.crm.domain.Trainee;
import uz.gym.crm.domain.Trainer;
import uz.gym.crm.dto.*;
import uz.gym.crm.mapper.Mapper;
import uz.gym.crm.service.abstr.TraineeService;
import java.util.List;

@RestController
@RequestMapping(value = "/api/trainees", produces = {"application/json", "application/xml"})
public class TraineeController {
    private static final Logger LOGGER = LoggerFactory.getLogger(TraineeController.class);

    private final TraineeService traineeService;
    private final Mapper mapper;

    public TraineeController(TraineeService traineeService, Mapper mapper) {
        this.traineeService = traineeService;
        this.mapper = mapper;
    }

    @GetMapping("/profiles/{username}")
    public ResponseEntity<TraineeProfileDTO> getTraineeProfile(@PathVariable String username) {
        TraineeProfileDTO traineeProfile = traineeService.getTraineeProfile(username);
        return ResponseEntity.ok(traineeProfile);
    }

    @PostMapping
    public ResponseEntity<BaseUserDTO> createTrainee(@RequestBody @Valid TraineeProfileDTO traineeDTO) {
        Trainee trainee = mapper.toTrainee(traineeDTO);
        traineeService.create(trainee);
        BaseUserDTO userDTO = new BaseUserDTO(trainee.getUser().getUsername(), trainee.getUser().getPassword());
        return ResponseEntity.ok(userDTO);
    }

    @PutMapping(value = "/update-profile", consumes = "application/json", produces = "application/json")
    public ResponseEntity<TraineeProfileDTO> updateTraineeProfile(
            @Valid @RequestBody TraineeUpdateDTO traineeDTO) {
        traineeService.updateTraineeProfile(traineeDTO.getUsername(), traineeDTO);
        TraineeProfileDTO traineeProfile = traineeService.getTraineeProfile(traineeDTO.getUsername());
        TraineeProfileDTO response = traineeProfile;
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<String> deleteTraineeProfile(@PathVariable String username) {
        traineeService.deleteProfileByUsername(username);
        return ResponseEntity.ok("Trainee profile deleted successfully");
    }

    @PatchMapping(value = "/update-trainers", consumes = "application/json", produces = "application/json")
    public ResponseEntity<List<TrainerDTO>> updateTraineeTrainers(
            @Valid @RequestBody UpdateTraineeTrainersDTO updateDTO) {
        List<Trainer> trainers = traineeService.updateTraineeTrainerList(updateDTO.getTraineeUsername(), updateDTO.getTrainingId(), updateDTO.getTrainerUsernames());
        List<TrainerDTO> trainersDTO = mapper.mapTrainersToProfileDTOs(trainers);
        return ResponseEntity.ok(trainersDTO);
    }
}
