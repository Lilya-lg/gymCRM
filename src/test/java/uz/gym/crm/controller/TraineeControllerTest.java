package uz.gym.crm.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import uz.gym.crm.domain.Trainee;
import uz.gym.crm.domain.Trainer;
import uz.gym.crm.domain.User;
import uz.gym.crm.dto.*;
import uz.gym.crm.mapper.Mapper;
import uz.gym.crm.service.abstr.TraineeService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TraineeControllerTest {

    @Mock
    private TraineeService traineeService;

    @Mock
    private Mapper mapper;

    @InjectMocks
    private TraineeController traineeController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getTraineeProfile_ShouldReturnProfile() {
        TraineeProfileDTO profileDTO = new TraineeProfileDTO();
        profileDTO.setFirstName("testuser");

        when(traineeService.getTraineeProfile("testuser")).thenReturn(profileDTO);

        ResponseEntity<TraineeProfileDTO> response = traineeController.getTraineeProfile("testuser", null);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("testuser", response.getBody().getFirstName());
    }

    @Test
    void createTrainee_ShouldReturnCreatedUser() {
        TraineeProfileDTO profileDTO = new TraineeProfileDTO();
        profileDTO.setFirstName("newuser");

        User user = new User();
        user.setUsername("newuser");
        user.setPassword("password");

        Trainee trainee = new Trainee();
        trainee.setUser(user); // Ensure the User object is initialized

        when(mapper.toTrainee(profileDTO)).thenReturn(trainee);
        doNothing().when(traineeService).create(trainee);

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false); // Simulate no validation errors

        ResponseEntity<?> response = traineeController.createTrainee(profileDTO, bindingResult);

        assertEquals(200, response.getStatusCodeValue());
        BaseUserDTO responseBody = (BaseUserDTO) response.getBody();
        assertNotNull(responseBody);
        assertEquals("newuser", responseBody.getUsername());
    }

    @Test
    void deleteTraineeProfile_ShouldReturnSuccessMessage() {
        doNothing().when(traineeService).deleteProfileByUsername("testuser");

        ResponseEntity<String> response = traineeController.deleteTraineeProfile("testuser");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Trainee profile deleted successfully", response.getBody());
    }

    @Test
    void updateTraineeProfile_ShouldReturnUpdatedProfile() {
        TraineeUpdateDTO updateDTO = new TraineeUpdateDTO();
        updateDTO.setAddress("123456789");

        TraineeProfileDTO updatedProfile = new TraineeProfileDTO();
        updatedProfile.setAddress("123456789");

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);

        doNothing().when(traineeService).updateTraineeProfile("testuser", updateDTO);
        when(traineeService.getTraineeProfile("testuser")).thenReturn(updatedProfile);

        ResponseEntity<ResponseWrapper<TraineeProfileDTO>> response =
                traineeController.updateTraineeProfile("testuser", updateDTO, bindingResult);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("123456789", response.getBody().getData().getAddress());
    }

    @Test
    void updateTraineeTrainers_ShouldReturnUpdatedTrainers() {
        UpdateTraineeTrainersDTO updateDTO = new UpdateTraineeTrainersDTO();
        updateDTO.setTrainerUsernames(List.of("trainer1", "trainer2"));

        Trainer trainer1 = new Trainer();
        Trainer trainer2 = new Trainer();
        when(traineeService.updateTraineeTrainerList("testuser", updateDTO.getTrainerUsernames()))
                .thenReturn(List.of(trainer1, trainer2));

        TrainerDTO trainerDTO1 = new TrainerDTO();
        TrainerDTO trainerDTO2 = new TrainerDTO();
        when(mapper.mapTrainersToProfileDTOs(List.of(trainer1, trainer2)))
                .thenReturn(List.of(trainerDTO1, trainerDTO2));

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);

        ResponseEntity<List<TrainerDTO>> response = traineeController.updateTraineeTrainers("testuser", updateDTO, bindingResult);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void updateTraineeStatus_ShouldActivateTrainee() {
        doNothing().when(traineeService).activate("testuser");

        ResponseEntity<Void> response = traineeController.updateTraineeStatus("testuser", true);

        assertEquals(200, response.getStatusCodeValue());
        verify(traineeService, times(1)).activate("testuser");
    }

    @Test
    void updateTraineeStatus_ShouldDeactivateTrainee() {
        doNothing().when(traineeService).deactivate("testuser");

        ResponseEntity<Void> response = traineeController.updateTraineeStatus("testuser", false);

        assertEquals(200, response.getStatusCodeValue());
        verify(traineeService, times(1)).deactivate("testuser");
    }

    @Test
    void createTrainee_ShouldReturnBadRequest_WhenValidationFails() {
        TraineeProfileDTO profileDTO = new TraineeProfileDTO();

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getAllErrors()).thenReturn(List.of(new ObjectError("trainee", "Validation failed")));

        ResponseEntity<?> response = traineeController.createTrainee(profileDTO, bindingResult);

        assertEquals(400, response.getStatusCodeValue());
        verifyNoInteractions(mapper);
        verifyNoInteractions(traineeService);
    }

    @Test
    void deleteTraineeProfile_ShouldReturnBadRequest_WhenTraineeNotFound() {
        doThrow(new IllegalArgumentException("Trainee not found")).when(traineeService).deleteProfileByUsername("invalidUser");

        ResponseEntity<String> response = traineeController.deleteTraineeProfile("invalidUser");

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Trainee not found", response.getBody());
    }

    @Test
    void deleteTraineeProfile_ShouldReturnInternalServerError_WhenUnexpectedErrorOccurs() {
        doThrow(new RuntimeException("Unexpected error")).when(traineeService).deleteProfileByUsername("testuser");

        ResponseEntity<String> response = traineeController.deleteTraineeProfile("testuser");

        assertEquals(500, response.getStatusCodeValue());
        assertEquals("An unexpected error occurred", response.getBody());
    }

    @Test
    void updateTraineeProfile_ShouldReturnBadRequest_WhenValidationFails() {
        TraineeUpdateDTO updateDTO = new TraineeUpdateDTO();
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);

        ResponseEntity<ResponseWrapper<TraineeProfileDTO>> response = traineeController.updateTraineeProfile("testuser", updateDTO, bindingResult);

        assertEquals(400, response.getStatusCodeValue());
        assertNull(response.getBody());
        verifyNoInteractions(traineeService);
    }

    @Test
    void updateTraineeStatus_ShouldReturnBadRequest_WhenIllegalArgumentExceptionIsThrown() {
        doThrow(new IllegalArgumentException("Invalid username")).when(traineeService).activate("invalidUser");

        ResponseEntity<Void> response = traineeController.updateTraineeStatus("invalidUser", true);

        assertEquals(400, response.getStatusCodeValue());
        verify(traineeService, times(1)).activate("invalidUser");
    }


}
