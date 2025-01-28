package uz.gym.crm.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.gym.crm.domain.Trainee;
import uz.gym.crm.domain.Trainer;
import uz.gym.crm.dto.*;
import uz.gym.crm.mapper.Mapper;
import uz.gym.crm.service.abstr.TraineeService;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping(value = "/api/trainees", produces = {"application/JSON", "application/XML"})
public class TraineeController {
    private static final Logger LOGGER = LoggerFactory.getLogger(TraineeController.class);

    @Autowired
    private TraineeService traineeService;
    @Autowired
    private final Mapper mapper;

    @Autowired
    public TraineeController(TraineeService traineeService, Mapper mapper) {
        this.traineeService = traineeService;
        this.mapper = mapper;
    }

    @GetMapping("/profiles/{username}")
    public ResponseEntity<TraineeProfileDTO> getTraineeProfile(
            @PathVariable("username") String username) {
        TraineeProfileDTO traineeProfile = traineeService.getTraineeProfile(username);
        return ResponseEntity.ok(traineeProfile);
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<?> createTrainee
            (@RequestBody @Valid TraineeProfileDTO traineeDTO) {
        Trainee trainee = mapper.toTrainee(traineeDTO);
        traineeService.create(trainee);
        BaseUserDTO userDTO = new BaseUserDTO(trainee.getUser().getUsername(), trainee.getUser().getPassword());
        return ResponseEntity.ok(userDTO);
    }

    @PutMapping(value = "/update-profile", consumes = "application/json", produces = "application/json")
    public ResponseEntity<ResponseWrapper<TraineeProfileDTO>> updateTraineeProfile(
            @Valid @RequestBody TraineeUpdateDTO traineeDTO) {
            traineeService.updateTraineeProfile(traineeDTO.getUsername(), traineeDTO);
            TraineeProfileDTO traineeProfile = traineeService.getTraineeProfile(traineeDTO.getUsername());
            ResponseWrapper<TraineeProfileDTO> response = new ResponseWrapper<>(traineeDTO.getUsername(), traineeProfile);
            return ResponseEntity.ok(response);
    }


    @DeleteMapping("/{username}")
    public ResponseEntity<String> deleteTraineeProfile(@PathVariable("username") String username) {
            traineeService.deleteProfileByUsername(username);
            return ResponseEntity.ok("Trainee profile deleted successfully");
    }

    @PutMapping(value = "/update-trainers", consumes = "application/json", produces = "application/json")
    public ResponseEntity<List<TrainerDTO>> updateTraineeTrainers(
            @RequestParam("traineeUsername") String traineeUsername,
            @Valid @RequestBody UpdateTraineeTrainersDTO updateDTO) {
            List<Trainer> trainers = traineeService.updateTraineeTrainerList(traineeUsername, updateDTO.getTrainerUsernames());
            List<TrainerDTO> trainersDTO = mapper.mapTrainersToProfileDTOs(trainers);
            return ResponseEntity.ok(trainersDTO);
    }



}
