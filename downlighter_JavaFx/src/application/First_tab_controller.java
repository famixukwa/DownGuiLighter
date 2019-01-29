package application;


import java.io.File;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

public class First_tab_controller {
	Book testBook;
	Window controllerStage;
	
	public void setStage(Stage primaryStage) {
		Stage controllerStage=primaryStage;
	}
	public void setBindings() {
		String value=ebookSelected.toString();
		ebookSelected.setText(value);
	}
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
		FileChooser.ExtensionFilter htmlExtFilter = new FileChooser.ExtensionFilter("HTML files (*.html)", "*.html");
		fileChooser.getExtensionFilters().add(htmlExtFilter);
		File ebookFile = fileChooser.showOpenDialog(controllerStage);
		InputHandler.setEbook(ebookFile);
		String value=ebookFile.getAbsolutePath();
		ebookSelected.setText(value);
	}

	@FXML
	void chooseFileHighlights(ActionEvent event) {
		FileChooser fileChooser = new FileChooser();
		FileChooser.ExtensionFilter htmlExtFilter = new FileChooser.ExtensionFilter("HTML files (*.html)", "*.html");
		fileChooser.getExtensionFilters().add(htmlExtFilter);
		File highlightFile = fileChooser.showOpenDialog(controllerStage);
		InputHandler.setHighlights(highlightFile);
		String value=highlightFile.getAbsolutePath();
		highlightsSelected.setText(value);
	}
	@FXML
	void matchHighlights() {
		Book testBook=new Book();
		testBook.searchReplaceInBook();
		testBook.saveTheHtmlOfBook();
	}
	@FXML
	void updateMessages () {
		
	}
	

}
