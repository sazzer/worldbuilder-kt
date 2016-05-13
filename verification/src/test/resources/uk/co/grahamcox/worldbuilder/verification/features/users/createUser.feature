@wip
Feature: Create User

  Scenario: Create a user successfully
    When I create a user with the details:
      | Name  | Graham                 |
      | Email | graham@grahamcox.co.uk |
    Then the user was created successfully
    And the created user details are:
      | Name     | Graham                 |
      | Email    | graham@grahamcox.co.uk |
      | Created  | now within PT0S        |
      | Updated  | now within PT0S        |
      | Enabled  | True                   |
      | Verified | False                  |

    Scenario: Create an user with a duplicate email
      Given a user exists with the details:
        | Name  | Graham                 |
        | Email | graham@grahamcox.co.uk |
      When I create a user with the details:
        | Name  | Graham                 |
        | Email | graham@grahamcox.co.uk |
      Then user creation failed with the error "DUPLICATE_USER"

  Scenario: Create an user with an invalid email
    When I create a user with the details:
      | Name  | Graham |
      | Email | graham |
    Then user creation failed with the error "INVALID_EMAIL"