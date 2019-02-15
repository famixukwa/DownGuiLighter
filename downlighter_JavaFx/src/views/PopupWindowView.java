package views;

import application.Main;
import controllers.PopupWindowController;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Highlight;

public class PopupWindowView extends Application{
	Stage primaryStage = new Stage();
	ObservableList<Highlight>highlightsFound;
	
	public PopupWindowView(ObservableList<Highlight>highlightsFound) {
		this.highlightsFound= highlightsFound;
		try {
			start(primaryStage);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	@Override
	public void start(Stage primaryStage) throws Exception {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("/views/PopupWindow.fxml"));
			Parent root =  loader.load();
			PopupWindowController controller = loader.getController();
			controller.addHighlightToList(highlightsFound);
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("/application/application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
}
