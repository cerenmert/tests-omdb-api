import helper.ApiResource;
import service.ByIdOrTitleService;

public class BaseServiceTest {
    String url = ApiResource.get("URL");
    String apiKey = ApiResource.get("API_KEY");

    ByIdOrTitleService byIdOrTitleService = new ByIdOrTitleService();
    RequestMaps requestMaps = new RequestMaps();
}
