package uz.micro.gym.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.micro.gym.domain.Trainer;
import uz.micro.gym.domain.User;
import uz.micro.gym.dto.TrainerProfileDTO;
import uz.micro.gym.dto.TrainerProfileResponseDTO;
import uz.micro.gym.mapper.Mapper;
import uz.micro.gym.repository.BaseRepository;
import uz.micro.gym.repository.TrainerRepository;
import uz.micro.gym.repository.TrainingRepository;
import uz.micro.gym.repository.UserRepository;
import uz.micro.gym.service.abstr.AbstractProfileService;
import uz.micro.gym.service.abstr.TrainerService;
import uz.micro.gym.util.exceptions.EntityNotFoundException;

import java.util.List;
import java.util.Optional;



@Service("trainerServiceImpl")
@Transactional
public class TrainerServiceImpl extends AbstractProfileService<Trainer> implements TrainerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrainerServiceImpl.class);
    private final Mapper mapper;
    private final BaseRepository<Trainer> baseRepository;
    private final UserRepository userRepository;
    private final TrainingRepository trainingRepository;
    private final TrainerRepository trainerRepository;

    public TrainerServiceImpl(Mapper mapper, UserRepository userRepository, BaseRepository<Trainer> baseRepository, TrainingRepository trainingRepository, TrainerRepository trainerRepository) {
        super(userRepository, trainingRepository, baseRepository);
        this.baseRepository = baseRepository;
        this.trainingRepository = trainingRepository;
        this.mapper = mapper;
        this.userRepository = userRepository;
        this.trainerRepository = trainerRepository;
    }


    @Override
    public Optional<Trainer> findByUsername(String username) {
        return trainerRepository.findByUsername(username);
    }

    @Override
    public void create(Trainer trainer) {
        prepareUser(trainer.getUser());
        userRepository.save(trainer.getUser());
        trainerRepository.save(trainer);
        LOGGER.info("Trainer entity created successfully with ID: {}", trainer.getId());
    }


    public List<Trainer> getUnassignedTrainersForTrainee(String traineeUsername) {
        LOGGER.debug("Fetching unassigned trainers for trainee with username: {}", traineeUsername);
        List<Trainer> unassignedTrainers = trainerRepository.getUnassignedTrainersByTraineeUsername(traineeUsername);
        LOGGER.info("Found {} unassigned trainers for trainee with username: {}", unassignedTrainers.size(), traineeUsername);
        return unassignedTrainers;
    }


    public TrainerProfileDTO getTrainerProfile(String username) {
        Optional<Trainer> optionalTrainer = trainerRepository.findByUsername(username);

    Trainer trainer =
        optionalTrainer.orElseThrow(() -> new EntityNotFoundException("Trainee not found"));
        return mapper.mapToTrainerProfileDTO(trainer);
    }

    public TrainerProfileResponseDTO getTrainerProfileResponse(String username) {
        Optional<Trainer> optionalTrainer = baseRepository.findByUsername(username);

    Trainer trainer =
        optionalTrainer.orElseThrow(() -> new EntityNotFoundException("Trainee not found"));
        return mapper.mapToTrainerProfileResponseDTO(trainer);
    }
    public String putPassword(User user){
        String pass = generatePassword(user);
        userRepository.save(user);
        return pass;
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
        User user = trainer.getUser();
        user.setIsActive(Boolean.valueOf(trainerDTO.getIsActive()));
        user.setLastName(trainerDTO.getSecondName());
        userRepository.save(user);
        mapper.updateTrainerFromDTO(trainerDTO, trainer);
        super.updateProfile(username, trainer);
    }

    @Override
    protected User getUser(Trainer entity) {
        return entity.getUser();
    }
}
