@wip
Feature: Create User

  Scenario: Create a user successfully
    When I create a user with the details:
      | Name  | Graham                 |
      | Email | graham@grahamcox.co.uk |
    Then the user was created successfully
    And the created user details are:
      | Name     | Graham                     |
      | Email    | graham@grahamcox.co.uk     |
      | Enabled  | True                       |
      | Verified | False                      |
      | Created  | between now - PT2S and now |
      | Updated  | between now - PT2S and now |

    @ignore
    Scenario: Create an user with a duplicate email
      Given a user exists with the details:
        | Name  | Graham                 |
        | Email | graham@grahamcox.co.uk |
      When I create a user with the details:
        | Name  | Graham                 |
        | Email | graham@grahamcox.co.uk |
      Then user creation failed with the errors:
        | DUPLICATE_USER |

  @ignore
  Scenario: Create an user with an invalid email
    When I create a user with the details:
      | Name  | Graham |
      | Email | graham |
    Then user creation failed with the field errors:
      | Email | INVALID_EMAIL |