package spec;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;

public class RequestSpec {
    RequestSpecification requestSpecification;

    public RequestSpec(String baseUri) {
        requestSpecification = new RequestSpecBuilder()
                .setBaseUri(baseUri)
                .build();
    }

    public RequestSpecification getRequestSpecification() {
        return requestSpecification;
    }

}
