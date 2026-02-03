package helper;

import java.util.ResourceBundle;

public class ApiResource {

    private static ResourceBundle bundle = ResourceBundle.getBundle("api");

    public static String get(String key) {
        return bundle.getString(key);
    }
}
