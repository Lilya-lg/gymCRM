package uz.gym.crm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.gym.crm.domain.Trainer;
import uz.gym.crm.dto.*;
import uz.gym.crm.dto.abstr.BaseTrainerDTO;
import uz.gym.crm.mapper.Mapper;
import uz.gym.crm.service.abstr.ProfileService;
import uz.gym.crm.service.abstr.TrainerService;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping(value = "/api/trainers", produces = {"application/JSON", "application/XML"})
public class TrainerController {
    @Autowired
    private TrainerService trainerService;
    @Autowired
    @Qualifier("trainerServiceImpl")
    private ProfileService<Trainer> abstractProfileService;

    @Autowired
    private final Mapper mapper;

    @Autowired
    public TrainerController(TrainerService trainerService, Mapper mapper, @Qualifier("trainerServiceImpl") ProfileService<Trainer> profileService) {
        this.trainerService = trainerService;
        this.mapper = mapper;
        this.abstractProfileService = profileService;
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<?> createTrainer
            (@Valid @RequestBody TrainerDTO trainerDTO) {
        Trainer trainer = mapper.toTrainer(trainerDTO);
        trainerService.create(trainer);
        BaseUserDTO userDTO = new BaseUserDTO(trainer.getUser().getUsername(), trainer.getUser().getPassword());
        return ResponseEntity.ok(userDTO);
    }

    @PutMapping(value = "/update-profile", consumes = "application/json", produces = "application/json")
    public ResponseEntity<ResponseWrapper<TrainerProfileDTO>> updateTrainerProfile(
            @Valid @RequestBody TrainerProfileDTO trainerDTO) {
        trainerService.updateTrainerProfile(trainerDTO.getUsername(), trainerDTO);
        TrainerProfileDTO trainerProfile = trainerService.getTrainerProfile(trainerDTO.getUsername());

        ResponseWrapper<TrainerProfileDTO> response = new ResponseWrapper<>(trainerDTO.getUsername(), trainerProfile);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/profiles/{username}")
    public ResponseEntity<BaseTrainerDTO> getTrainerProfile(
            @PathVariable("username") String username) {
        BaseTrainerDTO trainerProfile = trainerService.getTrainerProfile(username);
        return ResponseEntity.ok(trainerProfile);
    }

    @GetMapping(value = "/unassigned-active-trainers")
    public ResponseEntity<List<TrainerDTO>> getUnassignedTrainee(
            @RequestParam("username") String username
    ) {
        List<TrainerDTO> trainers = mapper.mapTrainersToProfileDTOs(trainerService.getUnassignedTrainersForTrainee(username));
        return ResponseEntity.ok(trainers);
    }

}

