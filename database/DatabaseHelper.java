package com.adatbazis;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseHelper {

    private static final String DATABASE_URL =
            "jdbc:sqlite:newsapp.db";

    // Open a connection to the database
    public static Connection getConnection() throws SQLException {
        Connection connection =
                DriverManager.getConnection(DATABASE_URL);

        return connection;
    }

    // Create the news table (composite primary key: source + url)
    public static void createTables() {

        String createNewsTableSql =
                "CREATE TABLE IF NOT EXISTS news("
                + "title TEXT NOT NULL,"
                + "description TEXT,"
                + "source TEXT NOT NULL,"
                + "author TEXT,"
                + "publisher TEXT,"
                + "publish_date TIMESTAMP,"
                + "url TEXT NOT NULL,"
                + "creation_date TIMESTAMP NOT NULL,"
                + "last_modified_date TIMESTAMP NOT NULL,"
                + "language TEXT NOT NULL,"
                + "cover_image TEXT,"
                + "PRIMARY KEY (source, url)"
                + ")";

        try {
            Connection connection = getConnection();

            Statement statement =
                    connection.createStatement();

            statement.execute(createNewsTableSql);

            statement.close();
            connection.close();

            System.out.println("A news tabla letrejott.");

        } catch (SQLException exception) {

            System.out.println(
                    "Hiba a tabla letrehozasakor: "
                    + exception.getMessage());
        }
    }
}
