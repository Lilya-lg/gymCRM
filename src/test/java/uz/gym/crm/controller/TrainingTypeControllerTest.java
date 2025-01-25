package uz.gym.crm.controller;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import uz.gym.crm.dto.TrainingTypeDTO;
import uz.gym.crm.service.abstr.TrainingTypeService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainingTypeControllerTest {

    @Mock
    private TrainingTypeService trainingTypeService;

    @InjectMocks
    private TrainingTypeController trainingTypeController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getTrainingTypes_ShouldReturnListOfTrainingTypes() {

        TrainingTypeDTO trainingType1 = new TrainingTypeDTO(1L, "Yoga");
        TrainingTypeDTO trainingType2 = new TrainingTypeDTO(2L, "Cardio");


        List<TrainingTypeDTO> trainingTypes = Arrays.asList(trainingType1, trainingType2);
        when(trainingTypeService.getAllTrainingTypes()).thenReturn(trainingTypes);


        ResponseEntity<List<TrainingTypeDTO>> response = trainingTypeController.getTrainingTypes();


        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals("Yoga", response.getBody().get(0).getName());
        assertEquals("Cardio", response.getBody().get(1).getName());
    }

    @Test
    void getTrainingTypes_ShouldReturnEmptyList() {

        when(trainingTypeService.getAllTrainingTypes()).thenReturn(Collections.emptyList());


        ResponseEntity<List<TrainingTypeDTO>> response = trainingTypeController.getTrainingTypes();


        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void getTrainingTypes_ShouldHandleIllegalArgumentException() {

        when(trainingTypeService.getAllTrainingTypes()).thenThrow(new IllegalArgumentException("Invalid request"));


        ResponseEntity<List<TrainingTypeDTO>> response = trainingTypeController.getTrainingTypes();


        assertEquals(400, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void getTrainingTypes_ShouldHandleGeneralException() {

        when(trainingTypeService.getAllTrainingTypes()).thenThrow(new RuntimeException("Unexpected error"));


        ResponseEntity<List<TrainingTypeDTO>> response = trainingTypeController.getTrainingTypes();


        assertEquals(500, response.getStatusCodeValue());
        assertNull(response.getBody());
    }
}

