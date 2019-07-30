package ru.arlen.task.server.core;

import java.util.List;

/**
 * Persistent
 */
public interface Persistent {
    void push(Trade trade);

    List<Trade> getTenMinT();

    List<Trade> getOneMinT();
}