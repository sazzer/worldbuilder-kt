Feature: Health Checks

  Scenario: Performing the Health Checks works correctly
    When I request the health check status
    Then all health checks pass
