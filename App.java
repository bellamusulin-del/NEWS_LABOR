package com.main;

import com.adatbazis.DatabaseHelper;
import com.ui.MainWindow;

import javax.swing.SwingUtilities;

public class App {

    public static void main(String[] args) {

        // Create the table if it does not exist yet
        DatabaseHelper.createTables();

        // Swing windows must be created on the Event Dispatch Thread
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {

                MainWindow mainWindow =
                        new MainWindow();

                mainWindow.setVisible(true);
            }
        });
    }
}