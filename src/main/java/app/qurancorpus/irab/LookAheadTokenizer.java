package app.qurancorpus.irab;

import java.io.Closeable;

import static java.text.MessageFormat.format;

public class LookAheadTokenizer implements Closeable {
    private final HTMLTokenizer tokenizer;
    private String[] ringBuffer;
    private int head = 0;
    private int tail = 0;
    private int currentSize = 0;

    public LookAheadTokenizer(HTMLTokenizer tokenizer, int lookahead) {
        this.tokenizer = tokenizer;
        this.ringBuffer = new String[lookahead];
        fillBuffer();
    }

    public boolean canRead() {
        return currentSize > 0;
    }

    public boolean canRead(int n) {
        return currentSize >= n;
    }

    public String read(String token) {
        var output = read();
        if (!output.equals(token)) {
            throw new UnsupportedOperationException(format("Expected {0} not {1}", token, output));
        }
        return output;
    }

    public String read() {
        if (currentSize == 0) {
            throw new UnsupportedOperationException("End of file.");
        }

        var token = ringBuffer[head];
        head = (head + 1) % ringBuffer.length;
        currentSize--;

        fillBuffer();
        return token;
    }

    public String peek() {
        return peek(1);
    }

    public String peek(int n) {
        if (n < 1 || n > currentSize) {
            throw new UnsupportedOperationException(format("Invalid peek: n = {0}", n));
        }

        return ringBuffer[(head + n - 1) % ringBuffer.length];
    }

    public void patch(int n, String token) {
        if (n < 1 || n > currentSize) {
            throw new UnsupportedOperationException(format("Invalid patch: n = {0}", n));
        }

        ringBuffer[(head + n - 1) % ringBuffer.length] = token;
    }

    public void insertPatch(String... tokens) {
        growBuffer(tokens.length);

        for (var i = currentSize - 1; i >= 0; i--) {
            ringBuffer[(head + i + tokens.length) % ringBuffer.length] = ringBuffer[(head + i) % ringBuffer.length];
        }

        for (var i = 0; i < tokens.length; i++) {
            ringBuffer[(head + i) % ringBuffer.length] = tokens[i];
            currentSize++;
        }

        tail = (head + currentSize) % ringBuffer.length;
    }

    public void close() {
        tokenizer.close();
    }

    private void fillBuffer() {
        var fillCount = ringBuffer.length - currentSize;
        for (var i = 0; i < fillCount; i++) {
            if (!tokenizer.next()) {
                break;
            }
            ringBuffer[tail] = tokenizer.getToken();
            tail = (tail + 1) % ringBuffer.length;
            currentSize++;
        }
    }

    private void growBuffer(int delta) {
        var newBuffer = new String[ringBuffer.length + delta];

        for (var i = 0; i < ringBuffer.length; i++) {
            newBuffer[i] = ringBuffer[(head + i) % ringBuffer.length];
        }

        ringBuffer = newBuffer;
        head = 0;
        tail = currentSize;
    }
}