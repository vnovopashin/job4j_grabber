package ru.job4j.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.InputStream;
import java.sql.*;
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
        Properties properties = readProperties();
        try (Connection connection = getConnection(properties)) {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            JobDataMap data = new JobDataMap();
            data.put("connection", connection);
            JobDetail job = newJob(Rabbit.class)
                    .usingJobData(data)
                    .build();
            SimpleScheduleBuilder times = simpleSchedule()
                    .withIntervalInSeconds(Integer.parseInt(properties.getProperty("rabbit.interval")))
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
     * Метод читает конфигурационный файл rabbit.properties
     *
     * @return возвращает прочитанный файл
     */
    public static Properties readProperties() {
        try (InputStream in =
                     Rabbit.class.getClassLoader().getResourceAsStream("rabbit.properties")) {
            Properties config = new Properties();
            config.load(in);
            return config;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Метод выполняет подключение к базе,
     * необходимые настройки метод получает из файла rabbit.properties
     *
     * @param properties принимает в параметры properties
     * @return возвращает соединение с базой
     * @throws ClassNotFoundException бросает исключение, если класс не найден
     * @throws SQLException           бросает исключение, если возникает ошибка доступа к базе данных
     */
    public static Connection getConnection(Properties properties) throws ClassNotFoundException, SQLException {
        Class.forName(properties.getProperty("driver-class-name"));
        return DriverManager.getConnection(
                properties.getProperty("url"),
                properties.getProperty("username"),
                properties.getProperty("password")
        );
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
            Connection connection = (Connection) context.getJobDetail().getJobDataMap().get("connection");
            try {
                PreparedStatement statement =
                        connection.prepareStatement("insert into rabbit (created_date) values (?)");
                statement.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
                statement.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
