package ru.job4j.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Properties;

import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;

/**
 * Класс демонстрирует работу с внешней библиотекой Quartz,
 * которая позволяет выполнять действия с периодичностью.
 * Например, отправка рассылки, создание копии базы данных.
 *
 * @author Vasiliy Novopashin
 * @version 1.0
 */
public class AlertRabbit {

    public static void main(String[] args) {
        try {
            Connection connection = getConnection();
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            JobDataMap data = new JobDataMap();
            data.put("connection", connection);
            JobDetail job = newJob(Rabbit.class)
                    .usingJobData(data)
                    .build();
            SimpleScheduleBuilder times = simpleSchedule()
                    .withIntervalInSeconds(getInterval())
                    .repeatForever();
            Trigger trigger = newTrigger()
                    .startNow()
                    .withSchedule(times)
                    .build();
            scheduler.scheduleJob(job, trigger);
            Thread.sleep(10000);
            scheduler.shutdown();
        } catch (Exception se) {
            se.printStackTrace();
        }
    }

    /**
     * Метод выполняет подключение к базе,
     * необходимые настройки метод получает из файла rabbit.properties
     *
     * @return возвращает соединение с базой данных
     */
    public static Connection getConnection() {
        try (InputStream in = Rabbit.class.getClassLoader().getResourceAsStream("rabbit.properties")) {
            Properties config = new Properties();
            config.load(in);
            Class.forName(config.getProperty("driver-class-name"));
            return DriverManager.getConnection(
                    config.getProperty("url"),
                    config.getProperty("username"),
                    config.getProperty("password")
            );
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Метод читает файл конфигурации в котором содержится время
     * запуска задачи
     *
     * @return возвращает время с которой будет происходить запуск задачи
     */
    public static int getInterval() {
        String interval;
        try (InputStream in =
                     AlertRabbit.class.getClassLoader().getResourceAsStream("rabbit.properties")) {
            Properties config = new Properties();
            config.load(in);
            interval = config.getProperty("rabbit.interval");
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        return Integer.parseInt(interval);
    }

    /**
     * Метод записывает данные в таблицу
     */
    public static void insert() {
        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("insert into rabbit (created_date) values (?)")) {
                statement.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
                statement.execute();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Класс представляет собой задачу, которая будет выполняться в нужной периодичности
     */
    public static class Rabbit implements Job {

        public Rabbit() {
            System.out.println(hashCode());
        }

        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            System.out.println("Rabbit runs here ...");
            insert();
        }
    }
}
