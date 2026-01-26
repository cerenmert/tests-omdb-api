package service;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.ResponseSpecification;
import spec.RequestSpec;

import java.util.Map;

public class ByIdOrTitleService extends RequestSpec {

    public ByIdOrTitleService() {
        super("https://www.omdbapi.com/");
    }

    public Response getByIdOrTitle(Map<String, Object> params, ResponseSpecification responseSpecification) {
        return RestAssured
                .given()
                .spec(super.getRequestSpecification())
                .queryParams(params)
                .get()
                .then()
                .spec(responseSpecification)
                .extract().response();
    }
}
