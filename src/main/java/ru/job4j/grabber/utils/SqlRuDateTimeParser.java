package ru.job4j.grabber.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Класс реализует синтаксический анализ даты с сайта sql.ru
 *
 * @author Vasiliy Novopashin
 * @version 1.0
 */
public class SqlRuDateTimeParser implements DateTimeParser {

    private final static DateTimeFormatter DTF = DateTimeFormatter.ofPattern("dMMyyHHmm");
    private static final Map<String, String> MONTHS = Map.ofEntries(
            Map.entry("янв", "01"),
            Map.entry("фев", "02"),
            Map.entry("мар", "03"),
            Map.entry("апр", "04"),
            Map.entry("май", "05"),
            Map.entry("июн", "06"),
            Map.entry("июл", "07"),
            Map.entry("авг", "08"),
            Map.entry("сен", "09"),
            Map.entry("окт", "10"),
            Map.entry("ноя", "11"),
            Map.entry("дек", "12"));

    /**
     * Метод преобразует стоку, следующего вида 2 дек 19 в дату
     *
     * @param parse дата представленная стройкой
     * @return возвращает дату в виде объекта LocalDateTime
     */
    @Override
    public LocalDateTime parse(String parse) {
        String[] arr = parse.split("[\\p{Punct}\\s]+");
        if (arr[0].equals("сегодня")) {
            return getTodayOrYesterday(arr, 0);
        } else if (arr[0].equals("вчера")) {
            return getTodayOrYesterday(arr, 1);
        }
        for (Map.Entry<String, String> entry : MONTHS.entrySet()) {
            if (entry.getKey().equals(arr[1])) {
                arr[1] = entry.getValue();
                break;
            }
        }
        return LocalDateTime.parse(arr[0].concat(arr[1]).concat(arr[2]).concat(arr[3]).concat(arr[4]), DTF);
    }

    /**
     * Метод преобразует дату вида сегодня, 20:30 или вчера, 12:15
     *
     * @param arr массив примерно следующего содержания [сегодня, 20, 30]
     * @param days если нужно получить сегодняшнюю дату, days устанавливаем равным 0,
     *             если вчерашний тогда 1
     * @return возвращает дату в виде объекта LocalDateTime
     */
    private LocalDateTime getTodayOrYesterday(String[] arr, int days) {
        LocalTime time = LocalTime.of(Integer.parseInt(arr[1]), Integer.parseInt(arr[2]));
        return LocalDateTime.of(LocalDate.now().minusDays(days), time);
    }
}
