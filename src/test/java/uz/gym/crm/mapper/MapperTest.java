package uz.gym.crm.mapper;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import uz.gym.crm.domain.*;
import uz.gym.crm.dto.*;
import uz.gym.crm.dto.abstr.BaseTraineeDTO;
import uz.gym.crm.dto.abstr.BaseTrainerDTO;
import uz.gym.crm.service.abstr.TrainingTypeService;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MapperTest {

    private Mapper mapper;
    private SessionFactory mockSessionFactory;
    private TrainingTypeService mockTrainingTypeService;

    @BeforeEach
    void setUp() {
        mockSessionFactory = Mockito.mock(SessionFactory.class);
        mockTrainingTypeService = Mockito.mock(TrainingTypeService.class);
        mapper = new Mapper(mockSessionFactory, mockTrainingTypeService);
    }

    @Test
    void toTrainee_ShouldMapBaseTraineeDTOToTrainee() {

        BaseTraineeDTO traineeDTO = mock(BaseTraineeDTO.class);
        when(traineeDTO.getFirstName()).thenReturn("John");
        when(traineeDTO.getSecondName()).thenReturn("Doe");
        when(traineeDTO.getDateOfBirth()).thenReturn(LocalDate.of(2000, 1, 1));
        when(traineeDTO.getAddress()).thenReturn("123 Main Street");


        Trainee trainee = mapper.toTrainee(traineeDTO);


        assertNotNull(trainee, "Trainee should not be null");
        assertEquals("John", trainee.getUser().getFirstName(), "First name should match");
        assertEquals("Doe", trainee.getUser().getLastName(), "Second name should match");
        assertEquals(LocalDate.of(2000, 1, 1), trainee.getDateOfBirth(), "Date of birth should match");
        assertEquals("123 Main Street", trainee.getAddress(), "Address should match");
    }

    @Test
    void toTrainer_ShouldMapBaseTrainerDTOToTrainer() {

        BaseTrainerDTO trainerDTO = mock(BaseTrainerDTO.class);
        when(trainerDTO.getFirstName()).thenReturn("Alice");
        when(trainerDTO.getSecondName()).thenReturn("Smith");
        when(trainerDTO.getSpecialization()).thenReturn("Yoga");

        TrainingType trainingType = new TrainingType();
        trainingType.setTrainingType(PredefinedTrainingType.YOGA);
        when(mockTrainingTypeService.getOrCreateTrainingType(PredefinedTrainingType.YOGA)).thenReturn(trainingType);


        Trainer trainer = mapper.toTrainer(trainerDTO);


        assertNotNull(trainer, "Trainer should not be null");
        assertEquals("Alice", trainer.getUser().getFirstName(), "First name should match");
        assertEquals("Smith", trainer.getUser().getLastName(), "Second name should match");
        assertEquals(trainingType, trainer.getSpecialization(), "Specialization should match");
        verify(mockTrainingTypeService, times(1)).getOrCreateTrainingType(PredefinedTrainingType.YOGA);
    }

    @Test
    void toTraining_ShouldMapTrainingDTOToTraining() {

        TrainingDTO trainingDTO = new TrainingDTO();
        trainingDTO.setTrainingName("Morning Yoga");
        trainingDTO.setTrainingDate(LocalDate.of(2025, 1, 1));
        trainingDTO.setTrainingDuration(60);


        Training training = mapper.toTraining(trainingDTO);


        assertNotNull(training, "Training should not be null");
        assertEquals("Morning Yoga", training.getTrainingName(), "Training name should match");
        assertEquals(LocalDate.of(2025, 1, 1), training.getTrainingDate(), "Training date should match");
        assertEquals(60, training.getTrainingDuration(), "Training duration should match");
    }

    @Test
    void updateTraineeFromDTO_ShouldUpdateTraineeFields() {

        TraineeUpdateDTO traineeDTO = new TraineeUpdateDTO();
        traineeDTO.setFirstName("UpdatedFirstName");
        traineeDTO.setSecondName("UpdatedLastName");
        traineeDTO.setDateOfBirth(LocalDate.of(1995, 5, 15));
        traineeDTO.setAddress("Updated Address");
        traineeDTO.setIsActive("true");

        User user = new User();
        Trainee trainee = new Trainee();
        trainee.setUser(user);


        mapper.updateTraineeFromDTO(traineeDTO, trainee);


        assertEquals("UpdatedFirstName", user.getFirstName(), "First name should be updated");
        assertEquals("UpdatedLastName", user.getLastName(), "Last name should be updated");
        assertEquals(LocalDate.of(1995, 5, 15), trainee.getDateOfBirth(), "Date of birth should be updated");
        assertEquals("Updated Address", trainee.getAddress(), "Address should be updated");
    }

    @Test
    void updateTrainerFromDTO_ShouldUpdateTrainerFields() {

        TrainerProfileDTO trainerDTO = new TrainerProfileDTO();
        trainerDTO.setFirstName("UpdatedFirstName");
        trainerDTO.setSecondName("UpdatedLastName");
        trainerDTO.setSpecialization("Cardio");
        trainerDTO.setIsActive(true);

        User user = new User();
        TrainingType trainingType = new TrainingType();
        Trainer trainer = new Trainer();
        trainer.setUser(user);

        when(mockTrainingTypeService.getOrCreateTrainingType(PredefinedTrainingType.fromName("Cardio")))
                .thenReturn(trainingType);


        mapper.updateTrainerFromDTO(trainerDTO, trainer);


        assertEquals("UpdatedFirstName", user.getFirstName(), "First name should be updated");
        assertEquals("UpdatedLastName", user.getLastName(), "Last name should be updated");
        assertEquals(trainingType, trainer.getSpecialization(), "Specialization should be updated");
        verify(mockTrainingTypeService, times(1)).getOrCreateTrainingType(PredefinedTrainingType.fromName("Cardio"));
    }

    @Test
    void mapTrainingsToTrainingDTOs_ShouldMapTrainingsToDTOs() {

        Training training = new Training();
        training.setTrainingName("Yoga Session");
        training.setTrainingDate(LocalDate.of(2025, 2, 15));
        training.setTrainingDuration(90);

        TrainingType trainingType = new TrainingType();
        trainingType.setTrainingType(PredefinedTrainingType.YOGA);
        training.setTrainingType(trainingType);

        User trainerUser = new User();
        trainerUser.setUsername("trainerUser");
        Trainer trainer = new Trainer();
        trainer.setUser(trainerUser);
        training.setTrainer(trainer);


        List<TrainingTraineeTrainerDTO> trainingDTOs = mapper.mapTrainingsToTrainingDTOs(List.of(training));


        assertNotNull(trainingDTOs, "Training DTO list should not be null");
        assertEquals(1, trainingDTOs.size(), "There should be one training DTO");
        TrainingTraineeTrainerDTO dto = trainingDTOs.get(0);
        assertEquals("Yoga Session", dto.getTrainingName(), "Training name should match");
        assertEquals(LocalDate.of(2025, 2, 15), dto.getTrainingDate(), "Training date should match");
        assertEquals(90, dto.getTrainingDuration(), "Training duration should match");
        assertEquals("Yoga", dto.getTrainingType(), "Training type should match");
        assertEquals("trainerUser", dto.getTrainerName(), "Trainer name should match");
    }

    @Test
    void mapTrainersToProfileDTOs_ShouldMapTrainersToDTOs() {

        Trainer trainer = new Trainer();
        User user = new User();
        user.setFirstName("Alice");
        user.setLastName("Smith");
        user.setUsername("trainerAlice");
        trainer.setUser(user);

        TrainingType trainingType = new TrainingType();
        trainingType.setTrainingType(PredefinedTrainingType.YOGA);
        trainer.setSpecialization(trainingType);


        List<TrainerDTO> trainerDTOs = mapper.mapTrainersToProfileDTOs(List.of(trainer));


        assertNotNull(trainerDTOs, "Trainer DTO list should not be null");
        assertEquals(1, trainerDTOs.size(), "There should be one trainer DTO");
        TrainerDTO dto = trainerDTOs.get(0);
        assertEquals("Alice", dto.getFirstName(), "First name should match");
        assertEquals("Smith", dto.getSecondName(), "Last name should match");
        assertEquals("trainerAlice", dto.getUsername(), "Username should match");
        assertEquals("Yoga", dto.getSpecialization(), "Specialization should match");
    }
}
