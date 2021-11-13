package ru.job4j.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс парсит сайт www.sql.ru с помощью библиотеки jsoup
 *
 * @author Vasiliy Novopashin
 * @version 1.0
 */
public class SqlRuParse {
    public static void main(String[] args) throws Exception {
        String url = "https://www.sql.ru/forum/job-offers";
        Document doc = Jsoup.connect(url).get();
        Elements table = doc.select("table[class=sort_options]");
        Elements row = table.select("a[href]");
        List<String> urls = new ArrayList<>();
        urls.add(url);

        for (Element element : row) {
            if (element.attr("href").matches("https://www.sql.ru/forum/job-offers/[1-5]{1}")) {
                urls.add(element.attr("href"));
            }
        }

        for (String s : urls) {
            Document document = Jsoup.connect(s).get();
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
