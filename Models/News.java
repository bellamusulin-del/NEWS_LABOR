package com.modellek;

import java.sql.Timestamp;

public class News {

    private String title;
    private String description;
    private String source;
    private String author;
    private String publisher;
    private Timestamp publishDate;
    private String url;
    private Timestamp creationDate;
    private Timestamp lastModifiedDate;
    private String language;
    private String coverImage;

    public News() {
    }

    public News(String title, String description, String source,
                String author, String publisher, Timestamp publishDate,
                String url, Timestamp creationDate,
                Timestamp lastModifiedDate, String language,
                String coverImage) {

        this.title = title;
        this.description = description;
        this.source = source;
        this.author = author;
        this.publisher = publisher;
        this.publishDate = publishDate;
        this.url = url;
        this.creationDate = creationDate;
        this.lastModifiedDate = lastModifiedDate;
        this.language = language;
        this.coverImage = coverImage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public Timestamp getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(Timestamp publishDate) {
        this.publishDate = publishDate;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Timestamp getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Timestamp creationDate) {
        this.creationDate = creationDate;
    }

    public Timestamp getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Timestamp lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    // The JList shows this text for every news item
    @Override
    public String toString() {
        return title;
    }
}

    public void setLink(String link) {
        this.link = link;
    }
}
