package uz.gym.crm.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uz.gym.crm.config.TrainingTypeInitializer;
import uz.gym.crm.domain.*;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class TraineeDAOImplTest {
    private TraineeDAOImpl traineeDAO;
    private SessionFactory sessionFactory;
    private Session session;

    @BeforeEach
    void setUp() {
        // Configure Hibernate with H2 database
        Configuration configuration = new Configuration();
        configuration.addAnnotatedClass(Trainee.class);
        configuration.addAnnotatedClass(User.class);
        configuration.addAnnotatedClass(Trainer.class);
        configuration.addAnnotatedClass(TrainingType.class);
        configuration.addAnnotatedClass(Training.class);

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
        traineeDAO = new TraineeDAOImpl(session);

        // Clear the database before each test
        Transaction transaction = session.beginTransaction();
        session.createQuery("DELETE FROM Training").executeUpdate();
        session.createQuery("DELETE FROM Trainer").executeUpdate();
        session.createQuery("DELETE FROM Trainee").executeUpdate();
        session.createQuery("DELETE FROM User").executeUpdate();
        session.createQuery("DELETE FROM TrainingType").executeUpdate();
        transaction.commit();
        TrainingTypeInitializer.initializeTrainingTypes(sessionFactory);
    }

    @AfterEach
    void tearDown() {
        if (session.isOpen()) {
            session.close();
        }
        sessionFactory.close();
    }

    @Test
    void findByUser_UsernameAndUser_Password_ShouldReturnTrainee() {
        User user = new User();
        user.setFirstName("Test");
        user.setLastName("User");
        user.setUsername("testUser");
        user.setPassword("testPass");
        user.setActive(true);


        Trainee trainee = new Trainee();
        trainee.setUser(user);


        Transaction transaction = session.beginTransaction();
        session.save(user);
        session.save(trainee);
        transaction.commit();


        Optional<Trainee> result = traineeDAO.findByUsernameAndPassword("testUser", "testPass");
        assertTrue(result.isPresent(), "Trainee should be found");
        assertEquals(user.getUsername(), result.get().getUser().getUsername());
    }

    @Test
    void findByUser_UsernameAndUser_Password_ShouldReturnEmptyOptional() {
        Optional<Trainee> result = traineeDAO.findByUsernameAndPassword("nonExistentUser", "wrongPass");
        assertTrue(result.isEmpty(), "No Trainee should be found for invalid credentials");
    }

    @Test
    void findByUser_Username_ShouldReturnTrainee() {

        User user = new User();
        user.setFirstName("Test");
        user.setLastName("User");
        user.setUsername("testUser");
        user.setPassword("testPass");
        user.setActive(true);


        Trainee trainee = new Trainee();
        trainee.setUser(user);


        Transaction transaction = session.beginTransaction();
        session.save(user);
        session.save(trainee);
        transaction.commit();

        // Test the DAO method
        Optional<Trainee> result = traineeDAO.findByUsername("testUser");
        assertTrue(result.isPresent(), "Trainee should be found");
        assertEquals(user.getUsername(), result.get().getUser().getUsername());
    }

    private TrainingType getTrainingType(String type) {
        return session.createQuery("FROM TrainingType WHERE trainingType = :type", TrainingType.class).setParameter("type", type).uniqueResult();
    }

    @Test
    void findByUser_Username_ShouldReturnEmptyOptional() {
        Optional<Trainee> result = traineeDAO.findByUsername("nonExistentUser");
        assertTrue(result.isEmpty(), "No Trainee should be found for invalid username");
    }

    @Test
    void findByUsername_ShouldReturnTrainee() {
        User user = new User();
        user.setFirstName("Test");
        user.setLastName("User");
        user.setUsername("testUser");
        user.setPassword("testPass");
        user.setActive(true);


        Trainee trainee = new Trainee();
        trainee.setUser(user);

        // Save the trainee
        Transaction transaction = session.beginTransaction();
        session.save(user);
        session.save(trainee);
        transaction.commit();

        // Test the DAO method
        Optional<Trainee> result = traineeDAO.findByUsername("testUser");
        assertTrue(result.isPresent(), "Trainee should be found");
        assertEquals(user.getUsername(), result.get().getUser().getUsername());
    }

    @Test
    void findByUsername_ShouldReturnEmptyOptional() {
        Optional<Trainee> result = traineeDAO.findByUsername("nonExistentUser");
        assertTrue(result.isEmpty(), "No Trainee should be found for invalid username");
    }

    void updateTraineeTrainerList(Long traineeId, List newTrainerIds) {
        TrainingType yogaType = getTrainingType("Yoga");
        TrainingType cardioType = getTrainingType("Cardio");

        // Create trainee
        User traineeUser = new User();
        traineeUser.setFirstName("Trainee");
        traineeUser.setLastName("One");
        traineeUser.setUsername("trainee1");
        traineeUser.setPassword("password");
        traineeUser.setActive(true);

        Trainee trainee = new Trainee();
        trainee.setUser(traineeUser);


        User trainerUser1 = new User();
        trainerUser1.setFirstName("Trainer");
        trainerUser1.setLastName("One");
        trainerUser1.setUsername("trainer1");
        trainerUser1.setPassword("password");
        trainerUser1.setActive(true);

        Trainer trainer1 = new Trainer();
        trainer1.setUser(trainerUser1);
        trainer1.setSpecialization(yogaType);

        User trainerUser2 = new User();
        trainerUser2.setFirstName("Trainer");
        trainerUser2.setLastName("Two");
        trainerUser2.setUsername("trainer2");
        trainerUser2.setPassword("password");
        trainerUser2.setActive(true);

        Trainer trainer2 = new Trainer();
        trainer2.setUser(trainerUser2);
        trainer2.setSpecialization(cardioType);

        // Save entities
        Transaction transaction = session.beginTransaction();
        session.save(traineeUser);
        session.save(trainee);
        session.save(trainerUser1);
        session.save(trainer1);
        session.save(trainerUser2);
        session.save(trainer2);
        transaction.commit();

        // Update trainee's trainer list
        traineeDAO.updateTraineeTrainerList(trainee.getId(), List.of(trainer1.getId(), trainer2.getId()));

        // Verify updated associations
        List<Training> updatedTrainings = session.createQuery("SELECT t FROM Training t WHERE t.trainee.id = :traineeId", Training.class).setParameter("traineeId", trainee.getId()).list();

        assertEquals(2, updatedTrainings.size(), "Trainee should have two trainings");

        assertTrue(updatedTrainings.stream().anyMatch(training -> training.getTrainer().getId().equals(trainer1.getId())));
        assertTrue(updatedTrainings.stream().anyMatch(training -> training.getTrainer().getId().equals(trainer2.getId())));
    }

    @Test
    void updateTraineeTrainerList_ShouldHandleEmptyTrainerList() {
        Transaction transaction = session.beginTransaction();


        User traineeUser = new User();
        traineeUser.setFirstName("Trainee");
        traineeUser.setLastName("One");
        traineeUser.setUsername("uniqueTrainee1"); // Use a unique username
        traineeUser.setPassword("password");
        traineeUser.setActive(true);

        Trainee trainee = new Trainee();
        trainee.setUser(traineeUser);


        session.save(traineeUser);
        session.save(trainee);
        transaction.commit();


        session.beginTransaction();
        traineeDAO.updateTraineeTrainerList(trainee.getId(), List.of());
        session.getTransaction().commit();

        // Fetch updated trainings for the trainee
        List<Training> updatedTrainings = session.createQuery("SELECT t FROM Training t WHERE t.trainee.id = :traineeId", Training.class).setParameter("traineeId", trainee.getId()).list();


        assertTrue(updatedTrainings.isEmpty(), "Trainee should have no trainings");
    }

    @Test
    void updateTraineeTrainerList_ShouldThrowExceptionForInvalidTraineeId() {
        Long invalidTraineeId = 999L;


        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            traineeDAO.updateTraineeTrainerList(invalidTraineeId, List.of(1L, 2L));
        });


        assertEquals("Trainee with ID " + invalidTraineeId + " not found", exception.getMessage());
    }

    @Test
    void updateTraineeTrainerList_ShouldThrowExceptionForInvalidTrainerId() {
        User traineeUser = new User();
        traineeUser.setFirstName("Trainee");
        traineeUser.setLastName("One");
        traineeUser.setUsername("trainee1");
        traineeUser.setPassword("password");
        traineeUser.setActive(true);

        Trainee trainee = new Trainee();
        trainee.setUser(traineeUser);

        // Save trainee
        Transaction transaction = session.beginTransaction();
        session.save(traineeUser);
        session.save(trainee);
        transaction.commit();


        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            traineeDAO.updateTraineeTrainerList(trainee.getId(), List.of(999L));
        });


        assertEquals("Trainer with ID 999 not found", exception.getMessage());
    }

    @Test
    void findByUsername_ShouldHandleCaseSensitivity() {

        User user = new User();
        user.setFirstName("Test");
        user.setLastName("User");
        user.setUsername("CaseSensitiveUser");
        user.setPassword("password");
        user.setActive(true);

        Trainee trainee = new Trainee();
        trainee.setUser(user);

        Transaction transaction = session.beginTransaction();
        session.save(user);
        session.save(trainee);
        transaction.commit();

        // Test with exact case
        Optional<Trainee> resultExact = traineeDAO.findByUsername("CaseSensitiveUser");
        assertTrue(resultExact.isPresent(), "Trainee should be found with exact case");

        // Test with different case
        Optional<Trainee> resultDifferentCase = traineeDAO.findByUsername("casesensitiveuser");
        assertTrue(resultDifferentCase.isEmpty(), "Trainee should not be found with different case if case-sensitive");
    }


    @Test
    void save_ShouldThrowExceptionForDuplicateUsername() {
        User user1 = new User();
        user1.setFirstName("Trainee1");
        user1.setLastName("User1");
        user1.setUsername("duplicateUser");
        user1.setPassword("password");
        user1.setActive(true);

        User user2 = new User();
        user2.setFirstName("Trainee2");
        user2.setLastName("User2");
        user2.setUsername("duplicateUser"); // Duplicate username
        user2.setPassword("password");
        user2.setActive(true);

        Trainee trainee1 = new Trainee();
        trainee1.setUser(user1);

        Trainee trainee2 = new Trainee();
        trainee2.setUser(user2);

        Transaction transaction = session.beginTransaction();
        session.save(user1);
        session.save(trainee1);
        transaction.commit();

        // Attempt to save the second trainee
        Transaction transaction2 = session.beginTransaction();
        assertThrows(Exception.class, () -> session.save(user2));
        transaction2.rollback();
    }

    @Test
    void findTraineeWithoutTrainers_ShouldReturnEmptyTrainerList() {
        User user = new User();
        user.setFirstName("Trainee");
        user.setLastName("NoTrainer");
        user.setUsername("noTrainerUser");
        user.setPassword("password");
        user.setActive(true);

        Trainee trainee = new Trainee();
        trainee.setUser(user);

        // Save the entities
        Transaction transaction = session.beginTransaction();
        session.save(user);
        session.save(trainee);
        transaction.commit();

        // Fetch trainings for the trainee
        List<Training> trainings = session.createQuery("SELECT t FROM Training t WHERE t.trainee.id = :traineeId", Training.class).setParameter("traineeId", trainee.getId()).list();


        assertNotNull(trainings, "Trainings list should not be null");
        assertTrue(trainings.isEmpty(), "Trainee should have no associated trainings");
    }


}
