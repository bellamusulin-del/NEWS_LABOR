package com.modellekdao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.adatbazis.DatabaseHelper;
import com.modellek.News;

public class NewsDAO {

    // Add a new news item (duplicates are skipped by source + url)
    public void addNews(News news) {

        if (newsExists(news.getSource(), news.getUrl())) {
            System.out.println("Ez a hir mar letezik.");
            return;
        }

        String sql = "INSERT INTO news "
                + "(title, description, source, author, publisher, "
                + "publish_date, url, creation_date, last_modified_date, "
                + "language, cover_image) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            Connection connection =
                    DatabaseHelper.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(sql);

            statement.setString(1, news.getTitle());
            statement.setString(2, news.getDescription());
            statement.setString(3, news.getSource());
            statement.setString(4, news.getAuthor());
            statement.setString(5, news.getPublisher());
            statement.setTimestamp(6, news.getPublishDate());
            statement.setString(7, news.getUrl());
            statement.setTimestamp(8, news.getCreationDate());
            statement.setTimestamp(9, news.getLastModifiedDate());
            statement.setString(10, news.getLanguage());
            statement.setString(11, news.getCoverImage());

            statement.executeUpdate();

            statement.close();
            connection.close();

            System.out.println("A hir elmentve.");

        } catch (SQLException exception) {

            System.out.println(
                    "Hiba a hir mentese kozben: "
                    + exception.getMessage());
        }
    }

    // Check if the news item already exists (the primary key is source + url)
    public boolean newsExists(String source, String url) {

        String sql =
                "SELECT url FROM news WHERE source = ? AND url = ?";

        try {
            Connection connection =
                    DatabaseHelper.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(sql);

            statement.setString(1, source);
            statement.setString(2, url);

            ResultSet resultSet =
                    statement.executeQuery();

            boolean exists = resultSet.next();

            resultSet.close();
            statement.close();
            connection.close();

            return exists;

        } catch (SQLException exception) {

            System.out.println(
                    "Hiba a hir ellenorzesekor: "
                    + exception.getMessage());
        }

        return false;
    }

    // Get all news, the newest first
    public List<News> getAllNews() {

        List<News> newsList =
                new ArrayList<News>();

        String sql = "SELECT * FROM news ORDER BY publish_date DESC";

        try {
            Connection connection =
                    DatabaseHelper.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(sql);

            ResultSet resultSet =
                    statement.executeQuery();

            while (resultSet.next()) {

                News news = new News();

                news.setTitle(resultSet.getString("title"));
                news.setDescription(resultSet.getString("description"));
                news.setSource(resultSet.getString("source"));
                news.setAuthor(resultSet.getString("author"));
                news.setPublisher(resultSet.getString("publisher"));
                news.setPublishDate(resultSet.getTimestamp("publish_date"));
                news.setUrl(resultSet.getString("url"));
                news.setCreationDate(resultSet.getTimestamp("creation_date"));
                news.setLastModifiedDate(resultSet.getTimestamp("last_modified_date"));
                news.setLanguage(resultSet.getString("language"));
                news.setCoverImage(resultSet.getString("cover_image"));

                newsList.add(news);
            }

            resultSet.close();
            statement.close();
            connection.close();

        } catch (SQLException exception) {

            System.out.println(
                    "Hiba a hirek lekerese kozben: "
                    + exception.getMessage());
        }

        return newsList;
    }

    // Get one news item by its primary key (source + url)
    public News getNews(String source, String url) {

        String sql =
                "SELECT * FROM news WHERE source = ? AND url = ?";

        try {
            Connection connection =
                    DatabaseHelper.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(sql);

            statement.setString(1, source);
            statement.setString(2, url);

            ResultSet resultSet =
                    statement.executeQuery();

            if (resultSet.next()) {

                News news = new News();

                news.setTitle(resultSet.getString("title"));
                news.setDescription(resultSet.getString("description"));
                news.setSource(resultSet.getString("source"));
                news.setAuthor(resultSet.getString("author"));
                news.setPublisher(resultSet.getString("publisher"));
                news.setPublishDate(resultSet.getTimestamp("publish_date"));
                news.setUrl(resultSet.getString("url"));
                news.setCreationDate(resultSet.getTimestamp("creation_date"));
                news.setLastModifiedDate(resultSet.getTimestamp("last_modified_date"));
                news.setLanguage(resultSet.getString("language"));
                news.setCoverImage(resultSet.getString("cover_image"));

                resultSet.close();
                statement.close();
                connection.close();

                return news;
            }

            resultSet.close();
            statement.close();
            connection.close();

        } catch (SQLException exception) {

            System.out.println(
                    "Hiba a hir lekerese kozben: "
                    + exception.getMessage());
        }

        return null;
    }

    // Update a news item. The key fields (source, url) are not changed here,
    // they are only used to find the row.
    public void updateNews(News news) {

        String sql = "UPDATE news SET "
                + "title = ?, "
                + "description = ?, "
                + "author = ?, "
                + "publisher = ?, "
                + "publish_date = ?, "
                + "last_modified_date = ?, "
                + "language = ?, "
                + "cover_image = ? "
                + "WHERE source = ? AND url = ?";

        try {
            Connection connection =
                    DatabaseHelper.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(sql);

            statement.setString(1, news.getTitle());
            statement.setString(2, news.getDescription());
            statement.setString(3, news.getAuthor());
            statement.setString(4, news.getPublisher());
            statement.setTimestamp(5, news.getPublishDate());
            statement.setTimestamp(6, news.getLastModifiedDate());
            statement.setString(7, news.getLanguage());
            statement.setString(8, news.getCoverImage());
            statement.setString(9, news.getSource());
            statement.setString(10, news.getUrl());

            statement.executeUpdate();

            statement.close();
            connection.close();

            System.out.println("A hir frissitve.");

        } catch (SQLException exception) {

            System.out.println(
                    "Hiba a hir frissitese kozben: "
                    + exception.getMessage());
        }
    }

    // Delete a news item by its primary key (source + url)
    public void deleteNews(String source, String url) {

        String sql =
                "DELETE FROM news WHERE source = ? AND url = ?";

        try {
            Connection connection =
                    DatabaseHelper.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(sql);

            statement.setString(1, source);
            statement.setString(2, url);

            statement.executeUpdate();

            statement.close();
            connection.close();

            System.out.println("A hir torolve.");

        } catch (SQLException exception) {

            System.out.println(
                    "Hiba a hir torlese kozben: "
                    + exception.getMessage());
        }
    }

    // Delete every news item (used by the Clear DB button)
    public void deleteAllNews() {

        String sql = "DELETE FROM news";

        try {
            Connection connection =
                    DatabaseHelper.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(sql);

            statement.executeUpdate();

            statement.close();
            connection.close();

            System.out.println("Az adatbazis kiurult.");

        } catch (SQLException exception) {

            System.out.println(
                    "Hiba az adatbazis uritese kozben: "
                    + exception.getMessage());
        }
    }

    // Search in title, description, author and publisher
    public List<News> searchNews(String searchText) {

        List<News> newsList =
                new ArrayList<News>();

        String sql = "SELECT * FROM news "
                + "WHERE title LIKE ? "
                + "OR description LIKE ? "
                + "OR author LIKE ? "
                + "OR publisher LIKE ? "
                + "ORDER BY publish_date DESC";

        try {
            Connection connection =
                    DatabaseHelper.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(sql);

            String searchPattern =
                    "%" + searchText + "%";

            statement.setString(1, searchPattern);
            statement.setString(2, searchPattern);
            statement.setString(3, searchPattern);
            statement.setString(4, searchPattern);

            ResultSet resultSet =
                    statement.executeQuery();

            while (resultSet.next()) {

                News news = new News();

                news.setTitle(resultSet.getString("title"));
                news.setDescription(resultSet.getString("description"));
                news.setSource(resultSet.getString("source"));
                news.setAuthor(resultSet.getString("author"));
                news.setPublisher(resultSet.getString("publisher"));
                news.setPublishDate(resultSet.getTimestamp("publish_date"));
                news.setUrl(resultSet.getString("url"));
                news.setCreationDate(resultSet.getTimestamp("creation_date"));
                news.setLastModifiedDate(resultSet.getTimestamp("last_modified_date"));
                news.setLanguage(resultSet.getString("language"));
                news.setCoverImage(resultSet.getString("cover_image"));

                newsList.add(news);
            }

            resultSet.close();
            statement.close();
            connection.close();

        } catch (SQLException exception) {

            System.out.println(
                    "Hiba a kereseskor: "
                    + exception.getMessage());
        }

        return newsList;
    }
}
