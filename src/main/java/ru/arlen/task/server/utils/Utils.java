package ru.arlen.task.server.utils;

import static ru.arlen.task.server.utils.Constants.ONE_MINUTE;

import java.lang.invoke.MethodHandles;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.arlen.task.proto.TaskProtocol.TaskResponse;
import ru.arlen.task.server.core.Trade;

/**
 * Utils
 */
public class Utils {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static String getDateStr(long millis) {
        ZonedDateTime utc = Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:00'Z'");
        return formatter.format(utc);
    }

    public static long getDateMillis(String dateStr) {
        LocalDateTime localDateTime = LocalDateTime.parse(dateStr,
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));
        return localDateTime.atZone(ZoneId.of("UTC")).toInstant().toEpochMilli();
    }

    public static long getNoSecMillis(long millis) {
        return getDateMillis(getDateStr(millis));
    }

    public static List<TaskResponse> agregateOneMinTasks(List<Trade> list) {
        return agregateTask(list, 1, System.currentTimeMillis());
    }

    public static List<TaskResponse> agregateTenMinTasks(List<Trade> list) {
        return agregateTask(list, 2, System.currentTimeMillis());
    }

    private static List<TaskResponse> agregateTask(List<Trade> list, int minutes, long millis) {
        List<TaskResponse> result = new ArrayList<>();
        for (TaskResponse.Builder task : agregateBuilder(list, minutes, millis))
            result.add(task.build());
        return Lists.reverse(result);
    }

    public static List<String> agregateString(List<Trade> list, int minutes, long millis) {
        List<String> result = new ArrayList<>();
        try {
            for (TaskResponse.Builder task : agregateBuilder(list, minutes, millis)) {
                String jsonString = JsonFormat.printer().omittingInsignificantWhitespace().print(task.build());
                result.add(jsonString);
            }
        } catch (InvalidProtocolBufferException e) {
            logger.error("InvalidProtocolBufferException: {}", e.getMessage());
        }
        return result;
    }

    public static List<TaskResponse.Builder> agregateBuilder(List<Trade> list, int minutes, long millis) {
        List<TaskResponse.Builder> result = new ArrayList<>();
        recurCalc(list, getNoSecMillis(millis), minutes, result);
        return result;
    }

    private static List<TaskResponse.Builder> getTask(Map<String, List<Trade>> map) {
        List<TaskResponse.Builder> res = new ArrayList<>();
        for (List<Trade> l : map.values()) {
            double low = l.stream().min(Comparator.comparing(Trade::getPrice)).get().getPrice();
            double hight = l.stream().max(Comparator.comparing(Trade::getPrice)).get().getPrice();
            double open = l.stream().min(Comparator.comparing(Trade::getMillis)).get().getPrice();
            double close = l.stream().max(Comparator.comparing(Trade::getMillis)).get().getPrice();
            int volume = l.stream().mapToInt(t -> t.getSize()).sum();
            String ticker = l.get(0).getTicker();
            long timeStamp = l.get(0).getMillis();
            TaskResponse.Builder task = TaskResponse.newBuilder().setTicker(ticker).setTimeStamp(getDateStr(timeStamp))
                    .setOpen(open).setHight(hight).setLow(low).setClose(close).setVolume(volume);
            res.add(task);
        }
        return res;
    }

    private static void recurCalc(List<Trade> list, long millis, int minutes, List<TaskResponse.Builder> result) {
        if (minutes <= 0)
            return;
        Map<String, List<Trade>> map = list.stream()
                .filter(t -> t.getMillis() >= millis - ONE_MINUTE && t.getMillis() < millis)
                .collect(Collectors.groupingBy(t -> t.getTicker()));
        result.addAll(getTask(map));
        recurCalc(list, millis - ONE_MINUTE, minutes - 1, result);
    }
}