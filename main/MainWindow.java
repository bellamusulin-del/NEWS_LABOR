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
 
    // Constants (values that never change)
    private static final String DEFAULT_COVER_IMAGE = "res/images/default.jpg";
 
    private static final Color LIGHT_GREEN = new Color(220, 237, 218);
    private static final Color DARK_GREEN = new Color(35, 85, 55);
    private static final Color SELECTED_GREEN = new Color(175, 211, 169);
 
    // Helper objects: database access and RSS reader
    private NewsDAO newsDAO;
    private RssReader rssReader;
 
    // The list on the left side
    private DefaultListModel<News> newsListModel;
    private JList<News> newsList;
 
    // The search panel on the top
    private JTextField searchField;
    private JButton searchButton;
    private JButton refreshButton;
 
    // The details on the right side
    private JLabel coverImageLabel;
    private JTextArea detailsArea;
 
    // The buttons on the bottom
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton clearDbButton;
 
    // The loading indicator on the bottom right
    private JLabel loadingLabel;
    private JProgressBar loadingBar;
 
    // Constructor: create the helpers, build the window, fill the list
    public MainWindow() {
        newsDAO = new NewsDAO();
        rssReader = new RssReader();
 
        createWindow();
        reloadList();
    }
 
    // ------------------------------------------------------------
    // Building the window
    // ------------------------------------------------------------
 
    private void createWindow() {
        setTitle("News Manager");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
 
        getContentPane().setBackground(LIGHT_GREEN);
 
        JPanel searchPanel = createSearchPanel();
        JSplitPane splitPane = createSplitPane();
        JPanel bottomPanel = createBottomPanel();
 
        add(searchPanel, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }
 
    // The top panel: search field, Search button, Refresh button
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
 
        // One listener for the Search button and for the Enter key
        ActionListener searchListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                reloadList();
            }
        };
 
        searchButton.addActionListener(searchListener);
        searchField.addActionListener(searchListener);
 
        // Listener for the Refresh button
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                refreshNewsFromRss();
            }
        });
 
        return panel;
    }
 
    // The middle part: news list (left) and details (right)
    private JSplitPane createSplitPane() {
        JScrollPane listScrollPane = createListScrollPane();
        JPanel detailsPanel = createDetailsPanel();
 
        JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                listScrollPane,
                detailsPanel);
 
        splitPane.setDividerLocation(330);
        splitPane.setResizeWeight(0.33);
 
        return splitPane;
    }
 
    // The scrollable list of news titles
    private JScrollPane createListScrollPane() {
        newsListModel = new DefaultListModel<News>();
        newsList = new JList<News>(newsListModel);
 
        newsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        newsList.setBackground(LIGHT_GREEN);
        newsList.setForeground(DARK_GREEN);
 
        // This renderer draws a line below every news title
        newsList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index,
                    boolean isSelected, boolean hasFocus) {
 
                JLabel label = (JLabel) super.getListCellRendererComponent(
                        list, value, index, isSelected, hasFocus);
 
                label.setOpaque(true);
                label.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, DARK_GREEN),
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
 
        // When the selection changes, show the details of the selected news
        newsList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting()) {
                    showSelectedNewsDetails();
                }
            }
        });
 
        JScrollPane listScrollPane = new JScrollPane(newsList);
 
        return listScrollPane;
    }
 
    // The right side: cover image on the top, text details below it
    private JPanel createDetailsPanel() {
        JPanel detailsPanel = new JPanel(new BorderLayout(5, 5));
        detailsPanel.setBackground(LIGHT_GREEN);
 
        coverImageLabel = new JLabel();
        coverImageLabel.setHorizontalAlignment(JLabel.CENTER);
 
        detailsArea = new JTextArea();
        detailsArea.setEditable(false);
        detailsArea.setLineWrap(true);
        detailsArea.setWrapStyleWord(true);
        detailsArea.setBackground(LIGHT_GREEN);
        detailsArea.setForeground(DARK_GREEN);
 
        JScrollPane detailsScrollPane = new JScrollPane(detailsArea);
 
        detailsPanel.add(coverImageLabel, BorderLayout.NORTH);
        detailsPanel.add(detailsScrollPane, BorderLayout.CENTER);
 
        return detailsPanel;
    }
 
    // The bottom panel: Add, Edit, Delete, Clear DB and the loading bar
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
 
        // The loading indicator is hidden until the RSS refresh starts
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
 
        // Listeners of the buttons
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
 
    // Give every button the same colors
    private void styleButton(JButton button) {
        button.setBackground(DARK_GREEN);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
    }
 
    // ------------------------------------------------------------
    // Showing the news
    // ------------------------------------------------------------
 
    // Load the news from the database into the list
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
 
    // Show the image and the text of the selected news
    private void showSelectedNewsDetails() {
        News selectedNews = newsList.getSelectedValue();
 
        if (selectedNews == null) {
            coverImageLabel.setIcon(null);
            coverImageLabel.setText("");
            detailsArea.setText("Select a news item from the list.");
            return;
        }
 
        showCoverImage(selectedNews.getCoverImage());
 
        String detailsText =
                selectedNews.getTitle() + "\n\n"
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
 
    // Show the cover image. The path can be a web address or a file path.
    private void showCoverImage(String imagePath) {
 
        if (imagePath == null || imagePath.isEmpty()) {
            imagePath = DEFAULT_COVER_IMAGE;
        }
 
        BufferedImage coverImage = null;
 
        // Step 1: try to load the image from the web or from the disk
        try {
 
            if (imagePath.startsWith("http")) {
 
                URL imageUrl = new URL(imagePath);
 
                URLConnection connection = imageUrl.openConnection();
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                connection.setRequestProperty("User-Agent", "Mozilla/5.0");
 
                InputStream inputStream = connection.getInputStream();
                coverImage = ImageIO.read(inputStream);
                inputStream.close();
 
            } else {
 
                File imageFile = new File(imagePath);
                coverImage = ImageIO.read(imageFile);
            }
 
        } catch (Exception exception) {
            System.out.println("Image loading error: " + exception.getMessage());
        }
 
        // Step 2: if it failed (or the format is avif/webp), use the default image
        if (coverImage == null) {
 
            try {
                File defaultFile = new File(DEFAULT_COVER_IMAGE);
                coverImage = ImageIO.read(defaultFile);
            } catch (Exception exception) {
                System.out.println("Default image error: " + exception.getMessage());
            }
        }
 
        // Step 3: if even the default image is missing, show a text
        if (coverImage == null) {
            coverImageLabel.setIcon(null);
            coverImageLabel.setText("No image");
            return;
        }
 
        // Step 4: scale the image and show it
        Image scaledImage = coverImage.getScaledInstance(
                300, 170, Image.SCALE_SMOOTH);
 
        coverImageLabel.setText("");
        coverImageLabel.setIcon(new ImageIcon(scaledImage));
    }
 
    // ------------------------------------------------------------
    // Refresh from RSS
    // ------------------------------------------------------------
 
    // Download the news from the RSS feeds and save them in the database.
    // This runs in the background, so the window does not freeze.
    private void refreshNewsFromRss() {
 
        // Disable the button and show the loading indicator
        refreshButton.setEnabled(false);
        loadingLabel.setVisible(true);
        loadingBar.setVisible(true);
 
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
 
            // This part runs in the background
            @Override
            protected Void doInBackground() throws Exception {
                List<News> newsFromRss = rssReader.getNewsFromRss();
 
                for (News news : newsFromRss) {
                    newsDAO.addNews(news);
                }
 
                return null;
            }
 
            // This part runs when the background work is finished
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
 
    // ------------------------------------------------------------
    // Add, edit, delete, clear
    // ------------------------------------------------------------
 
    private void addNews() {
        News newNews = showNewsDialog(null);
 
        // The user pressed Cancel
        if (newNews == null) {
            return;
        }
 
        boolean alreadyExists = newsDAO.newsExists(
                newNews.getSource(), newNews.getUrl());
 
        if (alreadyExists) {
            JOptionPane.showMessageDialog(this, "This news already exists.");
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
 
        // The user pressed Cancel
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
 
    // ------------------------------------------------------------
    // The Add / Edit dialog
    // ------------------------------------------------------------
 
    // Show the dialog. For a new news the parameter is null.
    // Returns the News object, or null if the user pressed Cancel.
    private News showNewsDialog(News existingNews) {
 
        // Step 1: create the input fields
        JTextField titleField = new JTextField();
        JTextField descriptionField = new JTextField();
        JTextField sourceField = new JTextField();
        JTextField authorField = new JTextField();
        JTextField publisherField = new JTextField();
        JTextField publishDateField = new JTextField();
        JTextField urlField = new JTextField();
        JTextField languageField = new JTextField();
        JTextField coverImageField = new JTextField();
 
        // Step 2: when editing, fill the fields with the old values
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
 
            // Source and URL are the primary key, so they cannot be changed
            sourceField.setEditable(false);
            urlField.setEditable(false);
        }
 
        // Step 3: put the labels and fields into a panel
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
 
        // Step 4: show the dialog again and again until the input is correct
        while (true) {
 
            int answer = JOptionPane.showConfirmDialog(
                    this,
                    panel,
                    dialogTitle,
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE);
 
            // The user pressed Cancel
            if (answer != JOptionPane.OK_OPTION) {
                return null;
            }
 
            // Read the text from the fields
            String title = titleField.getText().trim();
            String description = descriptionField.getText().trim();
            String source = sourceField.getText().trim();
            String author = authorField.getText().trim();
            String publisher = publisherField.getText().trim();
            String dateText = publishDateField.getText().trim();
            String url = urlField.getText().trim();
            String language = languageField.getText().trim().toUpperCase();
            String coverImage = coverImageField.getText().trim();
 
            // Check the required fields
            if (title.isEmpty() || source.isEmpty()
                    || url.isEmpty() || language.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Title, source, URL and language are required.");
                continue;
            }
 
            // Check the language
            if (!language.equals("RO") && !language.equals("HU")) {
                JOptionPane.showMessageDialog(
                        this, "Language must be RO or HU.");
                continue;
            }
 
            // Convert the date text to a Timestamp (it can be empty)
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
 
            // If there is no cover image, use the default one
            if (coverImage.isEmpty()) {
                coverImage = DEFAULT_COVER_IMAGE;
            }
 
            // The creation date stays the same when editing
            Timestamp now = new Timestamp(System.currentTimeMillis());
            Timestamp creationDate = now;
 
            if (existingNews != null) {
                creationDate = existingNews.getCreationDate();
            }
 
            // Everything is correct, create and return the News object
            News news = new News(
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
 
            return news;
        }
    }
}
