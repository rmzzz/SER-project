package bong.util;

import java.io.InputStream;
import java.net.URL;

public interface ResourceLoader {
    URL getResource(String fileName);

    InputStream getResourceAsStream(String fileName);

    default URL getViewResource(String fileName) {
        return getResource(viewPath(fileName));
    }

    default InputStream getViewResourceAsStream(String fileName) {
        return getResourceAsStream(viewPath(fileName));
    }

    private static String viewPath(String fileName) {
        return "views/" + fileName;
    }
}
