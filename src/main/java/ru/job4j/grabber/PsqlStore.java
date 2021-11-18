package ru.job4j.grabber;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Класс реализует методы работы с базой данных,
 * позволяет подключиться к базе данных, закрыть подключение,
 * а также реализованы методы позволяющие выполнять
 * сохранение в базу данных, получение по идентификатору, получение всех записей из базы данных
 *
 * @author Vasiliy Novopashin
 * @version 1.0
 */
public class PsqlStore implements Store, AutoCloseable {
    private Connection cnn;

    public PsqlStore(Properties cfg) {
        try {
            Class.forName(cfg.getProperty("jdbc.driver"));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        try {
            cnn = DriverManager.getConnection(
                    cfg.getProperty("url"),
                    cfg.getProperty("username"),
                    cfg.getProperty("password")
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Метод добавляет модель данных типа Post в базу данных
     *
     * @param post модель данных для добавления в базу данных
     */
    @Override
    public void save(Post post) {
        try (PreparedStatement statement =
                     cnn.prepareStatement("insert into post(name, text, link, created) values (?,?,?,?)",
                             Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, post.getTitle());
            statement.setString(2, post.getDescription());
            statement.setString(3, post.getLink());
            statement.setTimestamp(4, Timestamp.valueOf(post.getLocalDateTime()));
            statement.execute();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    post.setId(generatedKeys.getInt(1));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Метод возвращает найденные данные из таблицы
     *
     * @return возвращает список моделей данных типа Post
     */
    @Override
    public List<Post> getAll() {
        List<Post> posts = new ArrayList<>();
        Post post = new Post();
        try (PreparedStatement statement = cnn.prepareStatement("select * from post")) {
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    post.setId(resultSet.getInt("id"));
                    post.setTitle(resultSet.getString("name"));
                    post.setDescription(resultSet.getString("text"));
                    post.setLink(resultSet.getString("link"));
                    post.setLocalDateTime(resultSet.getTimestamp("created").toLocalDateTime());
                    posts.add(post);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return posts;
    }

    /**
     * Метод находит модель данных типа Post по идентификатору
     *
     * @param id идентификатор по которому осуществляется поиск
     * @return возвращает найденную модель данных, с заданным id
     */
    @Override
    public Post findById(int id) {
        Post post = new Post();
        try (PreparedStatement statement =
                     cnn.prepareStatement("select * from post where id = ?")) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    post.setId(resultSet.getInt("id"));
                    post.setTitle(resultSet.getString("name"));
                    post.setDescription(resultSet.getString("text"));
                    post.setLink(resultSet.getString("link"));
                    post.setLocalDateTime(resultSet.getTimestamp("created").toLocalDateTime());
                    return post;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Метод закрывает соединение с базой данных
     *
     * @throws Exception в случае ошибки выбрасывает исключение
     */
    @Override
    public void close() throws Exception {
        if (cnn != null) {
            cnn.close();
        }
    }

    public static void main(String[] args) {
        Properties properties = new Properties();
        Post post = new Post();
        post.setTitle("Лиды BE/FE/senior cистемные аналитики/QA и DevOps, Москва, до 200т. [new]");
        post.setLink("https://www.sql.ru/forum/1325330/lidy-be-fe-senior-cistemnye-analitiki-qa-i-devops-moskva-do-200t");
        post.setDescription("Ищем команды разработчиков на аутстафф в офис в Москве,"
                + " а также тимлидов и ведущих системных аналитиков, QA и DevOps Зп до 200т."
                + " Наши приоритеты стаффинга: 1. Команды, микрокоманды:"
                + " - команда - 7-9 человек (типовой состав - 2 аналитика, 3 Java, 2 React, 2 QA);"
                + " - микрокоманда - 3-4 человека (специальность - BE/FE/QA/QA автотестирование)."
                + " Ключевые специалисты команды должны сделать минимум 1 проект вместе. "
                + "2. Лиды BE/FE/senior cистемные аналитики/QA и DevOps."
                + "Наш стек: Backend: java/kotlin, spring Frontend: react Mobile: kotlin/Android, Swift/iOS"
                + " Предложения и резюме присылайте на contact.softlantic@gmail.com',");
        post.setLocalDateTime(LocalDateTime.parse("2020-05-13T21:58"));
        try (InputStream in = PsqlStore.class.getClassLoader().getResourceAsStream("app.properties")) {
            properties.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        PsqlStore psqlStore = new PsqlStore(properties);
        psqlStore.save(post);
        System.out.println(psqlStore.findById(1));
        for (Post p : psqlStore.getAll()) {
            System.out.println(p);
        }
    }
}
