package model;
/**
 * 
 */

import java.io.IOException;
import application.Main;
import application.PopupWindow;
import controllers.PopupWindowController;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
/**
 * this class acts as an interface making accessible in both directions the communication between model and gui
 *
 */
public  class  ModelConnector {
	private static Highlight pastHighlight;
	private static ObservableList<EBook> ebookObservableList= FXCollections.observableArrayList();
	
	/**
	 * metadata properties
	 */
	private static StringProperty coverPath= new SimpleStringProperty();
	private static StringProperty bookTitleP= new SimpleStringProperty();
	private static StringProperty author= new SimpleStringProperty();
	private static StringProperty description= new SimpleStringProperty();
	private static StringProperty publisher= new SimpleStringProperty();
	private static ObservableList<Highlight> highlightsFound=FXCollections.observableArrayList();
	private static ObservableList<Highlight> highlightsFoundArchive=FXCollections.observableArrayList();
	private static StringProperty messages=new SimpleStringProperty("Begin");
	
	
	static int numberHighlightsFound;
	
	//progress Bar
	private static DoubleProperty progress= new SimpleDoubleProperty();
	
	//stores a temporal book while making search and replace
	public static Highlight getPastHighlight() {
		return pastHighlight;
	}
	
	// connection  between processBook, processingTab and popupWindow
	
	

	/**
	 * it creates a popupview where each book shows it highlights in the text. It is triggered by the process tab and the archive tab
	 * @param ebook the book being processed
	 */
	public static void popupWindowView(EBook eBook) {
		PopupWindow popupWindow=new PopupWindow(eBook, highlightsFound);
	}
	
	/**
	 * 
	 * point of connection between bookProcess and the archive table
	 */
	public static ObservableList<EBook> getEbookObservableList() {
		return ebookObservableList;
	}
	
	/**
	 * adds the ebook to an observable list later used by the archive tab to show the ebooks previously analyzed
	 * @param eBook
	 */
	public static void addBookToObservable(EBook eBook) {
		ebookObservableList.add(eBook);
	}
//setters and getters
	public static StringProperty bookTitlePProperty() {
		return bookTitleP;
	}
	

	public static String getBookTitleP() {
		return bookTitleP.toString();
	}
	

	public static void setBookTitleP(final String bookTitleP) {
		ModelConnector.bookTitlePProperty().set(bookTitleP);
	}
	
	
	

	public static StringProperty authorProperty() {
		return ModelConnector.author;
	}
	

	public static String getAuthor() {
		return ModelConnector.authorProperty().get();
	}
	

	public static void setAuthor(final String author) {
		ModelConnector.authorProperty().set(author);
		System.out.println("text : "+author);
	}
	
	
	
	

	public static StringProperty descriptionProperty() {
		return ModelConnector.description;
	}
	

	public static String getDescription() {
		return ModelConnector.descriptionProperty().get();
	}
	

	public static void setDescription(final String description) {
		ModelConnector.descriptionProperty().set(description);
	}
	

	public static StringProperty publisherProperty() {
		return ModelConnector.publisher;
	}
	

	public static String getPublisher() {
		return ModelConnector.publisherProperty().get();
	}
	

	public static void setPublisher(final String publisher) {
		ModelConnector.publisherProperty().set(publisher);
	}

	public static StringProperty coverPathProperty() {
		return ModelConnector.coverPath;
	}
	

	public static String getCoverPath() {
		return ModelConnector.coverPathProperty().get();
	}
	

	public static void setCoverPath(final String coverPath) {
		ModelConnector.coverPathProperty().set(coverPath);
	}

	public static DoubleProperty progressProperty() {
		return ModelConnector.progress;
	}
	

	public static double getProgress() {
		return ModelConnector.progressProperty().get();
	}
	

	public static  void setProgress(final double progress) {
		ModelConnector.progressProperty().set(progress);
	}

	public static ObservableList<Highlight> getHighlightsFound() {
		return highlightsFound;
	}

	public static void setHighlightsFound(ObservableList<Highlight> highlightsFound) {
		ModelConnector.highlightsFound = highlightsFound;
	}

	public static StringProperty messagesProperty() {
		return ModelConnector.messages;
	}
	

	public static String getMessages() {
		return ModelConnector.messagesProperty().get();
	}
	

	public static void setMessages(final String messages) {
		ModelConnector.messagesProperty().set(messages);
	}

	public static int getNumberHighlightsFound() {
		return numberHighlightsFound;
	}

	public static void setNumberHighlightsFound(int numberHighlightsFound) {
		ModelConnector.numberHighlightsFound = numberHighlightsFound;
	}
	
	
	
	
	
	
}
