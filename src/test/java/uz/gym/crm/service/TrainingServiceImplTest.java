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

        service = new TrainingServiceImpl(
                null,
                mockTrainingDAO,
                mockUserDAO,
                mockTrainerDAO,
                mockTraineeDAO
        );
    }

    @Test
    void addTraining_ShouldAddTraining_WhenTrainingTypeIsNull() {
        Training training = new Training();
        User authenticatedUser = new User();
        authenticatedUser.setUsername("adminUser");
        authenticatedUser.setPassword("adminPassword");

        when(mockUserDAO.findByUsernameAndPassword("adminUser", "adminPassword"))
                .thenReturn(Optional.of(authenticatedUser));
        doNothing().when(mockTrainingDAO).save(training);

        service.addTraining(training, "adminUser", "adminPassword");

        verify(mockUserDAO, times(1)).findByUsernameAndPassword("adminUser", "adminPassword");
        verify(mockTrainingDAO, times(1)).save(training);
    }

    @Test
    void findByCriteriaForTrainer_ShouldReturnTrainingList_WhenAuthenticated() {
        String trainerUsername = "trainerJohn";
        String traineeName = "traineeJane";
        LocalDate fromDate = LocalDate.now();
        LocalDate toDate = LocalDate.now().plusDays(10);

        Training training1 = new Training();
        Training training2 = new Training();
        List<Training> expectedTrainings = List.of(training1, training2);

        when(mockTrainingDAO.findByCriteriaForTrainer(trainerUsername, fromDate, toDate, traineeName))
                .thenReturn(expectedTrainings);

        List<Training> actualTrainings = service.findByCriteriaForTrainer(trainerUsername, fromDate, toDate, traineeName);

        assertEquals(expectedTrainings.size(), actualTrainings.size());
        verify(mockTrainingDAO, times(1))
                .findByCriteriaForTrainer(trainerUsername, fromDate, toDate, traineeName);
    }

    @Test
    void findByCriteria_ShouldReturnTrainingList_WhenAuthenticated() {
        String traineeUsername = "traineeJane";
        String trainingType = "Yoga";
        String trainerName = "trainerJohn";
        LocalDate fromDate = LocalDate.now();
        LocalDate toDate = LocalDate.now().plusDays(10);

        Training training1 = new Training();
        Training training2 = new Training();
        List<Training> expectedTrainings = List.of(training1, training2);

        when(mockTrainingDAO.findByCriteria(traineeUsername, PredefinedTrainingType.fromName(trainingType), fromDate, toDate, trainerName))
                .thenReturn(expectedTrainings);

        List<Training> actualTrainings = service.findByCriteria(traineeUsername, trainingType, fromDate, toDate, trainerName);

        assertEquals(expectedTrainings.size(), actualTrainings.size());
        verify(mockTrainingDAO, times(1))
                .findByCriteria(traineeUsername, PredefinedTrainingType.fromName(trainingType), fromDate, toDate, trainerName);
    }



    @Test
    void findByCriteria_ShouldThrowException_WhenInvalidTrainingType() {
        String invalidTrainingType = "InvalidType";

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                service.findByCriteria("traineeJane", invalidTrainingType, LocalDate.now(), LocalDate.now().plusDays(10), "trainerJohn"));

        assertEquals("Invalid specialization: InvalidType", exception.getMessage());
    }

    @Test
    void create_ShouldCallDAOCreate() {
        Training training = new Training();
        doNothing().when(mockTrainingDAO).save(training);

        service.create(training);

        verify(mockTrainingDAO, times(1)).save(training);
    }
    @Test
    void linkTraineeTrainer_ShouldLinkTraineeAndTrainer() {
        String traineeName = "traineeJane";
        String trainerName = "trainerJohn";

        Trainee trainee = new Trainee();
        Trainer trainer = new Trainer();
        Training training = new Training();

        when(mockTraineeDAO.findByUsername(traineeName)).thenReturn(Optional.of(trainee));
        when(mockTrainerDAO.findByUsername(trainerName)).thenReturn(Optional.of(trainer));

        service.linkTraineeTrainer(training, traineeName, trainerName);

        assertEquals(trainee, training.getTrainee());
        assertEquals(trainer, training.getTrainer());
        assertEquals(trainer.getSpecialization(), training.getTrainingType());

        verify(mockTraineeDAO, times(1)).findByUsername(traineeName);
        verify(mockTrainerDAO, times(1)).findByUsername(trainerName);
    }
    @Test
    void linkTraineeTrainer_ShouldThrowException_WhenTraineeNotFound() {
        String traineeName = "nonexistentTrainee";
        String trainerName = "trainerJohn";

        when(mockTraineeDAO.findByUsername(traineeName)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                service.linkTraineeTrainer(new Training(), traineeName, trainerName)
        );

        assertEquals("Trainee not found with username: nonexistentTrainee", exception.getMessage());
        verify(mockTraineeDAO, times(1)).findByUsername(traineeName);
        verifyNoInteractions(mockTrainerDAO);
    }
    @Test
    void linkTraineeTrainer_ShouldThrowException_WhenTrainerNotFound() {
        String traineeName = "traineeJane";
        String trainerName = "nonexistentTrainer";

        Trainee trainee = new Trainee();

        when(mockTraineeDAO.findByUsername(traineeName)).thenReturn(Optional.of(trainee));
        when(mockTrainerDAO.findByUsername(trainerName)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                service.linkTraineeTrainer(new Training(), traineeName, trainerName)
        );

        assertEquals("Trainer not found with username: nonexistentTrainer", exception.getMessage());
        verify(mockTraineeDAO, times(1)).findByUsername(traineeName);
        verify(mockTrainerDAO, times(1)).findByUsername(trainerName);
    }
    @Test
    void addTraining_ShouldThrowException_WhenAuthenticationFails() {
        Training training = new Training();

        when(mockUserDAO.findByUsernameAndPassword("invalidUser", "invalidPass"))
                .thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                service.addTraining(training, "invalidUser", "invalidPass")
        );

        assertEquals("Invalid username or password.", exception.getMessage());
        verify(mockUserDAO, times(1)).findByUsernameAndPassword("invalidUser", "invalidPass");
        verifyNoInteractions(mockTrainingDAO);
    }
    @Test
    void findByCriteriaForTrainer_ShouldLogError_WhenExceptionOccurs() {
        String trainerUsername = "trainerJohn";
        String traineeName = "traineeJane";
        LocalDate fromDate = LocalDate.now();
        LocalDate toDate = LocalDate.now().plusDays(10);

        when(mockTrainingDAO.findByCriteriaForTrainer(trainerUsername, fromDate, toDate, traineeName))
                .thenThrow(new RuntimeException("Database error"));

        Exception exception = assertThrows(RuntimeException.class, () ->
                service.findByCriteriaForTrainer(trainerUsername, fromDate, toDate, traineeName)
        );

        assertEquals("Database error", exception.getMessage());
        verify(mockTrainingDAO, times(1))
                .findByCriteriaForTrainer(trainerUsername, fromDate, toDate, traineeName);
    }
}
