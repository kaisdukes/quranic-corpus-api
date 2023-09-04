package app.qurancorpus;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ResourceReader {
    private ResourceReader() {
    }

    public static BufferedReader readResource(String path) {
        return new BufferedReader(new InputStreamReader(resourceStream(path)));
    }

    public static InputStream resourceStream(String path) {
        var stream = ResourceReader.class.getResourceAsStream(path);
        if (stream == null) throw new UnsupportedOperationException("Resource not found: " + path);
        return stream;
    }
}