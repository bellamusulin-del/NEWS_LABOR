package com.rssreader;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import java.io.FileReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.json.JSONObject;

public class RssReader {

    public List<News> getNewsFromRss() {

        List<News> newsList =
                new ArrayList<>();

        try {

            // JSON fajl beolvasasa
            FileReader fileReader =
                    new FileReader("rssSources.json");

            StringBuilder jsonText =
                    new StringBuilder();

            int character;

            while ((character = fileReader.read()) != -1) {

                jsonText.append((char) character);
            }

            fileReader.close();

            // JSON objektum letrehozasa
            JSONObject json =
                    new JSONObject(jsonText.toString());

            // RSS URL kiolvasasa
            String rssUrl =
                    json.getString("url");

            // RSS forras neve
            String publisher =
                    json.getString("source");

            // RSS feed megnyitasa
            URL url =
                    new URL(rssUrl);

            SyndFeedInput input =
                    new SyndFeedInput();

            SyndFeed feed =
                    input.build(new XmlReader(url));

            // RSS bejegyzesek feldolgozasa
            for (SyndEntry entry : feed.getEntries()) {

                String title = "";

                if (entry.getTitle() != null) {
                    title = entry.getTitle();
                }

                String description = "";

                if (entry.getDescription() != null) {
                    description =
                            entry.getDescription().getValue();
                }

                String link = "";

                if (entry.getLink() != null) {
                    link = entry.getLink();
                }

                String sourceOrAuthor = "";

                if (entry.getAuthor() != null) {
                    sourceOrAuthor =
                            entry.getAuthor();
                }

                String publicationDate = "";

                if (entry.getPublishedDate() != null) {
                    publicationDate =
                            entry.getPublishedDate().toString();
                }

                String createdAt =
                        new Date().toString();

                String updatedAt =
                        new Date().toString();

                String coverImage = "";

                News news =
                        new News(
                                title,
                                description,
                                sourceOrAuthor,
                                publicationDate,
                                publisher,
                                createdAt,
                                updatedAt,
                                coverImage,
                                link
                        );

                newsList.add(news);
            }

        } catch (Exception exception) {

            System.out.println(
                    "Hiba az RSS beolvasasa kozben: "
                    + exception.getMessage());
        }

        return newsList;
    }
}