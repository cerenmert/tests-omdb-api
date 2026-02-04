# OMDB API Test

This project contains automated tests for the [OMDB API](http://www.omdbapi.com/) using Java, TestNG, and RestAssured.

## Project Structure

- **Language**: Java 15
- **Build Tool**: Maven
- **Test Framework**: TestNG
- **API Client**: RestAssured
- **JSON Processing**: Jackson Databind

## Prerequisites

- Java JDK 15 or higher
- Maven installed

## Models

### GetResponse.java
A POJO (Plain Old Java Object) class representing the formatted JSON response from the OMDB API. 
- **Annotations**: Uses Jackson annotations (`@JsonNaming`, `@JsonIgnoreProperties`) to handle `PascalCase` JSON keys and ignore unknown properties.
- **Type Handling**: Handles complex types like `Ratings` which is a `List<Map<String, String>>`.

## Running Tests

To run all tests, navigate to the project root and execute:

```bash
mvn test
```

To run a specific test class:

```bash
mvn test -Dtest=TestsBySearch
mvn test -Dtest=TestsByIdOrTitle
```

## Test Cases

### TestsBySearch.java
Located in `src/test/java/TestsBySearch.java`, these tests focus on the search functionality (`?s=...`) and pagination.

#### Search Logic
- **`shouldCheckTotalResultWithSearchByTitle`**: Verifies that searching for "Batman" returns the expected total number of results.
- **`shouldTitleListContainsSomeOfTheTitles`**: Checks if the search results for "Batman" contain specific expected titles (e.g., "Batman Begins", "The Batman").
- **`shouldTypeCountIsCorrectInTheAllTypesList`**: Verifies the count of "movie" and "series" types in the first page of results.
- **`shouldAllYearsAreInExpectedRange`**: Ensures that the release years of the movies found are within the expected range (1989 - 2022).
- **`shouldAllImdbIDsAreUnique`**: Verifies that all `imdbID`s returned in the first page are unique.

#### Pagination
- **`shouldReturnResultsForAllPages`**: Iterates through **all pages** of the search results for "Batman" and validates that:
    - Titles list is not empty.
    - Years list is not empty.
    - ImdbIDs list is not empty.
    - Types list is not empty.
- **`shouldReturnDifferentResultsForDifferentPages`**: Compares results from Page 1 and Page 2 to ensure they contain different movies and no overlapping `imdbID`s.
- **`shouldHandleOutOfRangePage`**: Verifies that requesting a page number that exceeds the total number of pages returns a "False" response with the error "Movie not found!".

### TestsByIdOrTitle.java
Located in `src/test/java/TestsByIdOrTitle.java`, these tests focus on looking up specific titles (`?t=...`) or IDs (`?i=...`).

#### Lookup Logic
- **`shouldSearchByTitle`**: Verifies exact title search (e.g., "Batman").
- **`shouldSearchByTitleUsingModel`**: Demonstrates how to deserialize the JSON response directly into the `GetResponse` Java object for type-safe assertions.
- **`shouldSearchByImdbIDAndYear`**: Verifies search by specific ImdbID and Year.
- **`shouldSearchByImdbID`**: Verifies search by specific ImdbID.
- **`shouldNotGetResponseWithoutApiKey`**: Verifies that requests without an API key return a 401 Unauthorized status.
- **`shouldRatingsValueGreaterThanFifty`**: Checks if the Rotten Tomatoes rating (source index 1) is greater than 50%.
- **`shouldTypeIsMovie`**: Verifies that the `Type` field corresponds to "movie" for a specific title.
- **`shouldPosterIsURL`**: Verifies that the `Poster` field contains a valid URL starting with "https://".
- **`shouldLanguageIsEnglish`**: Verifies that the `Language` field includes "English".
- **`shouldMovieHasOscar`**: Verifies that the `Awards` field indicates the movie won an Oscar.
- **`shouldBoxOfficeGreaterThan850Million`**: Checks if the `BoxOffice` revenue exceeds $850 million for "Avengers: Endgame".

#### Series & Episodes
- **`shouldGetTvShowEpisodeByTitleSeasonAndEpisode`**: Verifies fetching a specific episode by Title, Season, and Episode number.
- **`shouldSeriesIdSameForDifferentEpisodes`**: Ensures that different episodes of the same show share the same `seriesID`.
- **`shouldEpisodeInfoMatchWhenSearchByOnlySeasonAndSearchBySeasonAndEpisode`**: Verifies data consistency between fetching a season list vs. fetching a specific episode directly.

## Configuration

- **Base URL**: `https://www.omdbapi.com`
- **API Key**: You should create an API key from this URL: [OMDB API Key](http://www.omdbapi.com/apikey.aspx) 
