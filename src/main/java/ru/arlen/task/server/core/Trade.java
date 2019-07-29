package ru.arlen.task.server.core;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Trade
 */
public class Trade implements Cloneable {
    private long millis;
    private String ticker;
    private double price;
    private int size;

    public Trade(long millis, String ticker, double price, int size) {
        this.millis = millis;
        this.ticker = ticker;
        this.price = price;
        this.size = size;
    }

    public long getMillis() {
        return this.millis;
    }

    public String getTicker() {
        return this.ticker;
    }

    public double getPrice() {
        return this.price;
    }

    public int getSize() {
        return this.size;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Trade)) {
            return false;
        }
        Trade trade = (Trade) o;
        return millis == trade.millis;
    }

    @Override
    public int hashCode() {
        return (int) (millis ^ (millis >>> 32));
    }

    @Override
    public String toString() {
        ZonedDateTime utc = Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return "{" + " millis='" + formatter.format(utc) + "'" + ", ticker='" + ticker + "'" + ", price='" + price + "'"
                + ", size='" + size + "'" + "}";
    }
}