Feature: verify database steps

  Scenario: should populate table with provided data
    Given table TEST_USERS contains
      | ID | FIRST_NAME  | LAST_NAME |
      | 1  | John        | Doe       |
      | 2  | Constantine |           |

    Then verify table TEST_USERS contains
      | ID | FIRST_NAME  | LAST_NAME |
      | 1  | John        | Doe       |
      | 2  | Constantine |           |


  Scenario: another
    Given 123 has value 222