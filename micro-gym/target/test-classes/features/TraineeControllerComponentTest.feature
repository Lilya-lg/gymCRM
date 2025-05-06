Feature: Trainee Management

  Scenario: Fetch a trainee profile by username
    Given a user exists with username "testuser"
    When I fetch trainee profile by username "testuser"
    Then the trainee profile is returned successfully

  Scenario: Create a new trainee
    Given a new trainee profile is ready
    When I create a new trainee
    Then the trainee is created successfully

  Scenario: Fetch a trainee profile by non-existent username
    When I fetch trainee profile by username "nonexistentuser"
    Then I receive a not found response

  Scenario: Create a trainee with missing required fields
    Given a trainee profile with missing required fields is ready
    When I create a new trainee
    Then I receive a bad request response

