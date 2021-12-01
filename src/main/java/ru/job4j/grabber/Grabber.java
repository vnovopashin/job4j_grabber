package ru.job4j.grabber;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import ru.job4j.grabber.utils.DateTimeParser;
import ru.job4j.grabber.utils.SqlRuDateTimeParser;
import ru.job4j.html.SqlRuParse;

import java.io.*;
import java.util.List;
import java.util.Properties;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * Класс считывает вакансии с сайта SQL.ru с раздела job относящиеся к Java и записывает их в базу,
 * запись производится с интервалом указанным в конфигурационном файле app.properties.
 *
 * @author Vasiliy Novopashin
 * @version 1.0
 */
public class Grabber implements Grab {
    private final Properties cfg = new Properties();

    /**
     * Метод создает экземпляр класса PsqlStore для хранения и манипуляции данными
     *
     * @return возвращает экземпляр класса PsqlStore
     */
    public Store store() {
        return new PsqlStore(cfg);
    }

    /**
     * Метод создает планировщик,
     * который является основным для выполнения задач с периодичностью.
     *
     * @return возвращает планировщик.
     * @throws SchedulerException бросает исключение, которое является базовым для Quartz Scheduler.
     */
    public Scheduler scheduler() throws SchedulerException {
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        scheduler.start();
        return scheduler;
    }

    /**
     * Метод читает конфигурационный файл с настройками подключения к базе данных
     * и установленным интервалом времени, когда необходимо производить извлечение данных с сайта.
     *
     * @throws IOException бросает исключение, если возникает ошибка ввода/вывода.
     */
    public void cfg() throws IOException {
        try (InputStream in = Grabber.class.getClassLoader().getResourceAsStream("app.properties")) {
            cfg.load(in);
        }
    }

    /**
     * Метод описывает требуемые действия для получения, хранения данных,
     * а также периодичность выполнения (интервал, повторение, начало запуска).
     *
     * @param parse     через данный параметр мы указываем сайт для получения данных.
     * @param store     через данный параметр мы указываем где мы будем хранить данные.
     * @param scheduler через данный параметр добавляется задачи, которые необходимо выполнять периодически.
     * @throws SchedulerException бросает исключение, которое является базовым для Quartz Scheduler.
     */
    @Override
    public void init(Parse parse, Store store, Scheduler scheduler) throws SchedulerException {
        JobDataMap data = new JobDataMap();
        data.put("store", store);
        data.put("parse", parse);
        JobDetail job = newJob(GrabJob.class)
                .usingJobData(data)
                .build();
        SimpleScheduleBuilder times = simpleSchedule()
                .withIntervalInSeconds(Integer.parseInt(cfg.getProperty("time")))
                .repeatForever();
        Trigger trigger = newTrigger()
                .startNow()
                .withSchedule(times)
                .build();
        scheduler.scheduleJob(job, trigger);
    }

    /**
     * Класс реализует "работу", которую необходимо выполнить.
     */
    public static class GrabJob implements Job {
        /**
         * Метод вызывается планировщиком связанного с заданием.
         *
         * @param context принимает контекст.
         * @throws JobExecutionException исключение, которое может быть вызвано заданием.
         */
        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            JobDataMap map = context.getJobDetail().getJobDataMap();
            Store store = (Store) map.get("store");
            Parse parse = (Parse) map.get("parse");
            List<Post> postList = parse.list("https://www.sql.ru/forum/job-offers/");
            for (Post post : postList) {
                store.save(post);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Grabber grab = new Grabber();
        grab.cfg();
        Scheduler scheduler = grab.scheduler();
        Store store = grab.store();
        DateTimeParser dtp = new SqlRuDateTimeParser();
        grab.init(new SqlRuParse(dtp), store, scheduler);
    }
}
