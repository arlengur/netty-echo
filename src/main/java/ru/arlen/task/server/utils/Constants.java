package ru.arlen.task.server.utils;

import java.time.Duration;

public interface Constants {
    long TEN_MINUTES = Duration.ofMinutes(10).toMillis();
    long ONE_MINUTE = Duration.ofMinutes(1).toMillis();

    String SERVER_HOST = "localhost";
    int SERVER_PORT = 8000;

    String UPSTREAM_HOST = "localhost";
    int UPSTREAM_PORT = 5555;
}