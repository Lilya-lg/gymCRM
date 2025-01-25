package uz.gym.crm.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;
import uz.gym.crm.domain.*;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TrainingDAOImplTest {

    private TrainingDAOImpl trainingDAO;
    private SessionFactory sessionFactory;
    private Session session;

    @BeforeEach
    void setUp() {
        // Configure Hibernate with H2 database
        Configuration configuration = new Configuration();
        configuration.addAnnotatedClass(Training.class);
        configuration.addAnnotatedClass(Trainer.class);
        configuration.addAnnotatedClass(Trainee.class);
        configuration.addAnnotatedClass(User.class);
        configuration.addAnnotatedClass(TrainingType.class);
        configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        configuration.setProperty("hibernate.connection.driver_class", "org.h2.Driver");
        configuration.setProperty("hibernate.connection.url", "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        configuration.setProperty("hibernate.hbm2ddl.auto", "create-drop");
        configuration.setProperty("hibernate.show_sql", "true");

        sessionFactory = configuration.buildSessionFactory();
        if (session != null) {
            session.close();
        }
        session = sessionFactory.openSession();


        Transaction transaction = session.beginTransaction();
        session.createQuery("DELETE FROM Training").executeUpdate();
        session.createQuery("DELETE FROM Trainer").executeUpdate();
        session.createQuery("DELETE FROM Trainee").executeUpdate();
        session.createQuery("DELETE FROM User").executeUpdate();
        session.createQuery("DELETE FROM TrainingType").executeUpdate();
        transaction.commit();
        trainingDAO = new TrainingDAOImpl(sessionFactory);
        session = sessionFactory.openSession();
        seedTrainingTypes();
    }

    private TrainingType getTrainingType(String type) {
        return session.createQuery("FROM TrainingType WHERE trainingType = :type", TrainingType.class).setParameter("type", PredefinedTrainingType.fromName(type)) // Convert string to enum
                .uniqueResult();
    }


    @AfterEach
    void tearDown() {
        if (session.isOpen()) {
            session.close();
        }
        sessionFactory.close();
    }

    @Test
    void findByCriteria_ShouldReturnMatchingTrainings() {

        User trainerUser = new User();
        trainerUser.setFirstName("John");
        trainerUser.setLastName("Doe");
        trainerUser.setUsername("trainerJohn");
        trainerUser.setPassword("password");

        Trainer trainer = new Trainer();
        trainer.setUser(trainerUser);
        trainer.setSpecialization(getTrainingType("Yoga"));

        User traineeUser = new User();
        traineeUser.setFirstName("Jane");
        traineeUser.setLastName("Smith");
        traineeUser.setUsername("traineeJane");
        traineeUser.setPassword("password");

        Trainee trainee = new Trainee();
        trainee.setUser(traineeUser);

        Training training = new Training();
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingType(getTrainingType("Yoga"));
        training.setTrainingName("Yoga Session");
        training.setTrainingDate(LocalDate.of(2024, 12, 1));
        training.setTrainingDuration(60);

        Transaction transaction = session.beginTransaction();
        session.save(trainerUser);
        session.save(trainer);
        session.save(traineeUser);
        session.save(trainee);
        session.save(training);
        transaction.commit();


        List<Training> results = trainingDAO.findByCriteria("traineeJane", PredefinedTrainingType.YOGA, LocalDate.of(2024, 12, 1), LocalDate.of(2024, 12, 31), "trainerJohn");


        assertEquals(1, results.size());
        assertEquals("Yoga", results.get(0).getTrainingType().getTrainingType().getDisplayName());
        assertEquals("Yoga Session", results.get(0).getTrainingName());
    }


    @Test
    void findByCriteriaForTrainer_ShouldReturnMatchingTrainings() {

        User trainerUser = new User();
        trainerUser.setFirstName("John");
        trainerUser.setLastName("Doe");
        trainerUser.setUsername("trainerJohn");
        trainerUser.setPassword("password");

        Trainer trainer = new Trainer();
        trainer.setUser(trainerUser);
        trainer.setSpecialization(getTrainingType("Yoga"));

        User traineeUser = new User();
        traineeUser.setFirstName("Jane");
        traineeUser.setLastName("Smith");
        traineeUser.setUsername("traineeJane");
        traineeUser.setPassword("password");

        Trainee trainee = new Trainee();
        trainee.setUser(traineeUser);

        Training training = new Training();
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingType(getTrainingType("Yoga"));
        training.setTrainingName("Yoga Session");
        training.setTrainingDate(LocalDate.of(2024, 12, 1));
        training.setTrainingDuration(60);

        Transaction transaction = session.beginTransaction();
        session.save(trainerUser);
        session.save(trainer);
        session.save(traineeUser);
        session.save(trainee);
        session.save(training);
        transaction.commit();


        List<Training> results = trainingDAO.findByCriteriaForTrainer("trainerJohn", LocalDate.of(2024, 12, 1), LocalDate.of(2024, 12, 31), "traineeJane");


        assertEquals(1, results.size());
        assertEquals("Yoga", results.get(0).getTrainingType().getTrainingType().getDisplayName());
        assertEquals("Yoga Session", results.get(0).getTrainingName());
    }

    @Test
    void findByCriteriaForTrainer_ShouldReturnEmptyListForNoMatches() {

        List<Training> results = trainingDAO.findByCriteriaForTrainer("nonexistentTrainer", LocalDate.now(), LocalDate.now().plusDays(10), "nonexistentTrainee");


        assertTrue(results.isEmpty(), "Expected no matching trainings");
    }

    private void seedTrainingTypes() {
        Transaction transaction = session.beginTransaction();
        for (PredefinedTrainingType type : PredefinedTrainingType.values()) {
            TrainingType trainingType = new TrainingType();
            trainingType.setTrainingType(type);
            session.save(trainingType);
        }
        transaction.commit();
    }

    @Test
    void findTrainersByTraineeId_ShouldReturnMatchingTrainers() {

        User trainerUser = new User();
        trainerUser.setFirstName("Trainer");
        trainerUser.setLastName("One");
        trainerUser.setUsername("trainer1");
        trainerUser.setPassword("password");

        Trainer trainer = new Trainer();
        trainer.setUser(trainerUser);
        trainer.setSpecialization(getTrainingType("Yoga"));

        User traineeUser = new User();
        traineeUser.setFirstName("Trainee");
        traineeUser.setLastName("One");
        traineeUser.setUsername("trainee1");
        traineeUser.setPassword("password");

        Trainee trainee = new Trainee();
        trainee.setUser(traineeUser);

        Training training = new Training();
        training.setTrainer(trainer);
        training.setTrainee(trainee);
        training.setTrainingType(getTrainingType("Yoga"));
        training.setTrainingName("Yoga Training");
        training.setTrainingDate(LocalDate.now());
        training.setTrainingDuration(60);

        Transaction transaction = session.beginTransaction();
        session.save(trainerUser);
        session.save(trainer);
        session.save(traineeUser);
        session.save(trainee);
        session.save(training);
        transaction.commit();


        List<Trainer> trainers = trainingDAO.findTrainersByTraineeId(trainee.getId());


        assertEquals(1, trainers.size(), "Expected one matching trainer");
        assertEquals("Trainer", trainers.get(0).getUser().getFirstName());
    }

    @Test
    void findTrainersByTraineeId_ShouldReturnEmptyListForInvalidTraineeId() {

        List<Trainer> trainers = trainingDAO.findTrainersByTraineeId(999L);


        assertTrue(trainers.isEmpty(), "Expected no trainers for invalid trainee ID");
    }

    @Test
    void findTraineesByTrainerId_ShouldReturnMatchingTrainees() {

        User trainerUser = new User();
        trainerUser.setFirstName("Trainer");
        trainerUser.setLastName("Two");
        trainerUser.setUsername("trainer2");
        trainerUser.setPassword("password");

        Trainer trainer = new Trainer();
        trainer.setUser(trainerUser);
        trainer.setSpecialization(getTrainingType("Cardio"));

        User traineeUser = new User();
        traineeUser.setFirstName("Trainee");
        traineeUser.setLastName("Two");
        traineeUser.setUsername("trainee2");
        traineeUser.setPassword("password");

        Trainee trainee = new Trainee();
        trainee.setUser(traineeUser);

        Training training = new Training();
        training.setTrainer(trainer);
        training.setTrainee(trainee);
        training.setTrainingType(getTrainingType("Cardio"));
        training.setTrainingName("Cardio Training");
        training.setTrainingDate(LocalDate.now());
        training.setTrainingDuration(60);

        Transaction transaction = session.beginTransaction();
        session.save(trainerUser);
        session.save(trainer);
        session.save(traineeUser);
        session.save(trainee);
        session.save(training);
        transaction.commit();


        List<Trainee> trainees = trainingDAO.findTraineesByTrainerId(trainer.getId());


        assertEquals(1, trainees.size(), "Expected one matching trainee");
        assertEquals("Trainee", trainees.get(0).getUser().getFirstName());
    }

    @Test
    void findTraineesByTrainerId_ShouldReturnEmptyListForInvalidTrainerId() {

        List<Trainee> trainees = trainingDAO.findTraineesByTrainerId(999L);


        assertTrue(trainees.isEmpty(), "Expected no trainees for invalid trainer ID");
    }


}

