package ru.job4j.quartz;

import org.junit.Assert;
import org.junit.Test;


/**
 * Класс тестирует методы класса AlertRabbit
 *
 * @author Vasiliy Novopashin
 * @version 1.0
 */
public class AlertRabbitTest {

    @Test
    public void whenReadAlertRabbitPropertiesThanTen() {
        int result = AlertRabbit.readAlertRabbitProperties();
        Assert.assertEquals(result, 10);
    }
}
