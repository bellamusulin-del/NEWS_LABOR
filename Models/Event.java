package com.modellek;

import java.sql.Timestamp;

public class Events extends News {

    private String date;
    private String place;

    public Events(String title, String description,
                  String source, String author,
                  String publisher, Timestamp publishDate,
                  String url, Timestamp creationDate,
                  Timestamp updatedAt, String language,
                  String coverImage, String date, String place) {

        super(title, description, source, author, publisher,
              publishDate, url, creationDate, updatedAt,
              language, coverImage);

        this.date = date;
        this.place = place;
    }

    public String getDate() {
        return date;
    }

    public String getPlace() {
        return place;
    }
}
