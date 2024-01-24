Feature: verify database steps

  Background:
    Given firstName has value John
    And lastName has value Doe
    And leon has value Leon


  Scenario: should populate table with provided data
    Given table TEST_USERS contains
      | ID | FIRST_NAME   | LAST_NAME   |
      | 1  | ${firstName} | ${lastName} |
      | 2  | Constantine  |             |

    Then verify table TEST_USERS contains
      | ID | FIRST_NAME   | LAST_NAME |
      | 1  | ${firstName} | Doe       |
      | 2  | Constantine  |           |


  Scenario: should insert into table
    Given table TEST_USERS contains
      | ID | FIRST_NAME | LAST_NAME |
      | 1  | John       | Doe       |

    When insert into table TEST_USERS
      | ID | FIRST_NAME | LAST_NAME |
      | 3  | Me         |           |
      | 4  | ${leon}    | Noel      |

    Then verify table TEST_USERS contains
      | ID | FIRST_NAME | LAST_NAME |
      | 1  | John       | Doe       |
      | 3  | Me         |           |
      | 4  | Leon       | Noel      |


  Scenario: should clear table
    Given table TEST_USERS contains
      | ID | FIRST_NAME  | LAST_NAME |
      | 1  | John        | Doe       |
      | 2  | Constantine |           |

    When clear table TEST_USERS

    Then verify table TEST_USERS is empty


  Scenario: should verify that table contains a few rows
    Given table TEST_USERS contains
      | ID | FIRST_NAME  | LAST_NAME |
      | 2  | John        | Doe       |
      | 4  | Leon        | Noel      |
      | 3  | Constantine |           |

    Then verify table TEST_USERS contains at least
      | ID | FIRST_NAME  | LAST_NAME   |
      | 2  | John        | ${lastName} |
      | 3  | Constantine |             |


  Scenario: should verify that table contains rows by few columns
    Given table TEST_USERS contains
      | ID | FIRST_NAME  | LAST_NAME |
      | 4  | John        | Doe       |
      | 1  | Leon        | Noel      |
      | 3  | Constantine |           |

    Then verify table TEST_USERS contains ignore columns
      | ID | FIRST_NAME   |
      | 1  | Leon         |
      | 4  | ${firstName} |

  Scenario: should update rows


  Scenario: should update row using one column


  Scenario: should populate table with provided data


  Scenario: should remove rows cascaded using foreign key


  Scenario: add error scenarios

  Scenario: foreign keys