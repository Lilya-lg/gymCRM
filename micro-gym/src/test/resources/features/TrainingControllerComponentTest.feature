Feature: Training Management

  Scenario: Create a new training successfully
    Given a new training is ready
    When I create a new training
    Then the training is created successfully

  Scenario: Fetch trainings for a trainee with invalid username
    When I search trainings for trainee username "invalid_user_xyz"
    Then I receive not found response

  Scenario: Create training with missing required fields
    Given a training with missing required fields is ready
    When I create a new training
    Then I receive bad request response