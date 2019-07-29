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

    public Trade() {
    }

    public Trade(long millis, String ticker, double price, int size) {
        this.millis = millis;
        this.ticker = ticker;
        this.price = price;
        this.size = size;
    }

    public long getMillis() {
        return this.millis;
    }

    public void setMillis(long millis) {
        this.millis = millis;
    }

    public String getTicker() {
        return this.ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public double getPrice() {
        return this.price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getSize() {
        return this.size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Trade millis(long millis) {
        this.millis = millis;
        return this;
    }

    public Trade ticker(String ticker) {
        this.ticker = ticker;
        return this;
    }

    public Trade price(double price) {
        this.price = price;
        return this;
    }

    public Trade size(int size) {
        this.size = size;
        return this;
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