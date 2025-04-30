Feature: User Authentication Management

  Scenario: Successful login
    Given a user exists with username "testuser" and password "password"
    When I login with username "testuser" and password "password"
    Then I receive a success response with a token

  Scenario: Failed login with wrong password
    When I login with username "testuser" and password "wrongpassword"
    Then I receive a failed login response

  Scenario: Failed login with non-existent user
    When I login with username "nonexistent" and password "password"
    Then I receive a failed login response

  Scenario: Successful logout
    Given I am logged in as "testuser"
    When I logout
    Then I receive a success logout response
