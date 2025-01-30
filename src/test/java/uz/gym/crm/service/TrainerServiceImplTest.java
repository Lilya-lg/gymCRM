package uz.gym.crm.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import uz.gym.crm.dao.TrainerDAOImpl;
import uz.gym.crm.dao.TrainingDAOImpl;
import uz.gym.crm.dao.UserDAOImpl;
import uz.gym.crm.domain.*;
import uz.gym.crm.dto.TrainerProfileDTO;
import uz.gym.crm.dto.UserDTO;
import uz.gym.crm.mapper.Mapper;


import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainerServiceImplTest {

    private UserDAOImpl mockUserDAO;
    private TrainerDAOImpl mockTrainerDAO;
    private TrainerServiceImpl service;
    private TrainingDAOImpl mockTrainingDAO;
    private Mapper mockMapper;


    @BeforeEach
    void setUp() {
        mockUserDAO = Mockito.mock(UserDAOImpl.class);
        mockTrainerDAO = Mockito.mock(TrainerDAOImpl.class);
        mockTrainingDAO = Mockito.mock(TrainingDAOImpl.class);
        mockMapper = Mockito.mock(Mapper.class);
        service = new TrainerServiceImpl(mockUserDAO, mockTrainerDAO, mockTrainingDAO,mockMapper);
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
        User authenticatedUser = new User();
        authenticatedUser.setUsername("adminUser");
        authenticatedUser.setPassword("adminPassword");
        Trainer trainer = new Trainer();
        trainer.setId(1L);

        when(mockTrainerDAO.findByUsernameAndPassword("johndoe", "password")).thenReturn(Optional.of(trainer));

        when(mockUserDAO.findByUsernameAndPassword("adminUser", "adminPassword"))
                .thenReturn(Optional.of(authenticatedUser));
        Optional<Trainer> result = service.findByUsernameAndPassword("adminUser", "adminPassword","johndoe", "password");

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        verify(mockTrainerDAO, times(1)).findByUsernameAndPassword("johndoe", "password");
    }

    @Test
    void findByUsernameAndPassword_ShouldThrowException_WhenErrorOccurs() {
        User authenticatedUser = new User();
        authenticatedUser.setUsername("adminUser");
        authenticatedUser.setPassword("adminPassword");
        when(mockTrainerDAO.findByUsernameAndPassword("johndoe", "password")).thenThrow(new RuntimeException("Database error"));

        when(mockUserDAO.findByUsernameAndPassword("adminUser", "adminPassword"))
                .thenReturn(Optional.of(authenticatedUser));
        Exception exception = assertThrows(RuntimeException.class, () -> service.findByUsernameAndPassword("adminUser", "adminPassword","johndoe", "password"));

        assertEquals("Database error", exception.getMessage());
        verify(mockTrainerDAO, times(1)).findByUsernameAndPassword("johndoe", "password");
    }

    @Test
    void create_ShouldLogSuccessMessage() {
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
    void findByUsername_ShouldReturnTrainer() {
        Trainer trainer = new Trainer();
        trainer.setId(1L);

        when(mockTrainerDAO.findByUsername("johndoe")).thenReturn(Optional.of(trainer));

        Optional<Trainer> result = service.findByUsername("johndoe");

        assertTrue(result.isPresent(), "Trainer should be found");
        assertEquals(1L, result.get().getId());
        verify(mockTrainerDAO, times(1)).findByUsername("johndoe");
    }

    @Test
    void findByUsername_ShouldReturnEmptyOptional() {
        when(mockTrainerDAO.findByUsername("nonexistent")).thenReturn(Optional.empty());

        Optional<Trainer> result = service.findByUsername("nonexistent");

        assertTrue(result.isEmpty(), "No trainer should be found for invalid username");
        verify(mockTrainerDAO, times(1)).findByUsername("nonexistent");
    }

    @Test
    void getUnassignedTrainersForTrainee_ShouldReturnListOfTrainers() {

        User authenticatedUser = new User();
        authenticatedUser.setUsername("adminUser");
        authenticatedUser.setPassword("adminPassword");

        Trainer trainer1 = new Trainer();
        trainer1.setId(1L);
        Trainer trainer2 = new Trainer();
        trainer2.setId(2L);
        when(mockUserDAO.findByUsernameAndPassword("adminUser", "adminPassword"))
                .thenReturn(Optional.of(authenticatedUser));

        when(mockTrainerDAO.getUnassignedTrainersByTraineeUsername("trainee1")).thenReturn(List.of(trainer1, trainer2));

        List<Trainer> result = service.getUnassignedTrainersForTrainee("trainee1");

        assertEquals(2, result.size(), "Should return two unassigned trainers");
        assertTrue(result.contains(trainer1) && result.contains(trainer2), "Returned trainers should match expected");
        verify(mockTrainerDAO, times(1)).getUnassignedTrainersByTraineeUsername("trainee1");



    }

    @Test
    void getUnassignedTrainersForTrainee_ShouldReturnEmptyList() {
        User authenticatedUser = new User();
        authenticatedUser.setUsername("adminUser");
        authenticatedUser.setPassword("adminPassword");
        when(mockUserDAO.findByUsernameAndPassword("adminUser", "adminPassword"))
                .thenReturn(Optional.of(authenticatedUser));
        when(mockTrainerDAO.getUnassignedTrainersByTraineeUsername("trainee1")).thenReturn(List.of());

        List<Trainer> result = service.getUnassignedTrainersForTrainee("trainee1");

        assertTrue(result.isEmpty(), "No trainers should be found for unassigned trainers query");
        verify(mockTrainerDAO, times(1)).getUnassignedTrainersByTraineeUsername("trainee1");
    }



    @Test
    void getTrainerProfile_ShouldThrowException_WhenTrainerNotFound() {
        when(mockTrainerDAO.findByUsername("nonexistent")).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> service.getTrainerProfile("nonexistent"));

        assertEquals("Trainee not found", exception.getMessage());
    }

    @Test
    void updateTrainerProfile_ShouldUpdateProfile() {
        Trainer trainer = new Trainer();
        trainer.setUser(new User());
        trainer.getUser().setUsername("trainer1");

        TrainerProfileDTO profileDTO = new TrainerProfileDTO();
        profileDTO.setFirstName("UpdatedFirstName");

        when(mockTrainerDAO.findByUsername("trainer1")).thenReturn(Optional.of(trainer));
        doNothing().when(mockMapper).updateTrainerFromDTO(profileDTO, trainer);

        service.updateTrainerProfile("trainer1", profileDTO);

        verify(mockMapper, times(1)).updateTrainerFromDTO(profileDTO, trainer);
        verify(mockTrainerDAO, times(1)).update(trainer);
    }

    @Test
    void updateTrainerProfile_ShouldThrowException_WhenTrainerNotFound() {
        when(mockTrainerDAO.findByUsername("nonexistent")).thenReturn(Optional.empty());

        TrainerProfileDTO profileDTO = new TrainerProfileDTO();
        profileDTO.setFirstName("UpdatedFirstName");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> service.updateTrainerProfile("nonexistent", profileDTO));

        assertEquals("Trainer with username 'nonexistent' does not exist", exception.getMessage());
    }

    @Test
    void mapToTrainerProfileDTO_ShouldReturnMappedProfileDTO() {

        Trainer trainer = new Trainer();
        trainer.setId(1L);
        trainer.setUser(new User());
        trainer.getUser().setFirstName("John");
        trainer.getUser().setLastName("Doe");


        TrainingType trainingType = new TrainingType();
        trainingType.setTrainingType(PredefinedTrainingType.YOGA);
        trainer.setSpecialization(trainingType);

        Trainee trainee1 = new Trainee();
        trainee1.setUser(new User());
        trainee1.getUser().setFirstName("Trainee1");

        Trainee trainee2 = new Trainee();
        trainee2.setUser(new User());
        trainee2.getUser().setFirstName("Trainee2");


        when(mockTrainerDAO.findByUsername("johndoe")).thenReturn(Optional.of(trainer));
        when(mockTrainingDAO.findTraineesByTrainerId(trainer.getId())).thenReturn(List.of(trainee1, trainee2));


        TrainerProfileDTO result = service.getTrainerProfile("johndoe");


        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getSecondName());
        assertEquals("Yoga", result.getSpecialization());
        assertEquals(2, result.getTrainees().size());
        assertEquals("Trainee1", result.getTrainees().get(0).getFirstName());
        assertEquals("Trainee2", result.getTrainees().get(1).getFirstName());
    }


    @Test
    void mapTraineesToProfileDTOs_ShouldReturnMappedList() {
        Trainee trainee1 = new Trainee();
        trainee1.setUser(new User());
        trainee1.getUser().setFirstName("Trainee1");

        Trainee trainee2 = new Trainee();
        trainee2.setUser(new User());
        trainee2.getUser().setFirstName("Trainee2");

        List<UserDTO> result = service.mapTraineesToProfileDTOs(List.of(trainee1, trainee2));

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Trainee1", result.get(0).getFirstName());
        assertEquals("Trainee2", result.get(1).getFirstName());
    }
}

