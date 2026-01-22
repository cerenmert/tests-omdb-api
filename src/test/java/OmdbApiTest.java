import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;

public class OmdbApiTest {

    String errorMessageWhen401 = "No API key provided.";

    @Test
    public void shouldSearchByTitle() {
        Response responseByTitle = RestAssured.given()
                .queryParam("t", "Batman")
                .queryParam("apikey", "3cad8349")
                .get("http://www.omdbapi.com")
                .then()
                .statusCode(200)
                .extract().response();
        responseByTitle.prettyPrint();
        assertThat(responseByTitle.getBody().jsonPath().getString("Title"), Matchers.equalToIgnoringCase("Batman"));
    }

    @Test
    public void shouldSearchByImdbIDAndYear() {
        Response responseByImdbIDAndYear = RestAssured.given()
                .queryParam("i", "tt0096895")
                .queryParam("y", "1989")
                .queryParam("apikey", "3cad8349")
                .get("http://www.omdbapi.com")
                .then()
                .statusCode(200)
                .extract().response();
        assertThat(responseByImdbIDAndYear.getBody().jsonPath().getString("Title"), Matchers.equalToIgnoringCase("Batman"));
    }

    @Test
    public void shouldSearchByImdbID() {
        Response responseByImdbID = RestAssured.given()
                .queryParam("i", "tt0096895")
                .queryParam("apikey", "3cad8349")
                .get("http://www.omdbapi.com")
                .then()
                .statusCode(200)
                .extract().response();
        assertThat(responseByImdbID.getBody().jsonPath().getString("Title"), Matchers.equalToIgnoringCase("Batman"));
    }

    @Test
    public void shouldNotGetResponseWithoutApiKey() {
        Response responseWhen401 = RestAssured.given()
                .queryParam("t", "Batman")
                .get("http://www.omdbapi.com")
                .then()
                .statusCode(401)
                .extract()
                .response();
        assertThat(responseWhen401.getBody().jsonPath().getString("Error"), Matchers.equalToIgnoringCase(errorMessageWhen401));
    }


}
