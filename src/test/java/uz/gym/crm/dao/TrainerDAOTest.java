package uz.gym.crm.dao;

import org.junit.jupiter.api.BeforeEach;
import uz.gym.crm.domain.Trainer;

import java.util.HashMap;


class TrainerDAOTest extends BaseDAOTest<Trainer, Integer> {

    private TrainerDAO trainerDAO;

    @BeforeEach
    void setUp() {
        trainerDAO = new TrainerDAOImpl(new HashMap<>());
    }

    @Override
    protected BaseDAO<Trainer, Integer> getDAO() {
        return trainerDAO;
    }

    @Override
    protected Trainer createEntity() {
        Trainer trainer = new Trainer();
        trainer.setId(1);
        trainer.setSpecialization("Yoga");
        trainer.setUserId(1);
        return trainer;
    }

    @Override
    protected Trainer updateEntity(Trainer trainer) {
        trainer.setSpecialization("Pilates");
        return trainer;
    }

    @Override
    protected Integer getId(Trainer trainer) {
        return trainer.getId();
    }
}
