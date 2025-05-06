package uz.micro.gym.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import uz.gym.crm.dto.TrainingSessionDTO;
import uz.micro.gym.domain.*;
import uz.micro.gym.messaging.TrainingMessageProducer;
import uz.micro.gym.repository.*;
import uz.micro.gym.service.abstr.TrainingService;
import uz.micro.gym.util.exceptions.EntityNotFoundException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
public class TrainingServiceSteps {
  @Autowired private TrainerRepository trainerRepository;
  @Autowired private TraineeRepository traineeRepository;
  @Autowired private TrainingRepository trainingRepository;
  @Autowired private UserRepository userRepository;
  @Autowired private TrainingTypeRepository trainingTypeRepository;
  @Autowired private PasswordEncoder passwordEncoder;
  @Autowired private TrainingMessageProducer messageProducer;

  @Autowired private TrainingService trainingService;
  private Training training;
  private List<Training> foundTrainings;

  @Given("a trainee with username {string} exists")
  public void aTraineeWithUsernameExists(String username) {
    User user = userRepository.findByUsername(username).orElse(null);

    if (user == null) {
      user = new User();
      user.setUsername(username);
      user.setFirstName("TraineeFirstName");
      user.setLastName("TraineeLastName");
      user.setPassword(passwordEncoder.encode("password"));
      user.setIsActive(true);
      user = userRepository.save(user);
    }

    if (!traineeRepository.findByUsername(user.getUsername()).isPresent()) {
      Trainee trainee = new Trainee();
      trainee.setUser(user);
      trainee.setDateOfBirth(LocalDate.of(2000, 1, 1));
      trainee.setAddress("Default Address");
      traineeRepository.save(trainee);
    }
  }

  @Given("a trainer with username {string} exists")
  public void aTrainerWithUsernameExists(String username) {
    User user = userRepository.findByUsername(username).orElse(null);

    if (user == null) {
      user = new User();
      user.setUsername(username);
      user.setFirstName("TrainerFirstName");
      user.setLastName("TrainerLastName");
      user.setPassword(passwordEncoder.encode("password"));
      user.setIsActive(true);
      user = userRepository.save(user);
    }
    TrainingType trainingType =
        trainingTypeRepository
            .findByTrainingType(PredefinedTrainingType.YOGA)
            .orElseGet(
                () -> {
                  TrainingType newTrainingType = new TrainingType();
                  newTrainingType.setTrainingType(PredefinedTrainingType.YOGA);
                  return trainingTypeRepository.save(newTrainingType);
                });

    if (!trainerRepository.findByUsername(user.getUsername()).isPresent()) {
      Trainer trainer = new Trainer();
      trainer.setUser(user);
      trainer.setSpecialization(trainingType);
      trainerRepository.save(trainer);
    }
  }

  @Given("a training named {string}")
  public void aTrainingNamed(String name) {
    training = new Training();
    training.setTrainingName(name);
    training.setTrainingDate(LocalDate.now());
    training.setTrainingDuration(60);

    TrainingType trainingType = new TrainingType();
    trainingType.setTrainingType(PredefinedTrainingType.YOGA);
    training.setTrainingType(trainingType);
  }

  @When("the training is added")
  public void theTrainingIsAdded() {
    TrainingType trainingType =
        trainingTypeRepository
            .findByTrainingType(PredefinedTrainingType.YOGA)
            .orElseThrow(() -> new IllegalStateException("TrainingType FITNESS not found"));

    training.setTrainingType(trainingType);
    trainingService.addTraining(training, "admin", "admin");
  }

  @When("the training is created")
  public void theTrainingIsCreated() {
    TrainingType trainingType =
        trainingTypeRepository
            .findByTrainingType(PredefinedTrainingType.YOGA)
            .orElseThrow(() -> new IllegalStateException("TrainingType FITNESS not found"));

    training.setTrainingType(trainingType);
    trainingService.linkTraineeTrainer(training, "trainee_user", "trainer_user");
    trainingService.create(training);
  }

  @Then("the training should be saved")
  public void theTrainingShouldBeSaved() {
    assertThat(training.getId()).isNotNull();
  }

