import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;

public class TestsBySearch {
    String url = "http://www.omdbapi.com";
    String apiKey = "3cad8349";

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
            Assert.assertTrue(year >= 1989 && year <= 2022, "Year " + year + " is out of expected range.");
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
}
