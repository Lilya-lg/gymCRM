package uz.gym.training.messaging;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uz.gym.crm.dto.TrainingSessionDTO;
import uz.gym.training.service.TrainingService;

import static org.mockito.Mockito.*;

class TrainingMessageConsumerTest {

    private TrainingService trainingService;
    private TrainingMessageConsumer trainingMessageConsumer;

    @BeforeEach
    void setUp() {
        trainingService = mock(TrainingService.class);
        trainingMessageConsumer = new TrainingMessageConsumer(trainingService);
    }

    @Test
    void receiveTrainingSession_shouldCallAddTraining_whenActionIsAdd() {
        TrainingSessionDTO dto = new TrainingSessionDTO();
        dto.setActionType("ADD");

        trainingMessageConsumer.receiveTrainingSession(dto);

        verify(trainingService, times(1)).addTraining(dto);
        verify(trainingService, never()).deleteTraining(any());
    }

    @Test
    void receiveTrainingSession_shouldCallDeleteTraining_whenActionIsDelete() {
        TrainingSessionDTO dto = new TrainingSessionDTO();
        dto.setActionType("DELETE");

        trainingMessageConsumer.receiveTrainingSession(dto);

        verify(trainingService, times(1)).deleteTraining(dto);
        verify(trainingService, never()).addTraining(any());
    }

    @Test
    void receiveTrainingSession_shouldDoNothing_whenActionIsUnknown() {
        TrainingSessionDTO dto = new TrainingSessionDTO();
        dto.setActionType("UPDATE");

        trainingMessageConsumer.receiveTrainingSession(dto);

        verify(trainingService, never()).addTraining(any());
        verify(trainingService, never()).deleteTraining(any());
    }

    @Test
    void receiveTrainingSession_shouldBeCaseInsensitive() {
        TrainingSessionDTO dto = new TrainingSessionDTO();
        dto.setActionType("add");

        trainingMessageConsumer.receiveTrainingSession(dto);

        verify(trainingService, times(1)).addTraining(dto);
    }
}
