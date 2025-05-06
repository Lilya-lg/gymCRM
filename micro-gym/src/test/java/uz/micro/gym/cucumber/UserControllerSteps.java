package uz.micro.gym.cucumber;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import uz.micro.gym.domain.User;
import uz.micro.gym.repository.UserRepository;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class UserControllerSteps {

  @Autowired private TestRestTemplate restTemplate;

  @Autowired private PasswordEncoder passwordEncoder;

  @Autowired private UserRepository userRepository;

  private String jwtToken;
  private ResponseEntity<Map> response;

  @Before
  public void setupTestUser() {
    if (!userRepository.findByUsername("testuser").isPresent()) {
      User user = new User();
      user.setFirstName("Test");
      user.setLastName("User");
      user.setUsername("testuser");
      user.setPassword(passwordEncoder.encode("password"));
      userRepository.save(user);
    }
  }

  @Given("a user exists with username {string} and password {string}")
  public void a_user_exists_with_username_and_password(String username, String password) {}

  @When("I login with username {string} and password {string}")
  public void i_login_with_username_and_password(String username, String password) {
    String url = "/api/users/login?username=" + username + "&password=" + password;
    response = restTemplate.postForEntity(url, null, Map.class);
  }

  @Then("I receive a success response with a token")
  public void i_receive_a_success_response_with_a_token() {
    assertEquals(200, response.getStatusCodeValue());
    assertTrue((Boolean) response.getBody().get("success"));
    assertNotNull(response.getBody().get("token"));

    jwtToken = (String) response.getBody().get("token");
  }

  @Given("I am logged in as {string}")
  public void i_am_logged_in_as(String username) {
    // Log in manually to get token
    String url = "/api/users/login?username=" + username + "&password=password";
    response = restTemplate.postForEntity(url, null, Map.class);
    jwtToken = (String) response.getBody().get("token");
  }

  @When("I logout")
  public void i_logout() {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + jwtToken);

    HttpEntity<String> entity = new HttpEntity<>(headers);
    response = restTemplate.exchange("/api/users/logout", HttpMethod.POST, entity, Map.class);
  }

  @Then("I receive a success logout response")
  public void i_receive_a_success_logout_response() {
    assertEquals(200, response.getStatusCodeValue());
    assertTrue((Boolean) response.getBody().get("success"));
  }

  @Then("I receive a failed login response")
  public void i_receive_a_failed_login_response() {
    if (response.getStatusCode().value() == 404) {
      return;
    }

    assertEquals(200, response.getStatusCodeValue());
    assertFalse((Boolean) response.getBody().get("success"));
    assertNull(response.getBody().get("token"));
  }

  @Given("I have an invalid token")
  public void i_have_an_invalid_token() {
    jwtToken = "invalid.jwt.token";
  }

  @Then("I receive an unauthorized response")
  public void i_receive_an_unauthorized_response() {
    assertEquals(401, response.getStatusCodeValue(), "Expected 401 Unauthorized");
  }
}
