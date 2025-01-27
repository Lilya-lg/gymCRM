package uz.gym.crm.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;
import uz.gym.crm.dao.abstr.TraineeDAO;
import uz.gym.crm.domain.Trainee;
import uz.gym.crm.domain.Trainer;
import uz.gym.crm.domain.Training;
import org.springframework.transaction.annotation.Transactional;
import uz.gym.crm.util.DynamicQueryBuilder;


import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


@Repository
public class TraineeDAOImpl extends BaseDAOImpl<Trainee> implements TraineeDAO {
    private static final Logger LOGGER = LoggerFactory.getLogger(TraineeDAOImpl.class);
    private static final String FIND_BY_USERNAME_AND_PASSWORD = "SELECT t FROM Trainee t JOIN t.user u WHERE u.username = :username AND u.password = :password";
    private static final String FIND_BY_USERNAME = "SELECT t FROM Trainee t JOIN t.user u WHERE u.username = :username";
    private static final String FETCH_TRAININGS_BY_TRAINEE = "SELECT t FROM Training t WHERE t.trainee.id = :traineeId";
    private final SessionFactory sessionFactory;
    private final TrainerDAOImpl trainerDAO;
    private final TrainingTypeDAOImpl trainingTypeDAO;
    private final TrainingDAOImpl trainingDAO;


    @Autowired
    public TraineeDAOImpl(SessionFactory sessionFactory, TrainerDAOImpl trainerDAO, TrainingTypeDAOImpl trainingTypeDAO, TrainingDAOImpl trainingDAO) {
        super(Trainee.class, sessionFactory);
        this.sessionFactory = sessionFactory;
        this.trainerDAO = trainerDAO;
        this.trainingTypeDAO = trainingTypeDAO;
        this.trainingDAO = trainingDAO;
    }

    @Transactional
    private Session getSession() {
        return sessionFactory.openSession();
    }

    @Override
    public Optional<Trainee> findByUsernameAndPassword(String username, String password) {
        DynamicQueryBuilder<Trainee> queryBuilder = new DynamicQueryBuilder<>(FIND_BY_USERNAME_AND_PASSWORD);
        queryBuilder.addCondition("u.username = :username", "username", username).addCondition("u.password = :password", "password", password);

        return Optional.ofNullable(queryBuilder.buildQuery(getSession(), Trainee.class).uniqueResult());
    }

    @Override
    public Optional<Trainee> findByUsername(String username) {
        if (username == null) {
            throw new IllegalArgumentException("Username cannot be null");
        }
        DynamicQueryBuilder<Trainee> queryBuilder = new DynamicQueryBuilder<>(FIND_BY_USERNAME);
        queryBuilder.addCondition("u.username = :username", "username", username);

        return Optional.ofNullable(queryBuilder.buildQuery(getSession(), Trainee.class).uniqueResult());
    }

    public void deleteByUsername(String username) {
        try (Session session = getSession()) {
            String hql = "FROM Trainee t WHERE t.user.username = :username";
            Query<Trainee> query = session.createQuery(hql, Trainee.class);
            query.setParameter("username", username);
            Trainee trainee = query.uniqueResult();

            if (trainee != null) {
                session.delete(trainee);
            } else {
                throw new IllegalArgumentException("No Trainee found with username: " + username);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void updateTraineeTrainerList(Long traineeId, List<String> trainerIds) {
        Trainee trainee = getSession().find(Trainee.class, traineeId);
        if (trainee == null) {
            throw new IllegalArgumentException("Trainee with ID " + traineeId + " not found");
        }


        DynamicQueryBuilder<Training> queryBuilder = new DynamicQueryBuilder<>(FETCH_TRAININGS_BY_TRAINEE);
        queryBuilder.addCondition("t.trainee = :traineeId", "traineeId", traineeId);

        List<Training> existingTrainings = Optional.ofNullable(queryBuilder.buildQuery(getSession(), Training.class).getResultList()).orElse(Collections.emptyList());


        for (Training training : existingTrainings) {
            if (training == null) continue;

            Trainer trainer = training.getTrainer();
            if (trainer == null || trainer.getUser() == null) {
                continue;
            }

            String trainerUsername = trainer.getUser().getUsername();
            if (!trainerIds.contains(trainerUsername)) {
                getSession().remove(training);
            }
        }


        for (String trainerId : trainerIds) {


            boolean exists = existingTrainings.stream().anyMatch(training -> training.getTrainer().getUser().getUsername().equals(trainerId));

            if (!exists) {
                Optional<Trainer> optionalTrainer = trainerDAO.findByUsername(trainerId);
                Trainer trainer = optionalTrainer.orElseThrow(() -> new IllegalArgumentException("Trainer not found with username: " + trainerId));
                if (trainer.getSpecialization() == null) {
                    throw new IllegalArgumentException("Trainer specialization is missing");
                }
                try {


                    Training newTraining = new Training();
                    newTraining.setTrainee(trainee);
                    if (trainer != null) {
                        newTraining.setTrainer(trainer);
                    }
                    newTraining.setTrainingName("Yoga");
                    newTraining.setTrainingDate(LocalDate.now());
                    newTraining.setTrainingDuration(60);
                    newTraining.setTrainingType(trainer.getSpecialization());
                    try {
                        getSession().save(newTraining);
                    } catch (Exception e) {
                        LOGGER.error("Error persisting new training", e);
                    }
                } catch (IllegalArgumentException ex) {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
                }

            }
        }
    }

}
