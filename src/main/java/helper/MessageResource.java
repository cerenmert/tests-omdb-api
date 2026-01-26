package helper;

import java.util.ResourceBundle;

public class MessageResource {

    private static ResourceBundle bundle = ResourceBundle.getBundle("messages");

    public static String getMessage(String key) {
        return bundle.getString(key);
    }
}
