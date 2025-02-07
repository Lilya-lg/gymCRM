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
import uz.gym.crm.domain.Trainer;
import uz.gym.crm.domain.User;
import uz.gym.crm.dto.*;
import uz.gym.crm.dto.abstr.BaseTrainerDTO;
import uz.gym.crm.mapper.Mapper;
import uz.gym.crm.metrics.MetricsService;
import uz.gym.crm.service.abstr.TrainerService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TrainerControllerTest {

    @Mock
    private TrainerService trainerService;

    @Mock
    private Mapper mapper;
    @Mock
    private MetricsService meterRegistry;
    @Mock
    private Counter counter;
    @Mock
    private Timer timer;


    @InjectMocks
    private TrainerController trainerController;

    private TrainerDTO trainerDTO;
    private TrainerProfileDTO trainerProfileDTO;
    private BaseUserDTO baseUserDTO;
    private TrainerProfileResponseDTO trainerProfileResponseDTO;
    private BaseTrainerDTO baseTrainerDTO;

    @BeforeEach
    void setUp() {
        lenient().when(meterRegistry.createCounter(any(String.class), any(String[].class))).thenReturn(counter);
        lenient().when(meterRegistry.createTimer(any(String.class), any(String[].class))).thenReturn(timer);
        trainerController = new TrainerController(trainerService, mapper, meterRegistry);

        trainerDTO = new TrainerDTO();
        trainerDTO.setUsername("trainer1");
        trainerDTO.setFirstName("Trainer");
        trainerDTO.setSecondName("One");
        trainerDTO.setSpecialization("Fitness");

        baseUserDTO = new BaseUserDTO("trainer1", "password123");

        trainerProfileDTO = new TrainerProfileDTO();
        trainerProfileDTO.setUsername("trainer1");
        trainerProfileDTO.setFirstName("Trainer");
        trainerProfileDTO.setSecondName("One");
        trainerProfileDTO.setSpecialization("Fitness");
        trainerProfileDTO.setIsActive(true);
        trainerProfileDTO.setTrainees(Collections.emptyList());
        trainerProfileResponseDTO = new TrainerProfileResponseDTO();
        trainerProfileResponseDTO.setFirstName("Trainer");
        trainerProfileResponseDTO.setSecondName("One");
        trainerProfileResponseDTO.setSpecialization("Fitness");
        trainerProfileResponseDTO.setIsActive(true);

    }

    @Test
    void testCreateTrainer() {
        Trainer trainer = new Trainer();
        User user = new User();
        user.setUsername("trainer1");
        user.setPassword("password123");
        trainer.setUser(user);

        when(mapper.toTrainer(trainerDTO)).thenReturn(trainer);
        doNothing().when(trainerService).create(trainer);


        ResponseEntity<BaseUserDTO> response = trainerController.createTrainer(trainerDTO);


        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(baseUserDTO.getUsername(), response.getBody().getUsername());
        verify(trainerService, times(1)).create(trainer);
    }

    @Test
    void testUpdateTrainerProfile() {

        doNothing().when(trainerService).updateTrainerProfile("trainer1", trainerProfileDTO);
        when(trainerService.getTrainerProfileResponse("trainer1")).thenReturn(trainerProfileResponseDTO);


        ResponseEntity<TrainerProfileResponseDTO> response = trainerController.updateTrainerProfile(trainerProfileDTO);


        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(trainerProfileResponseDTO, response.getBody());
        verify(trainerService, times(1)).updateTrainerProfile("trainer1", trainerProfileDTO);
        verify(trainerService, times(1)).getTrainerProfileResponse("trainer1");
        verify(trainerService, times(1)).getTrainerProfileResponse("trainer1");
    }

    @Test
    void testGetTrainerProfile() {

        when(trainerService.getTrainerProfile("trainer1")).thenReturn(null);


        ResponseEntity<BaseTrainerDTO> response = trainerController.getTrainerProfile("trainer1");


        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(baseTrainerDTO, response.getBody());
        verify(trainerService, times(1)).getTrainerProfile("trainer1");
    }

    @Test
    void testGetUnassignedTrainee() {

        List<TrainerDTO> trainerDTOs = Arrays.asList(new TrainerDTO(), new TrainerDTO());
        when(trainerService.getUnassignedTrainersForTrainee("trainee1")).thenReturn(Arrays.asList(new Trainer(), new Trainer()));
        when(mapper.mapTrainersToProfileDTOs(any())).thenReturn(trainerDTOs);


        ResponseEntity<List<TrainerDTO>> response = trainerController.getUnassignedTrainee("trainee1");


        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(trainerDTOs, response.getBody());
        verify(trainerService, times(1)).getUnassignedTrainersForTrainee("trainee1");
        verify(mapper, times(1)).mapTrainersToProfileDTOs(any());
    }
}