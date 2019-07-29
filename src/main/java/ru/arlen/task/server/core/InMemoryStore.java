package ru.arlen.task.server.core;

import java.time.Duration;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

/**
 * InMemoryStore
 */
public class InMemoryStore {
    private static long TEN_MINUTES = Duration.ofMinutes(10).toMillis();
    private static long ONE_MINUTE = Duration.ofMinutes(1).toMillis();
    private ConcurrentLinkedQueue<Trade> store = new ConcurrentLinkedQueue<>();

    public void push(Trade trade) {
        if (!store.isEmpty() && (trade.getMillis() - store.peek().getMillis() > TEN_MINUTES)) {
            store.poll();
        }
        store.offer(trade);
    }

    public List<Trade> getTenMTrades() {
        return new LinkedList<Trade>(store);
    }

    public List<Trade> getOneMTrades() {
        long oneMinuteBefore = System.currentTimeMillis() - ONE_MINUTE;
        return store.stream().filter(trade -> trade.getMillis() > oneMinuteBefore)
                .collect(Collectors.toCollection(LinkedList::new));
    }
}