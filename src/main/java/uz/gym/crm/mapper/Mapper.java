package uz.gym.crm.mapper;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uz.gym.crm.domain.*;
import uz.gym.crm.dto.*;
import uz.gym.crm.dto.abstr.BaseTraineeDTO;
import uz.gym.crm.dto.abstr.BaseTrainerDTO;
import uz.gym.crm.service.abstr.TrainingTypeService;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class Mapper {
    private final SessionFactory sessionFactory;
    private final TrainingTypeService trainingTypeDAO;

    @Autowired
    public Mapper(SessionFactory sessionFactory, TrainingTypeService trainingTypeDAO) {
        this.sessionFactory = sessionFactory;
        this.trainingTypeDAO = trainingTypeDAO;
    }

    public Trainee toTrainee(BaseTraineeDTO traineeDTO) {
        User user = new User(traineeDTO.getFirstName(), traineeDTO.getSecondName());
        return new Trainee(traineeDTO.getDateOfBirth(), traineeDTO.getAddress(), user);
    }

    public Trainer toTrainer(BaseTrainerDTO trainerDTO) {
        User user = new User(trainerDTO.getFirstName(), trainerDTO.getSecondName());
        PredefinedTrainingType predefinedType = PredefinedTrainingType.fromName(trainerDTO.getSpecialization());
        TrainingType trainingType = trainingTypeDAO.getOrCreateTrainingType(predefinedType);
        return new Trainer(trainingType, user);
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
            trainee.setDateOfBirth(traineeDTO.getDateOfBirth());
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

            trainer.setSpecialization(trainingTypeDAO.getOrCreateTrainingType(trainingType));
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


}

