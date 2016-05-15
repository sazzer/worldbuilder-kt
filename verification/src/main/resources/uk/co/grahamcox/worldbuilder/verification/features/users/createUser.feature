Feature: Create User

  Scenario Outline: Create a user successfully: <Notes>
    When I create a user with the details:
      | Name  | Graham  |
      | Email | <Email> |
    Then the user was created successfully
    And the created user details are:
      | Name     | Graham                     |
      | Email    | <Email>                    |
      | Enabled  | True                       |
      | Verified | False                      |
      | Created  | between now - PT2S and now |
      | Updated  | between now - PT2S and now |

    Examples: Various email addresses
      | Email                       | Notes                     |
      | graham@grahamcox.co.uk      |                           |
      | graham+test@grahamcox.co.uk | Plus sign in address      |

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

  Scenario Outline: Create an user with an invalid email: <Notes>
    When I create a user with the details:
      | Name  | Graham  |
      | Email | <Email> |
    Then user creation failed with the field errors:
      | Email | INVALID_EMAIL |

    Examples: Various email addresses
      | Email                   | Notes           |
      | graham                  | No domain       |
      | graham@localhost        | Local domain    |
      | .graham@grahamcox.co.uk | Leading period  |
      | graham.@grahamcox.co.uk | Trailing period |