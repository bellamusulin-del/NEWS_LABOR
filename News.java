package com.modellek;

public class News {

    private int id;
    private String title;
    private String description;
    private String sourceOrAuthor;
    private String publicationDate;
    private String publisher;
    private String createdAt;
    private String updatedAt;
    private String coverImage;
    private String link;

    public News() {
    }

    public News(String title, String description, String sourceOrAuthor,
                String publicationDate, String publisher,
                String createdAt, String updatedAt,
                String coverImage, String link) {

        this.title = title;
        this.description = description;
        this.sourceOrAuthor = sourceOrAuthor;
        this.publicationDate = publicationDate;
        this.publisher = publisher;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.coverImage = coverImage;
        this.link = link;
    }

    public News(int id, String title, String description,
                String sourceOrAuthor, String publicationDate,
                String publisher, String createdAt, String updatedAt,
                String coverImage, String link) {

        this.id = id;
        this.title = title;
        this.description = description;
        this.sourceOrAuthor = sourceOrAuthor;
        this.publicationDate = publicationDate;
        this.publisher = publisher;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.coverImage = coverImage;
        this.link = link;
    }

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSourceOrAuthor() {
        return sourceOrAuthor;
    }

    public void setSourceOrAuthor(String sourceOrAuthor) {
        this.sourceOrAuthor = sourceOrAuthor;
    }

    public String getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(String publicationDate) {
        this.publicationDate = publicationDate;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
