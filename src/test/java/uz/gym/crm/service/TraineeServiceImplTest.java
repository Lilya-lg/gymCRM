package uz.gym.crm.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import uz.gym.crm.dao.TraineeDAOImpl;
import uz.gym.crm.dao.TrainingDAOImpl;
import uz.gym.crm.dao.UserDAOImpl;
import uz.gym.crm.domain.Trainee;
import uz.gym.crm.domain.Trainer;
import uz.gym.crm.domain.Training;
import uz.gym.crm.domain.User;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class TraineeServiceImplTest {
    private UserDAOImpl mockUserDAO;
    private TraineeDAOImpl mockTraineeDAO;
    private TrainingDAOImpl mockTrainingDAO;
    private TraineeServiceImpl service;

    @BeforeEach
    void setUp() {
        mockUserDAO = Mockito.mock(UserDAOImpl.class);
        mockTraineeDAO = Mockito.mock(TraineeDAOImpl.class);
        mockTrainingDAO = Mockito.mock(TrainingDAOImpl.class);

        service = new TraineeServiceImpl(mockUserDAO, mockTraineeDAO, mockTrainingDAO);
    }

    @Test
    void create_ShouldPrepareUserAndSaveTrainee() {
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");

        Trainee trainee = new Trainee();
        trainee.setUser(user);

        doNothing().when(mockTraineeDAO).save(any(Trainee.class));

        service.create(trainee);

        assertNotNull(user.getUsername());
        assertNotNull(user.getPassword());
        verify(mockTraineeDAO, times(1)).save(trainee);
    }

    @Test
    void deleteProfileByUsername_ShouldDeleteTraineeAndUser() {
        User user = new User();
        user.setUsername("johndoe");
        user.setPassword("password");

        Trainee trainee = new Trainee();
        trainee.setId(1L);
        trainee.setUser(user);

        when(mockUserDAO.findByUsernameAndPassword("johndoe", "password")).thenReturn(Optional.of(user));
        when(mockTraineeDAO.findByUsername("johndoe")).thenReturn(Optional.of(trainee));

        service.deleteProfileByUsername("johndoe", "password");

        verify(mockTraineeDAO, times(1)).delete(1L);
    }

    @Test
    void findByUsernameAndPassword_ShouldReturnTrainee_WhenCredentialsAreValid() {
        Trainee trainee = new Trainee();
        trainee.setId(1L);

        when(mockTraineeDAO.findByUsernameAndPassword("johndoe", "password"))
                .thenReturn(Optional.of(trainee));

        Optional<Trainee> result = service.findByUsernameAndPassword("johndoe", "password");

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        verify(mockTraineeDAO, times(1)).findByUsernameAndPassword("johndoe", "password");
    }

    @Test
    void findByUsernameAndPassword_ShouldThrowException_WhenCredentialsAreInvalid() {
        when(mockTraineeDAO.findByUsernameAndPassword("johndoe", "wrongpassword"))
                .thenReturn(Optional.empty());

        Optional<Trainee> result = service.findByUsernameAndPassword("johndoe", "wrongpassword");

        assertFalse(result.isPresent());
    }
/*
        @Test
        void updateTraineeTrainers_ShouldUpdateTrainerList() {
            User user = new User();
            user.setUsername("johndoe");

            Trainee trainee = new Trainee();
            trainee.setUser(user);

            Training existingTraining = new Training();
            existingTraining.setId(1L);

            Training newTraining = new Training();

            when(mockTraineeDAO.findByUser_Username("johndoe")).thenReturn(Optional.of(trainee));
            when(mockTrainingDAO.findByTraineeUsername("johndoe")).thenReturn(List.of(existingTraining));
            doNothing().when(mockTrainingDAO).delete(anyLong());
            doNothing().when(mockTrainingDAO).save(any(Training.class));

            service.updateTraineeTrainers("johndoe", List.of(2L));

            verify(mockTraineeDAO, times(1)).findByUser_Username("johndoe");
            verify(mockTrainingDAO, times(1)).findByTraineeUsername("johndoe");
            verify(mockTrainingDAO, times(1)).delete(existingTraining.getId());
            verify(mockTrainingDAO, times(1)).save(any(Training.class));
        }
    }

 */
}