Feature: Verify REST requests feature

  Scenario: verify GET request executed
    When I send GET request to http://localhost:8080/api/users

    Then the following json response is received ignore fields: [createdAt] and contains
      """
      {
        "id": "1",
        "firstName": "John",
        "lastName": "Doe",
        "updatedAt": null
      }
      """


  Scenario: verify GET request executed with the request parameters
    When I send GET request to http://localhost:8080/api/users/1/with-headers with the following params
      """
      {
        "requestMetadata": {
          "headers": {
            "header_key": "header_value"
          }
        }
      }
      """
    Then the following json response is received ignore fields: [createdAt]
      """
      {
        "id": "1",
        "firstName": "John",
        "lastName": "Doe",
        "updatedAt": null
      }
      """


  Scenario: verify POST request executed
    When I send POST request to http://localhost:8080/api/users with the following params
    """
    {
      "body": {
        "firstName": "Other",
        "lastName": "One"
      }
    }
    """
    Then the following json response is received ignore fields: [id, createdAt]
      """
      {
        "firstName": "Other",
        "lastName": "One",
        "updatedAt": null
      }
      """


  Scenario: verify PUT request executed
    When I send PUT request to http://localhost:8080/api/users with the following params
    """
    {
      "body": {
        "id": "2",
        "firstName": "Jonny",
        "lastName": "Updated"
      }
    }
    """
    Then the following json response is received ignore fields: [id, createdAt, updatedAt]
      """
      {
        "firstName": "Jonny",
        "lastName": "Updated"
      }
      """

#    status code