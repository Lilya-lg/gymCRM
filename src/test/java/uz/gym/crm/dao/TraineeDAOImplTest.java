package uz.gym.crm.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uz.gym.crm.domain.*;
import uz.gym.crm.service.TrainingServiceImpl;
import uz.gym.crm.service.abstr.TrainingService;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TraineeDAOImplTest {
    private TrainerDAOImpl trainerDAO;
    private TraineeDAOImpl traineeDAO;
    private SessionFactory sessionFactory;
    private Session session;
    private TrainingTypeDAOImpl trainingTypeDAO;
    private TrainingDAOImpl trainingDAO;
    private TrainingServiceImpl trainingService;

    @BeforeEach
    void setUp() {
        Configuration configuration = new Configuration();
        configuration.addAnnotatedClass(Trainee.class);
        configuration.addAnnotatedClass(User.class);
        configuration.addAnnotatedClass(Trainer.class);
        configuration.addAnnotatedClass(TrainingType.class);
        configuration.addAnnotatedClass(Training.class);
        configuration.addAnnotatedClass(TrainingServiceImpl.class);
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

        // Mock the dependencies
        trainerDAO = mock(TrainerDAOImpl.class); // Mocked TrainerDAOImpl
        trainingTypeDAO = mock(TrainingTypeDAOImpl.class); // Mocked TrainingTypeDAOImpl
        trainingDAO = mock(TrainingDAOImpl.class);
        trainingService= mock(TrainingServiceImpl.class);

        traineeDAO = new TraineeDAOImpl(sessionFactory, trainerDAO, trainingTypeDAO, trainingDAO);

        // Clear the database before each test
        Transaction transaction = session.beginTransaction();
        session.createQuery("DELETE FROM Training").executeUpdate();
        session.createQuery("DELETE FROM Trainer").executeUpdate();
        session.createQuery("DELETE FROM Trainee").executeUpdate();
        session.createQuery("DELETE FROM User").executeUpdate();
        session.createQuery("DELETE FROM TrainingType").executeUpdate();
        transaction.commit();

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
        user.setIsActive(true);


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
        user.setIsActive(true);


        Trainee trainee = new Trainee();
        trainee.setUser(user);


        Transaction transaction = session.beginTransaction();
        session.save(user);
        session.save(trainee);
        transaction.commit();

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
        user.setIsActive(true);


        Trainee trainee = new Trainee();
        trainee.setUser(user);

        Transaction transaction = session.beginTransaction();
        session.save(user);
        session.save(trainee);
        transaction.commit();


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


        User traineeUser = new User();
        traineeUser.setFirstName("Trainee");
        traineeUser.setLastName("One");
        traineeUser.setUsername("trainee1");
        traineeUser.setPassword("password");
        traineeUser.setIsActive(true);

        Trainee trainee = new Trainee();
        trainee.setUser(traineeUser);


        User trainerUser1 = new User();
        trainerUser1.setFirstName("Trainer");
        trainerUser1.setLastName("One");
        trainerUser1.setUsername("trainer1");
        trainerUser1.setPassword("password");
        trainerUser1.setIsActive(true);

        Trainer trainer1 = new Trainer();
        trainer1.setUser(trainerUser1);
        trainer1.setSpecialization(yogaType);

        User trainerUser2 = new User();
        trainerUser2.setFirstName("Trainer");
        trainerUser2.setLastName("Two");
        trainerUser2.setUsername("trainer2");
        trainerUser2.setPassword("password");
        trainerUser2.setIsActive(true);

        Trainer trainer2 = new Trainer();
        trainer2.setUser(trainerUser2);
        trainer2.setSpecialization(cardioType);


        Transaction transaction = session.beginTransaction();
        session.save(traineeUser);
        session.save(trainee);
        session.save(trainerUser1);
        session.save(trainer1);
        session.save(trainerUser2);
        session.save(trainer2);
        transaction.commit();


        traineeDAO.updateTraineeTrainerList(trainee.getId(), List.of(trainer1.getUser().getUsername()));


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
        traineeUser.setIsActive(true);

        Trainee trainee = new Trainee();
        trainee.setUser(traineeUser);


        session.save(traineeUser);
        session.save(trainee);
        transaction.commit();


        session.beginTransaction();
        traineeDAO.updateTraineeTrainerList(trainee.getId(), List.of());
        session.getTransaction().commit();


        List<Training> updatedTrainings = session.createQuery("SELECT t FROM Training t WHERE t.trainee.id = :traineeId", Training.class).setParameter("traineeId", trainee.getId()).list();


        assertTrue(updatedTrainings.isEmpty(), "Trainee should have no trainings");
    }

    @Test
    void updateTraineeTrainerList_ShouldThrowExceptionForInvalidTraineeId() {
        Long invalidTraineeId = 999L;


        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            traineeDAO.updateTraineeTrainerList(invalidTraineeId, List.of("a", "b"));
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
        traineeUser.setIsActive(true);

        Trainee trainee = new Trainee();
        trainee.setUser(traineeUser);

        Transaction transaction = session.beginTransaction();
        session.save(traineeUser);
        session.save(trainee);
        transaction.commit();


        when(trainerDAO.findByUsername("invalidTrainerId")).thenReturn(Optional.empty());



        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            traineeDAO.updateTraineeTrainerList(trainee.getId(), List.of("invalidTrainerId"));
        });


        assertEquals("Trainer not found with username: invalidTrainerId", exception.getMessage());
        verify(trainerDAO, times(1)).findByUsername("invalidTrainerId"); // Ensure the method was called
    }


    @Test
    void findByUsername_ShouldHandleCaseSensitivity() {

        User user = new User();
        user.setFirstName("Test");
        user.setLastName("User");
        user.setUsername("CaseSensitiveUser");
        user.setPassword("password");
        user.setIsActive(true);

        Trainee trainee = new Trainee();
        trainee.setUser(user);

        Transaction transaction = session.beginTransaction();
        session.save(user);
        session.save(trainee);
        transaction.commit();


        Optional<Trainee> resultExact = traineeDAO.findByUsername("CaseSensitiveUser");
        assertTrue(resultExact.isPresent(), "Trainee should be found with exact case");


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
        user1.setIsActive(true);

        User user2 = new User();
        user2.setFirstName("Trainee2");
        user2.setLastName("User2");
        user2.setUsername("duplicateUser"); // Duplicate username
        user2.setPassword("password");
        user2.setIsActive(true);

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
        user.setIsActive(true);

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



    @Test
    void updateTraineeTrainerList_ShouldHandleNullTrainer() {
        User traineeUser = new User();
        traineeUser.setFirstName("Trainee");
        traineeUser.setLastName("One");
        traineeUser.setUsername("trainee1");
        traineeUser.setPassword("password");
        traineeUser.setIsActive(true);

        Trainee trainee = new Trainee();
        trainee.setUser(traineeUser);

        Transaction transaction = session.beginTransaction();
        session.save(traineeUser);
        session.save(trainee);
        transaction.commit();


        when(trainerDAO.findByUsername("nonexistentTrainer")).thenReturn(Optional.empty());


        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            traineeDAO.updateTraineeTrainerList(trainee.getId(), List.of("nonexistentTrainer"));
        });



        assertEquals("Trainer not found with username: nonexistentTrainer", exception.getMessage(), "Exception reason should match");

    }
    @Test
    void updateTraineeTrainerList_ShouldIgnoreDuplicateTrainers() {
        User traineeUser = new User();
        traineeUser.setFirstName("Trainee");
        traineeUser.setLastName("One");
        traineeUser.setUsername("trainee1");
        traineeUser.setPassword("password");
        traineeUser.setIsActive(true);

        Trainee trainee = new Trainee();
        trainee.setUser(traineeUser);

        User trainerUser = new User();
        trainerUser.setFirstName("Trainer");
        trainerUser.setLastName("One");
        trainerUser.setUsername("trainer1");
        trainerUser.setPassword("password");
        trainerUser.setIsActive(true);

        TrainingType trainingType = new TrainingType();
        trainingType.setTrainingType(PredefinedTrainingType.fromName("Yoga"));

        Trainer trainer = new Trainer();
        trainer.setUser(trainerUser);
        trainer.setSpecialization(trainingType); // Ensure specialization is set

        Training existingTraining = new Training();
        existingTraining.setTrainee(trainee);
        existingTraining.setTrainer(trainer);
        existingTraining.setTrainingType(trainingType); // Set a valid training type
        existingTraining.setTrainingDate(LocalDate.now());
        existingTraining.setTrainingName("Yoga Training");
        existingTraining.setTrainingDuration(60);

        Transaction transaction = session.beginTransaction();
        session.save(trainingType); // Save training type to ensure persistence
        session.save(traineeUser);
        session.save(trainee);
        session.save(trainerUser);
        session.save(trainer);
        session.save(existingTraining);
        transaction.commit();

        traineeDAO.updateTraineeTrainerList(trainee.getId(), List.of("trainer1"));

        List<Training> updatedTrainings = session.createQuery(
                        "SELECT t FROM Training t WHERE t.trainee.id = :traineeId", Training.class)
                .setParameter("traineeId", trainee.getId())
                .list();

        assertEquals(1, updatedTrainings.size(), "Trainee should have one training for the same trainer");
        assertEquals(trainer.getId(), updatedTrainings.get(0).getTrainer().getId(), "Trainer should remain unchanged");
    }
    @Test
    void findByUsername_ShouldThrowExceptionForNullUsername() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            traineeDAO.findByUsername(null);
        });

        assertEquals("Username cannot be null", exception.getMessage(), "Exception message should match");
    }
    @Test
    void updateTraineeTrainerList_ShouldHandleEmptyTrainerIds() {
        User traineeUser = new User();
        traineeUser.setFirstName("Trainee");
        traineeUser.setLastName("One");
        traineeUser.setUsername("trainee1");
        traineeUser.setPassword("password");
        traineeUser.setIsActive(true);

        Trainee trainee = new Trainee();
        trainee.setUser(traineeUser);

        Transaction transaction = session.beginTransaction();
        session.save(traineeUser);
        session.save(trainee);
        transaction.commit();


        traineeDAO.updateTraineeTrainerList(trainee.getId(), Collections.emptyList());


        List<Training> updatedTrainings = session.createQuery(
                        "SELECT t FROM Training t WHERE t.trainee.id = :traineeId", Training.class)
                .setParameter("traineeId", trainee.getId())
                .list();

        assertTrue(updatedTrainings.isEmpty(), "No trainings should exist for an empty trainer list");
    }
    @Test
    void updateTraineeTrainerList_ShouldThrowExceptionForMissingTrainerSpecialization() {
        User traineeUser = new User();
        traineeUser.setFirstName("Trainee");
        traineeUser.setLastName("One");
        traineeUser.setUsername("trainee1");
        traineeUser.setPassword("password");
        traineeUser.setIsActive(true);

        Trainee trainee = new Trainee();
        trainee.setUser(traineeUser);

        User trainerUser = new User();
        trainerUser.setFirstName("Trainer");
        trainerUser.setLastName("One");
        trainerUser.setUsername("trainer1");
        trainerUser.setPassword("password");
        trainerUser.setIsActive(true);

        Trainer trainer = new Trainer();
        trainer.setUser(trainerUser);
        trainer.setSpecialization(null);


        Transaction transaction = session.beginTransaction();
        session.save(traineeUser);
        session.save(trainee);
        session.save(trainerUser);
        transaction.commit();


        when(trainerDAO.findByUsername("trainer1")).thenReturn(Optional.of(trainer));


        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            traineeDAO.updateTraineeTrainerList(trainee.getId(), List.of("trainer1"));
        });


        assertEquals("Trainer specialization is missing", exception.getMessage(), "Exception message should match");


        verify(trainerDAO, times(1)).findByUsername("trainer1");
    }

}
