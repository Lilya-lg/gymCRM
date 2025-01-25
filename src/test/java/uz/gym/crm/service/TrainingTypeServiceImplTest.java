package uz.gym.crm.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import uz.gym.crm.dao.TrainingTypeDAOImpl;
import uz.gym.crm.dao.UserDAOImpl;
import uz.gym.crm.domain.PredefinedTrainingType;
import uz.gym.crm.domain.TrainingType;
import uz.gym.crm.dto.TrainingTypeDTO;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainingTypeServiceImplTest {

    private TrainingTypeDAOImpl mockTrainingTypeDAO;
    private UserDAOImpl mockUserDAO;
    private TrainingTypeServiceImpl service;

    @BeforeEach
    void setUp() {
        mockTrainingTypeDAO = Mockito.mock(TrainingTypeDAOImpl.class);
        mockUserDAO = Mockito.mock(UserDAOImpl.class);
        service = new TrainingTypeServiceImpl(mockTrainingTypeDAO, mockUserDAO);
    }

    @Test
    void getOrCreateTrainingType_ShouldReturnExistingType() {

        PredefinedTrainingType predefinedType = PredefinedTrainingType.YOGA;
        TrainingType trainingType = new TrainingType();
        trainingType.setTrainingType(predefinedType);

        when(mockTrainingTypeDAO.getOrCreateTrainingType(predefinedType)).thenReturn(trainingType);


        TrainingType result = service.getOrCreateTrainingType(predefinedType);


        assertNotNull(result, "TrainingType should not be null");
        assertEquals(predefinedType, result.getTrainingType(), "TrainingType should match the predefined type");
        verify(mockTrainingTypeDAO, times(1)).getOrCreateTrainingType(predefinedType);
    }

    @Test
    void getAllTrainingTypes_ShouldReturnListOfTrainingTypeDTOs() {

        TrainingType trainingType1 = new TrainingType();
        trainingType1.setTrainingType(PredefinedTrainingType.YOGA);

        TrainingType trainingType2 = new TrainingType();
        trainingType2.setTrainingType(PredefinedTrainingType.CARDIO);

        List<TrainingType> trainingTypes = Arrays.asList(trainingType1, trainingType2);
        when(mockTrainingTypeDAO.getAll()).thenReturn(trainingTypes);


        List<TrainingTypeDTO> result = service.getAllTrainingTypes();


        assertNotNull(result, "Result should not be null");
        assertEquals(2, result.size(), "Result list size should be 2");
        assertEquals("YOGA", result.get(0).getName(), "First training type name should match");
        assertEquals("CARDIO", result.get(1).getName(), "Second training type name should match");
        verify(mockTrainingTypeDAO, times(1)).getAll();
    }
}
