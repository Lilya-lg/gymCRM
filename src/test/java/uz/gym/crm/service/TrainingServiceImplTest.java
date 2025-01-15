package uz.gym.crm.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;

import uz.gym.crm.dao.TraineeDAOImpl;
import uz.gym.crm.dao.TrainerDAOImpl;
import uz.gym.crm.dao.TrainingDAOImpl;

import uz.gym.crm.dao.UserDAOImpl;
import uz.gym.crm.domain.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainingServiceImplTest {

    private TraineeDAOImpl mockTraineeDAO;
    private TrainerDAOImpl mockTrainerDAO;
    private TrainingDAOImpl mockTrainingDAO;
    private UserDAOImpl mockUserDAO;
    private TrainingServiceImpl service;

    @BeforeEach
    void setUp() {
        mockTraineeDAO = Mockito.mock(TraineeDAOImpl.class);
        mockTrainerDAO = Mockito.mock(TrainerDAOImpl.class);
        mockTrainingDAO = Mockito.mock(TrainingDAOImpl.class);
        mockUserDAO = Mockito.mock(UserDAOImpl.class);
        service = new TrainingServiceImpl(null, mockTrainingDAO,mockUserDAO);
    }

    @Test
    void addTraining_ShouldAddTraining_WhenTrainingTypeIsNull() {
        User authenticatedUser = new User();
        authenticatedUser.setUsername("adminUser");
        authenticatedUser.setPassword("adminPassword");

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
        when(mockUserDAO.findByUsernameAndPassword("adminUser", "adminPassword"))
                .thenReturn(Optional.of(authenticatedUser));
        doNothing().when(mockTrainingDAO).save(training);


        service.addTraining(training,"adminUser","adminPassword");


        verify(mockTrainingDAO, times(1)).save(training);

        assertNull(training.getTrainingType());
    }
    @Test
    void findByCriteriaForTrainer_ShouldReturnTrainingList_WhenAuthenticated() {

        String username = "adminUser";
        String password = "adminPassword";
        String trainerUsername = "trainerJohn";
        String traineeName = "traineeJane";
        LocalDate fromDate = LocalDate.now();
        LocalDate toDate = LocalDate.now().plusDays(10);

        User authenticatedUser = new User();
        authenticatedUser.setUsername(username);
        authenticatedUser.setPassword(password);

        Training training1 = new Training();
        Training training2 = new Training();
        List<Training> expectedTrainings = List.of(training1, training2);

        when(mockUserDAO.findByUsernameAndPassword(username, password)).thenReturn(Optional.of(authenticatedUser));
        when(mockTrainingDAO.findByCriteriaForTrainer(trainerUsername, fromDate, toDate, traineeName))
                .thenReturn(expectedTrainings);


        List<Training> actualTrainings = service.findByCriteriaForTrainer(username, password, trainerUsername, fromDate, toDate, traineeName);


        assertEquals(expectedTrainings.size(), actualTrainings.size());
        verify(mockUserDAO, times(1)).findByUsernameAndPassword(username, password);
        verify(mockTrainingDAO, times(1)).findByCriteriaForTrainer(trainerUsername, fromDate, toDate, traineeName);
    }

    @Test
    void findByCriteria_ShouldReturnTrainingList_WhenAuthenticated() {

        String username = "adminUser";
        String password = "adminPassword";
        String traineeUsername = "traineeJane";
        String trainingType = "Yoga";
        String trainerName = "trainerJohn";
        LocalDate fromDate = LocalDate.now();
        LocalDate toDate = LocalDate.now().plusDays(10);

        User authenticatedUser = new User();
        authenticatedUser.setUsername(username);
        authenticatedUser.setPassword(password);

        Training training1 = new Training();
        Training training2 = new Training();
        List<Training> expectedTrainings = List.of(training1, training2);

        when(mockUserDAO.findByUsernameAndPassword(username, password)).thenReturn(Optional.of(authenticatedUser));
        when(mockTrainingDAO.findByCriteria(traineeUsername, trainingType, fromDate, toDate, trainerName))
                .thenReturn(expectedTrainings);


        List<Training> actualTrainings = service.findByCriteria(username, password, traineeUsername, trainingType, fromDate, toDate, trainerName);

        assertEquals(expectedTrainings.size(), actualTrainings.size());
        verify(mockUserDAO, times(1)).findByUsernameAndPassword(username, password);
        verify(mockTrainingDAO, times(1)).findByCriteria(traineeUsername, trainingType, fromDate, toDate, trainerName);
    }

    @Test
    void findByCriteriaForTrainer_ShouldThrowException_WhenNotAuthenticated() {

        String username = "adminUser";
        String password = "wrongPassword";
        String trainerUsername = "trainerJohn";
        String traineeName = "traineeJane";
        LocalDate fromDate = LocalDate.now();
        LocalDate toDate = LocalDate.now().plusDays(10);

        when(mockUserDAO.findByUsernameAndPassword(username, password)).thenReturn(Optional.empty());


        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                service.findByCriteriaForTrainer(username, password, trainerUsername, fromDate, toDate, traineeName));
        assertEquals("Invalid username or password.", exception.getMessage());
        verify(mockUserDAO, times(1)).findByUsernameAndPassword(username, password);
        verify(mockTrainingDAO, never()).findByCriteriaForTrainer(anyString(), any(), any(), anyString());
    }

    @Test
    void findByCriteria_ShouldThrowException_WhenNotAuthenticated() {

        String username = "adminUser";
        String password = "wrongPassword";
        String traineeUsername = "traineeJane";
        String trainingType = "Yoga";
        String trainerName = "trainerJohn";
        LocalDate fromDate = LocalDate.now();
        LocalDate toDate = LocalDate.now().plusDays(10);

        when(mockUserDAO.findByUsernameAndPassword(username, password)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                service.findByCriteria(username, password, traineeUsername, trainingType, fromDate, toDate, trainerName));
        assertEquals("Invalid username or password.", exception.getMessage());
        verify(mockUserDAO, times(1)).findByUsernameAndPassword(username, password);
        verify(mockTrainingDAO, never()).findByCriteria(anyString(), anyString(), any(), any(), anyString());
    }
}
