package uz.gym.crm.service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import uz.gym.crm.dao.TraineeDAOImpl;
import uz.gym.crm.dao.TrainerDAOImpl;
import uz.gym.crm.dao.TrainingDAOImpl;
import uz.gym.crm.dao.TrainingTypeDAOImpl;
import uz.gym.crm.domain.*;


import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainingServiceImplTest {

    private TraineeDAOImpl mockTraineeDAO;
    private TrainerDAOImpl mockTrainerDAO;
    private TrainingDAOImpl mockTrainingDAO;
    private TrainingServiceImpl service;

    @BeforeEach
    void setUp() {
        mockTraineeDAO = Mockito.mock(TraineeDAOImpl.class);
        mockTrainerDAO = Mockito.mock(TrainerDAOImpl.class);
        mockTrainingDAO = Mockito.mock(TrainingDAOImpl.class);

        service = new TrainingServiceImpl(mockTraineeDAO, mockTrainerDAO, null, mockTrainingDAO);
    }
/*
    @Test
    void addTraining_ShouldAddTraining_WhenAllDependenciesExistAndTrainingTypeIsValid() {
        // Prepare mocks
        Trainee trainee = new Trainee();
        trainee.setId(1L);

        Trainer trainer = new Trainer();
        trainer.setId(2L);

        TrainingType trainingType = new TrainingType();
        trainingType.setId(1L); // Valid TrainingType ID (Yoga)

        Training training = new Training();
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingType(trainingType);

        when(mockTraineeDAO.read(1L)).thenReturn(Optional.of(trainee));
        when(mockTrainerDAO.read(2L)).thenReturn(Optional.of(trainer));
        doNothing().when(mockTrainingDAO).save(training);

        // Call the method under test
        service.addTraining(training);

        // Verify interactions
        verify(mockTraineeDAO, times(1)).read(1L);
        verify(mockTrainerDAO, times(1)).read(2L);
        verify(mockTrainingDAO, times(1)).save(training);

        assertEquals("Yoga", training.getTrainingType().getName());
    }



    @Test
    void addTraining_ShouldThrowException_WhenTrainingTypeIsInvalid() {
        // Prepare mocks
        Trainee trainee = new Trainee();
        trainee.setId(1L);

        Trainer trainer = new Trainer();
        trainer.setId(2L);

        TrainingType trainingType = new TrainingType();
        trainingType.setId(99L); // Invalid TrainingType ID

        Training training = new Training();
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingType(trainingType);

        when(mockTraineeDAO.read(1L)).thenReturn(Optional.of(trainee));
        when(mockTrainerDAO.read(2L)).thenReturn(Optional.of(trainer));

        // Call the method under test and expect exception
        Exception exception = assertThrows(IllegalArgumentException.class, () -> service.addTraining(training));

        assertEquals("Invalid TrainingType ID: 99", exception.getMessage());
        verify(mockTraineeDAO, times(1)).read(1L);
        verify(mockTrainerDAO, times(1)).read(2L);
        verifyNoInteractions(mockTrainingDAO);
    }
 */
    @Test
    void addTraining_ShouldAddTraining_WhenTrainingTypeIsNull() {
        // Prepare mocks
        Trainee trainee = new Trainee();
        trainee.setId(1L);

        Trainer trainer = new Trainer();
        trainer.setId(2L);

        Training training = new Training();
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingType(null);

        when(mockTraineeDAO.read(1L)).thenReturn(Optional.of(trainee));
        when(mockTrainerDAO.read(2L)).thenReturn(Optional.of(trainer));
        doNothing().when(mockTrainingDAO).save(training);

        // Call the method under test
        service.addTraining(training);

        // Verify interactions
        verify(mockTraineeDAO, times(1)).read(1L);
        verify(mockTrainerDAO, times(1)).read(2L);
        verify(mockTrainingDAO, times(1)).save(training);

        assertNull(training.getTrainingType());
    }
}
