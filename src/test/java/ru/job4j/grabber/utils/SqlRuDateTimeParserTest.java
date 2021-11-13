package ru.job4j.grabber.utils;

import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.Assert.*;

/**
 * Класс тестирует методы класса SqlRuDateTimeParser
 *
 * @author Vasiliy Novopashin
 * @version 1.0
 */
public class SqlRuDateTimeParserTest {

    SqlRuDateTimeParser parser = new SqlRuDateTimeParser();

    @Test
    public void whenJunThen06() {
        LocalDateTime res = parser.parse("14 июн 19, 22:19");
        assertEquals(res.toString(), "2019-06-14T22:19");
    }

    @Test
    public void whenToday() {
        LocalDateTime res = parser.parse("сегодня, 22:19");
        assertEquals(res.toString(), LocalDate.now() + "T22:19");
    }

    @Test
    public void whenYesterday() {
        LocalDateTime res = parser.parse("вчера, 22:19");
        assertEquals(res.toString(), LocalDate.now().minusDays(1) + "T22:19");
    }
}
