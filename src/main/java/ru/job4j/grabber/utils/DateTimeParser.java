package ru.job4j.grabber.utils;

import java.time.LocalDateTime;

/**
 * @author Vasiliy Novopashin
 * @version 1.0
 */
public interface DateTimeParser {
    LocalDateTime parse(String parse);
}
