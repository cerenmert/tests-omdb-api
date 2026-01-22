import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;

public class OmdbApiTests_ByIdOdTitle {
    String url = "http://www.omdbapi.com";
    String errorMessageWhen401 = "No API key provided.";

    @Test
    public void shouldSearchByTitle() {
        Response responseByTitle = RestAssured.given()
                .queryParam("t", "Batman")
                .queryParam("apikey", "3cad8349")
                .get(url)
                .then()
                .statusCode(200)
                .extract().response();
        responseByTitle.prettyPrint();
        assertThat(responseByTitle.getBody().jsonPath().getString("Title"),
                Matchers.equalToIgnoringCase("Batman"));
    }

    @Test
    public void shouldSearchByImdbIDAndYear() {
        Response responseByImdbIDAndYear = RestAssured.given()
                .queryParam("i", "tt0096895")
                .queryParam("y", "1989")
                .queryParam("apikey", "3cad8349")
                .get(url)
                .then()
                .statusCode(200)
                .extract().response();
        assertThat(responseByImdbIDAndYear.getBody().jsonPath().getString("Title"),
                Matchers.equalToIgnoringCase("Batman"));
    }

    @Test
    public void shouldSearchByImdbID() {
        Response responseByImdbID = RestAssured.given()
                .queryParam("i", "tt0096895")
                .queryParam("apikey", "3cad8349")
                .get(url)
                .then()
                .statusCode(200)
                .extract().response();
        assertThat(responseByImdbID.getBody().jsonPath().getString("Title"),
                Matchers.equalToIgnoringCase("Batman"));
    }

    @Test
    public void shouldNotGetResponseWithoutApiKey() {
        Response responseWhen401 = RestAssured.given()
                .queryParam("t", "Batman")
                .get(url)
                .then()
                .statusCode(401)
                .extract()
                .response();
        assertThat(responseWhen401.getBody().jsonPath().getString("Error"),
                Matchers.equalToIgnoringCase(errorMessageWhen401));
    }

    @Test
    public void shouldRatingsValueGreaterThanFifty() {
        Response responseByTitle = RestAssured.given()
                .queryParam("t", "Batman")
                .queryParam("apikey", "3cad8349")
                .get(url)
                .then()
                .statusCode(200)
                .extract().response();
        String ratingsValue = responseByTitle.getBody().jsonPath().getString("Ratings[1].Value");
        int rating = Integer.parseInt(ratingsValue.split("%")[0]);
        assertThat(rating, Matchers.greaterThan(50));
    }

    @Test
    public void shouldTypeIsMovie() {
        Response response = RestAssured.given()
                .queryParam("t", "Shrek")
                .queryParam("apikey", "3cad8349")
                .get(url)
                .then()
                .statusCode(200)
                .extract().response();
        String type = response.getBody().jsonPath().getString("Type");
        assertThat(type, Matchers.equalToIgnoringCase("movie"));
    }

    @Test
    public void shouldPosterIsURL() {
        Response response = RestAssured.given()
                .queryParam("t", "Shrek")
                .queryParam("apikey", "3cad8349")
                .get(url)
                .then()
                .statusCode(200)
                .extract().response();
        String posterUrl = response.getBody().jsonPath().getString("Poster");
        assertThat(posterUrl, Matchers.startsWith("https://"));
    }

    @Test
    public void shouldLanguageIsEnglish() {
        Response response = RestAssured.given()
                .queryParam("t", "Shrek")
                .queryParam("apikey", "3cad8349")
                .get(url)
                .then()
                .statusCode(200)
                .extract().response();
        String language = response.getBody().jsonPath().getString("Language");
        assertThat(language, Matchers.containsString("English"));
    }

    @Test
    public void shouldMovieHasOscar() {
        Response response = RestAssured.given()
                .queryParam("t", "Shrek")
                .queryParam("apikey", "3cad8349")
                .get(url)
                .then()
                .statusCode(200)
                .extract().response();
        String awards = response.getBody().jsonPath().getString("Awards");
        boolean hasTheMovieOscar = awards.split("Oscar")[0].contains("Won");
        assertThat(hasTheMovieOscar, Matchers.is(true));
    }

    @Test
    public void shouldBoxOfficeGreaterThan850Million() {
        Response response = RestAssured.given()
                .queryParam("t", "Avengers: Endgame")
                .queryParam("apikey", "3cad8349")
                .get(url)
                .then()
                .statusCode(200)
                .extract().response();
        String boxOffice = response.getBody().jsonPath().getString("BoxOffice");
        int boxOfficeValue = Integer.parseInt(boxOffice.replace("$", "").replace(",", ""));
        assertThat(boxOfficeValue, Matchers.greaterThan(850_000_000));
    }
}
