package helper;

import org.testng.annotations.DataProvider;

public class DataProviderHelper {

    @DataProvider(name = "titleAndYear")
    public Object[][] getData() {
        return new Object[][]{
                {"Batman", "1989"},
                {"Inception", "2010"},
                {"The Matrix", "1999"},
                {"Interstellar", "2014"},
                {"The Dark Knight", "2008"}
        };
    }
}
