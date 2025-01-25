package uz.gym.crm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import uz.gym.crm.domain.Training;
import uz.gym.crm.dto.*;
import uz.gym.crm.mapper.Mapper;
import uz.gym.crm.service.abstr.TrainingService;


import javax.validation.Valid;
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
    public ResponseEntity<List<TrainingTraineeTrainerDTO>> getTrainingsForTrainee(@Valid @RequestBody TrainingTraineeListDTO traineeListDTO, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(null);
        }
        try {

            List<Training> trainings = trainingService.findByCriteria(traineeListDTO.getUsername(), traineeListDTO.getTrainingType(), traineeListDTO.getPeriodFrom(), traineeListDTO.getPeriodTo(), traineeListDTO.getTrainerName());
            List<TrainingTraineeTrainerDTO> trainingTrainee = mapper.mapTrainingsToTrainingDTOs(trainings);
            return ResponseEntity.ok(trainingTrainee);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

    }


    @GetMapping("/trainer")
    @ResponseBody
    public ResponseEntity<List<TrainingTraineeTrainerDTO>> getTrainingsForTrainer(@Valid @RequestBody TrainingTrainerListDTO trainerListDTO, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(null);
        }
        try {
            List<Training> trainings = trainingService.findByCriteriaForTrainer(trainerListDTO.getUsername(), trainerListDTO.getPeriodFrom(), trainerListDTO.getPeriodTo(), trainerListDTO.getTraineeName());
            List<TrainingTraineeTrainerDTO> trainingTrainee = mapper.mapTrainingsToTrainingDTOs(trainings);
            return ResponseEntity.ok(trainingTrainee);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

    }

    @PostMapping("/new")
    @ResponseBody
    public ResponseEntity<String> createTraining(@Valid @RequestBody TrainingDTO trainingDTO, BindingResult result) {
        if (result.hasErrors()) {
            String errorMessage = result.getAllErrors().isEmpty()
                    ? "Validation failed"
                    : result.getAllErrors().get(0).getDefaultMessage();
            return ResponseEntity.badRequest().body(errorMessage);
        }
        try {
            Training training = mapper.toTraining(trainingDTO);
            trainingService.linkTraineeTrainer(training, trainingDTO.getTraineeUsername(), trainingDTO.getTrainerUsername());
            trainingService.create(training);
            return ResponseEntity.ok("Training was created successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

    }

}

