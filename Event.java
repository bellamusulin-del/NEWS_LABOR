package com.modellek;

public class Event extends News {

    private String dateTime;
    private String location;

    public Event() {
        super();
    }

    public Event(String title, String description, String sourceOrAuthor,
                 String publicationDate, String publisher,
                 String createdAt, String updatedAt,
                 String coverImage, String link,
                 String dateTime, String location) {

        super(title, description, sourceOrAuthor,
                publicationDate, publisher,
                createdAt, updatedAt,
                coverImage, link);

        this.dateTime = dateTime;
        this.location = location;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}