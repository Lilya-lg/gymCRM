Feature: Trainer Controller Component Tests

  Scenario: Successfully create a trainer
    Given a valid trainer registration request
    When I create a new trainer
    Then the trainer is created and a password is returned

  Scenario: Get trainer profile by username
    Given a trainer exists with username "traineruser"
    When I request the trainer profile for "traineruser"
    Then the trainer profile is returned successfully

  Scenario: Update trainer profile
    Given a trainer profile update request is ready for "traineruser"
    When I update the trainer profile
    Then the updated trainer profile is returned

  Scenario: Fetch unassigned active trainers
    Given I want to get unassigned trainers for trainee "traineeuser"
    When I request the unassigned active trainers
    Then a list of available trainers is returned
