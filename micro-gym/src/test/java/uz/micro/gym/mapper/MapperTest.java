package uz.micro.gym.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uz.micro.gym.domain.*;
import uz.micro.gym.dto.*;
import uz.micro.gym.dto.abstr.BaseTraineeDTO;
import uz.micro.gym.dto.abstr.BaseTrainerDTO;
import uz.micro.gym.repository.TrainingRepository;
import uz.micro.gym.repository.TrainingTypeRepository;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MapperTest {

    @Mock
    private TrainingTypeRepository trainingTypeRepository;

    @Mock
    private TrainingRepository trainingRepository;

    @InjectMocks
    private Mapper mapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testToTrainee() {
        BaseTraineeDTO traineeDTO = mock(BaseTraineeDTO.class);
        when(traineeDTO.getFirstName()).thenReturn("John");
        when(traineeDTO.getSecondName()).thenReturn("Doe");
        when(traineeDTO.getDateOfBirth()).thenReturn("2000-01-01");
        when(traineeDTO.getAddress()).thenReturn("123 Street");

        Trainee trainee = mapper.toTrainee(traineeDTO);

        assertNotNull(trainee);
        assertEquals("John", trainee.getUser().getFirstName());
        assertEquals("Doe", trainee.getUser().getLastName());
        assertEquals(LocalDate.of(2000, 1, 1), trainee.getDateOfBirth());
        assertEquals("123 Street", trainee.getAddress());
    }

    @Test
    void testToTrainer() {
        BaseTrainerDTO trainerDTO = mock(BaseTrainerDTO.class);
        when(trainerDTO.getFirstName()).thenReturn("Alice");
        when(trainerDTO.getSecondName()).thenReturn("Smith");
        when(trainerDTO.getSpecialization()).thenReturn("Yoga");

        PredefinedTrainingType predefinedType = PredefinedTrainingType.fromName("Yoga");
        TrainingType trainingType = new TrainingType();
        trainingType.setTrainingType(predefinedType);

        when(trainingTypeRepository.getOrCreateTrainingType(predefinedType)).thenReturn(trainingType);

        Trainer trainer = mapper.toTrainer(trainerDTO);

        assertNotNull(trainer);
        assertEquals("Alice", trainer.getUser().getFirstName());
        assertEquals("Smith", trainer.getUser().getLastName());
        assertEquals(trainingType, trainer.getSpecialization());

        verify(trainingTypeRepository, times(1)).getOrCreateTrainingType(predefinedType);
    }

    @Test
    void testToTraining() {
        TrainingDTO trainingDTO = new TrainingDTO();
        trainingDTO.setTrainingName("Boxing Basics");
        trainingDTO.setTrainingDuration(60);
        trainingDTO.setTrainingDate(LocalDate.of(2025, 5, 15));

        Training training = mapper.toTraining(trainingDTO);

        assertNotNull(training);
        assertEquals("Boxing Basics", training.getTrainingName());
        assertEquals(60, training.getTrainingDuration());
        assertEquals(LocalDate.of(2025, 5, 15), training.getTrainingDate());
    }

    @Test
    void testUpdateTraineeFromDTO() {
        Trainee trainee = new Trainee();
        User user = new User();
        trainee.setUser(user);

        TraineeUpdateDTO traineeDTO = new TraineeUpdateDTO();
        traineeDTO.setFirstName("Michael");
        traineeDTO.setSecondName("Jordan");
        traineeDTO.setAddress("New Address");
        traineeDTO.setDateOfBirth("1985-10-10");

        mapper.updateTraineeFromDTO(traineeDTO, trainee);

        assertEquals("Michael", trainee.getUser().getFirstName());
        assertEquals("Jordan", trainee.getUser().getLastName());
        assertEquals("New Address", trainee.getAddress());
        assertEquals(LocalDate.of(1985, 10, 10), trainee.getDateOfBirth());
    }

    @Test
    void testUpdateTrainerFromDTO() {
        Trainer trainer = new Trainer();
        User user = new User();
        trainer.setUser(user);

        TrainerProfileDTO trainerDTO = new TrainerProfileDTO();
        trainerDTO.setFirstName("Bruce");
        trainerDTO.setSecondName("Lee");
        trainerDTO.setSpecialization("Yoga");

        PredefinedTrainingType trainingType = PredefinedTrainingType.fromName("Yoga");
        TrainingType newTrainingType = new TrainingType();
        newTrainingType.setTrainingType(trainingType);

        when(trainingTypeRepository.getOrCreateTrainingType(trainingType)).thenReturn(newTrainingType);

        mapper.updateTrainerFromDTO(trainerDTO, trainer);

        assertEquals("Bruce", trainer.getUser().getFirstName());
        assertEquals("Lee", trainer.getUser().getLastName());
        assertEquals(newTrainingType, trainer.getSpecialization());

        verify(trainingTypeRepository, times(1)).getOrCreateTrainingType(trainingType);
    }

    @Test
    void testMapTrainingsToTrainingDTOs() {

        Training training = new Training();
        training.setTrainingName("Pilates");
        training.setTrainingDuration(90);
        training.setTrainingDate(LocalDate.of(2025, 3, 10));


        PredefinedTrainingType predefinedTrainingType = PredefinedTrainingType.fromName("Pilates");
        TrainingType trainingType = new TrainingType();
        trainingType.setTrainingType(predefinedTrainingType);

        when(trainingTypeRepository.getOrCreateTrainingType(predefinedTrainingType)).thenReturn(trainingType);

        training.setTrainingType(trainingType);


        Trainer trainer = new Trainer();
        User user = new User();
        user.setUsername("coach_john");
        trainer.setUser(user);
        training.setTrainer(trainer);


        List<TrainingTraineeTrainerDTO> trainingDTOs = mapper.mapTrainingsToTrainingDTOs(List.of(training));


        assertEquals(1, trainingDTOs.size());
        assertEquals("Pilates", trainingDTOs.get(0).getTrainingName());
        assertEquals(90, trainingDTOs.get(0).getTrainingDuration());
        assertEquals("coach_john", trainingDTOs.get(0).getTrainerName());


        assertNotNull(trainingDTOs.get(0).getTrainingType());
        assertEquals("Pilates", trainingDTOs.get(0).getTrainingType());
    }


    @Test
    void testMapToTrainerProfileResponseDTO() {
        Trainer trainer = new Trainer();
        User user = new User();
        user.setFirstName("Tom");
        user.setLastName("Cruise");
        user.setIsActive(true);
        trainer.setUser(user);

        TrainingType trainingType = new TrainingType();
        trainingType.setTrainingType(PredefinedTrainingType.fromName("Pilates"));
        trainer.setSpecialization(trainingType);

        User user1 = new User();
        user1.setFirstName("John");
        user1.setFirstName("Doe");
        user1.setUsername("trainee1");
        user1.setIsActive(false);
        user1.setPassword("q231");

        Trainee trainee1 = new Trainee();
        trainee1.setUser(user1);


        when(trainingRepository.findTraineesByTrainerId(trainer.getId())).thenReturn(List.of(trainee1));

        TrainerProfileResponseDTO profileDTO = mapper.mapToTrainerProfileResponseDTO(trainer);

        assertEquals("Tom", profileDTO.getFirstName());
        assertEquals("Cruise", profileDTO.getSecondName());
        assertEquals("Pilates", profileDTO.getSpecialization());
        assertTrue(profileDTO.getIsActive());
        assertEquals(1, profileDTO.getTrainees().size());

        verify(trainingRepository, times(1)).findTraineesByTrainerId(trainer.getId());
    }
}
