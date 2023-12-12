package com.example.demo;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.control.TextInputDialog;

import java.io.*;
import java.util.*;

class Book implements Comparable<Book>, Serializable {
    String title;
    String author;
    int[] location;
    int quantity;

    public Book(String title, String author, int[] location, int quantity) {
        this.title = title;
        this.author = author;
        this.location = location;
        this.quantity = quantity;
    }

    @Override
    public int compareTo(Book other) {
        return this.title.compareTo(other.title);
    }

    @Override
    public String toString() {
        return "Title: " + title + ", Author: " + author + ", Quantity: " + quantity;
    }

    public String getTitle() {
        return title;
    }
}

class LibrarySystem {
    private TreeMap<String, Book> bookBST;
    private HashMap<String, Book> bookHashMap;

    public LibrarySystem() {
        bookBST = new TreeMap<>();
        bookHashMap = new HashMap<>();
    }
    public TreeMap<String, Book> getBookBST() {
        return bookBST;
    }
    public void addBook(String title, String author, int[] location, int quantity) {
        Book newBook = new Book(title, author, location, quantity);
        bookBST.put(title, newBook);
        bookHashMap.put(title, newBook);
    }

    public int[] getBook(String title) {
        return bookHashMap.get(title).location;
    }

    public void displayBooksSorted() {
        List<Book> books = new ArrayList<>(bookBST.values());
        // 冒泡排序
        int n = books.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (books.get(j).compareTo(books.get(j + 1)) > 0) {
                    // 交换元素位置
                    Book temp = books.get(j);
                    books.set(j, books.get(j + 1));
                    books.set(j + 1, temp);
                }
            }
        }
        for (Book book : books) {
            System.out.println(book);
        }
    }

    public boolean borrowBook(String title) {
        if (bookHashMap.get(title).quantity > 0) {
            bookHashMap.get(title).quantity--;
            return true;
        }
        return false;
    }

    public boolean returnBook(String title) {
        bookHashMap.get(title).quantity++;
        return true;
    }

    public int bookQuantity(String title) {
        return bookHashMap.get(title).quantity;
    }

    public void saveLibraryStateToFile(String fileName)  {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
                for (Book book : bookBST.values()) {
                    writer.write(book.title + "," + book.author + "," + Arrays.toString(book.location) + "," + book.quantity);
                    writer.newLine();
                }
                System.out.println("Library state saved to CSV file successfully!");
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
    private Stage primaryStage;
    private TextArea resultTextArea;
    private TextField userInputField;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.librarySystem = new LibrarySystem();
        setupUI();
    }

    private void setupUI() {
        BorderPane borderPane = new BorderPane();
        VBox buttonBox = createButtonBox();
        HBox inputResultBox = createInputResultBox();

        borderPane.setCenter(inputResultBox);
        borderPane.setRight(buttonBox);

        Scene scene = new Scene(borderPane, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Library Management System");
        primaryStage.show();
    }

    private VBox createButtonBox() {
        Button addBookButton = new Button("Add Book");
        Button searchBookButton = new Button("Search Book");
        Button displayBooksButton = new Button("Display Books (Sorted)");
        Button saveButton = new Button("Save Library State");
        Button loadButton = new Button("Load Library State");
        Button borrowButton = new Button("Borrow Book");
        Button returnButton = new Button("Return Book");
        Button exitButton = new Button("Exit");

        addBookButton.setOnAction(e -> handleAddBook());
        searchBookButton.setOnAction(e -> handleSearchBook());
        displayBooksButton.setOnAction(e -> handleDisplayBooks());
        saveButton.setOnAction(e -> handleSaveLibraryState());
        loadButton.setOnAction(e -> handleLoadLibraryState());
        borrowButton.setOnAction(e -> handleBorrowBook());
        returnButton.setOnAction(e -> handleReturnBook());
        exitButton.setOnAction(e -> primaryStage.close());

        VBox buttonBox = new VBox(10, addBookButton, searchBookButton, displayBooksButton,
                saveButton, loadButton, borrowButton, returnButton, exitButton);
        buttonBox.setPadding(new Insets(20));
        return buttonBox;
    }

    private HBox createInputResultBox() {
        userInputField = new TextField();
        resultTextArea = new TextArea();

        HBox inputResultBox = new HBox(10, userInputField, resultTextArea);
        inputResultBox.setPadding(new Insets(20));
        return inputResultBox;
    }

    private void handleAddBook() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Book");
        dialog.setHeaderText("Please enter book information:");
        dialog.setContentText("Title:");

        Optional<String> titleResult = dialog.showAndWait();
        titleResult.ifPresent(title -> {
            dialog.setContentText("Author:");
            Optional<String> authorResult = dialog.showAndWait();
            authorResult.ifPresent(author -> {
                dialog.setContentText("Location (shelf number and row number):");
                Optional<String> locationResult = dialog.showAndWait();
                locationResult.ifPresent(locationString -> {
                    try {
                        int[] location = Arrays.stream(locationString.split(" "))
                                .mapToInt(Integer::parseInt)
                                .toArray();
                        librarySystem.addBook(title, author, location, 1); // Assuming quantity is 1 by default
                        resultTextArea.setText("Book added successfully!");
                    } catch (Exception e) {
                        resultTextArea.setText("Error adding book. Please check your input.");
                    }
                });
            });
        });
    }

    private void handleSearchBook() {
        String title = userInputField.getText();
        try {
            int[] newLocation = librarySystem.getBook(title);
            resultTextArea.setText("Book found, Location: the " + newLocation[0] + "th shelf " + newLocation[0] + "th row\n" +
                    "The quantity of the book " + title + " is: " + librarySystem.bookQuantity(title));
        } catch (NullPointerException e) {
            resultTextArea.setText("Book not found!");
        }
    }

    private void handleDisplayBooks() {
        try {
            StringBuilder booksInfo = new StringBuilder("Books in the library (sorted by title):\n");
            List<Book> books = new ArrayList<>(librarySystem.getBookBST().values());
            books.sort(Comparator.comparing(Book::getTitle));
            for (Book book : books) {
                booksInfo.append(book).append("\n");
            }
            resultTextArea.setText(booksInfo.toString());
        } catch (Exception e) {
            resultTextArea.setText("Error displaying books.");
        }
    }

    private void handleSaveLibraryState() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Save Library State");
        dialog.setHeaderText("Enter the file name to save library state:");

        Optional<String> fileNameResult = dialog.showAndWait();
        fileNameResult.ifPresent(fileName -> {
            librarySystem.saveLibraryStateToFile("book.csv");
            resultTextArea.setText("Library state saved to file successfully!");
        });
    }

    private void handleLoadLibraryState() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Load Library State");
        dialog.setHeaderText("Enter the file name to load library state from:");

        Optional<String> fileNameResult = dialog.showAndWait();
        fileNameResult.ifPresent(fileName -> {
            librarySystem.loadLibraryStateFromFile(fileName);
            resultTextArea.setText("Library state loaded from file successfully!");
        });
    }

    private void handleBorrowBook() {
        String title = userInputField.getText();
        try {
            if (librarySystem.borrowBook(title)) {
                resultTextArea.setText("Borrow the book successfully!");
            } else {
                resultTextArea.setText("Borrow failed. Book not available.");
            }
        } catch (NullPointerException e) {
            resultTextArea.setText("Book not found!");
        }
    }

    private void handleReturnBook() {
        String title = userInputField.getText();
        try {
            if (librarySystem.returnBook(title)) {
                resultTextArea.setText("Return the book successfully!");
            } else {
                resultTextArea.setText("Return failed. Book not found.");
            }
        } catch (NullPointerException e) {
            resultTextArea.setText("Book not found!");
        }
    }
}