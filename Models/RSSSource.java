package com.rssreader;

public class RSSSource {

    private String name;
    private String url;
    private String language;

    public RSSSource(String name, String url, String language) {
        this.name = name;
        this.url = url;
        this.language = language;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getLanguage() {
        return language;
    }
}
