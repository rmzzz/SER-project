package bong;

import bong.util.ResourceLoader;

import java.io.InputStream;
import java.net.URL;

class AppResourceLoader implements ResourceLoader {
    @Override
    public URL getResource(String fileName) {
        return App.class.getResource(fileName);
    }

    @Override
    public InputStream getResourceAsStream(String fileName) {
        return App.class.getResourceAsStream(fileName);
    }
}
