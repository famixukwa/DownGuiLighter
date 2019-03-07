package model;
/**
 * this class acts as an interface making accessible in both directions the communication between model and gui
 */

import java.io.IOException;
import application.Main;
import controllers.PopupWindowController;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.StringPropertyBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public  class  ModelInterface {
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
	
	//progress Bar
	private static DoubleProperty progress= new SimpleDoubleProperty();
	
	//stores a temporal book while making search and replace
	public static Highlight getPastHighlight() {
		return pastHighlight;
	}
	
	// connection  between processBook, processingTab and popupWindow
	
	

	/**
	 * it creates a popupview on the gui
	 * @param highlightsFound
	 */
	public static void popupWindowView(ObservableList<Highlight>highlightsFound, EBook eBook) {
		Stage primaryStage = new Stage();
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("/views/PopupWindow.fxml"));
		Parent root = null;
		try {
			root = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		PopupWindowController controller = loader.getController();
		controller.addHighlightToList(highlightsFound);
		controller.addMetadata(eBook);
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	/**
	 * 
	 * point of connection between bookProcess and the archive table
	 */
	public static ObservableList<EBook> getEbookObservableList() {
		return ebookObservableList;
	}
	

	//temporal bookprocess variable for the search algorithm
	public static void setPastHighlight(Highlight pastHighlight) {
		ModelInterface.pastHighlight = pastHighlight;
	}
	
	public static void addBookToObservable(EBook eBook) {
		ebookObservableList.add(eBook);
	}

	

	public static StringProperty bookTitlePProperty() {
		return bookTitleP;
	}
	

	public static String getBookTitleP() {
		return bookTitleP.toString();
	}
	

	public static void setBookTitleP(final String bookTitleP) {
		ModelInterface.bookTitlePProperty().set(bookTitleP);
	}
	
	
	

	public static StringProperty authorProperty() {
		return ModelInterface.author;
	}
	

	public static String getAuthor() {
		return ModelInterface.authorProperty().get();
	}
	

	public static void setAuthor(final String author) {
		ModelInterface.authorProperty().set(author);
		System.out.println("text : "+author);
	}
	
	
	
	

	public static StringProperty descriptionProperty() {
		return ModelInterface.description;
	}
	

	public static String getDescription() {
		return ModelInterface.descriptionProperty().get();
	}
	

	public static void setDescription(final String description) {
		ModelInterface.descriptionProperty().set(description);
	}
	

	public static StringProperty publisherProperty() {
		return ModelInterface.publisher;
	}
	

	public static String getPublisher() {
		return ModelInterface.publisherProperty().get();
	}
	

	public static void setPublisher(final String publisher) {
		ModelInterface.publisherProperty().set(publisher);
	}

	public static StringProperty coverPathProperty() {
		return ModelInterface.coverPath;
	}
	

	public static String getCoverPath() {
		return ModelInterface.coverPathProperty().get();
	}
	

	public static void setCoverPath(final String coverPath) {
		ModelInterface.coverPathProperty().set(coverPath);
	}

	public static DoubleProperty progressProperty() {
		return ModelInterface.progress;
	}
	

	public static double getProgress() {
		return ModelInterface.progressProperty().get();
	}
	

	public static  void setProgress(final double progress) {
		ModelInterface.progressProperty().set(progress);
	}
	
	
	
	
	
}
