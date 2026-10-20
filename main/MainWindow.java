package com.ui;

import com.modellek.News;
import com.modellekdao.NewsDAO;
import com.rssreader.RssReader;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Timestamp;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class MainWindow extends JFrame {

    private static final String DEFAULT_COVER_IMAGE = "res/images/default.jpg";

    private static final Color LIGHT_GREEN = new Color(220, 237, 218);
    private static final Color DARK_GREEN = new Color(35, 85, 55);
    private static final Color SELECTED_GREEN = new Color(175, 211, 169);

    private NewsDAO newsDAO;
    private RssReader rssReader;

    private DefaultListModel<News> newsListModel;
    private JList<News> newsList;

    private JTextField searchField;
    private JButton searchButton;
    private JButton refreshButton;

    private JLabel coverImageLabel;
    private JTextArea detailsArea;

    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton clearDbButton;

    private JLabel loadingLabel;
    private JProgressBar loadingBar;

    public MainWindow() {
        newsDAO = new NewsDAO();
        rssReader = new RssReader();

        createWindow();
        reloadList();
    }

    // Window setup

    private void createWindow() {
        setTitle("News Manager");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        getContentPane().setBackground(LIGHT_GREEN);

        add(createSearchPanel(), BorderLayout.NORTH);
        add(createSplitPane(), BorderLayout.CENTER);
        add(createBottomPanel(), BorderLayout.SOUTH);
    }

    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(LIGHT_GREEN);

        JLabel searchLabel = new JLabel(" Search: ");
        searchLabel.setForeground(DARK_GREEN);

        searchField = new JTextField();
        searchField.setBackground(Color.WHITE);
        searchField.setForeground(DARK_GREEN);

        searchButton = new JButton("Search");
        refreshButton = new JButton("Refresh");

        styleButton(searchButton);
        styleButton(refreshButton);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(LIGHT_GREEN);
        buttonPanel.add(searchButton);
        buttonPanel.add(refreshButton);

        panel.add(searchLabel, BorderLayout.WEST);
        panel.add(searchField, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.EAST);

        ActionListener searchListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                reloadList();
            }
        };

        searchButton.addActionListener(searchListener);
        searchField.addActionListener(searchListener);

        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                refreshNewsFromRss();
            }
        });

        return panel;
    }

    private JSplitPane createSplitPane() {
        JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                createListScrollPane(),
                createDetailsPanel());

        splitPane.setDividerLocation(330);
        splitPane.setResizeWeight(0.33);

        return splitPane;
    }

    private JScrollPane createListScrollPane() {
        newsListModel = new DefaultListModel<News>();
        newsList = new JList<News>(newsListModel);

        newsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        newsList.setBackground(LIGHT_GREEN);
        newsList.setForeground(DARK_GREEN);

        newsList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list,
                    Object value,
                    int index,
                    boolean isSelected,
                    boolean hasFocus) {

                JLabel label = (JLabel) super.getListCellRendererComponent(
                        list, value, index, isSelected, hasFocus);

                label.setOpaque(true);
                label.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(
                                0, 0, 1, 0, DARK_GREEN),
                        BorderFactory.createEmptyBorder(7, 8, 7, 8)));

                if (isSelected) {
                    label.setBackground(SELECTED_GREEN);
                } else {
                    label.setBackground(LIGHT_GREEN);
                }

                label.setForeground(DARK_GREEN);

                return label;
            }
        });

        newsList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting()) {
                    showSelectedNewsDetails();
                }
            }
        });

        return new JScrollPane(newsList);
    }

    private JPanel createDetailsPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(LIGHT_GREEN);

        coverImageLabel = new JLabel();
        coverImageLabel.setHorizontalAlignment(JLabel.CENTER);

        detailsArea = new JTextArea();
        detailsArea.setEditable(false);
        detailsArea.setLineWrap(true);
        detailsArea.setWrapStyleWord(true);
        detailsArea.setBackground(LIGHT_GREEN);
        detailsArea.setForeground(DARK_GREEN);

        panel.add(coverImageLabel, BorderLayout.NORTH);
        panel.add(new JScrollPane(detailsArea), BorderLayout.CENTER);

        return panel;
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(LIGHT_GREEN);

        addButton = new JButton("Add");
        editButton = new JButton("Edit");
        deleteButton = new JButton("Delete");
        clearDbButton = new JButton("Clear DB");

        styleButton(addButton);
        styleButton(editButton);
        styleButton(deleteButton);
        styleButton(clearDbButton);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(LIGHT_GREEN);

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearDbButton);

        loadingLabel = new JLabel("Loading...");
        loadingLabel.setForeground(DARK_GREEN);

        loadingBar = new JProgressBar();
        loadingBar.setIndeterminate(true);
        loadingLabel.setVisible(false);
        loadingBar.setVisible(false);

        JPanel loadingPanel = new JPanel(new FlowLayout());
        loadingPanel.setBackground(LIGHT_GREEN);
        loadingPanel.add(loadingLabel);
        loadingPanel.add(loadingBar);

        panel.add(buttonPanel, BorderLayout.CENTER);
        panel.add(loadingPanel, BorderLayout.EAST);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                addNews();
            }
        });

        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                editNews();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                deleteNews();
            }
        });

        clearDbButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                clearDatabase();
            }
        });

        return panel;
    }

    private void styleButton(JButton button) {
        button.setBackground(DARK_GREEN);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
    }

    // Display news

    private void reloadList() {
        String searchText = searchField.getText().trim();
        List<News> newsFromDatabase;

        if (searchText.isEmpty()) {
            newsFromDatabase = newsDAO.getAllNews();
        } else {
            newsFromDatabase = newsDAO.searchNews(searchText);
        }

        newsListModel.clear();

        for (News news : newsFromDatabase) {
            newsListModel.addElement(news);
        }

        showSelectedNewsDetails();
    }

    private void showSelectedNewsDetails() {
        News selectedNews = newsList.getSelectedValue();

        if (selectedNews == null) {
            coverImageLabel.setIcon(null);
            coverImageLabel.setText("");
            detailsArea.setText("Select a news item from the list.");
            return;
        }

        showCoverImage(selectedNews.getCoverImage());

        String detailsText
                = selectedNews.getTitle() + "\n\n"
                + selectedNews.getDescription() + "\n\n"
                + "Source: " + selectedNews.getSource() + "\n"
                + "Author: " + selectedNews.getAuthor() + "\n"
                + "Publisher: " + selectedNews.getPublisher() + "\n"
                + "Publish date: " + selectedNews.getPublishDate() + "\n"
                + "Language: " + selectedNews.getLanguage() + "\n"
                + "URL: " + selectedNews.getUrl();

        detailsArea.setText(detailsText);
        detailsArea.setCaretPosition(0);
    }

    private void showCoverImage(String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            imagePath = DEFAULT_COVER_IMAGE;
        }

        BufferedImage coverImage = null;

        try {
            if (imagePath.startsWith("http")) {
                URL imageUrl = new URL(imagePath);
                URLConnection connection = imageUrl.openConnection();

                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                connection.setRequestProperty("User-Agent", "Mozilla/5.0");

                try (InputStream inputStream = connection.getInputStream()) {
                    coverImage = ImageIO.read(inputStream);
                }
            } else {
                coverImage = ImageIO.read(new File(imagePath));
            }
        } catch (Exception exception) {
            System.out.println("Image loading error: "
                    + exception.getMessage());
        }

        if (coverImage == null) {
            try {
                coverImage = ImageIO.read(new File(DEFAULT_COVER_IMAGE));
            } catch (Exception exception) {
                System.out.println("Default image error: "
                        + exception.getMessage());
            }
        }

        if (coverImage == null) {
            coverImageLabel.setIcon(null);
            coverImageLabel.setText("No image");
            return;
        }

        Image scaledImage = coverImage.getScaledInstance(
                300, 170, Image.SCALE_SMOOTH);

        coverImageLabel.setText("");
        coverImageLabel.setIcon(new ImageIcon(scaledImage));
    }

    // RSS refresh

    private void refreshNewsFromRss() {
        refreshButton.setEnabled(false);
        loadingLabel.setVisible(true);
        loadingBar.setVisible(true);

        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                List<News> newsFromRss = rssReader.getNewsFromRss();

                for (News news : newsFromRss) {
                    newsDAO.addNews(news);
                }

                return null;
            }

            @Override
            protected void done() {
                loadingLabel.setVisible(false);
                loadingBar.setVisible(false);
                refreshButton.setEnabled(true);

                try {
                    get();
                    reloadList();
                } catch (Exception exception) {
                    JOptionPane.showMessageDialog(
                            MainWindow.this,
                            "Error while refreshing news: "
                            + exception.getMessage());
                }
            }
        };

        worker.execute();
    }

    // Add, edit and delete news

    private void addNews() {
        News newNews = showNewsDialog(null);

        if (newNews == null) {
            return;
        }

        boolean alreadyExists = newsDAO.newsExists(
                newNews.getSource(), newNews.getUrl());

        if (alreadyExists) {
            JOptionPane.showMessageDialog(
                    this, "This news already exists.");
            return;
        }

        newsDAO.addNews(newNews);
        reloadList();
    }

    private void editNews() {
        News selectedNews = newsList.getSelectedValue();

        if (selectedNews == null) {
            JOptionPane.showMessageDialog(
                    this, "Please select a news item first.");
            return;
        }

        News editedNews = showNewsDialog(selectedNews);

        if (editedNews == null) {
            return;
        }

        newsDAO.updateNews(editedNews);
        reloadList();
    }

    private void deleteNews() {
        News selectedNews = newsList.getSelectedValue();

        if (selectedNews == null) {
            JOptionPane.showMessageDialog(
                    this, "Please select a news item first.");
            return;
        }

        int answer = JOptionPane.showConfirmDialog(
                this,
                "Delete the selected news?",
                "Delete news",
                JOptionPane.YES_NO_OPTION);

        if (answer == JOptionPane.YES_OPTION) {
            newsDAO.deleteNews(
                    selectedNews.getSource(), selectedNews.getUrl());
            reloadList();
        }
    }

    private void clearDatabase() {
        int answer = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to clear the database?",
                "Clear database",
                JOptionPane.YES_NO_OPTION);

        if (answer == JOptionPane.YES_OPTION) {
            newsDAO.deleteAllNews();
            reloadList();
        }
    }

    // Add and edit dialog

    private News showNewsDialog(News existingNews) {
        JTextField titleField = new JTextField();
        JTextField descriptionField = new JTextField();
        JTextField sourceField = new JTextField();
        JTextField authorField = new JTextField();
        JTextField publisherField = new JTextField();
        JTextField publishDateField = new JTextField();
        JTextField urlField = new JTextField();
        JTextField languageField = new JTextField();
        JTextField coverImageField = new JTextField();

        if (existingNews != null) {
            titleField.setText(existingNews.getTitle());
            descriptionField.setText(existingNews.getDescription());
            sourceField.setText(existingNews.getSource());
            authorField.setText(existingNews.getAuthor());
            publisherField.setText(existingNews.getPublisher());
            urlField.setText(existingNews.getUrl());
            languageField.setText(existingNews.getLanguage());
            coverImageField.setText(existingNews.getCoverImage());

            if (existingNews.getPublishDate() != null) {
                publishDateField.setText(
                        existingNews.getPublishDate().toString());
            }

            // Source and URL identify the news item in the database.
            sourceField.setEditable(false);
            urlField.setEditable(false);
        }

        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));

        panel.add(new JLabel("Title:"));
        panel.add(titleField);
        panel.add(new JLabel("Description:"));
        panel.add(descriptionField);
        panel.add(new JLabel("Source:"));
        panel.add(sourceField);
        panel.add(new JLabel("Author:"));
        panel.add(authorField);
        panel.add(new JLabel("Publisher:"));
        panel.add(publisherField);
        panel.add(new JLabel("Publish date (yyyy-MM-dd HH:mm:ss):"));
        panel.add(publishDateField);
        panel.add(new JLabel("URL:"));
        panel.add(urlField);
        panel.add(new JLabel("Language (RO or HU):"));
        panel.add(languageField);
        panel.add(new JLabel("Cover image path:"));
        panel.add(coverImageField);

        String dialogTitle = "Add news";

        if (existingNews != null) {
            dialogTitle = "Edit news";
        }

        while (true) {
            int answer = JOptionPane.showConfirmDialog(
                    this,
                    panel,
                    dialogTitle,
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE);

            if (answer != JOptionPane.OK_OPTION) {
                return null;
            }

            String title = titleField.getText().trim();
            String description = descriptionField.getText().trim();
            String source = sourceField.getText().trim();
            String author = authorField.getText().trim();
            String publisher = publisherField.getText().trim();
            String dateText = publishDateField.getText().trim();
            String url = urlField.getText().trim();
            String language = languageField.getText().trim().toUpperCase();
            String coverImage = coverImageField.getText().trim();

            if (title.isEmpty() || source.isEmpty()
                    || url.isEmpty() || language.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Title, source, URL and language are required.");
                continue;
            }

            if (!language.equals("RO") && !language.equals("HU")) {
                JOptionPane.showMessageDialog(
                        this, "Language must be RO or HU.");
                continue;
            }

            Timestamp publishDate = null;

            if (!dateText.isEmpty()) {
                try {
                    publishDate = Timestamp.valueOf(dateText);
                } catch (IllegalArgumentException exception) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Wrong date format. Example: 2026-10-20 18:30:00");
                    continue;
                }
            }

            if (coverImage.isEmpty()) {
                coverImage = DEFAULT_COVER_IMAGE;
            }

            Timestamp now = new Timestamp(System.currentTimeMillis());
            Timestamp creationDate = now;

            if (existingNews != null) {
                creationDate = existingNews.getCreationDate();
            }

            return new News(
                    title,
                    description,
                    source,
                    author,
                    publisher,
                    publishDate,
                    url,
                    creationDate,
                    now,
                    language,
                    coverImage);
        }
    }
}
