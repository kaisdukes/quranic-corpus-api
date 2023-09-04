package app.qurancorpus.irab;

import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.Closeable;

public class HTMLTokenizer implements Closeable {
    private final LookAheadReader reader;
    private final StringBuilder token = new StringBuilder();

    public HTMLTokenizer(BufferedReader reader) {
        this.reader = new LookAheadReader(reader, 1);
    }

    @SneakyThrows
    public boolean next() {
        if (!reader.canRead()) {
            return false;
        }
        if (reader.peek() == '<') {
            readTag();
        } else {
            readContent();
        }
        return true;
    }

    public String getToken() {
        return token.toString();
    }

    @Override
    @SneakyThrows
    public void close() {
        reader.close();
    }

    private void readTag() {
        token.setLength(0);
        while (reader.peek() != '>') {
            token.append(reader.read());
        }
        token.append(reader.read());
    }

    private void readContent() {
        token.setLength(0);
        while (reader.peek() != '<') {
            token.append(reader.read());
        }
    }
}