package uz.micro.gym.cucumber;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import uz.micro.gym.domain.*;
import uz.micro.gym.dto.TrainingDTO;
import uz.micro.gym.dto.TrainingTraineeListDTO;
import uz.micro.gym.dto.TrainingTraineeTrainerDTO;
import uz.micro.gym.repository.*;
import uz.micro.gym.util.TestJwtTokenProvider;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TrainingControllerSteps {

  @Autowired private TestRestTemplate restTemplate;

  @Autowired private PasswordEncoder passwordEncoder;

  @Autowired private UserRepository userRepository;

  @Autowired private TraineeRepository traineeRepository;

  @Autowired private TrainerRepository trainerRepository;

  @Autowired private TrainingRepository trainingRepository;

  @Autowired private TrainingTypeRepository trainingTypeRepository;

  @Autowired private TestJwtTokenProvider testJwtTokenProvider;

  @LocalServerPort private int port;

  private String jwtToken;
  private ResponseEntity<String> response;
  private TrainingDTO trainingDTO;
  private TrainingTraineeListDTO trainingTraineeListDTO;
  private ClassicHttpResponse rawResponse;
  private final ObjectMapper objectMapper =
      new ObjectMapper()
          .registerModule(new JavaTimeModule())
          .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

  @Before
  public void setupTestData() {
    User traineeUser =
        userRepository
            .findByUsername("traineeuser")
            .orElseGet(
                () -> {
                  User user = new User();
                  user.setFirstName("Trainee");
                  user.setLastName("User");
                  user.setUsername("traineeuser");
                  user.setPassword(passwordEncoder.encode("password"));
                  return userRepository.save(user);
                });

    User trainerUser =
        userRepository
            .findByUsername("traineruser")
            .orElseGet(
                () -> {
                  User user = new User();
                  user.setFirstName("Trainer");
                  user.setLastName("User");
                  user.setUsername("traineruser");
                  user.setPassword(passwordEncoder.encode("password"));
                  return userRepository.save(user);
                });

    if (traineeRepository.findByUsername("traineeuser").isEmpty()) {
      Trainee trainee = new Trainee();
      trainee.setUser(traineeUser);
      trainee.setDateOfBirth(LocalDate.of(1999, 1, 1));
      traineeRepository.save(trainee);
    }
    if (trainingTypeRepository.findByTrainingType(PredefinedTrainingType.YOGA).isEmpty()) {
      TrainingType trainingType = new TrainingType();
      trainingType.setTrainingType(PredefinedTrainingType.YOGA);
      trainingTypeRepository.save(trainingType);
    }
    if (trainerRepository.findByUsername("traineruser").isEmpty()) {
      Trainer trainer = new Trainer();
      trainer.setUser(trainerUser);
      trainer.setSpecialization(
          trainingTypeRepository.getOrCreateTrainingType(PredefinedTrainingType.YOGA));
      trainerRepository.save(trainer);
    }

    if (trainingRepository
        .findByTraineeId(
            traineeRepository.findByUsername("traineeuser").map(Trainee::getId).orElse(-1L))
        .isEmpty()) {
      Training training = new Training();
      training.setTrainingDate(LocalDate.of(2000, 1, 1));
      training.setTrainingDuration(60);
      training.setTrainingName("Test Session");
      training.setTrainee(traineeRepository.findByUsername("traineeuser").get());
      training.setTrainer(trainerRepository.findByUsername("traineruser").get());
      training.setTrainingType(
          trainingTypeRepository.getOrCreateTrainingType(PredefinedTrainingType.YOGA));
      trainingRepository.save(training);
    }
    jwtToken = testJwtTokenProvider.generateTestToken("traineeuser");
  }

  @Given("a new training is ready")
  public void a_new_training_is_ready() {
    trainingDTO = new TrainingDTO();
    trainingDTO.setTrainerUsername("traineruser");
    trainingDTO.setTraineeUsername("traineeuser");
    trainingDTO.setTrainingDate(LocalDate.of(2000, 1, 1));
    trainingDTO.setTrainingDuration(60);
    trainingDTO.setTrainingName("Yoga");
  }

  @When("I create a new training")
  public void i_create_a_new_training() {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("Authorization", "Bearer " + jwtToken);

    HttpEntity<TrainingDTO> entity = new HttpEntity<>(trainingDTO, headers);
    response = restTemplate.exchange("/api/trainings", HttpMethod.POST, entity, String.class);
  }

  @Then("the training is created successfully")
  public void the_training_is_created_successfully() {
    assertEquals(200, response.getStatusCodeValue());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().contains("message"));
  }

  @Given("a trainee search request is ready")
  public void a_trainee_search_request_is_ready() {
    trainingTraineeListDTO = new TrainingTraineeListDTO();
    trainingTraineeListDTO.setUsername("traineeuser");
    trainingTraineeListDTO.setPeriodFrom(LocalDate.of(2000, 1, 1));
    trainingTraineeListDTO.setPeriodTo(LocalDate.of(2000, 1, 1));
    trainingTraineeListDTO.setTrainerName("traineruser");
    trainingTraineeListDTO.setTrainingType("Yoga");
    jwtToken = testJwtTokenProvider.generateTestToken("traineeuser");
  }

  @When("I search trainings for a trainee")
  public void i_search_trainings_for_a_trainee() {
    String fullUrl = "http://localhost:" + port + "/api/trainings/trainee";

    ResponseEntity<String> response = restTemplate.getForEntity(fullUrl, String.class);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("Authorization", "Bearer " + jwtToken);

    HttpEntity<TrainingTraineeListDTO> entity = new HttpEntity<>(trainingTraineeListDTO, headers);
    response = restTemplate.exchange(fullUrl, HttpMethod.GET, entity, String.class);
  }

  @Then("the list of trainings is returned")
  public void the_list_of_trainings_is_returned() throws Exception {
    if (response.getStatusCodeValue() == 404) {
      fail("Endpoint not found. See logs for details");
    }

    assertEquals(200, response.getStatusCodeValue());

    List<TrainingTraineeTrainerDTO> trainings =
        objectMapper.readValue(
            response.getBody(), new TypeReference<List<TrainingTraineeTrainerDTO>>() {});

    assertFalse(trainings.isEmpty());
  }

  @When("I search trainings for trainee username {string}")
  public void i_search_trainings_for_invalid_username(String username) {
    TrainingTraineeListDTO request = new TrainingTraineeListDTO();
    request.setUsername(username);
    request.setPeriodFrom(LocalDate.of(2000, 1, 1));
    request.setPeriodTo(LocalDate.of(2000, 1, 2));

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("Authorization", "Bearer " + jwtToken);

    HttpEntity<TrainingTraineeListDTO> entity = new HttpEntity<>(request, headers);
    response =
        restTemplate.exchange("/api/trainings/trainee", HttpMethod.GET, entity, String.class);
  }

  @Then("I receive not found response")
  public void i_receive_not_found_response() {
    assertTrue(
        response.getStatusCode() == HttpStatus.NOT_FOUND
            || response.getStatusCode() == HttpStatus.BAD_REQUEST,
        "Expected 404 or 400, but was: " + response.getStatusCode());
  }

  @Given("a training with missing required fields is ready")
  public void a_training_with_missing_required_fields_is_ready() {
    trainingDTO = new TrainingDTO();
  }

  @Then("I receive bad request response")
  public void i_receive_bad_request_response() {
    assertEquals(400, response.getStatusCodeValue());
  }
}
