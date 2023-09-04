package app.qurancorpus.irab;

import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.Closeable;

import static java.text.MessageFormat.format;

public class LookAheadReader implements Closeable {
    private final BufferedReader reader;
    private final char[] ringBuffer;
    private int head = 0;
    private int tail = 0;
    private int currentSize = 0;

    public LookAheadReader(BufferedReader reader, int lookahead) {
        this.reader = reader;
        this.ringBuffer = new char[lookahead];
        fillBuffer();
    }

    public boolean canRead() {
        return currentSize > 0;
    }

    public char read() {
        if (currentSize == 0) {
            throw new UnsupportedOperationException("End of file.");
        }

        var ch = ringBuffer[head];
        head = (head + 1) % ringBuffer.length;
        currentSize--;

        fillBuffer();
        return ch;
    }

    public char peek() {
        return peek(1);
    }

    public char peek(int n) {
        if (n < 1 || n > currentSize) {
            System.err.println("ERROR! CURRENT SIZE = " + currentSize);
            throw new UnsupportedOperationException(format("Invalid peek: n = {0}", n));
        }

        return ringBuffer[(head + n - 1) % ringBuffer.length];
    }

    @SneakyThrows
    public void close() {
        reader.close();
    }

    @SneakyThrows
    private void fillBuffer() {
        var fillCount = ringBuffer.length - currentSize;
        for (var i = 0; i < fillCount; i++) {
            var ch = reader.read();
            if (ch == -1) {
                break;
            }
            ringBuffer[tail] = (char) ch;
            tail = (tail + 1) % ringBuffer.length;
            currentSize++;
        }
    }
}