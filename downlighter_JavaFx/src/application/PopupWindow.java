package application;

import java.io.IOException;

import controllers.PopupWindowController;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.EBook;
import model.Highlight;
/**
 * This class represents the popup triggered by the book processed tab and the archive tab
 *
 */
public class PopupWindow extends Application{
	private EBook eBook;
	private ObservableList<Highlight> highlightsFound;
	public PopupWindow(EBook eBook, ObservableList<Highlight> highlightsFound) {
		super();
		this.eBook = eBook;
		this.highlightsFound = highlightsFound;
		start(primaryStage);
	}
	Stage primaryStage= new Stage();
	@Override
	public void start(Stage primaryStage) {
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
}
