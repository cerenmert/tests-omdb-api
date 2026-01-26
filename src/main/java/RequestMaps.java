import java.util.HashMap;
import java.util.Map;

public class RequestMaps {

    public Map<String, Object> titleMap(String title, String apiKey) {
        Map<String, Object> params = new HashMap<>();
        params.put("t", title);
        params.put("apikey", apiKey);
        return params;
    }

    public Map<String, Object> titleAndYearMap(String title, String year, String apiKey) {
        Map<String, Object> params = new HashMap<>();
        params.put("t", title);
        params.put("y", year);
        params.put("apikey", apiKey);
        return params;
    }

    public Map<String, Object> imdbIDMap(String apiKey) {
        Map<String, Object> params = new HashMap<>();
        params.put("i", "tt0096895");
        params.put("apikey", apiKey);
        return params;
    }

    public Map<String, Object> imdbIDAndYearMap(String apiKey) {
        Map<String, Object> params = new HashMap<>();
        params.put("i", "tt0096895");
        params.put("y", "1989");
        params.put("apikey", apiKey);
        return params;
    }

    public Map<String, Object> noApiKeyMap(String title) {
        Map<String, Object> params = new HashMap<>();
        params.put("t", title);
        return params;
    }


}
