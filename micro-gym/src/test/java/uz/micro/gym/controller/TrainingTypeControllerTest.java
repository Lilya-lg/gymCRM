package uz.micro.gym.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import uz.micro.gym.dto.TrainingTypeDTO;
import uz.micro.gym.service.abstr.TrainingTypeService;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

class TrainingTypeControllerTest {

  @Mock private TrainingTypeService trainingTypeService;

  @InjectMocks private TrainingTypeController trainingTypeController;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testGetTrainingTypes() {
    TrainingTypeDTO trainingTypeDTO1 = new TrainingTypeDTO();
    trainingTypeDTO1.setName("Yoga");
    TrainingTypeDTO trainingTypeDTO2 = new TrainingTypeDTO();
    trainingTypeDTO2.setName("Pilates");
    List<TrainingTypeDTO> trainingTypes = Arrays.asList(trainingTypeDTO1, trainingTypeDTO2);
    when(trainingTypeService.getAllTrainingTypes()).thenReturn(trainingTypes);

    ResponseEntity<List<TrainingTypeDTO>> response = trainingTypeController.getTrainingTypes();

    assertNotNull(response.getBody());
    assertEquals(2, response.getBody().size());
    assertEquals("Yoga", response.getBody().get(0).getName());
    assertEquals("Pilates", response.getBody().get(1).getName());
  }
}
