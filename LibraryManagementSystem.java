package cps2232;


	import java.io.*;
	import java.util.Arrays;
	import java.util.*;

	class Book implements Comparable<Book>, Serializable {
	    String title;
	    String author;
	    int[] location;
	    int quantity;

	    public Book(String title, String author,int[]location, int quantity) {
	        this.title = title;
	        this.author = author;
	        this.location = location;
	        this.quantity =quantity;
	    }

	    @Override
	    public int compareTo(Book other) {
	        return this.title.compareTo(other.title);
	    }

	    @Override
	    public String toString() {
	        return "Title: " + title + ", Author: " + author + ", quantity: " + quantity;
	    }
	}

	class LibrarySystem {
	    private TreeMap<String, Book> bookBST; // Binary Search Tree to store books sorted by title
	    private HashMap<String, Book> bookHashMap; // HashMap for efficient retrieval using book title
	    

	    public LibrarySystem() {
	        bookBST = new TreeMap<>();
	        bookHashMap = new HashMap<>();
	    }

	    public void addBook(String title, String author,int[] location, int quantity) {
	        Book newBook = new Book(title, author, location, quantity);
	        bookBST.put(title, newBook);
	        bookHashMap.put(title, newBook);
	    }

	    public int[] getBook(String title) {
	            return bookHashMap.get(title).location;
	    }


	    public void displayBooksSorted() {
	        List<Book> books = new ArrayList<>(bookBST.values());
	        Collections.sort(books);
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
	    
	    public int bookquabtity(String title) {
	         return bookHashMap.get(title).quantity;
		           
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

	public class LibraryManagementSystem {
	    public static void main(String[] args) {
	        LibrarySystem librarySystem = new LibrarySystem();
	        Scanner scanner = new Scanner(System.in);

	        while (true) {
	            System.out.println("\nLibrary Management System");
	            System.out.println("1. Add Book");
	            System.out.println("2. Search Book");
	            System.out.println("3. Display Books (Sorted)");
	            System.out.println("4. Save Library State to File");
	            System.out.println("5. Load Library State from File");
	            System.out.println("6. Borrow book");
	            System.out.println("7. return book");
	            System.out.println("8. Exit");

	            System.out.print("Enter your choice: ");
	            int choice = scanner.nextInt();
	            scanner.nextLine(); // Consume the newline character

	            switch (choice) {
	                case 1:
	                    System.out.print("Enter book title: ");
	                    String title = scanner.nextLine();
	                    System.out.print("Enter author name: ");
	                    String author = scanner.nextLine();
	                    Scanner sc = new Scanner(System.in);
	                    System.out.print("Enter quantity: " );
	                    int quantity = sc.nextInt();
	                    System.out.print("Enter location(shelf number and row number): " );
	                    Scanner in = new Scanner(System.in);
	                    int[] location = new int[2];
	                    for(int i =0; i<2;i++) {
	                    location[i]=in.nextInt();
	                    }
	                    librarySystem.addBook(title, author, location,quantity);
	                    System.out.println("Book added successfully!");
	                    break;
	                case 2:
	                    System.out.print("Enter book title to search: ");
	                    String searchTitle = scanner.nextLine();
	                    try{int[]newLocation = librarySystem.getBook(searchTitle);
	                    System.out.println("Book found, Location: " + Arrays.toString(newLocation));
	                    System.out.println("The quantity of the book " + searchTitle +" is: "+librarySystem.bookquabtity(searchTitle));
	                    }catch(NullPointerException e) {
	                    	System.out.println("book not found!");
	                    }
	                    break;
	                case 3:
	                    System.out.println("Books in the library (sorted by title):");
	                    librarySystem.displayBooksSorted();
	                    break;
	                case 4:
	                    System.out.print("Enter the file name to save library state: ");
	                    String saveFileName = scanner.nextLine();
	                    librarySystem.saveLibraryStateToFile(saveFileName);
	                    System.out.println("Library state saved to file successfully!");
	                    break;
	                case 5:
	                    System.out.print("Enter the file name to load library state from: ");
	                    String loadFileName = scanner.nextLine();
	                    librarySystem.loadLibraryStateFromFile(loadFileName);
	                    System.out.println("Library state loaded from file successfully!");
	                    break;
	                case 6:
	                    System.out.print("Enter the file name to borrow: ");
	                    String title1 = scanner.nextLine();
	                    try{if(librarySystem.borrowBook(title1)) {
	                    	System.out.println("Borrow the book successfully!");}
	                    	else {
	                    		System.out.println("Borrow failed");
	                    	};}
	                    catch(NullPointerException e) {
	                    	System.out.println("book not found!");
	                    }
	                    
	                    break;
	                case 7:
	                    System.out.print("Enter the file name to return: ");
	                    String title2 = scanner.nextLine();
	                    try{librarySystem.returnBook(title2);}
	                    catch(NullPointerException e) {
	                    	System.out.println("book not found!");
	                    }
	                    break;
	                case 8:
	                    System.out.println("Exiting the Library Management System. Goodbye!");
	                    System.exit(0);
	                    break;
	                default:
	                    System.out.println("Invalid choice. Please enter a number between 1 and 6.");
	            }
	        }
	    }
	}


