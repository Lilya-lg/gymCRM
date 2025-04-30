package uz.micro.gym.cucumber;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import uz.micro.gym.domain.Trainee;
import uz.micro.gym.domain.User;
import uz.micro.gym.dto.TraineeProfileDTO;
import uz.micro.gym.repository.TraineeRepository;
import uz.micro.gym.repository.UserRepository;
import uz.micro.gym.util.TestJwtTokenProvider;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class TraineeControllerSTeps {

  @Autowired private TestRestTemplate restTemplate;

  @Autowired private PasswordEncoder passwordEncoder;

  @Autowired private UserRepository userRepository;

  @Autowired private TraineeRepository traineeRepository;

  @Autowired private TestJwtTokenProvider testJwtTokenProvider;

  private String jwtToken;
  private ResponseEntity<String> response;
  private TraineeProfileDTO traineeProfileDTO;

  @Before
  public void setupTestData() {

    User user = userRepository.findByUsername("testuser").orElse(null);
    if (user == null) {
      user = new User();
      user.setFirstName("Test");
      user.setLastName("User");
      user.setUsername("testuser");
      user.setPassword(passwordEncoder.encode("password"));
      userRepository.save(user);
    }

    if (traineeRepository.findByUsername("testuser").isEmpty()) {
      Trainee trainee = new Trainee();
      trainee.setUser(user);
      trainee.setDateOfBirth(LocalDate.of(2000, 1, 1));
      traineeRepository.save(trainee);
    }
    jwtToken = testJwtTokenProvider.generateTestToken("testuser");
  }

  @Given("a user exists with username {string}")
  public void a_user_exists_with_username(String username) {
    // Already created in @Before
  }

  @When("I fetch trainee profile by username {string}")
  public void i_fetch_trainee_profile_by_username(String username) {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + jwtToken);

    HttpEntity<String> entity = new HttpEntity<>(headers);
    response =
        restTemplate.exchange(
            "/api/trainees/profiles/" + username, HttpMethod.GET, entity, String.class);
  }

  @Then("the trainee profile is returned successfully")
  public void the_trainee_profile_is_returned_successfully() {
    assertEquals(200, response.getStatusCodeValue());
    assertNotNull(response.getBody());
  }

  @Given("a new trainee profile is ready")
  public void a_new_trainee_profile_is_ready() {
    traineeProfileDTO = new TraineeProfileDTO();
    traineeProfileDTO.setFirstName("New");
    traineeProfileDTO.setSecondName("Trainee");
    traineeProfileDTO.setDateOfBirth("2000-01-01"); // assuming it's a String
  }

  @When("I create a new trainee")
  public void i_create_a_new_trainee() {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("Authorization", "Bearer " + jwtToken);

    HttpEntity<TraineeProfileDTO> entity = new HttpEntity<>(traineeProfileDTO, headers);
    response = restTemplate.exchange("/api/trainees", HttpMethod.POST, entity, String.class);
  }

  @Then("the trainee is created successfully")
  public void the_trainee_is_created_successfully() {
    assertEquals(200, response.getStatusCodeValue());
    assertNotNull(response.getBody());
  }

  @Then("I receive a not found response")
  public void i_receive_a_not_found_response() {
    int code = response.getStatusCodeValue();
    assertTrue(code == 404 || code == 400, "Expected 404 or 400, but was " + code);
  }

  @Given("a trainee profile with missing required fields is ready")
  public void a_trainee_profile_with_missing_required_fields_is_ready() {
    traineeProfileDTO = new TraineeProfileDTO();
  }

  @Then("I receive a bad request response")
  public void i_receive_a_bad_request_response() {
    assertEquals(400, response.getStatusCodeValue());
  }

  @When("I delete trainee profile by username {string}")
  public void i_delete_trainee_profile_by_username(String username) {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + jwtToken);
    HttpEntity<String> entity = new HttpEntity<>(headers);

    response =
        restTemplate.exchange("/api/trainees/" + username, HttpMethod.DELETE, entity, String.class);
  }
}
