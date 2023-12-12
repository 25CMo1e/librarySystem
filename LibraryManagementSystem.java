package com.example.demo;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.*;
import java.util.*;

class Book implements Comparable<Book> {
    String title;
    String author;
    int[] location;

    public Book(String title, String author, int[] location) {
        this.title = title;
        this.author = author;
        this.location = location;
    }

    @Override
    public int compareTo(Book other) {
        return this.title.compareTo(other.title);
    }

    @Override
    public String toString() {
        return "Title: " + title + ", Author: " + author;
    }
}

class LibrarySystem {
    private TreeMap<String, Book> bookBST; // Binary Search Tree to store books sorted by title
    private HashMap<String, Book> bookHashMap; // HashMap for efficient retrieval using book title

    public LibrarySystem() {
        bookBST = new TreeMap<>();
        bookHashMap = new HashMap<>();
    }

    public void addBook(String title, String author, int[] location) {
        Book newBook = new Book(title, author, location);
        bookBST.put(title, newBook);
        bookHashMap.put(title, newBook);
    }

    public int[] getBook(String title) {
        return Optional.ofNullable(bookHashMap.get(title))
                .map(book -> book.location)
                .orElse(null);
    }

    public List<Book> getAllBooksSorted() {
        return new ArrayList<>(bookBST.values());
    }

    public void saveLibraryStateToFile(String fileName) {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(fileName))) {
            outputStream.writeObject(bookBST);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public void loadLibraryStateFromFile(String fileName) {
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(fileName))) {
            bookBST = (TreeMap<String, Book>) inputStream.readObject();
            bookHashMap = new HashMap<>(bookBST);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}

public class LibraryManagementSystem extends Application {
    private LibrarySystem librarySystem;
    private TextArea outputTextArea;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        librarySystem = new LibrarySystem();
        outputTextArea = new TextArea();
        outputTextArea.setEditable(false);

        BorderPane root = createRootPane();

        Scene scene = new Scene(root, 600, 400);
        primaryStage.setTitle("Library Management System");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private BorderPane createRootPane() {
        BorderPane root = new BorderPane();
        root.setTop(createMenu());
        root.setCenter(outputTextArea);
        return root;
    }

    private MenuBar createMenu() {
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");
        MenuItem exitMenuItem = new MenuItem("Exit");
        exitMenuItem.setOnAction(event -> exitSystem());
        fileMenu.getItems().add(exitMenuItem);

        Menu bookMenu = new Menu("Book");
        MenuItem addBookMenuItem = new MenuItem("Add Book");
        addBookMenuItem.setOnAction(event -> addBook());
        MenuItem searchBookMenuItem = new MenuItem("Search Book");
        searchBookMenuItem.setOnAction(event -> searchBook());
        MenuItem displayBooksMenuItem = new MenuItem("Display Books (Sorted)");
        displayBooksMenuItem.setOnAction(event -> displayBooks());
        bookMenu.getItems().addAll(addBookMenuItem, searchBookMenuItem, displayBooksMenuItem);

        Menu libraryMenu = new Menu("Library");
        MenuItem saveLibraryStateMenuItem = new MenuItem("Save Library State to File");
        saveLibraryStateMenuItem.setOnAction(event -> saveLibraryState());
        MenuItem loadLibraryStateMenuItem = new MenuItem("Load Library State from File");
        loadLibraryStateMenuItem.setOnAction(event -> loadLibraryState());
        libraryMenu.getItems().addAll(saveLibraryStateMenuItem, loadLibraryStateMenuItem);

        menuBar.getMenus().addAll(fileMenu, bookMenu, libraryMenu);
        return menuBar;
    }

    private void addBook() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Book");
        dialog.setHeaderText(null);
        dialog.setContentText("Enter book title:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(title -> {
            TextInputDialog authorDialog = new TextInputDialog();
            authorDialog.setTitle("Add Book");
            authorDialog.setHeaderText(null);
            authorDialog.setContentText("Enter author name:");

            Optional<String> authorResult = authorDialog.showAndWait();
            authorResult.ifPresent(author -> {
                TextInputDialog locationDialog = new TextInputDialog();
                locationDialog.setTitle("Add Book");
                locationDialog.setHeaderText(null);
                locationDialog.setContentText("Enter location (shelf number and row number, separated by space):");

                Optional<String> locationResult = locationDialog.showAndWait();
                locationResult.ifPresent(location -> {
                    String[] locationArray = location.split(" ");
                    if (locationArray.length == 2) {
                        try {
                            int[] locationInt = {Integer.parseInt(locationArray[0]), Integer.parseInt(locationArray[1])};
                            librarySystem.addBook(title, author, locationInt);
                            appendTextToOutput("Book added successfully!");
                        } catch (NumberFormatException e) {
                            appendTextToOutput("Invalid location format. Please enter numbers.");
                        }
                    } else {
                        appendTextToOutput("Invalid location format. Please enter shelf number and row number separated by space.");
                    }
                });
            });
        });
    }

    private void searchBook() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Search Book");
        dialog.setHeaderText(null);
        dialog.setContentText("Enter book title to search:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(title -> {
            int[] location = librarySystem.getBook(title);
            if (location != null) {
                appendTextToOutput("Book found, Location: " + Arrays.toString(location));
            } else {
                appendTextToOutput("Book not found!");
            }
        });
    }

    private void displayBooks() {
        List<Book> books = librarySystem.getAllBooksSorted();
        if (!books.isEmpty()) {
            appendTextToOutput("Books in the library (sorted by title):");
            for (Book book : books) {
                appendTextToOutput(book.toString());
            }
        } else {
            appendTextToOutput("No books in the library.");
        }
    }

    private void saveLibraryState() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Save Library State");
        dialog.setHeaderText(null);
        dialog.setContentText("Enter the file name to save library state:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(fileName -> {
            librarySystem.saveLibraryStateToFile(fileName);
            appendTextToOutput("Library state saved to file successfully!");
        });
    }

    private void loadLibraryState() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Load Library State");
        dialog.setHeaderText(null);
        dialog.setContentText("Enter the file name to load library state from:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(fileName -> {
            librarySystem.loadLibraryStateFromFile(fileName);
            appendTextToOutput("Library state loaded from file successfully!");
        });
    }

    private void exitSystem() {
        System.out.println("Exiting the Library Management System. Goodbye!");
        System.exit(0);
    }

    private void appendTextToOutput(String text) {
        outputTextArea.appendText(text + "\n");
    }
}
