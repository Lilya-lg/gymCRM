package uz.gym.crm.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.gym.crm.dao.UserDAOImpl;
import uz.gym.crm.dao.abstr.TraineeDAO;
import uz.gym.crm.dao.abstr.TrainingDAO;
import uz.gym.crm.domain.Trainee;
import uz.gym.crm.domain.Trainer;
import uz.gym.crm.domain.User;
import uz.gym.crm.dto.TraineeProfileDTO;
import uz.gym.crm.dto.TraineeUpdateDTO;
import uz.gym.crm.dto.TrainerDTO;
import uz.gym.crm.mapper.Mapper;
import uz.gym.crm.service.abstr.AbstractProfileService;
import uz.gym.crm.service.abstr.TraineeService;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@Transactional
public class TraineeServiceImpl extends AbstractProfileService<Trainee> implements TraineeService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TraineeServiceImpl.class);
    @Autowired
    TraineeService traineeService;
    private final TraineeDAO traineeDAO;
    private final UserDAOImpl userDAO;
    private final TrainingDAO trainingDAO;
    private final Mapper mapper;

    @Autowired
    public TraineeServiceImpl(UserDAOImpl userDAO, TraineeDAO traineeDAO, TrainingDAO trainingDAO, Mapper mapper) {
        super(traineeDAO, userDAO, trainingDAO);
        this.traineeDAO = traineeDAO;
        this.userDAO = userDAO;
        this.trainingDAO = trainingDAO;
        this.mapper = mapper;
    }


    @Override
    public void create(Trainee trainee) {
        prepareUser(trainee.getUser());
        userDAO.save(trainee.getUser());
        super.create(trainee);
        LOGGER.info("Trainee entity created successfully with ID: {}", trainee.getId());
    }

    @Override
    public void deleteProfileByUsername(String username) {
        LOGGER.debug("Deleting Trainee profile with username: {}", username);
        traineeDAO.deleteByUsername(username);
        LOGGER.info("Trainee profile and associated user deleted successfully for username: {}", username);
    }


    public Optional<Trainee> findByUsername(String username, String password, String usernameToSelect) {
        if (!super.authenticate(username, password)) {
            throw new IllegalArgumentException("Invalid username or password.");
        }
        LOGGER.debug("Searching for profile with username: {}", usernameToSelect);
        return getDao().findByUsername(usernameToSelect);
    }

    @Override
    public Optional<Trainee> findByUsername(String username) {
        LOGGER.debug("!!Searching for profile with username: {}", username);
        return getDao().findByUsername(username);
    }

    @Override
    public Optional<Trainee> findByUsernameAndPassword(String usernameAuth, String passwordAuth, String username, String password) {
        if (!super.authenticate(usernameAuth, passwordAuth)) {
            throw new IllegalArgumentException("Invalid username or password.");
        }
        LOGGER.debug("Attempting to find trainer with username: {}", username);
        try {
            return traineeDAO.findByUsernameAndPassword(username, password);
        } catch (Exception e) {
            LOGGER.error("Error finding trainer with username: {}", username, e);
            throw e;
        }
    }

    @Override
    public List<Trainer> updateTraineeTrainerList(String username, List<String> trainerIds) {
        Optional<Trainee> optionalTrainee = traineeDAO.findByUsername(username);
        Trainee trainee = optionalTrainee.orElseThrow(() ->
                new IllegalArgumentException("Trainee not found"));
        traineeDAO.updateTraineeTrainerList(trainee.getId(), trainerIds);
        return trainingDAO.findTrainersByTraineeId(trainee.getId());


    }


    @Override
    public TraineeProfileDTO getTraineeProfile(String username) {
        // Fetch trainee details
        Optional<Trainee> optionalTrainee = traineeDAO.findByUsername(username);

        Trainee trainee = optionalTrainee.orElseThrow(() ->
                new IllegalArgumentException("Trainee not found"));

        // Map Trainee to DTO
        TraineeProfileDTO profileDTO = new TraineeProfileDTO();
        profileDTO.setFirstName(trainee.getUser().getFirstName());
        profileDTO.setSecondName(trainee.getUser().getLastName());
        profileDTO.setDateOfBirth(trainee.getDateOfBirth());
        profileDTO.setAddress(trainee.getAddress());
        profileDTO.setActive(trainee.getUser().isActive());

        // Fetch associated trainers and map to DTO
        List<Trainer> trainers = trainingDAO.findTrainersByTraineeId(trainee.getId());
        List<TrainerDTO> trainerDTOs = trainers.stream()
                .map(trainer -> {
                    TrainerDTO trainerDTO = new TrainerDTO();
                    trainerDTO.setUsername(trainer.getUser().getUsername());
                    trainerDTO.setFirstName(trainer.getUser().getFirstName());
                    trainerDTO.setSecondName(trainer.getUser().getLastName());
                    trainerDTO.setSpecialization(trainer.getSpecialization().getTrainingType().getDisplayName());
                    return trainerDTO;
                })
                .collect(Collectors.toList());

        profileDTO.setTrainers(trainerDTOs);
        return profileDTO;
    }

    @Override
    public void updateProfile(String username, Trainee updatedTrainee) {
        super.updateProfile(username, updatedTrainee);
    }

    public void updateTraineeProfile(String username, TraineeUpdateDTO traineeDTO) {
        Optional<Trainee> optionalTrainee = findByUsername(username);
        Trainee trainee = optionalTrainee.orElseThrow(() ->
                new IllegalArgumentException("Trainee with username '" + username + "' does not exist"));

        mapper.updateTraineeFromDTO(traineeDTO, trainee);
        updateProfile(username, trainee);
    }

    @Override
    public void activate(String username) {
        super.activate(username);
    }

    @Override
    public void deactivate(String username) {
        super.deactivate(username);
    }
    @Override
    protected User getUser(Trainee entity) {
        return entity.getUser();
    }

}
