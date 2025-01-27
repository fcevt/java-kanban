package com.yandex.tracker.servise;

public class NonExistingTaskException extends RuntimeException {
    public NonExistingTaskException(String massage) {
        super(massage);
    }
}
