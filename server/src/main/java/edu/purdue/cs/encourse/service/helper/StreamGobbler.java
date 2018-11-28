package edu.purdue.cs.encourse.service.helper;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.function.Consumer;

/**
 * Helper object for consuming input from a stream
 * Primarily used to run bash scripts where input needs to be consumed and ignored
 *
 * @author William Jordan Reed
 * @author reed226@purdue.edu
 */
public class StreamGobbler implements Runnable {
    private InputStream inputStream;
    private Consumer<String> consumer;

    public StreamGobbler(InputStream inputStream, Consumer<String> consumer) {
        this.inputStream = inputStream;
        this.consumer = consumer;
    }

    @Override
    public void run() {
        new BufferedReader(new InputStreamReader(inputStream)).lines().forEach(consumer);
    }
}
