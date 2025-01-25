package uz.gym.crm.controller;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import uz.gym.crm.domain.Trainer;
import uz.gym.crm.domain.User;
import uz.gym.crm.dto.*;
import uz.gym.crm.dto.abstr.BaseTrainerDTO;
import uz.gym.crm.mapper.Mapper;
import uz.gym.crm.service.abstr.ProfileService;
import uz.gym.crm.service.abstr.TrainerService;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainerControllerTest {

    @Mock
    private TrainerService trainerService;

    @Mock
    private Mapper mapper;
    @Mock
    ProfileService<Trainer> abstractProfileService = mock(ProfileService.class);

    @InjectMocks
    private TrainerController trainerController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createTrainer_ShouldReturnCreatedUser() {

        TrainerDTO trainerDTO = new TrainerDTO();
        trainerDTO.setUsername("trainer1");

        Trainer trainer = new Trainer();
        User user = new User();
        user.setUsername("trainer1");
        user.setPassword("password");
        trainer.setUser(user);

        when(mapper.toTrainer(trainerDTO)).thenReturn(trainer);
        doNothing().when(trainerService).create(trainer);


        ResponseEntity<?> response = trainerController.createTrainer(trainerDTO, mockBindingResult(false));


        assertEquals(200, response.getStatusCodeValue());
        BaseUserDTO responseBody = (BaseUserDTO) response.getBody();
        assertNotNull(responseBody);
        assertEquals("trainer1", responseBody.getUsername());
    }

    @Test
    void updateTrainerProfile_ShouldReturnUpdatedProfile() {

        String username = "trainer1";
        TrainerProfileDTO trainerProfileDTO = new TrainerProfileDTO();
        trainerProfileDTO.setFirstName(username);

        when(trainerService.getTrainerProfile(username)).thenReturn(trainerProfileDTO);


        ResponseEntity<ResponseWrapper<TrainerProfileDTO>> response = trainerController.updateTrainerProfile(username, trainerProfileDTO, mockBindingResult(false));


        assertEquals(200, response.getStatusCodeValue());
        ResponseWrapper<TrainerProfileDTO> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(username, responseBody.getUsername());
    }

    @Test
    void getTrainerProfile_ShouldReturnProfile() {

        String username = "trainer1";
        TrainerProfileDTO trainerProfileDTO = new TrainerProfileDTO();
        trainerProfileDTO.setFirstName(username);

        when(trainerService.getTrainerProfile(username)).thenReturn(trainerProfileDTO);


        ResponseEntity<BaseTrainerDTO> response = trainerController.getTrainerProfile(username, null);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(username, response.getBody().getFirstName());
    }

    @Test
    void getUnassignedTrainee_ShouldReturnTrainerList() {

        String username = "trainee1";
        TrainerDTO trainerDTO = new TrainerDTO();
        trainerDTO.setUsername("trainer1");

        when(trainerService.getUnassignedTrainersForTrainee(username)).thenReturn(Collections.singletonList(new Trainer()));
        when(mapper.mapTrainersToProfileDTOs(anyList())).thenReturn(Collections.singletonList(trainerDTO));


        ResponseEntity<List<TrainerDTO>> response = trainerController.getUnassignedTrainee(username, null);


        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("trainer1", response.getBody().get(0).getUsername());
    }

    @Test
    void updateTrainerStatus_ShouldActivateTrainer() {

        String username = "trainer1";


        doNothing().when(trainerService).activate(username);


        ResponseEntity<Void> response = trainerController.updateTrainerStatus(username, true);

        assertEquals(200, response.getStatusCodeValue(), "Response should have status 200");
        verify(abstractProfileService, times(1)).activate(username); // Ensure the activate method was called once
    }


    private BindingResult mockBindingResult(boolean hasErrors) {
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(hasErrors);
        return bindingResult;
    }
}

