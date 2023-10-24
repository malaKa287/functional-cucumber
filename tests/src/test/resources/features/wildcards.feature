Feature: verify wildcards

  Scenario: should persist variable
    Given my_value has value 42
    Then my_value should have value 42
