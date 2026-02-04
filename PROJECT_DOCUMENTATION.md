# OMDB API Test Project Documentation

This document describes the structure of the OMDB API test automation project, the technologies used, and a detailed analysis of an example test scenario.

## 1. Project Overview
This project was developed to test the [OMDB API](http://www.omdbapi.com/), a database of movies and series. The project automatically tests the accuracy of functions such as movie search, detailed viewing, and season/episode querying provided by the API.

### Key Features
*   **API Testing:** HTTP requests are sent and responses are verified using the RestAssured library.
*   **Modeling (POJO):** JSON data returned from the API is converted into Java objects (POJO) (Deserialization) to ensure type-safe tests.
*   **Test Management:** Tests are organized, grouped, and reported using TestNG.
*   **Service Layer:** API requests are abstracted under the `service` package. This ensures test codes are cleaner and more readable.

## 2. Technologies Used

| Technology | Purpose | Version |
| :--- | :--- | :--- |
| **Java** | Programming language (JDK 15+) | 15 |
| **Maven** | Project management and dependency management | - |
| **TestNG** | Test framework (Assertion and test execution) | 7.11.0 |
| **RestAssured** | Creating HTTP requests and API testing | 4.4.0 |
| **Jackson Databind** | Converting JSON data to Java objects (Mapping) | 2.15.2 |
| **Hamcrest** | Advanced assertion expressions | - |

## 3. Project Structure

The project follows the standard Maven directory structure:

```
src
├── main
│   └── java
│       ├── helper          # Helper classes (API key, messages, data providers)
│       ├── model           # POJO classes mapping API responses (e.g., GetResponse)
│       ├── service         # Service classes managing API requests (e.g., ByIdOrTitleService)
│       └── spec            # Request and Response specifications (Base URL, common headers)
└── test
    └── java
        ├── BaseServiceTest.java # Base class inherited by all tests
        ├── TestsByIdOrTitle.java # Tests for search by ID or Title (?t=..., ?i=...)
        └── TestsBySearch.java    # Search tests (?s=...)
```

### Important Components
*   **`helper/ApiResource.java`**: Reads constant values like API Key and URL.
*   **`model/GetResponse.java`**: The Java counterpart of the JSON response returned from the API. Handles complex structures like the `Ratings` field and `PascalCase` fields like `Title`.
*   **`service/ByIdOrTitleService.java`**: Wraps RestAssured codes to prevent repetition. Test classes only call this service.

## 4. Example Test Analysis: `shouldSearchByTitleUsingModel`

Below is a detailed analysis of the method in `TestsByIdOrTitle.java` that converts the API response to a model and tests it.

### Code
```java
@Test
public void shouldSearchByTitleUsingModel() {
    // 1. Arrange
    Map<String, Object> params = requestMaps.titleMap("Batman", apiKey);

    // 2. Act
    Response response = byIdOrTitleService.getByIdOrTitle(params, ResponseSpec.checkStatusCodeOK());

    // 3. Assert
    GetResponse getResponse = response.as(GetResponse.class);
    assertThat(getResponse.getTitle(), Matchers.equalToIgnoringCase("Batman"));
}
```

### Step-by-Step Explanation

#### 1. Arrange
```java
Map<String, Object> params = requestMaps.titleMap("Batman", apiKey);
```
*   Parameters required for the test (`t=Batman`, `apikey=...`) are prepared in a `Map`. The `RequestMaps` helper class facilitates this process.

#### 2. Act
```java
Response response = byIdOrTitleService.getByIdOrTitle(params, ResponseSpec.checkStatusCodeOK());
```
*   A GET request is sent to the API using `byIdOrTitleService`.
*   **Service Layer:** This method uses RestAssured in the background: `RestAssured.given().queryParams(params).get()...`
*   **ResponseSpec:** The `ResponseSpec.checkStatusCodeOK()` parameter verifies that the incoming response is HTTP 200 (OK) at this stage. If it does not return 200, the test fails here.

#### 3. Assert
```java
GetResponse getResponse = response.as(GetResponse.class);
assertThat(getResponse.getTitle(), Matchers.equalToIgnoringCase("Batman"));
```
*   **Deserialization (Important):** The code `response.as(GetResponse.class)` converts the JSON response into an instance of the `GetResponse` class.
    *   For example, the `"Title": "Batman"` field in JSON corresponds to the `getTitle()` method in the Java object.
    *   The `jackson-databind` library performs this conversion automatically.
*   **Assertion:** Instead of dealing with JSON paths (strings), data is accessed via the Java object (`getResponse.getTitle()`) to check if it matches "Batman". This method reduces typos and makes code maintenance easier.

## 5. How to Run?

To run all tests, use the following command in the terminal:

```bash
mvn test
```

To run only a specific class:

```bash
mvn test -Dtest=TestsByIdOrTitle
```
