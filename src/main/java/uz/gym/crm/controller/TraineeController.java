package uz.gym.crm.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import uz.gym.crm.domain.Trainee;
import uz.gym.crm.domain.Trainer;
import uz.gym.crm.dto.*;
import uz.gym.crm.mapper.Mapper;
import uz.gym.crm.service.abstr.TraineeService;

import javax.servlet.http.HttpServletRequest;
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

    @GetMapping("/profile")
    public ResponseEntity<TraineeProfileDTO> getTraineeProfile(
            @RequestParam("username") String username,
            HttpServletRequest request) {

        try {
            TraineeProfileDTO traineeProfile = traineeService.getTraineeProfile(username);
            return ResponseEntity.ok(traineeProfile);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/new")
    @ResponseBody
    public ResponseEntity<?> createTrainee
            (@RequestBody @Valid TraineeProfileDTO traineeDTO, BindingResult result) {
        if (result.hasErrors()) {
            result.getFieldErrors().forEach(error -> {
                LOGGER.error("Field: {}, Error: {}", error.getField(), error.getDefaultMessage());
            });
            StringBuilder errorMessage = new StringBuilder();
            for (FieldError error : result.getFieldErrors()) {
                errorMessage.append(error.getField())
                        .append(": ")
                        .append(error.getDefaultMessage())
                        .append("; ");
            }
            return ResponseEntity.badRequest().body(errorMessage.toString().trim());
        }
        Trainee trainee = mapper.toTrainee(traineeDTO);
        LOGGER.info("Mapped Trainee: {}", trainee);
        LOGGER.info("Mapped User: {}", trainee.getUser());
        traineeService.create(trainee);
        BaseUserDTO userDTO = new BaseUserDTO(trainee.getUser().getUsername(), trainee.getUser().getPassword());
        return ResponseEntity.ok(userDTO);
    }

    @PutMapping(value = "/update-profile", consumes = "application/json", produces = "application/json")
    public ResponseEntity<ResponseWrapper<TraineeProfileDTO>> updateTraineeProfile(
            @RequestParam("username") String username,
            @Valid @RequestBody TraineeUpdateDTO traineeDTO,
            BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(null);
        }
        try {
            traineeService.updateTraineeProfile(username, traineeDTO);
            TraineeProfileDTO traineeProfile = traineeService.getTraineeProfile(username);
            ResponseWrapper<TraineeProfileDTO> response = new ResponseWrapper<>(username, traineeProfile);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @DeleteMapping("/{username}")
    public ResponseEntity<String> deleteTraineeProfile(@PathVariable("username") String username) {
        try {
            // Delete the trainee profile
            traineeService.deleteProfileByUsername(username);
            return ResponseEntity.ok("Trainee profile deleted successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Trainee not found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }

    @PutMapping(value = "/update-trainers", consumes = "application/json", produces = "application/json")
    public ResponseEntity<List<TrainerDTO>> updateTraineeTrainers(
            @RequestParam("traineeUsername") String traineeUsername,
            @Valid @RequestBody UpdateTraineeTrainersDTO updateDTO,
            BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(null);
        }
        try {
            List<Trainer> trainers = traineeService.updateTraineeTrainerList(traineeUsername, updateDTO.getTrainerUsernames());
            List<TrainerDTO> trainersDTO = mapper.mapTrainersToProfileDTOs(trainers);
            return ResponseEntity.ok(trainersDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PatchMapping("/activate")
    public ResponseEntity<Void> updateTraineeStatus(
            @RequestParam String username,
            @RequestParam boolean isActive) {
        try {
            if (isActive) {
                traineeService.activate(username);
            } else {
                traineeService.deactivate(username);
            }
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}
