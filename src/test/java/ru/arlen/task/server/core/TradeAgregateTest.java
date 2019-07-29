package ru.arlen.task.server.core;

import static org.junit.Assert.assertTrue;
import static ru.arlen.task.server.utils.Utils.agregateString;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.google.protobuf.InvalidProtocolBufferException;

import org.junit.Before;
import org.junit.Test;

public class TradeAgregateTest {
    List<Trade> store = null;
    List<String> result = null;

    @Before
    public void init() {
        store = new LinkedList<Trade>() {
            {
                add(new Trade(1564402186581l, "AAPL", 101.1, 200));
                add(new Trade(1564402196581l, "AAPL", 101.2, 100));
                add(new Trade(1564402206581l, "AAPL", 101.3, 300));
                add(new Trade(1564402216581l, "MSFT", 120.1, 500));
                add(new Trade(1564402226581l, "AAPL", 101.0, 700));
                add(new Trade(1564402236581l, "AAPL", 102.1, 1000));
                add(new Trade(1564402246581l, "MSFT", 120.2, 1000));
                add(new Trade(1564402256581l, "AAPL", 103.2, 100));
                add(new Trade(1564402266581l, "MSFT", 120.0, 700));
                add(new Trade(1564402276581l, "AAPL", 102.1, 100));
                add(new Trade(1564402286581l, "MSFT", 102.1, 200));
            }
        };

        result = new ArrayList<String>() {
            {
                add("{\"ticker\":\"MSFT\",\"timeStamp\":\"2019-07-29T12:11:00Z\",\"open\":120.0,\"hight\":120.0,\"low\":102.1,\"close\":102.1,\"volume\":900}");
                add("{\"ticker\":\"AAPL\",\"timeStamp\":\"2019-07-29T12:11:00Z\",\"open\":102.1,\"hight\":102.1,\"low\":102.1,\"close\":102.1,\"volume\":100}");
                add("{\"ticker\":\"MSFT\",\"timeStamp\":\"2019-07-29T12:10:00Z\",\"open\":120.1,\"hight\":120.2,\"low\":120.1,\"close\":120.2,\"volume\":1500}");
                add("{\"ticker\":\"AAPL\",\"timeStamp\":\"2019-07-29T12:10:00Z\",\"open\":101.3,\"hight\":103.2,\"low\":101.0,\"close\":103.2,\"volume\":2100}");
                add("{\"ticker\":\"AAPL\",\"timeStamp\":\"2019-07-29T12:09:00Z\",\"open\":101.1,\"hight\":101.2,\"low\":101.1,\"close\":101.2,\"volume\":300}");
            }
        };
    }

    @Test
    public void testAgregate() throws InvalidProtocolBufferException {
        for (String task : agregateString(store, 10, 1564402260000l)) {
            assertTrue("Wrong result!", result.contains(task));
        }
    };
}