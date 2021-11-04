package ru.job4j.quartz;

import org.junit.Assert;
import org.junit.Test;

import java.util.Properties;


/**
 * Класс тестирует методы класса AlertRabbit
 *
 * @author Vasiliy Novopashin
 * @version 1.0
 */
public class AlertRabbitTest {

    @Test
    public void whenGetIntervalThanTen() {
        Properties properties = AlertRabbit.readProperties();
        int result = Integer.parseInt(properties.getProperty("rabbit.interval"));
        Assert.assertEquals(result, 10);
    }
}
