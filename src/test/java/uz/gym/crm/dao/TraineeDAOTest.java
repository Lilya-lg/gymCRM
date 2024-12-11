package uz.gym.crm.dao;
import org.junit.jupiter.api.BeforeEach;
import uz.gym.crm.domain.Trainee;

import java.util.HashMap;

class TraineeDAOTest extends BaseDAOTest<Trainee, Integer> {

    private TraineeDAO traineeDAO;

    @BeforeEach
    void setUp() {
        traineeDAO = new TraineeDAOImpl(new HashMap<>());
    }

    @Override
    protected BaseDAO<Trainee, Integer> getDAO() {
        return traineeDAO;
    }

    @Override
    protected Trainee createEntity() {
        Trainee trainee = new Trainee();
        trainee.setId(1);
        trainee.setUserId(2);
        return trainee;
    }

    @Override
    protected Trainee updateEntity(Trainee trainee) {
        trainee.setUserId(3);
        return trainee;
    }

    @Override
    protected Integer getId(Trainee trainee) {
        return trainee.getId();
    }
}
