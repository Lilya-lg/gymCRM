package uz.gym.crm.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.gym.crm.dao.UserDAOImpl;
import uz.gym.crm.dao.abstr.TrainerDAO;
import uz.gym.crm.dao.abstr.TrainingDAO;
import uz.gym.crm.domain.Trainee;
import uz.gym.crm.domain.Trainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uz.gym.crm.domain.User;
import uz.gym.crm.dto.*;
import uz.gym.crm.mapper.Mapper;
import uz.gym.crm.service.abstr.AbstractProfileService;
import uz.gym.crm.service.abstr.ProfileService;
import uz.gym.crm.service.abstr.TrainerService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service("trainerServiceImpl")
@Transactional
public class TrainerServiceImpl extends AbstractProfileService<Trainer> implements TrainerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrainerServiceImpl.class);
    @Autowired
    TrainerService trainerServirce;
    ProfileService<Trainer> profileService;
    private final TrainerDAO trainerDAO;
    private final TrainingDAO trainingDAO;
    private final Mapper mapper;

    public TrainerServiceImpl(UserDAOImpl userDAO, TrainerDAO trainerDAO, TrainingDAO trainingDAO, Mapper mapper) {
        super(trainerDAO, userDAO, trainingDAO);
        this.trainerDAO = trainerDAO;
        this.trainingDAO = trainingDAO;
        this.mapper = mapper;
    }


    @Override
    public void create(Trainer trainer) {
        prepareUser(trainer.getUser());
        super.create(trainer);
        LOGGER.info("Trainer entity created successfully with ID: {}", trainer.getId());
    }

    @Override
    public Optional<Trainer> findByUsernameAndPassword(String usernameAuth, String passwordAuth, String username, String password) {
        if (!super.authenticate(usernameAuth, passwordAuth)) {
            throw new IllegalArgumentException("Invalid username or password.");
        }
        LOGGER.debug("Attempting to find trainer with username: {}", username);
        try {
            return trainerDAO.findByUsernameAndPassword(username, password);
        } catch (Exception e) {
            LOGGER.error("Error finding trainer with username: {}", username, e);
            throw e;
        }
    }

    @Override
    public Optional<Trainer> findByUsername(String username, String password, String usernameToSelect) {
        return Optional.empty();
    }

    @Override
    public Optional<Trainer> findByUsername(String username) {
        LOGGER.debug("Searching for profile with username: {}", username);
        return getDao().findByUsername(username);
    }

    @Override
    public List<Trainer> getUnassignedTrainersForTrainee(String traineeUsername) {
        LOGGER.debug("Fetching unassigned trainers for trainee with username: {}", traineeUsername);
        List<Trainer> unassignedTrainers = trainerDAO.getUnassignedTrainersByTraineeUsername(traineeUsername);
        LOGGER.info("Found {} unassigned trainers for trainee with username: {}", unassignedTrainers.size(), traineeUsername);
        return unassignedTrainers;
    }


    @Override
    public TrainerProfileDTO getTrainerProfile(String username) {
        Optional<Trainer> optionalTrainer = trainerDAO.findByUsername(username);

        Trainer trainer = optionalTrainer.orElseThrow(() ->
                new IllegalArgumentException("Trainee not found"));
        return mapToTrainerProfileDTO(trainer);
    }

    public TrainerProfileDTO mapToTrainerProfileDTO(Trainer trainer) {
        List<Trainee> trainees = trainingDAO.findTraineesByTrainerId(trainer.getId());
        TrainerProfileDTO profileDTO = new TrainerProfileDTO();
        profileDTO.setFirstName(trainer.getUser().getFirstName());
        profileDTO.setSecondName(trainer.getUser().getLastName());
        profileDTO.setSpecialization(trainer.getSpecialization().getTrainingType().getDisplayName());
        profileDTO.setIsActive(trainer.getUser().getIsActive());
        profileDTO.setTrainees(mapTraineesToProfileDTOs(trainees));
        return profileDTO;
    }

    public List<UserDTO> mapTraineesToProfileDTOs(List<Trainee> trainees) {
        return trainees.stream()
                .map(trainee -> {
                    UserDTO traineeProfileDTO = new UserDTO();
                    traineeProfileDTO.setFirstName(trainee.getUser().getFirstName());
                    traineeProfileDTO.setSecondName(trainee.getUser().getLastName());
                    traineeProfileDTO.setUsername(trainee.getUser().getUsername());
                    return traineeProfileDTO;
                })
                .collect(Collectors.toList());
    }

    @Override
    public void updateTrainerProfile(String username, TrainerProfileDTO trainerDTO) {
        Optional<Trainer> optionalTrainer = findByUsername(username);
        Trainer trainer = optionalTrainer.orElseThrow(() ->
                new IllegalArgumentException("Trainer with username '" + username + "' does not exist"));
        if (trainer.getUser().getIsActive() != Boolean.valueOf(trainerDTO.getIsActive())) {
            if (Boolean.valueOf(trainerDTO.getIsActive())) {
                super.activate(username);
            } else {
                super.deactivate(username);
            }
        }
        mapper.updateTrainerFromDTO(trainerDTO, trainer);
        super.updateProfile(username, trainer);
    }


    @Override
    protected User getUser(Trainer entity) {
        return entity.getUser();
    }


}
