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


  Scenario: should insert into the table
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


  Scenario: should clear the table
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


  Scenario: should verify that table contains rows by a few columns
    Given table TEST_USERS contains
      | ID | FIRST_NAME  | LAST_NAME |
      | 4  | John        | Doe       |
      | 1  | Leon        | Noel      |
      | 3  | Constantine |           |

    Then verify table TEST_USERS contains ignore columns
      | ID | FIRST_NAME   |
      | 1  | Leon         |
      | 4  | ${firstName} |


  Scenario: should update multiple rows
    Given table TEST_USERS contains
      | ID | FIRST_NAME  | LAST_NAME |
      | 2  | John        | Doe       |
      | 4  | Leon        | Noel      |
      | 3  | Constantine |           |

    Given table TEST_USERS contains after update
      | ID | FIRST_NAME  | LAST_NAME |
      | 2  | John        | Noel      |
      | 4  | Leon        |           |
      | 3  | Constantine | Doe       |

    Then verify table TEST_USERS contains
      | ID | FIRST_NAME  | LAST_NAME |
      | 2  | John        | Noel      |
      | 4  | Leon        |           |
      | 3  | Constantine | Doe       |


  Scenario: should update a single row
    Given table TEST_USERS contains
      | ID | FIRST_NAME  | LAST_NAME |
      | 2  | John        | Doe       |
      | 3  | Constantine |           |

    Given table TEST_USERS contains after update
      | ID | FIRST_NAME | LAST_NAME |
      | 2  | John       | J         |

    Then verify table TEST_USERS contains at least
      | ID | FIRST_NAME | LAST_NAME |
      | 2  | John       | J         |


  Scenario: should remove rows cascaded using foreign key


  Scenario: add error scenarios

  Scenario: foreign keys