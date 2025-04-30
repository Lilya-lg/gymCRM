package uz.micro.gym.cucumber;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import uz.micro.gym.domain.Trainee;
import uz.micro.gym.domain.Trainer;
import uz.micro.gym.domain.User;
import uz.micro.gym.dto.*;
import uz.micro.gym.repository.TraineeRepository;
import uz.micro.gym.repository.TrainerRepository;
import uz.micro.gym.repository.UserRepository;
import uz.micro.gym.util.TestJwtTokenProvider;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class TrainerControllerSteps {

  @Autowired private TestRestTemplate restTemplate;
  @Autowired private UserRepository userRepository;
  @Autowired private TrainerRepository trainerRepository;
  @Autowired private TraineeRepository traineeRepository;
  @Autowired private TestJwtTokenProvider tokenProvider;

  private TrainerDTO trainerDTO;
  private TrainerProfileDTO updateDTO;
  private String jwtToken;
  private ResponseEntity<String> response;

  @Given("a valid trainer registration request")
  public void a_valid_trainer_registration_request() {
    trainerDTO = new TrainerDTO();
    trainerDTO.setUsername("traineruser");
    trainerDTO.setFirstName("Trainer");
    trainerDTO.setSecondName("User");
    trainerDTO.setSpecialization("Cardio");
  }

  @When("I create a new trainer")
  public void i_create_a_new_trainer() {
    response = restTemplate.postForEntity("/api/trainers", trainerDTO, String.class);
  }

  @Then("the trainer is created and a password is returned")
  public void the_trainer_is_created_and_a_password_is_returned() {
    Assertions.assertEquals(200, response.getStatusCodeValue());
    Assertions.assertTrue(response.getBody().contains("password"));
  }

  @Given("a trainer exists with username {string}")
  public void a_trainer_exists_with_username(String username) {
    User user =
        userRepository
            .findByUsername(username)
            .orElseGet(
                () -> {
                  User newUser = new User();
                  newUser.setUsername(username);
                  newUser.setFirstName("Trainer");
                  newUser.setLastName("Test");
                  newUser.setPassword("pass");
                  return userRepository.save(newUser);
                });

    if (trainerRepository.findByUsername(username).isEmpty()) {
      Trainer trainer = new Trainer();
      trainer.setUser(user);
      trainerRepository.save(trainer);
    }

    jwtToken = tokenProvider.generateTestToken(username);
  }

  @When("I request the trainer profile for {string}")
  public void i_request_the_trainer_profile_for(String username) {
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(jwtToken);
    HttpEntity<Void> entity = new HttpEntity<>(headers);

    response =
        restTemplate.exchange(
            "/api/trainers/profiles/" + username, HttpMethod.GET, entity, String.class);
  }

  @Then("the trainer profile is returned successfully")
  public void the_trainer_profile_is_returned_successfully() {
    Assertions.assertEquals(200, response.getStatusCodeValue());
    Assertions.assertTrue(response.getBody().contains("Trainer"));
  }

  @Given("a trainer profile update request is ready for {string}")
  public void a_trainer_profile_update_request_is_ready_for(String username) {
    updateDTO = new TrainerProfileDTO();
    updateDTO.setUsername(username);
    updateDTO.setFirstName("Updated");
    updateDTO.setSecondName("Trainer");
    updateDTO.setSpecialization("Cardio");
    updateDTO.setIsActive(true);

    jwtToken = tokenProvider.generateTestToken(username);
  }

  @When("I update the trainer profile")
  public void i_update_the_trainer_profile() {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setBearerAuth(jwtToken);
    HttpEntity<TrainerProfileDTO> entity = new HttpEntity<>(updateDTO, headers);

    response =
        restTemplate.exchange("/api/trainers/update-profile", HttpMethod.PUT, entity, String.class);
  }

  @Then("the updated trainer profile is returned")
  public void the_updated_trainer_profile_is_returned() {
    Assertions.assertEquals(200, response.getStatusCodeValue());
    Assertions.assertTrue(response.getBody().contains("Updated"));
  }

  @Given("I want to get unassigned trainers for trainee {string}")
  public void i_want_to_get_unassigned_trainers_for_trainee(String traineeUsername) {
    User traineeUser =
        userRepository
            .findByUsername(traineeUsername)
            .orElseGet(
                () -> {
                  User user = new User();
                  user.setUsername(traineeUsername);
                  user.setFirstName("Trainee");
                  user.setLastName("User");
                  user.setPassword("pass");
                  return userRepository.save(user);
                });

    if (traineeRepository.findByUsername(traineeUsername).isEmpty()) {
      Trainee trainee = new Trainee();
      trainee.setUser(traineeUser);
      trainee.setDateOfBirth(LocalDate.of(2000, 1, 1));
      traineeRepository.save(trainee);
    }

    User trainerUser =
        userRepository
            .findByUsername("traineruser")
            .orElseGet(
                () -> {
                  User user = new User();
                  user.setUsername("traineruser");
                  user.setFirstName("Trainer");
                  user.setLastName("User");
                  user.setPassword("pass");
                  return userRepository.save(user);
                });

    if (trainerRepository.findByUsername("traineruser").isEmpty()) {
      Trainer trainer = new Trainer();
      trainer.setUser(trainerUser);
      trainerRepository.save(trainer);
    }

    jwtToken = tokenProvider.generateTestToken("traineruser");
  }

  @When("I request the unassigned active trainers")
  public void i_request_the_unassigned_active_trainers() {
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(jwtToken);
    HttpEntity<Void> entity = new HttpEntity<>(headers);
    response =
        restTemplate.exchange(
            "/api/trainers/unassigned-active-trainers?username=traineeuser",
            HttpMethod.GET,
            entity,
            String.class);
  }

  @Then("a list of available trainers is returned")
  public void a_list_of_available_trainers_is_returned() throws JsonProcessingException {
    Assertions.assertEquals(200, response.getStatusCodeValue());

    ObjectMapper mapper = new ObjectMapper();
    List<Map<String, Object>> trainers = mapper.readValue(response.getBody(), List.class);

    boolean found =
        trainers.stream().anyMatch(trainer -> "traineruser".equals(trainer.get("username")));

    Assertions.assertTrue(
        trainers.stream().anyMatch(trainer -> "trainer.user".equals(trainer.get("username"))),
        "Expected 'trainer.user' in unassigned trainer list, but not found.");
  }
}
