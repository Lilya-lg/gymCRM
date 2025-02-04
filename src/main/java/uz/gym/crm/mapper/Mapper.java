package uz.gym.crm.mapper;

import org.springframework.stereotype.Component;
import uz.gym.crm.domain.*;
import uz.gym.crm.dto.*;
import uz.gym.crm.dto.abstr.BaseTraineeDTO;
import uz.gym.crm.dto.abstr.BaseTrainerDTO;
import uz.gym.crm.repository.TrainingRepository;
import uz.gym.crm.repository.TrainingTypeRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class Mapper {
    private final TrainingTypeRepository trainingTypeRepository;
    private final TrainingRepository trainingRepository;
    public Mapper(TrainingTypeRepository trainingTypeRepository, TrainingRepository trainingRepository) {
        this.trainingTypeRepository = trainingTypeRepository;
        this.trainingRepository = trainingRepository;
    }

    public Trainee toTrainee(BaseTraineeDTO traineeDTO) {
        User user = new User();
        user.setLastName(traineeDTO.getSecondName());
        user.setFirstName(traineeDTO.getFirstName());
        Trainee trainee = new Trainee();
        trainee.setDateOfBirth(LocalDate.parse(traineeDTO.getDateOfBirth()));
        trainee.setAddress(traineeDTO.getAddress());
        trainee.setUser(user);
        return trainee;
    }

    public Trainer toTrainer(BaseTrainerDTO trainerDTO) {
        User user = new User();
        user.setLastName(trainerDTO.getSecondName());
        user.setFirstName(trainerDTO.getFirstName());
        Trainer trainer = new Trainer();
        PredefinedTrainingType predefinedType = PredefinedTrainingType.fromName(trainerDTO.getSpecialization());
        TrainingType trainingType = trainingTypeRepository.getOrCreateTrainingType(predefinedType);
        trainer.setSpecialization(trainingType);
        trainer.setUser(user);
        return trainer;
    }

    public Training toTraining(TrainingDTO trainingDTO) {
        Training training = new Training();
        training.setTrainingDuration(trainingDTO.getTrainingDuration());
        training.setTrainingName(trainingDTO.getTrainingName());
        training.setTrainingDate(trainingDTO.getTrainingDate());
        return training;
    }

    public void updateTraineeFromDTO(TraineeUpdateDTO traineeDTO, Trainee trainee) {
        if (traineeDTO.getFirstName() != null) {
            trainee.getUser().setFirstName(traineeDTO.getFirstName());
        }
        if (traineeDTO.getSecondName() != null) {
            trainee.getUser().setLastName(traineeDTO.getSecondName());
        }
        if (traineeDTO.getDateOfBirth() != null) {
            trainee.setDateOfBirth(LocalDate.parse(traineeDTO.getDateOfBirth()));
        }
        if (traineeDTO.getAddress() != null) {
            trainee.setAddress(traineeDTO.getAddress());
        }

    }

    public void updateTrainerFromDTO(TrainerProfileDTO trainerDTO, Trainer trainer) {
        if (trainerDTO.getFirstName() != null) {
            trainer.getUser().setFirstName(trainerDTO.getFirstName());
        }
        if (trainerDTO.getSecondName() != null) {
            trainer.getUser().setLastName(trainerDTO.getSecondName());
        }
        if (trainerDTO.getSpecialization() != null) {
            PredefinedTrainingType trainingType = PredefinedTrainingType.fromName(trainerDTO.getSpecialization());

            trainer.setSpecialization(trainingTypeRepository.getOrCreateTrainingType(trainingType));
        }


    }

    public List<TrainingTraineeTrainerDTO> mapTrainingsToTrainingDTOs(List<Training> trainings) {
        return trainings.stream()
                .map(training -> {
                    TrainingTraineeTrainerDTO trainingDTO = new TrainingTraineeTrainerDTO();
                    trainingDTO.setTrainingName(training.getTrainingName());
                    trainingDTO.setTrainingDate(training.getTrainingDate());
                    trainingDTO.setTrainingType(training.getTrainingType().getTrainingType().getDisplayName());
                    trainingDTO.setTrainingDuration(training.getTrainingDuration());
                    trainingDTO.setTrainerName(training.getTrainer().getUser().getUsername());
                    return trainingDTO;
                })
                .collect(Collectors.toList());
    }

    public List<TrainerDTO> mapTrainersToProfileDTOs(List<Trainer> trainers) {
        return trainers.stream()
                .map(trainer -> {
                    TrainerDTO trainerDTO = new TrainerDTO();
                    trainerDTO.setFirstName(trainer.getUser().getFirstName());
                    trainerDTO.setSecondName(trainer.getUser().getLastName());
                    trainerDTO.setUsername(trainer.getUser().getUsername());
                    trainerDTO.setSpecialization(trainer.getSpecialization().getTrainingType().getDisplayName());
                    return trainerDTO;
                })
                .collect(Collectors.toList());
    }
    public TrainerProfileResponseDTO mapToTrainerProfileResponseDTO(Trainer trainer) {
        List<Trainee> trainees = trainingRepository.findTraineesByTrainerId(trainer.getId());
        TrainerProfileResponseDTO profileDTO = new TrainerProfileResponseDTO();
        profileDTO.setFirstName(trainer.getUser().getFirstName());
        profileDTO.setSecondName(trainer.getUser().getLastName());
        profileDTO.setSpecialization(trainer.getSpecialization().getTrainingType().getDisplayName());
        profileDTO.setIsActive(trainer.getUser().getIsActive());
        profileDTO.setTrainees(mapTraineesToProfileDTOs(trainees));
        return profileDTO;
    }

    public TrainerProfileDTO mapToTrainerProfileDTO(Trainer trainer) {
        List<Trainee> trainees = trainingRepository.findTraineesByTrainerId(trainer.getId());
        TrainerProfileDTO profileDTO = new TrainerProfileDTO();
        profileDTO.setFirstName(trainer.getUser().getFirstName());
        profileDTO.setSecondName(trainer.getUser().getLastName());
        profileDTO.setUsername(trainer.getUser().getUsername());
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

}

