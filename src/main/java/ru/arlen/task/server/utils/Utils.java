package ru.arlen.task.server.utils;

import java.time.Duration;
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

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;

import ru.arlen.task.proto.TaskProtocol.TaskResponse;
import ru.arlen.task.server.core.Trade;

/**
 * Utils
 */
public class Utils {
    public static final long ONE_MIN = Duration.ofMinutes(1).toMillis();
    public static final String DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    public static String getDateStr(long millis) {
        ZonedDateTime utc = Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:00'Z'");
        return formatter.format(utc);
    }

    public static long getDateMillis(String dateStr) {
        LocalDateTime localDateTime = LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern(DATE_PATTERN));
        return localDateTime.atZone(ZoneId.of("UTC")).toInstant().toEpochMilli();
    }

    public static List<TaskResponse> agregateTask(List<Trade> list, int minutes, long millis)
            throws InvalidProtocolBufferException {
        List<TaskResponse> result = new ArrayList<>();
        for (TaskResponse.Builder task : agregateBuilder(list, minutes, millis))
            result.add(task.build());
        return result;
    }

    public static List<String> agregateString(List<Trade> list, int minutes, long millis)
            throws InvalidProtocolBufferException {
        List<String> result = new ArrayList<>();
        for (TaskResponse.Builder task : agregateBuilder(list, minutes, millis)) {
            String jsonString = JsonFormat.printer().omittingInsignificantWhitespace().print(task.build());
            result.add(jsonString);
        }
        return result;
    }

    public static List<TaskResponse.Builder> agregateBuilder(List<Trade> list, int minutes, long millis)
            throws InvalidProtocolBufferException {
        List<TaskResponse.Builder> result = new ArrayList<>();

        Map<String, List<Trade>> map = list.stream().filter(t -> t.getMillis() >= millis)
                .collect(Collectors.groupingBy(t -> t.getTicker()));
        result.addAll(getTask(map));
        recurCalc(list, millis, minutes - 2, result);
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

    private static void recurCalc(List<Trade> list, long millis, int minutes, List<TaskResponse.Builder> result)
            throws InvalidProtocolBufferException {
        if (minutes <= 0)
            return;
        Map<String, List<Trade>> map = list.stream()
                .filter(t -> t.getMillis() >= millis - ONE_MIN && t.getMillis() < millis)
                .collect(Collectors.groupingBy(t -> t.getTicker()));
        result.addAll(getTask(map));
        recurCalc(list, millis - ONE_MIN, minutes - 1, result);
    }
}