package com.adatbazis;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseHelper {

    private static final String DATABASE_URL =
            "jdbc:sqlite:newsapp.db";

    // Kapcsolat megnyitasa adatbazissal
    public static Connection getConnection() throws SQLException {
        Connection connection =
                DriverManager.getConnection(DATABASE_URL);

        return connection;
    }

    // news letrehozasa
    public static void createTables() {

        String createNewsTableSql =
                "CREATE TABLE IF NOT EXISTS news("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "title TEXT,"
                + "description TEXT NOT NULL,"
                + "source_or_author TEXT,"
                + "publication_date TEXT,"
                + "publisher TEXT,"
                + "created_at TEXT NOT NULL,"
                + "updated_at TEXT NOT NULL,"
                + "cover_image TEXT,"
                + "link TEXT UNIQUE"
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
