package uz.gym.crm.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uz.gym.crm.domain.Trainee;
import uz.gym.crm.domain.Trainer;
import uz.gym.crm.domain.Training;
import uz.gym.crm.domain.User;
import uz.gym.crm.dto.TraineeProfileDTO;
import uz.gym.crm.dto.TraineeUpdateDTO;
import uz.gym.crm.dto.TrainerDTO;
import uz.gym.crm.mapper.Mapper;
import uz.gym.crm.repository.*;
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
    private final Mapper mapper;
    private final TraineeRepository traineeRepository;
    private final TrainingRepository trainingRepository;
    private final UserRepository userRepository;
    private final TrainerRepository trainerRepository;

    public TraineeServiceImpl(Mapper mapper, TraineeRepository traineeRepository, TrainingRepository trainingRepository, UserRepository userRepository, TraineeRepository baseRepository, TrainerRepository trainerRepository) {
        super(userRepository, trainingRepository, baseRepository);
        this.mapper = mapper;
        this.traineeRepository = traineeRepository;
        this.trainingRepository = trainingRepository;
        this.userRepository = userRepository;
        this.trainerRepository = trainerRepository;
    }


    @Override
    public void create(Trainee trainee) {
        prepareUser(trainee.getUser());
        userRepository.save(trainee.getUser());
        traineeRepository.save(trainee);
        LOGGER.info("Trainee entity created successfully with ID: {}", trainee.getId());
    }

    public String putPassword(User user) {
        String pass = generatePassword(user);
        userRepository.save(user);
        return pass;
    }

    @Override
    public void deleteProfileByUsername(String username) {
        LOGGER.debug("Deleting Trainee profile with username: {}", username);
        Optional<Trainee> trainee = traineeRepository.findByUsername(username);
        if (trainee.isPresent()) {
            int deletedRows = traineeRepository.deleteByUsername(username);
            LOGGER.info("Deleted rows: " + deletedRows);
        } else {
            LOGGER.info("Trainee not found for username: " + username);
        }
        LOGGER.info("Trainee profile and associated user deleted successfully for username: {}", username);
    }


    @Override
    public List<Trainer> updateTraineeTrainerList(String username, Long trainingId, List<String> trainerIds) {
        Trainee trainee = traineeRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("Trainee not found"));

        Training training = trainingRepository.findById(trainingId).orElseThrow(() -> new IllegalArgumentException("Training not found for trainee"));

        List<Trainer> trainers = trainerRepository.findByUsernameIn(trainerIds);
        if (trainers.isEmpty()) {
            throw new IllegalArgumentException("No valid trainers found for given usernames");
        }


        for (Trainer trainer : trainers) {
            traineeRepository.updateTraineeTrainer(trainee.getId(), trainingId, trainer.getId());
        }


        return trainingRepository.findTrainersByTraineeId(trainee.getId());

    }

    @Override
    public TraineeProfileDTO getTraineeProfile(String username) {
        Optional<Trainee> optionalTrainee = traineeRepository.findByUsername(username);

        Trainee trainee = optionalTrainee.orElseThrow(() -> new IllegalArgumentException("Trainee not found"));

        TraineeProfileDTO profileDTO = new TraineeProfileDTO();
        profileDTO.setFirstName(trainee.getUser().getFirstName());
        profileDTO.setSecondName(trainee.getUser().getLastName());
        profileDTO.setDateOfBirth(trainee.getDateOfBirth().toString());
        profileDTO.setAddress(trainee.getAddress());
        profileDTO.setActive(trainee.getUser().getIsActive());

        List<Trainer> trainers = trainingRepository.findTrainersByTraineeId(trainee.getId());
        List<TrainerDTO> trainerDTOs = trainers.stream().map(trainer -> {
            TrainerDTO trainerDTO = new TrainerDTO();
            trainerDTO.setUsername(trainer.getUser().getUsername());
            trainerDTO.setFirstName(trainer.getUser().getFirstName());
            trainerDTO.setSecondName(trainer.getUser().getLastName());
            trainerDTO.setSpecialization(trainer.getSpecialization().getTrainingType().getDisplayName());
            return trainerDTO;
        }).collect(Collectors.toList());

        profileDTO.setTrainers(trainerDTOs);
        return profileDTO;
    }

    @Override
    public void updateProfile(String username, Trainee updatedTrainee) {
        super.updateProfile(username, updatedTrainee);
    }

    @Override
    protected User getUser(Trainee entity) {
        return entity.getUser();
    }

    @Override
    public Optional<Trainee> findByUsername(String username) {
        return traineeRepository.findByUsername(username);
    }

    @Override
    public void updateTraineeProfile(String username, TraineeUpdateDTO traineeDTO) {
        Optional<Trainee> optionalTrainee = traineeRepository.findByUsername(username);
        Trainee trainee = optionalTrainee.orElseThrow(() -> new IllegalArgumentException("Trainee with username '" + username + "' does not exist"));
        if (trainee.getUser().getIsActive() != Boolean.valueOf(traineeDTO.getIsActive())) {
            if (Boolean.valueOf(traineeDTO.getIsActive())) {
                super.activate(username);
            } else {
                super.deactivate(username);
            }
        }
        mapper.updateTraineeFromDTO(traineeDTO, trainee);
        updateProfile(username, trainee);

    }

}
