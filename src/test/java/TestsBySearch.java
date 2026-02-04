import helper.ApiResource;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;

public class TestsBySearch {
    String url = ApiResource.get("URL");
    String apiKey = ApiResource.get("API_KEY");

    @Test
    public void shouldCheckTotalResultWithSearchByTitle() {
        Response responseBySearch = RestAssured.given()
                .queryParam("s", "Batman")
                .queryParam("apikey", apiKey)
                .get(url)
                .then()
                .statusCode(200)
                .extract().response();
        responseBySearch.prettyPrint();
        responseBySearch.then().body("Response", Matchers.equalTo("True"));
        int totalResults = Integer.parseInt(responseBySearch.getBody().jsonPath().getString("totalResults"));
        Assert.assertEquals(totalResults, 632);
    }

    @Test
    public void shouldTitleListContainsSomeOfTheTitles() {
        Response responseBySearch = RestAssured.given()
                .queryParam("s", "Batman")
                .queryParam("apikey", apiKey)
                .get(url)
                .then()
                .statusCode(200)
                .extract().response();
        List<String> allTitles = responseBySearch.getBody().jsonPath().getList("Search.Title");
        List<String> expectedTitles = new ArrayList<>();
        expectedTitles.add("Batman Begins");
        expectedTitles.add("The Batman");
        expectedTitles.add("Batman v Superman: Dawn of Justice");
        for (String expectedTitle : expectedTitles) {
            assertThat(allTitles, Matchers.hasItem(expectedTitle));
        }
    }

    @Test
    public void shouldTypeCountIsCorrectInTheAllTypesList() {
        Response responseBySearch = RestAssured.given()
                .queryParam("s", "Batman")
                .queryParam("apikey", apiKey)
                .get(url)
                .then()
                .statusCode(200)
                .extract().response();
        List<String> allTypes = responseBySearch.getBody().jsonPath().getList("Search.Type");
        long movieCount = allTypes.stream().filter(type -> type.equalsIgnoreCase("movie")).count();
        Assert.assertEquals(movieCount, 9);
        long seriesCount = allTypes.stream().filter(type -> type.equalsIgnoreCase("series")).count();
        Assert.assertEquals(seriesCount, 1);
    }

    @Test
    public void shouldAllYearsAreInExpectedRange() {
        Response responseBySearch = RestAssured.given()
                .queryParam("s", "Batman")
                .queryParam("apikey", apiKey)
                .get(url)
                .then()
                .statusCode(200)
                .extract().response();
        List<String> allYears = responseBySearch.getBody().jsonPath().getList("Search.Year");
        for (String yearStr : allYears) {
            String yearToCheck = yearStr;
            if (yearStr.contains("–")) { // Handle cases like "2014–2019"
                yearToCheck = yearStr.split("–")[0];
            }
            int year = Integer.parseInt(yearToCheck);
            Assert.assertTrue(year >= 1989 && year <= 2022, "Year " + year + " is out of expected " +
                    "range.");
        }
    }

    @Test
    public void shouldAllImdbIDsAreUnique() {
        Response responseBySearch = RestAssured.given()
                .queryParam("s", "Batman")
                .queryParam("apikey", apiKey)
                .get(url)
                .then()
                .statusCode(200)
                .extract().response();
        List<String> allImdbIDs = responseBySearch.getBody().jsonPath().getList("Search.imdbID");
        long uniqueImdbIDCount = allImdbIDs.stream().distinct().count();
        Assert.assertEquals(uniqueImdbIDCount, allImdbIDs.size(), "There are duplicate imdbIDs in the list.");
    }

    @Test
    public void shouldReturnResultsForAllPages() {
        Response responseOnly = RestAssured.given()
                .queryParam("s", "Batman")
                .queryParam("apikey", apiKey)
                .get(url)
                .then()
                .statusCode(200)
                .extract().response();
        int totalResults = Integer.parseInt(responseOnly.getBody().jsonPath().getString("totalResults"));
        int totalPages = (int) Math.ceil((double) totalResults / 10);

        for (int page = 1; page <= totalPages; page++) {
            Response response = RestAssured.given()
                    .queryParam("s", "Batman")
                    .queryParam("page", String.valueOf(page))
                    .queryParam("apikey", apiKey)
                    .get(url)
                    .then()
                    .statusCode(200)
                    .extract().response();

            List<String> titles = response.getBody().jsonPath().getList("Search.Title");
            Assert.assertNotNull(titles, "Search results should not be null");
            Assert.assertFalse(titles.isEmpty(), "Search results should not be empty for page " + page);

            List<String> years = response.getBody().jsonPath().getList("Search.Year");
            Assert.assertNotNull(years, "Year results should not be null");
            Assert.assertFalse(years.isEmpty(), "Year results should not be empty for page " + page);

            List<String> imdbID = response.getBody().jsonPath().getList("Search.imdbID");
            Assert.assertNotNull(imdbID, "imdbID results should not be null");
            Assert.assertFalse(imdbID.isEmpty(), "imdbID results should not be empty for page " + page);

            List<String> types = response.getBody().jsonPath().getList("Search.Type");
            Assert.assertNotNull(types, "Type results should not be null");
            Assert.assertFalse(types.isEmpty(), "Type results should not be empty for page " + page);
        }
    }

    @Test
    public void shouldReturnDifferentResultsForDifferentPages() {
        Response responsePage1 = RestAssured.given()
                .queryParam("s", "Batman")
                .queryParam("page", "1")
                .queryParam("apikey", apiKey)
                .get(url)
                .then()
                .statusCode(200)
                .extract().response();
        List<String> imdbIDsPage1 = responsePage1.getBody().jsonPath().getList("Search.imdbID");

        Response responsePage2 = RestAssured.given()
                .queryParam("s", "Batman")
                .queryParam("page", "2")
                .queryParam("apikey", apiKey)
                .get(url)
                .then()
                .statusCode(200)
                .extract().response();
        List<String> imdbIDsPage2 = responsePage2.getBody().jsonPath().getList("Search.imdbID");
        Assert.assertNotEquals(imdbIDsPage1, imdbIDsPage2, "Page 1 and Page 2 results should be different");

        // Ensure no overlap (optional but good)
        for (String id : imdbIDsPage1) {
            Assert.assertFalse(imdbIDsPage2.contains(id), "Page 2 should not contain ID from Page 1: " + id);
        }
    }

    @Test
    public void shouldHandleOutOfRangePage() {
        // First get total results to calculate a clearly out of range page
        Response initResponse = RestAssured.given()
                .queryParam("s", "Batman")
                .queryParam("apikey", apiKey)
                .get(url)
                .then()
                .statusCode(200)
                .extract().response();
        int totalResults = Integer.parseInt(initResponse.getBody().jsonPath().getString("totalResults"));
        int totalPages = (int) Math.ceil((double) totalResults / 10);
        int outOfRangePage = totalPages + 100; // safely out of range

        Response response = RestAssured.given()
                .queryParam("s", "Batman")
                .queryParam("page", outOfRangePage)
                .queryParam("apikey", apiKey)
                .get(url)
                .then()
                .statusCode(200)
                .extract().response();
        // OMDB usually returns 200 even for errors/not found, but json body has Response: False
        String responseStatus = response.getBody().jsonPath().getString("Response");
        String errorMsg = response.getBody().jsonPath().getString("Error");
        Assert.assertEquals(responseStatus, "False", "Response should be False for out of range page");
        Assert.assertEquals(errorMsg, "Movie not found!", "Error message should be 'Movie not found!'");
    }
}
