package uz.gym.crm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.gym.crm.dto.TrainingTypeDTO;
import uz.gym.crm.service.abstr.TrainingTypeService;

import java.util.List;

@RestController
@RequestMapping("/api/training-types")
public class TrainingTypeController {

    private final TrainingTypeService trainingTypeService;

    @Autowired
    public TrainingTypeController(TrainingTypeService trainingTypeService) {
        this.trainingTypeService = trainingTypeService;
    }

    @GetMapping
    public ResponseEntity<List<TrainingTypeDTO>> getTrainingTypes() {
            List<TrainingTypeDTO> trainingTypes = trainingTypeService.getAllTrainingTypes();
            return ResponseEntity.ok(trainingTypes);
    }
}

