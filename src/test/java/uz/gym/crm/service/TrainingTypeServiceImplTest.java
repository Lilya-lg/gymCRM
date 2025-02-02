package uz.gym.crm.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uz.gym.crm.domain.PredefinedTrainingType;
import uz.gym.crm.domain.TrainingType;
import uz.gym.crm.dto.TrainingTypeDTO;
import uz.gym.crm.repository.TrainingTypeRepository;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainingTypeServiceImplTest {

    @Mock
    private TrainingTypeRepository trainingTypeRepository;

    @InjectMocks
    private TrainingTypeServiceImpl trainingTypeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getOrCreateTrainingType() {
        PredefinedTrainingType predefinedType = PredefinedTrainingType.CARDIO;
        TrainingType trainingType = new TrainingType();

        when(trainingTypeRepository.getOrCreateTrainingType(predefinedType)).thenReturn(trainingType);

        TrainingType result = trainingTypeService.getOrCreateTrainingType(predefinedType);

        assertNotNull(result);
        assertEquals(trainingType, result);
    }

    @Test
    void getAllTrainingTypes() {
        TrainingType trainingType = new TrainingType();
        trainingType.setId(1L);
        trainingType.setTrainingType(PredefinedTrainingType.CARDIO);

        when(trainingTypeRepository.findAll()).thenReturn(Collections.singletonList(trainingType));

        List<TrainingTypeDTO> result = trainingTypeService.getAllTrainingTypes();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals("CARDIO", result.get(0).getName());
    }

}