package uz.gym.crm.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.gym.crm.domain.Training;
import uz.gym.crm.dto.*;
import uz.gym.crm.mapper.Mapper;
import uz.gym.crm.service.abstr.TrainingService;

import java.util.List;

@RestController
@RequestMapping(value = "api/trainings", produces = {"application/json", "application/xml"})
public class TrainingController {

    private final TrainingService trainingService;
    private final Mapper mapper;

    public TrainingController(TrainingService trainingService, Mapper mapper) {
        this.trainingService = trainingService;
        this.mapper = mapper;
    }

    @GetMapping("/trainee")
    public ResponseEntity<List<TrainingTraineeTrainerDTO>> getTrainingsForTrainee(@Valid @RequestBody TrainingTraineeListDTO traineeListDTO) {
        List<Training> trainings = trainingService.findByCriteria(traineeListDTO.getUsername(), traineeListDTO.getTrainingType(), traineeListDTO.getPeriodFrom(), traineeListDTO.getPeriodTo(), traineeListDTO.getTrainerName());
        List<TrainingTraineeTrainerDTO> trainingTrainee = mapper.mapTrainingsToTrainingDTOs(trainings);
        return ResponseEntity.ok(trainingTrainee);
    }

    @GetMapping("/trainer")
    public ResponseEntity<List<TrainingTraineeTrainerDTO>> getTrainingsForTrainer(@Valid @RequestBody TrainingTrainerListDTO trainerListDTO) {


        List<Training> trainings = trainingService.findByCriteriaForTrainer(trainerListDTO.getUsername(), trainerListDTO.getPeriodFrom(), trainerListDTO.getPeriodTo(), trainerListDTO.getTraineeName());
        List<TrainingTraineeTrainerDTO> trainingTrainee = mapper.mapTrainingsToTrainingDTOs(trainings);
        return ResponseEntity.ok(trainingTrainee);
    }

    @PostMapping
    public ResponseEntity<String> createTraining(@Valid @RequestBody TrainingDTO trainingDTO) {
        Training training = mapper.toTraining(trainingDTO);
        trainingService.linkTraineeTrainer(training, trainingDTO.getTraineeUsername(), trainingDTO.getTrainerUsername());
        trainingService.create(training);
        return ResponseEntity.ok("Training was created successfully");
    }
}
