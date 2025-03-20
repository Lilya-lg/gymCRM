package uz.micro.gym.controller;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.micro.gym.domain.Trainee;
import uz.micro.gym.domain.Trainer;
import uz.micro.gym.dto.*;
import uz.micro.gym.mapper.Mapper;
import uz.micro.gym.metrics.MetricsService;
import uz.micro.gym.service.abstr.TraineeService;

import java.util.List;


@RestController
@RequestMapping(value = "/api/trainees", produces = {"application/json", "application/xml"})
public class TraineeController {
    private final TraineeService traineeService;
    private final Mapper mapper;
    private final MetricsService metricsService;
    private final Counter getProfileCounter;
    private final Timer createTraineeTimer;

    public TraineeController(TraineeService traineeService, Mapper mapper, MetricsService metricsService) {
        this.traineeService = traineeService;
        this.mapper = mapper;
        this.metricsService = metricsService;
        this.getProfileCounter = metricsService.createCounter("trainee_profile_requests", "endpoint", "/api/trainees/profiles/{username}");
        this.createTraineeTimer = metricsService.createTimer("create_trainee_timer", "endpoint", "/api/trainees");
    }

    @GetMapping("/profiles/{username}")
    public ResponseEntity<TraineeProfileDTO> getTraineeProfile(@PathVariable String username) {
        getProfileCounter.increment();
        TraineeProfileDTO traineeProfile = traineeService.getTraineeProfile(username);
        return ResponseEntity.ok(traineeProfile);
    }

    @PostMapping
    public ResponseEntity<BaseUserDTO> createTrainee(@RequestBody @Valid TraineeProfileDTO traineeDTO) {
        long startTime = System.nanoTime();
        Trainee trainee = mapper.toTrainee(traineeDTO);
        traineeService.create(trainee);
        String password = traineeService.putPassword(trainee.getUser());
        BaseUserDTO userDTO = new BaseUserDTO(trainee.getUser().getUsername(), password);
        metricsService.recordTimer(createTraineeTimer, startTime);
        return ResponseEntity.ok(userDTO);
    }

    @PutMapping(value = "/update-profile", consumes = "application/json", produces = "application/json")
    public ResponseEntity<TraineeProfileDTO> updateTraineeProfile(@Valid @RequestBody TraineeUpdateDTO traineeDTO) {
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
    public ResponseEntity<List<TrainerDTO>> updateTraineeTrainers(@Valid @RequestBody UpdateTraineeTrainersDTO updateDTO) {
        List<Trainer> trainers = traineeService.updateTraineeTrainerList(updateDTO.getTraineeUsername(), updateDTO.getTrainingId(), updateDTO.getTrainerUsernames());
        List<TrainerDTO> trainersDTO = mapper.mapTrainersToProfileDTOs(trainers);
        return ResponseEntity.ok(trainersDTO);
    }
}
