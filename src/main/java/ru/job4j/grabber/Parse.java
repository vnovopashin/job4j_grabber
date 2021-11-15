package ru.job4j.grabber;

import java.util.List;

/**
 * Интерфейс описывающий парсинг сайта
 *
 * @author Vasiliy Novopashin
 * @version 1.0
 */
public interface Parse {

    List<Post> list(String link);

    Post detail(String link);
}
