package ru.job4j.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.Post;
import ru.job4j.grabber.utils.SqlRuDateTimeParser;

import java.io.IOException;

/**
 * Класс парсит сайт www.sql.ru с помощью библиотеки jsoup
 *
 * @author Vasiliy Novopashin
 * @version 1.0
 */
public class SqlRuParse {
    /**
     * Метод извлекает данные с сайта www.sql.ru по переданной ссылке,
     * и инициализирует объект типа Post полученными данными
     *
     * @param link ссылка на вакансию сайта www.sql.ru
     * @return возвращает объект типа Post
     */
    public Post detail(String link) {
        Post post = new Post();
        try {
            Document doc = Jsoup.connect(link).get();
            Element description = doc.select("td[class=msgBody]").first().nextElementSibling();
            Element date = doc.select("td[class=msgFooter]").first();
            Element title = doc.select("td[class=messageHeader]").first();
            String preparedDate = date.text().split("\\[")[0].trim();
            post.setTitle(title.text());
            post.setLink(link);
            post.setDescription(description.text());
            post.setLocalDateTime(new SqlRuDateTimeParser().parse(preparedDate));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return post;
    }

    public static void main(String[] args) throws Exception {
        SqlRuParse sqlRuParse = new SqlRuParse();
        Post post = sqlRuParse.detail("https://www.sql.ru/forum/1325330/lidy-be-fe-senior-cistemnye-analitiki-qa-i-devops-moskva-do-200t");
        System.out.println(post);

        String url = "https://www.sql.ru/forum/job-offers/";

        for (int i = 1; i <= 5; i++) {
            Document document = Jsoup.connect(url + i).get();
            Elements rows = document.select(".postslisttopic");
            for (Element element : rows) {
                Element href = element.child(0);
                Element data = element.parent().child(5);
                System.out.println(href.attr("href"));
                System.out.println(href.text());
                System.out.println(data.text());
            }
        }
    }
}
