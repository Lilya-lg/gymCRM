package uz.gym.crm.dao;
import org.junit.jupiter.api.BeforeEach;
import uz.gym.crm.domain.Training;

import java.time.LocalDate;
import java.util.concurrent.ConcurrentHashMap;

class TrainingDAOTest extends BaseDAOTest<Training, Integer> {

    private TrainingDAOImpl trainingDAO;

    @BeforeEach
    void setUp() {
        trainingDAO = new TrainingDAOImpl(new ConcurrentHashMap<>());
    }

    @Override
    protected BaseDAO<Training, Integer> getDAO() {
        return trainingDAO;
    }

    @Override
    protected Training createEntity() {
        Training training = new Training();
        training.setId(1);
        training.setTrainingName("Yoga Basics");
        training.setTrainingDate(LocalDate.now());
        training.setTrainingDuration(60);
        return training;
    }

    @Override
    protected Training updateEntity(Training entity) {
        entity.setTrainingName("Advanced Yoga");
        return entity;
    }

    @Override
    protected Integer getId(Training entity) {
        return entity.getId();
    }
}
