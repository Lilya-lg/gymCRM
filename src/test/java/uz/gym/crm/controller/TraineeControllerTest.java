package uz.gym.crm.controller;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uz.gym.crm.domain.Trainee;
import uz.gym.crm.domain.User;
import uz.gym.crm.dto.*;
import uz.gym.crm.mapper.Mapper;
import uz.gym.crm.service.abstr.TraineeService;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TraineeControllerTest {

    @Mock
    private TraineeService traineeService;

    @Mock
    private Mapper mapper;

    @Mock
    private MeterRegistry meterRegistry;

    @Mock
    private Counter counter;

    @Mock
    private Timer timer;

    @InjectMocks
    private TraineeController traineeController;

    private TraineeProfileDTO traineeProfileDTO;
    private BaseUserDTO baseUserDTO;
    private TraineeUpdateDTO traineeUpdateDTO;
    private UpdateTraineeTrainersDTO updateTraineeTrainersDTO;

    @BeforeEach
    void setUp() {
        lenient().when(meterRegistry.counter(any(String.class), any(String[].class))).thenReturn(counter);
        lenient().when(meterRegistry.timer(any(String.class), any(String[].class))).thenReturn(timer);
        traineeController = new TraineeController(traineeService, mapper, meterRegistry);
        traineeProfileDTO = new TraineeProfileDTO();
        traineeProfileDTO.setFirstName("John");
        traineeProfileDTO.setSecondName("Doe");
        traineeProfileDTO.setDateOfBirth("1970-01-01");
        traineeProfileDTO.setAddress("123 Main St");
        traineeProfileDTO.setActive(true);

        baseUserDTO = new BaseUserDTO("john.doe", "password123");

        traineeUpdateDTO = new TraineeUpdateDTO();
        traineeUpdateDTO.setUsername("john.doe");
        traineeUpdateDTO.setFirstName("John");
        traineeUpdateDTO.setSecondName("Doe");
        traineeUpdateDTO.setDateOfBirth("1970-01-01");
        traineeUpdateDTO.setAddress("123 Main St");
        traineeUpdateDTO.setIsActive("true");

        updateTraineeTrainersDTO = new UpdateTraineeTrainersDTO();
        updateTraineeTrainersDTO.setTrainerUsernames(Arrays.asList("trainer1", "trainer2"));
    }


    @Test
    void testGetTraineeProfile() {
        when(traineeService.getTraineeProfile("john.doe")).thenReturn(traineeProfileDTO);

        ResponseEntity<TraineeProfileDTO> response = traineeController.getTraineeProfile("john.doe");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(traineeProfileDTO, response.getBody());
        verify(traineeService, times(1)).getTraineeProfile("john.doe");

        verify(counter, times(1)).increment(); // Ensure counter is used
    }

    @Test
    void testCreateTrainee() {
        User user = new User();
        user.setUsername("john.doe");
        user.setPassword("password123");

        Trainee trainee = new Trainee();
        trainee.setUser(user);

        when(mapper.toTrainee(traineeProfileDTO)).thenReturn(trainee);
        doNothing().when(traineeService).create(trainee);

        ResponseEntity<BaseUserDTO> response = traineeController.createTrainee(traineeProfileDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(baseUserDTO.getUsername(), response.getBody().getUsername());
        verify(traineeService, times(1)).create(trainee);

        verify(timer, times(1)).record(anyLong(), any(TimeUnit.class)); // Ensure timer is used
    }

    @Test
    void testUpdateTraineeProfile() {
        when(traineeService.getTraineeProfile("john.doe")).thenReturn(traineeProfileDTO);

        ResponseEntity<TraineeProfileDTO> response = traineeController.updateTraineeProfile(traineeUpdateDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(traineeProfileDTO, response.getBody());
        verify(traineeService, times(1)).updateTraineeProfile("john.doe", traineeUpdateDTO);
        verify(traineeService, times(1)).getTraineeProfile("john.doe");
    }

    @Test
    void testDeleteTraineeProfile() {
        doNothing().when(traineeService).deleteProfileByUsername("john.doe");

        ResponseEntity<String> response = traineeController.deleteTraineeProfile("john.doe");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Trainee profile deleted successfully", response.getBody());
        verify(traineeService, times(1)).deleteProfileByUsername("john.doe");
    }
}
