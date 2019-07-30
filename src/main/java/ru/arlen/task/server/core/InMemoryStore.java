package ru.arlen.task.server.core;

import static ru.arlen.task.server.utils.Constants.ONE_MINUTE;
import static ru.arlen.task.server.utils.Constants.TEN_MINUTES;
import static ru.arlen.task.server.utils.Utils.getNoSecMillis;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

/**
 * InMemoryStore
 */
public class InMemoryStore implements Persistent {
    private ConcurrentLinkedQueue<Trade> store = new ConcurrentLinkedQueue<>();

    @Override
    public void push(Trade trade) {
        if (!store.isEmpty() && (trade.getMillis() - store.peek().getMillis() > TEN_MINUTES)) {
            store.poll();
        }
        store.offer(trade);
    }

    /**
     * Gets trades for last 10 minutes
     * 
     * @return list of trades
     */
    @Override
    public List<Trade> getTenMinT() {
        return getCopy();
    }

    /**
     * Gets trades for last 1 minute
     * 
     * @return list of trades
     */
    @Override
    public List<Trade> getOneMinT() {
        long oneMinuteBefore = getNoSecMillis(System.currentTimeMillis() - ONE_MINUTE);
        return getCopy().stream().filter(trade -> trade.getMillis() > oneMinuteBefore).collect(Collectors.toList());
    }

    private List<Trade> getCopy() {
        return Arrays.asList(store.toArray(new Trade[0]));
    }
}