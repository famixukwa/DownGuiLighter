package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class PopupWindowView extends Application{
	Stage primaryStage = new Stage();
	EBook eBook;
	public PopupWindowView(EBook eBook) {
		this.eBook= eBook;
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
			loader.setLocation(Main.class.getResource("PopupWindow.fxml"));
			Parent root =  loader.load();
			PopupWindowController controller = loader.getController();
			controller.addHighlightToList(eBook);
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
}
