Feature: Trainer Workload Management - Component

  Scenario: Fetch trainer workload summary successfully
    Given the trainer with ID 1 exists
    When the client retrieves the workload summary for trainer ID 1
    Then the response status should be 200
    And the response should include total training hours

  Scenario: Fetch trainer workload summary for non-existing trainer
    Given no trainer with ID 999 exists
    When the client retrieves the workload summary for trainer ID 999
    Then the response status should be 404

  Scenario: Fetch trainer workload summary without authentication
    When the client retrieves the workload summary for trainer ID 1 without authentication
    Then the response status should be 401