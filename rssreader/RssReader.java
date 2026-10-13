package com.rssreader;

import com.modellek.News;
import com.network.HttpHelper;
import com.rometools.rome.feed.synd.SyndEnclosure;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import java.io.FileReader;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import org.jdom2.Element;
import org.json.JSONArray;
import org.json.JSONObject;

public class RssReader {

    private static final String RSS_CONFIG_FILE = "res/rss.json";
    private static final String DEFAULT_COVER_IMAGE = "res/images/default.jpg";
    private static final int TIMEOUT_MILLISECONDS = 5000;

    // Read every source from rss.json and collect the news of all feeds
    public List<News> getNewsFromRss() {

        List<News> allNews =
                new ArrayList<>();

        List<RSSSource> sourceList =
                readSources();

        for (RSSSource source : sourceList) {

            List<News> newsOfSource =
                    readOneSource(source);

            allNews.addAll(newsOfSource);
        }

        return allNews;
    }

    // Read the list of RSS sources from the JSON file
    private List<RSSSource> readSources() {

        List<RSSSource> sourceList =
                new ArrayList<>();

        try {

            FileReader fileReader =
                    new FileReader(RSS_CONFIG_FILE);

            StringBuilder jsonText =
                    new StringBuilder();

            int character;

            while ((character = fileReader.read()) != -1) {

                jsonText.append((char) character);
            }

            fileReader.close();

            JSONObject json =
                    new JSONObject(jsonText.toString());

            if (json.has("rssSources")) {

                // Format 1: a list of sources in the "rssSources" array
                JSONArray sourcesArray =
                        json.getJSONArray("rssSources");

                for (int index = 0; index < sourcesArray.length(); index++) {

                    JSONObject sourceJson =
                            sourcesArray.getJSONObject(index);

                    String name =
                            sourceJson.getString("name");

                    String url =
                            sourceJson.getString("url");

                    String language =
                            sourceJson.getString("language").toUpperCase();

                    RSSSource source =
                            new RSSSource(name, url, language);

                    sourceList.add(source);
                }

            } else {

                // Format 2: one single source object (source, url, language)
                String name =
                        json.getString("source");

                String url =
                        json.getString("url");

                String language =
                        json.getString("language").toUpperCase();

                RSSSource source =
                        new RSSSource(name, url, language);

                sourceList.add(source);
            }

        } catch (Exception exception) {

            System.out.println(
                    "Hiba az rss.json beolvasasa kozben: "
                    + exception.getMessage());
        }

        return sourceList;
    }

    // Download and parse one feed. A broken feed must not stop the others.
    private List<News> readOneSource(RSSSource source) {

        List<News> newsList =
                new ArrayList<>();

        // Check the HTTP status code first (with timeout)
        int statusCode =
                HttpHelper.getStatusCode(source.getUrl());

        if (statusCode != 200) {

            System.out.println(
                    source.getName() + ": "
                    + HttpHelper.getStatusMessage(statusCode));

            return newsList;
        }

        try {

            URL url =
                    new URL(source.getUrl());

            URLConnection connection =
                    url.openConnection();

            connection.setConnectTimeout(TIMEOUT_MILLISECONDS);
            connection.setReadTimeout(TIMEOUT_MILLISECONDS);
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");

            SyndFeedInput input =
                    new SyndFeedInput();

            SyndFeed feed =
                    input.build(new XmlReader(connection));

            // The publisher is the title of the feed, if it has one
            String publisher =
                    source.getName();

            if (feed.getTitle() != null) {
                publisher = feed.getTitle();
            }

            for (SyndEntry entry : feed.getEntries()) {

                // The link is part of the primary key, so it is required
                if (entry.getLink() == null || entry.getLink().isEmpty()) {
                    continue;
                }

                String link =
                        entry.getLink();

                String title = "";

                if (entry.getTitle() != null) {
                    title = entry.getTitle();
                }

                // Keep the raw description (with HTML), because the image
                // can be inside it as an <img> tag
                String description = "";
                String rawDescription = "";

                if (entry.getDescription() != null
                        && entry.getDescription().getValue() != null) {

                    rawDescription =
                            entry.getDescription().getValue();

                    // Remove the HTML tags from the description
                    description =
                            rawDescription.replaceAll("<[^>]*>", "").trim();
                }

                String author = "";

                if (entry.getAuthor() != null) {
                    author = entry.getAuthor();
                }

                Timestamp publishDate = null;

                if (entry.getPublishedDate() != null) {
                    publishDate =
                            new Timestamp(entry.getPublishedDate().getTime());
                }

                Timestamp now =
                        new Timestamp(System.currentTimeMillis());

                // Try to find the real image of this news
                String coverImage =
                        findImageUrl(entry, rawDescription);

                // Test line: shows what image was found for each news
                System.out.println("IMAGE: [" + coverImage + "] for " + title);

                // Use the default image only if the feed has no image
                if (coverImage.isEmpty()) {
                    coverImage = DEFAULT_COVER_IMAGE;
                }

                News news =
                        new News(
                                title,
                                description,
                                source.getName(),
                                author,
                                publisher,
                                publishDate,
                                link,
                                now,
                                now,
                                source.getLanguage(),
                                coverImage
                        );

                newsList.add(news);
            }

        } catch (Exception exception) {

            System.out.println(
                    "Hiba az RSS beolvasasa kozben ("
                    + source.getName() + "): "
                    + exception.getMessage());
        }

        return newsList;
    }

    // Try to find an image URL in one RSS entry.
    // Returns an empty text if the entry has no image.
    private String findImageUrl(SyndEntry entry, String rawDescription) {

        // Place 1: the enclosure tag (many feeds put the image here)
        List<SyndEnclosure> enclosureList =
                entry.getEnclosures();

        for (SyndEnclosure enclosure : enclosureList) {

            String enclosureType =
                    enclosure.getType();

            String enclosureUrl =
                    enclosure.getUrl();

            boolean isImage =
                    enclosureType != null
                    && enclosureType.startsWith("image");

            if (isImage && enclosureUrl != null && !enclosureUrl.isEmpty()) {
                return enclosureUrl;
            }
        }

        // Place 2: the media:content and media:thumbnail tags
        List<Element> foreignMarkupList =
                entry.getForeignMarkup();

        for (Element markupElement : foreignMarkupList) {

            String tagName =
                    markupElement.getName();

            if (tagName.equals("content") || tagName.equals("thumbnail")) {

                String mediaUrl =
                        markupElement.getAttributeValue("url");

                if (mediaUrl != null && !mediaUrl.isEmpty()) {
                    return mediaUrl;
                }
            }
        }

        // Place 3: the first <img src="..."> inside the description HTML
        int imageTagStart =
                rawDescription.indexOf("<img");

        if (imageTagStart != -1) {

            int sourceStart =
                    rawDescription.indexOf("src=\"", imageTagStart);

            if (sourceStart != -1) {

                sourceStart = sourceStart + 5;

                int sourceEnd =
                        rawDescription.indexOf("\"", sourceStart);

                if (sourceEnd != -1) {
                    return rawDescription.substring(sourceStart, sourceEnd);
                }
            }
        }

        // No image found in this entry
        return "";
    }
}
      
