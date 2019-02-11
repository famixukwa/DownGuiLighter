package application;


import java.io.File;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

public class First_tab_controller {
	Window controllerStage;
	boolean pressed=false;
	private File highlightFile;
	private File ebookFile;
	
	public void setStage(Stage primaryStage) {
		Stage controllerStage=primaryStage;
	}
	public void setBindings() {
		String value=ebookSelected.toString();
		ebookSelected.setText(value);
	}
	@FXML
	private BorderPane borderpane;
	@FXML
	private AnchorPane base_tab_1;

	@FXML
	private Button choose_ebook;
	@FXML
	private TextField ebookSelected;
	@FXML
	private Button Choose_highlight_HTML;

	@FXML
	private TextField highlightsSelected;

	@FXML
	private TextArea messagesWindow;

	@FXML
	void chooseFileEbook(ActionEvent event) {
		FileChooser fileChooser = new FileChooser();
		FileChooser.ExtensionFilter epubExtFilter = new FileChooser.ExtensionFilter("EPUB files (*.epub)", "*.epub");
		fileChooser.getExtensionFilters().add(epubExtFilter);
		ebookFile = fileChooser.showOpenDialog(controllerStage);
		InputHandler.setEbookFile(ebookFile);
		String value=ebookFile.getAbsolutePath();
		ebookSelected.setText(value);
	}

	@FXML
	void chooseFileHighlights(ActionEvent event) {
		FileChooser fileChooser = new FileChooser();
		FileChooser.ExtensionFilter htmlExtFilter = new FileChooser.ExtensionFilter("HTML files (*.html)", "*.html");
		fileChooser.getExtensionFilters().add(htmlExtFilter);
		highlightFile = fileChooser.showOpenDialog(controllerStage);
		InputHandler.setHighlights(highlightFile);
		String value=highlightFile.getAbsolutePath();
		highlightsSelected.setText(value);
	}
	@FXML
	void matchHighlights() {
		
		if (highlightFile!=null&ebookFile!=null&pressed==false) {
			pressed=true;
			BookProcess processedBook=new BookProcess();
			processedBook.messagesProperty().addListener(new ChangeListener<String>() {
				@Override
				public void changed(ObservableValue<? extends String> observable, String oldvalue, String newValue ) {
					messagesWindow.appendText(newValue);
				}
			});
		//	processedBook.searchReplaceInBook(processedBook.getEBook());
		}
		
	}

	@FXML
	void updateMessages () {

	}
	@FXML
    void initialize() {
        assert borderpane != null : "fx:id=\"borderpane\" was not injected: check your FXML file 'First_tab.fxml'.";
        assert base_tab_1 != null : "fx:id=\"base_tab_1\" was not injected: check your FXML file 'First_tab.fxml'.";
        assert choose_ebook != null : "fx:id=\"choose_ebook\" was not injected: check your FXML file 'First_tab.fxml'.";
        assert ebookSelected != null : "fx:id=\"ebookSelected\" was not injected: check your FXML file 'First_tab.fxml'.";
        assert highlightsSelected != null : "fx:id=\"highlightsSelected\" was not injected: check your FXML file 'First_tab.fxml'.";
        assert Choose_highlight_HTML != null : "fx:id=\"Choose_highlight_HTML\" was not injected: check your FXML file 'First_tab.fxml'.";
        assert messagesWindow != null : "fx:id=\"messagesWindow\" was not injected: check your FXML file 'First_tab.fxml'.";

    }

}
