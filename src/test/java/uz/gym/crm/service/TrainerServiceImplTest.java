package uz.gym.crm.service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import uz.gym.crm.dao.TrainerDAOImpl;
import uz.gym.crm.dao.UserDAOImpl;
import uz.gym.crm.domain.Trainer;
import uz.gym.crm.domain.User;


import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainerServiceImplTest {

    private UserDAOImpl mockUserDAO;
    private TrainerDAOImpl mockTrainerDAO;
    private TrainerServiceImpl service;

    @BeforeEach
    void setUp() {
        mockUserDAO = Mockito.mock(UserDAOImpl.class);
        mockTrainerDAO = Mockito.mock(TrainerDAOImpl.class);
        service = new TrainerServiceImpl(mockUserDAO, mockTrainerDAO);
    }

    @Test
    void create_ShouldPrepareUserAndSaveTrainer() {
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");

        Trainer trainer = new Trainer();
        trainer.setUser(user);

        doNothing().when(mockTrainerDAO).save(any(Trainer.class));

        service.create(trainer);

        assertNotNull(user.getUsername());
        assertNotNull(user.getPassword());
        verify(mockTrainerDAO, times(1)).save(trainer);
    }

    @Test
    void findByUsernameAndPassword_ShouldReturnTrainer_WhenCredentialsAreValid() {
        Trainer trainer = new Trainer();
        trainer.setId(1L);

        when(mockTrainerDAO.findByUser_UsernameAndUser_Password("johndoe", "password"))
                .thenReturn(Optional.of(trainer));

        Optional<Trainer> result = service.findByUsernameAndPassword("johndoe", "password");

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        verify(mockTrainerDAO, times(1)).findByUser_UsernameAndUser_Password("johndoe", "password");
    }

    @Test
    void findByUsernameAndPassword_ShouldThrowException_WhenErrorOccurs() {
        when(mockTrainerDAO.findByUser_UsernameAndUser_Password("johndoe", "password"))
                .thenThrow(new RuntimeException("Database error"));

        Exception exception = assertThrows(RuntimeException.class, () ->
                service.findByUsernameAndPassword("johndoe", "password"));

        assertEquals("Database error", exception.getMessage());
        verify(mockTrainerDAO, times(1)).findByUser_UsernameAndUser_Password("johndoe", "password");
    }
    /*
    @Test
    void getUnassignedTrainersForTrainee_ShouldReturnListOfTrainers() {
        Trainer trainer1 = new Trainer();
        trainer1.setId(1L);
        Trainer trainer2 = new Trainer();
        trainer2.setId(2L);

        when(mockTrainerDAO.findUnassignedTrainersForTrainee("trainee1"))
                .thenReturn(List.of(trainer1, trainer2));

        List<Trainer> result = service.getUnassignedTrainersForTrainee("trainee1");

        assertEquals(2, result.size());
        verify(mockTrainerDAO, times(1)).findUnassignedTrainersForTrainee("trainee1");
    }

    @Test
    void getUnassignedTrainersForTrainee_ShouldReturnEmptyList_WhenNoTrainersFound() {
        when(mockTrainerDAO.findUnassignedTrainersForTrainee("trainee1"))
                .thenReturn(List.of());

        List<Trainer> result = service.getUnassignedTrainersForTrainee("trainee1");

        assertTrue(result.isEmpty());
        verify(mockTrainerDAO, times(1)).findUnassignedTrainersForTrainee("trainee1");
    }

     */
}

