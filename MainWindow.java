
package com.ui;

import com.modellek.News;
import com.modellekdao.NewsDAO;
import com.rssreader.RssReader;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import java.io.File;
import java.sql.Timestamp;
import java.util.List;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class MainWindow extends JFrame {

    private static final String DEFAULT_COVER_IMAGE =
            "res/images/default.jpg";

    private static final Color LIGHT_GREEN =
            new Color(220, 237, 218);

    private static final Color DARK_GREEN =
            new Color(35, 85, 55);

    private static final Color SELECTED_GREEN =
            new Color(175, 211, 169);

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

        JLabel label = new JLabel(" Search: ");
        label.setForeground(DARK_GREEN);

        searchField = new JTextField();
        searchField.setBackground(Color.WHITE);
        searchField.setForeground(DARK_GREEN);

        searchButton = new JButton("Search");
        refreshButton = new JButton("Refresh");

        styleButton(searchButton);
        styleButton(refreshButton);

        JPanel buttons = new JPanel(new FlowLayout());
        buttons.setBackground(LIGHT_GREEN);
        buttons.add(searchButton);
        buttons.add(refreshButton);

        panel.add(label, BorderLayout.WEST);
        panel.add(searchField, BorderLayout.CENTER);
        panel.add(buttons, BorderLayout.EAST);

        searchButton.addActionListener(e -> reloadList());
        searchField.addActionListener(e -> reloadList());
        refreshButton.addActionListener(e -> refreshNewsFromRss());

        return panel;
    }

    private JSplitPane createSplitPane() {
        newsListModel = new DefaultListModel<>();
        newsList = new JList<>(newsListModel);

        newsList.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);

        newsList.setBackground(LIGHT_GREEN);
        newsList.setForeground(DARK_GREEN);

        // Add a line below every news item
        newsList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index,
                    boolean selected, boolean focus) {

                JLabel label = (JLabel)
                        super.getListCellRendererComponent(
                                list, value, index, selected, focus);

                label.setOpaque(true);
                label.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(
                                0, 0, 1, 0, DARK_GREEN),
                        BorderFactory.createEmptyBorder(7, 8, 7, 8)));

                if (selected) {
                    label.setBackground(SELECTED_GREEN);
                    label.setForeground(DARK_GREEN);
                } else {
                    label.setBackground(LIGHT_GREEN);
                    label.setForeground(DARK_GREEN);
                }

                return label;
            }
        });

        newsList.addListSelectionListener(
                new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting()) {
                    showSelectedNewsDetails();
                }
            }
        });

        JScrollPane listScrollPane = new JScrollPane(newsList);

        JPanel detailsPanel = new JPanel(new BorderLayout(5, 5));
        detailsPanel.setBackground(LIGHT_GREEN);

        coverImageLabel = new JLabel();
        coverImageLabel.setHorizontalAlignment(JLabel.CENTER);
        coverImageLabel.setBackground(LIGHT_GREEN);

        detailsArea = new JTextArea();
        detailsArea.setEditable(false);
        detailsArea.setLineWrap(true);
        detailsArea.setWrapStyleWord(true);
        detailsArea.setBackground(LIGHT_GREEN);
        detailsArea.setForeground(DARK_GREEN);
        detailsArea.setCaretColor(DARK_GREEN);

        JScrollPane detailsScrollPane =
                new JScrollPane(detailsArea);

        detailsPanel.add(coverImageLabel, BorderLayout.NORTH);
        detailsPanel.add(detailsScrollPane, BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                listScrollPane,
                detailsPanel);

        splitPane.setDividerLocation(330);
        splitPane.setResizeWeight(0.33);

        return splitPane;
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

        JPanel buttons = new JPanel(new FlowLayout());
        buttons.setBackground(LIGHT_GREEN);

        buttons.add(addButton);
        buttons.add(editButton);
        buttons.add(deleteButton);
        buttons.add(clearDbButton);

        loadingLabel = new JLabel("Loading...");
        loadingLabel.setForeground(DARK_GREEN);

        loadingBar = new JProgressBar();
        loadingBar.setIndeterminate(true);

        JPanel loadingPanel = new JPanel(new FlowLayout());
        loadingPanel.setBackground(LIGHT_GREEN);
        loadingPanel.add(loadingLabel);
        loadingPanel.add(loadingBar);

        loadingLabel.setVisible(false);
        loadingBar.setVisible(false);

        panel.add(buttons, BorderLayout.CENTER);
        panel.add(loadingPanel, BorderLayout.EAST);

        addButton.addActionListener(e -> addNews());
        editButton.addActionListener(e -> editNews());
        deleteButton.addActionListener(e -> deleteNews());
        clearDbButton.addActionListener(e -> clearDatabase());

        return panel;
    }

    private void styleButton(JButton button) {
        button.setBackground(DARK_GREEN);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
    }

    private void reloadList() {
        String searchText = searchField.getText().trim();

        List<News> news;

        if (searchText.isEmpty()) {
            news = newsDAO.getAllNews();
        } else {
            news = newsDAO.searchNews(searchText);
        }

        newsListModel.clear();

        for (News item : news) {
            newsListModel.addElement(item);
        }

        showSelectedNewsDetails();
    }

    private void showSelectedNewsDetails() {
        News news = newsList.getSelectedValue();

        if (news == null) {
            coverImageLabel.setIcon(null);
            detailsArea.setText(
                    "Select a news item from the list.");
            return;
        }

        showCoverImage(news.getCoverImage());

        String text =
                news.getTitle() + "\n\n"
                + news.getDescription() + "\n\n"
                + "Source: " + news.getSource() + "\n"
                + "Author: " + news.getAuthor() + "\n"
                + "Publisher: " + news.getPublisher() + "\n"
                + "Publish date: " + news.getPublishDate() + "\n"
                + "Language: " + news.getLanguage() + "\n"
                + "URL: " + news.getUrl();

        detailsArea.setText(text);
        detailsArea.setCaretPosition(0);
    }

    private void showCoverImage(String path) {
        if (path == null || path.isEmpty()) {
            path = DEFAULT_COVER_IMAGE;
        }

        File imageFile = new File(path);

        if (!imageFile.exists()) {
            imageFile = new File(DEFAULT_COVER_IMAGE);
        }

        if (!imageFile.exists()) {
            coverImageLabel.setIcon(null);
            coverImageLabel.setText("No image");
            return;
        }

        ImageIcon icon = new ImageIcon(imageFile.getPath());

        Image image = icon.getImage().getScaledInstance(
                300, 170, Image.SCALE_SMOOTH);

        coverImageLabel.setText("");
        coverImageLabel.setIcon(new ImageIcon(image));
    }

    private void refreshNewsFromRss() {
        refreshButton.setEnabled(false);
        loadingLabel.setVisible(true);
        loadingBar.setVisible(true);

        SwingWorker<Void, Void> worker =
                new SwingWorker<Void, Void>() {

            @Override
            protected Void doInBackground() throws Exception {
                List<News> news = rssReader.getNewsFromRss();

                for (News item : news) {
                    newsDAO.addNews(item);
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
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                            MainWindow.this,
                            "Error while refreshing news: "
                                    + ex.getMessage());
                }
            }
        };

        worker.execute();
    }

    private void addNews() {
        News news = showNewsDialog(null);

        if (news == null) {
            return;
        }

        if (newsDAO.newsExists(news.getSource(), news.getUrl())) {
            JOptionPane.showMessageDialog(
                    this, "This news already exists.");
            return;
        }

        newsDAO.addNews(news);
        reloadList();
    }

    private void editNews() {
        News selected = newsList.getSelectedValue();

        if (selected == null) {
            JOptionPane.showMessageDialog(
                    this, "Please select a news item first.");
            return;
        }

        News edited = showNewsDialog(selected);

        if (edited != null) {
            newsDAO.updateNews(edited);
            reloadList();
        }
    }

    private void deleteNews() {
        News selected = newsList.getSelectedValue();

        if (selected == null) {
            JOptionPane.showMessageDialog(
                    this, "Please select a news item first.");
            return;
        }

        int result = JOptionPane.showConfirmDialog(
                this,
                "Delete the selected news?",
                "Delete news",
                JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            newsDAO.deleteNews(
                    selected.getSource(), selected.getUrl());
            reloadList();
        }
    }

    private void clearDatabase() {
        int result = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to clear the database?",
                "Clear database",
                JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            newsDAO.deleteAllNews();
            reloadList();
        }
    }

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

        String dialogTitle =
                existingNews == null ? "Add news" : "Edit news";

        while (true) {
            int result = JOptionPane.showConfirmDialog(
                    this,
                    panel,
                    dialogTitle,
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE);

            if (result != JOptionPane.OK_OPTION) {
                return null;
            }

            String title = titleField.getText().trim();
            String description = descriptionField.getText().trim();
            String source = sourceField.getText().trim();
            String author = authorField.getText().trim();
            String publisher = publisherField.getText().trim();
            String dateText = publishDateField.getText().trim();
            String url = urlField.getText().trim();
            String language = languageField.getText().trim()
                    .toUpperCase();
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
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Wrong date format. Example: "
                                    + "2026-10-20 18:30:00");
                    continue;
                }
            }

            if (coverImage.isEmpty()) {
                coverImage = DEFAULT_COVER_IMAGE;
            }

            Timestamp now =
                    new Timestamp(System.currentTimeMillis());

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
