package ru.job4j.grabber;

import java.util.List;

/**
 * Интерфейс для хранения данных
 *
 * @author Vasiliy Novopashin
 * @version 1.0
 */
public interface Store {
    void save(Post post);

    List<Post> getAll();

    Post findById(int id);
}
