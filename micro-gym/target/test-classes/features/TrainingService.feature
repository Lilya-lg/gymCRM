Feature: Training Service Integration

  Scenario: Add a training
    Given a trainee with username "trainee_user" exists
    And a trainer with username "trainer_user" exists
    Given a training named "Morning Session"
    When the training is added
    Then the training should be saved

  Scenario: Link trainee and trainer
    Given a trainee with username "trainee_user" exists
    And a trainer with username "trainer_user" exists
    Given a training to link
    When trainee "trainee_user" and trainer "trainer_user" are linked
    Then the training should have linked entities

  Scenario: Find trainings by trainer
    Given a trainee with username "trainee_user" exists
    And a trainer with username "trainer_user" exists
    Given existing training for trainer "trainer_user"
    When I search trainings for trainer "trainer_user"
    Then I should get a list of trainings

  Scenario: Find trainings by trainee
    Given a trainee with username "trainee_user" exists
    And a trainer with username "trainer_user" exists
    Given existing training for trainee "trainee_user"
    When I search trainings for trainee "trainee_user"
    Then I should get a list of trainings

  Scenario: Create a training and notify microservice
    Given a trainer with username "trainer_user" exists
    And a trainee with username "trainee_user" exists
    And a training named "Yoga Class"
    When the training is created
    Then the training should be saved
    And a message should be sent to the microservice

  Scenario: Sending a training message to the microservice fails
    Given a training named "FailedTraining"
    When the training is created
    Then sending the message should fail