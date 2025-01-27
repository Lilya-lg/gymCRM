package uz.gym.crm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import uz.gym.crm.domain.Trainer;
import uz.gym.crm.dto.*;
import uz.gym.crm.dto.abstr.BaseTrainerDTO;
import uz.gym.crm.mapper.Mapper;
import uz.gym.crm.service.abstr.ProfileService;
import uz.gym.crm.service.abstr.TrainerService;

import javax.servlet.http.HttpServletRequest;
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

    @PostMapping("/new")
    @ResponseBody
    public ResponseEntity<?> createTrainer
            (@Valid @RequestBody TrainerDTO trainerDTO, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors().get(0).getDefaultMessage());
        }
        Trainer trainer = mapper.toTrainer(trainerDTO);
        trainerService.create(trainer);
        BaseUserDTO userDTO = new BaseUserDTO(trainer.getUser().getUsername(), trainer.getUser().getPassword());
        return ResponseEntity.ok(userDTO);
    }

    @PutMapping(value = "/update-profile", consumes = "application/json", produces = "application/json")
    public ResponseEntity<ResponseWrapper<TrainerProfileDTO>> updateTrainerProfile(
            @RequestParam("username") String username,
            @Valid @RequestBody TrainerProfileDTO trainerDTO,
            BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(null);
        }
        try {
            trainerService.updateTrainerProfile(username, trainerDTO);
            TrainerProfileDTO trainerProfile = trainerService.getTrainerProfile(username);

            ResponseWrapper<TrainerProfileDTO> response = new ResponseWrapper<>(username, trainerProfile);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping(value = "/profile", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseTrainerDTO> getTrainerProfile(
            @RequestParam("username") String username,
            HttpServletRequest request) {

        try {
            BaseTrainerDTO trainerProfile = trainerService.getTrainerProfile(username);
            return ResponseEntity.ok(trainerProfile);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping(value = "/unassigned-active-trainers")
    public ResponseEntity<List<TrainerDTO>> getUnassignedTrainee(
            @RequestParam("username") String username,
            HttpServletRequest request
    ) {
        try {
            List<TrainerDTO> trainers = mapper.mapTrainersToProfileDTOs(trainerService.getUnassignedTrainersForTrainee(username));
            return ResponseEntity.ok(trainers);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PatchMapping("/activate")
    public ResponseEntity<Void> updateTrainerStatus(
            @RequestParam String username,
            @RequestParam boolean isActive) {
        try {
            if (isActive) {
                abstractProfileService.activate(username);
            } else {
                abstractProfileService.deactivate(username);
            }

            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}

