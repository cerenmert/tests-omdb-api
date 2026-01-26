import helper.DataProviderHelper;
import helper.MessageResource;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import model.GetResponse;
import org.hamcrest.Matchers;
import org.testng.annotations.Test;
import spec.ResponseSpec;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;

public class TestsByIdOrTitle extends BaseServiceTest {
    String apiKey = "3cad8349";
    String url = "https://www.omdbapi.com";

    @Test
    public void shouldSearchByTitle() {
        Map<String, Object> params = requestMaps.titleMap("Batman", apiKey);
        Response response = byIdOrTitleService.getByIdOrTitle(params, ResponseSpec.checkStatusCodeOK());
        response.prettyPrint();
        assertThat(response.getBody().jsonPath().getString("Title"),
                Matchers.equalToIgnoringCase("Batman"));
    }

    @Test
    public void shouldSearchByTitleAndYearVersion1() {
        Map<String, Object> params = new HashMap<>();
        params.put("t", "Batman");
        params.put("y", "1989");
        params.put("apikey", apiKey);
        Response response = byIdOrTitleService.getByIdOrTitle(params, ResponseSpec.checkStatusCodeOK());
        String title = response.getBody().jsonPath().getString("Title");
        assertThat(title, Matchers.equalTo("Batman"));
    }

    @Test
    public void shouldSearchByTitleAndYearVersion2() {
        Map<String, Object> params = requestMaps.titleAndYearMap("Batman", "1989", apiKey);
        Response response = byIdOrTitleService.getByIdOrTitle(params, ResponseSpec.checkStatusCodeOK());
        assertThat(response.getBody().jsonPath().getString("Title"),
                Matchers.equalToIgnoringCase("Batman"));
    }

    @Test
    public void shouldSearchByTitleAndYearVersion3() {
        Map<String, Object> params = requestMaps.titleAndYearMap("Batman", "1989", apiKey);
        Response response = byIdOrTitleService.getByIdOrTitle(params, ResponseSpec.checkStatusCodeOK());
        GetResponse as = response.as(GetResponse.class);
        assertThat(as.getTitle(), Matchers.equalTo("Batman"));
    }

    @Test(dataProvider = "titleAndYear", dataProviderClass = DataProviderHelper.class)
    public void shouldSearchByTitleAndYearVersion4(String title, String year) {
        Map<String, Object> titledAndYear = requestMaps.titleAndYearMap(title, year, apiKey);
        Response response = byIdOrTitleService.getByIdOrTitle(titledAndYear, ResponseSpec.checkStatusCodeOK());
        assertThat(response.getBody().jsonPath().getString("Title"), Matchers.equalTo(title));
    }

    @Test
    public void shouldSearchByImdbIDAndYear() {
        Map<String, Object> params = new HashMap<>();
        params.put("i", "tt0096895");
        params.put("y", "1989");
        params.put("apikey", apiKey);
        Response responseByImdbIDAndYear = byIdOrTitleService.getByIdOrTitle(params, ResponseSpec.checkStatusCodeOK());
        assertThat(responseByImdbIDAndYear.getBody().jsonPath().getString("Title"),
                Matchers.equalToIgnoringCase("Batman"));
    }

    @Test
    public void shouldSearchByImdbID() {
        Map<String, Object> imdbIDMap = requestMaps.imdbIDMap(apiKey);
        Response responseByImdbID = byIdOrTitleService.getByIdOrTitle(imdbIDMap, ResponseSpec.checkStatusCodeOK());
        assertThat(responseByImdbID.getBody().jsonPath().getString("Title"),
                Matchers.equalToIgnoringCase("Batman"));
    }

    @Test
    public void shouldNotGetResponseWithoutApiKey() {
        String errorMessageWhen401 = "No API key provided.";
        Map<String, Object> params = new HashMap<>();
        params.put("t", "Batman");
        Response responseWhen401 = byIdOrTitleService.getByIdOrTitle(params, ResponseSpec.checkStatusCodeUnauthorized());
        assertThat(responseWhen401.getBody().jsonPath().getString("Error"),
                Matchers.equalToIgnoringCase(errorMessageWhen401));
    }

    @Test
    public void shouldNotGetResponseWithoutApiKeyVersion2() {
        RequestMaps requestMaps = new RequestMaps();
        Map<String, Object> params = requestMaps.noApiKeyMap("Batman");

        Response responseWhen401 = byIdOrTitleService.getByIdOrTitle(params, ResponseSpec.checkStatusCodeUnauthorized());
        String expected_error_message_when_401 = MessageResource.getMessage("ERROR_MESSAGE_WHEN_401");
        assertThat(responseWhen401.getBody().jsonPath().getString("Error"),
                Matchers.equalToIgnoringCase(expected_error_message_when_401));
    }

    @Test
    public void shouldRatingsValueGreaterThanFifty() {
        Map<String, Object> params = new HashMap<>();
        params.put("t", "Batman");
        params.put("apikey", apiKey);
        Response responseByTitle = byIdOrTitleService.getByIdOrTitle(params, ResponseSpec.checkStatusCodeOK());
        String ratingsValue = responseByTitle.getBody().jsonPath().getString("Ratings[1].Value");
        int rating = Integer.parseInt(ratingsValue.split("%")[0]);
        assertThat(rating, Matchers.greaterThan(50));
    }

