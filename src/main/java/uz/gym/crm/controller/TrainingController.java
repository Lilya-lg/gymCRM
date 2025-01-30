package uz.gym.crm.controller;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import uz.gym.crm.domain.Training;
import uz.gym.crm.dto.*;
import uz.gym.crm.mapper.Mapper;
import uz.gym.crm.service.abstr.TrainingService;


import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(value = "api/trainings", produces = {"application/JSON", "application/XML"})
public class TrainingController {
    @Autowired
    private final Mapper mapper;
    @Autowired
    private TrainingService trainingService;

    @Autowired
    public TrainingController(TrainingService trainingService, Mapper mapper) {
        this.trainingService = trainingService;
        this.mapper = mapper;
    }

    @GetMapping("/trainee")
    @ResponseBody
    public ResponseEntity<List<TrainingTraineeTrainerDTO>> getTrainingsForTrainee(
            @RequestParam String username,
            @RequestParam(required = false) String trainingType,
            @RequestParam(required = false) String periodFrom,
            @RequestParam(required = false) String periodTo,
            @RequestParam(required = false) String trainerName
    ) {
        LocalDate fromDate = (periodFrom != null && !periodFrom.isEmpty()) ? LocalDate.parse(periodFrom) : null;
        LocalDate toDate = (periodTo != null && !periodTo.isEmpty()) ? LocalDate.parse(periodTo) : null;

        List<Training> trainings = trainingService.findByCriteria(
                username, trainingType, fromDate, toDate, trainerName
        );
        List<TrainingTraineeTrainerDTO> trainingTrainee = mapper.mapTrainingsToTrainingDTOs(trainings);
        return ResponseEntity.ok(trainingTrainee);
    }


    @GetMapping("/trainer")
    @ResponseBody
    public ResponseEntity<List<TrainingTraineeTrainerDTO>> getTrainingsForTrainer(  @RequestParam String username,
                                                                                    @RequestParam(required = false) String periodFrom,
                                                                                    @RequestParam(required = false) String periodTo,
                                                                                    @RequestParam(required = false) String traineeName) {
            LocalDate fromDate = (periodFrom != null && !periodFrom.isEmpty()) ? LocalDate.parse(periodFrom) : null;
            LocalDate toDate = (periodTo != null && !periodTo.isEmpty()) ? LocalDate.parse(periodTo) : null;
            List<Training> trainings = trainingService.findByCriteriaForTrainer(username, fromDate, toDate, traineeName);
            List<TrainingTraineeTrainerDTO> trainingTrainee = mapper.mapTrainingsToTrainingDTOs(trainings);
            return ResponseEntity.ok(trainingTrainee);
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<String> createTraining(@Valid @RequestBody TrainingDTO trainingDTO) {
            Training training = mapper.toTraining(trainingDTO);
            trainingService.linkTraineeTrainer(training, trainingDTO.getTraineeUsername(), trainingDTO.getTrainerUsername());
            trainingService.create(training);
            return ResponseEntity.ok("Training was created successfully");
    }

}

