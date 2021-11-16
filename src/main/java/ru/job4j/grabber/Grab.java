package ru.job4j.grabber;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;

/**
 * Интерфейс для запуска парсера
 *
 * @author Vasiliy Novopashin
 * @version 1.0
 */
public interface Grab {
    void init(Parse parse, Store store, Scheduler scheduler) throws SchedulerException;
}