    @Test
    public void shouldTypeIsMovie() {
        Map<String, Object> params = new HashMap<>();
        params.put("t", "Shrek");
        params.put("apikey", apiKey);
        Response response = byIdOrTitleService.getByIdOrTitle(params, ResponseSpec.checkStatusCodeOK());
        String type = response.getBody().jsonPath().getString("Type");
        assertThat(type, Matchers.equalToIgnoringCase("movie"));
    }

    @Test
    public void shouldPosterIsURL() {
        Map<String, Object> params = new HashMap<>();
        params.put("t", "Shrek");
        params.put("apikey", apiKey);
        Response response = byIdOrTitleService.getByIdOrTitle(params, ResponseSpec.checkStatusCodeOK());
        String posterUrl = response.getBody().jsonPath().getString("Poster");
        assertThat(posterUrl, Matchers.startsWith("https://"));
    }

    @Test
    public void shouldLanguageIsEnglish() {
        Map<String, Object> params = new HashMap<>();
        params.put("t", "Shrek");
        params.put("apikey", apiKey);
        Response response = byIdOrTitleService.getByIdOrTitle(params, ResponseSpec.checkStatusCodeOK());
        String language = response.getBody().jsonPath().getString("Language");
        assertThat(language, Matchers.containsString("English"));
    }

    @Test
    public void shouldMovieHasOscar() {
        Response response = RestAssured.given()
                .queryParam("t", "Shrek")
                .queryParam("apikey", apiKey)
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
                .queryParam("apikey", apiKey)
                .get(url)
                .then()
                .statusCode(200)
                .extract().response();
        String boxOffice = response.getBody().jsonPath().getString("BoxOffice");
        int boxOfficeValue = Integer.parseInt(boxOffice.replace("$", "").replace(",",
                ""));
        assertThat(boxOfficeValue, Matchers.greaterThan(850_000_000));
    }

    @Test
    public void shouldGetTvShowEpisodeByTitleSeasonAndEpisode() {
        Response response = RestAssured.given()
                .queryParam("t", "Game of Thrones")
                .queryParam("Season", "1")
                .queryParam("Episode", "6")
                .queryParam("apikey", apiKey)
                .get(url)
                .then()
                .statusCode(200)
                .extract().response();
        String episodeTitle = response.getBody().jsonPath().getString("Title");
        assertThat(episodeTitle, Matchers.equalToIgnoringCase("A Golden Crown"));
        String season = response.getBody().jsonPath().getString("Season");
        assertThat(season, Matchers.equalTo("1"));
        String episode = response.getBody().jsonPath().getString("Episode");
        assertThat(episode, Matchers.equalTo("6"));
        String seriesID = response.getBody().jsonPath().getString("seriesID");
        assertThat(seriesID, Matchers.equalTo("tt0944947"));
    }

    @Test
    public void shouldSeriesIdSameForDifferentEpisodes() {
        Response response = RestAssured.given()
                .queryParam("t", "Game of Thrones")
                .queryParam("Season", "1")
                .queryParam("Episode", "6")
                .queryParam("apikey", apiKey)
                .get(url)
                .then()
                .statusCode(200)
                .extract().response();
        String seriesID = response.getBody().jsonPath().getString("seriesID");
        assertThat(seriesID, Matchers.equalTo("tt0944947"));

        Response response2 = RestAssured.given()
                .queryParam("t", "Game of Thrones")
                .queryParam("Season", "1")
                .queryParam("Episode", "7")
                .queryParam("apikey", apiKey)
                .get(url)
                .then()
                .statusCode(200)
                .extract().response();
        String seriesID2 = response2.getBody().jsonPath().getString("seriesID");
        assertThat(seriesID, Matchers.equalTo(seriesID2));
    }

    @Test
    public void shouldEpisodeInfoMatchWhenSearchByOnlySeasonAndSearchBySeasonAndEpisode() {
        Response response = RestAssured.given()
                .queryParam("i", "tt0944947")
                .queryParam("Season", "2")
                .queryParam("apikey", apiKey)
                .get(url)
                .then()
                .statusCode(200)
                .extract().response();
        String episode1TitleFromSeasonResponse = response.getBody().jsonPath().getString("Episodes[0].Title");
        String imdbID1FromSeasonResponse = response.getBody().jsonPath().getString("Episodes[0].imdbID");
        String releaseDateFromSeasonResponse = response.getBody().jsonPath().getString("Episodes[0].Released");
        String releaseDateYearFromSeasonResponse = releaseDateFromSeasonResponse.split("-")[0]; //"2012-04-01"

        Response response2 = RestAssured.given()
                .queryParam("t", "Game of Thrones")
                .queryParam("Season", "2")
                .queryParam("Episode", "1")
                .queryParam("apikey", apiKey)
                .get(url)
                .then()
                .statusCode(200)
                .extract().response();
        String episode1TitleFromEpisodeResponse = response2.getBody().jsonPath().getString("Title");
        String imdbIDFromEpisodeResponse = response2.getBody().jsonPath().getString("imdbID");
        String releaseDateFromEpisodeResponse = response2.getBody().jsonPath().getString("Released");
        String releaseDateYearFromEpisodeResponse = releaseDateFromEpisodeResponse.split(" ")[2]; //"01 Apr 2012"

        assertThat(episode1TitleFromSeasonResponse, Matchers.equalToIgnoringCase(episode1TitleFromEpisodeResponse));
        assertThat(imdbID1FromSeasonResponse, Matchers.equalTo(imdbIDFromEpisodeResponse));
        assertThat(releaseDateYearFromSeasonResponse, Matchers.equalTo(releaseDateYearFromEpisodeResponse));
    }
}
