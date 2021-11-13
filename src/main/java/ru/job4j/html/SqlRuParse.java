package ru.job4j.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Класс парсит сайт www.sql.ru с помощью библиотеки jsoup
 *
 * @author Vasiliy Novopashin
 * @version 1.0
 */
public class SqlRuParse {
    public static void main(String[] args) throws Exception {
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
