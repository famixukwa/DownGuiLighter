package model;
/**
 * this class acts as an interface making accessible in both directions the communication between model and gui
 */

import java.io.IOException;
import application.Main;
import controllers.PopupWindowController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public  class  ModelInterface {
	private static Highlight pastHighlight;
	private static ObservableList<EBook> ebookObservableList= FXCollections.observableArrayList();
	public static Highlight getPastHighlight() {
		return pastHighlight;
	}
	
	// connection  between processBook, processingTab and popupWindow
	
	/**
	 * it creates a popupview on the gui
	 * @param highlightsFound
	 */
	public static void popupWindowView(ObservableList<Highlight>highlightsFound) {
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
		Scene scene = new Scene(root);
//		scene.getStylesheets().add(getClass().getResource("/application/application.css").toExternalForm());
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
	
}
