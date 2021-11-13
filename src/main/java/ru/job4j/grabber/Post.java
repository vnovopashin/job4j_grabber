package ru.job4j.grabber;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Класс представляет собой модель данных
 *
 * @author Vasiliy Novopashin
 * @version 1.0
 */
public class Post {
    private int id;
    private String title;
    private String link;
    private String description;
    private LocalDateTime localDateTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Post post = (Post) o;
        return Objects.equals(title, post.title) && Objects.equals(link, post.link) && Objects.equals(description, post.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, link, description);
    }
}