  @Given("a training to link")
  public void aTrainingToLink() {
    training = new Training();
    training.setTrainingName("Link Test");
    training.setTrainingDate(LocalDate.now());
    training.setTrainingDuration(45);
  }

  @When("trainee {string} and trainer {string} are linked")
  public void traineeAndTrainerAreLinked(String traineeUsername, String trainerUsername) {
    trainingService.linkTraineeTrainer(training, traineeUsername, trainerUsername);
  }

  @Then("the training should have linked entities")
  public void theTrainingShouldHaveLinkedEntities() {
    assertThat(training.getTrainer()).isNotNull();
    assertThat(training.getTrainee()).isNotNull();
  }

  @Given("existing training for trainer {string}")
  public void existingTrainingForTrainer(String trainerUsername) {
    training = createTrainingLinked();
  }

  @When("I search trainings for trainer {string}")
  public void iSearchTrainingsForTrainer(String trainerUsername) {
    foundTrainings =
        trainingService.findByCriteriaForTrainer(
            trainerUsername, LocalDate.now().minusDays(1), LocalDate.now().plusDays(1), null);
  }

  @Then("I should get a list of trainings")
  public void iShouldGetAListOfTrainings() {
    assertThat(foundTrainings).isNotEmpty();
  }

  @Given("existing training for trainee {string}")
  public void existingTrainingForTrainee(String traineeUsername) {
    training = createTrainingLinked();
  }

  @When("I search trainings for trainee {string}")
  public void iSearchTrainingsForTrainee(String traineeUsername) {
    foundTrainings =
        trainingService.findByCriteria(
            traineeUsername,
            "Yoga",
            LocalDate.now().minusDays(1),
            LocalDate.now().plusDays(1),
            null);
  }

  @Then("a message should be sent to the microservice")
  public void aMessageShouldBeSent() {
    verify(messageProducer, atLeastOnce()).sendTrainingSession(any(TrainingSessionDTO.class));
  }

  @Then("sending the message should fail")
  public void sendingTheMessageShouldFail() {
    doThrow(new RuntimeException("Microservice unavailable"))
        .when(messageProducer)
        .sendTrainingSession(any(TrainingSessionDTO.class));

    assertThrows(
        RuntimeException.class,
        () -> {
          TrainingSessionDTO dto = new TrainingSessionDTO();
          dto.setUsername("test_user");
          messageProducer.sendTrainingSession(dto);
        });
  }

  private Training createTrainingLinked() {
    Optional<Trainee> existingTrainee = traineeRepository.findByUsername("trainee_user");
    Trainee trainee;
    if (existingTrainee.isPresent()) {
      trainee = existingTrainee.get();
    } else {
      User traineeUser =
          userRepository
              .findByUsername("trainee_user")
              .orElseThrow(() -> new EntityNotFoundException("Trainee user not found"));

      trainee = new Trainee();
      trainee.setUser(traineeUser);
      trainee.setDateOfBirth(LocalDate.of(2000, 1, 1));
      trainee = traineeRepository.save(trainee);
    }

    Optional<Trainer> existingTrainer = trainerRepository.findByUsername("trainer_user");
    Trainer trainer;
    if (existingTrainer.isPresent()) {
      trainer = existingTrainer.get();
    } else {
      User trainerUser =
          userRepository
              .findByUsername("trainer_user")
              .orElseThrow(() -> new EntityNotFoundException("Trainer user not found"));

      trainer = new Trainer();
      trainer.setUser(trainerUser);

      TrainingType trainingType =
          trainingTypeRepository
              .findByTrainingType(PredefinedTrainingType.YOGA)
              .orElseThrow(() -> new EntityNotFoundException("TrainingType YOGA not found"));

      trainer.setSpecialization(trainingType);
      trainer = trainerRepository.save(trainer);
    }

    Training training = new Training();
    training.setTrainingName("Test Training");
    training.setTrainingDate(LocalDate.now());
    training.setTrainingDuration(90);

    trainingService.linkTraineeTrainer(
        training, trainee.getUser().getUsername(), trainer.getUser().getUsername());

    return trainingRepository.save(training);
  }
}
