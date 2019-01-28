package application;


import java.io.File;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

public class First_tab_controller {
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
	void chooseFileEbook(ActionEvent event) {
		FileChooser fileChooser = new FileChooser();
		File ebookFile = fileChooser.showOpenDialog(controllerStage);
		InputHandler.setEbook(ebookFile);
		String value=ebookFile.getAbsolutePath();
		ebookSelected.setText(value);
	}

	@FXML
	void chooseFileHighlights(ActionEvent event) {
		System.out.println("test");
		FileChooser fileChooser = new FileChooser();
		File highlightFile = fileChooser.showOpenDialog(controllerStage);
		InputHandler.setHighlights(highlightFile);
	}

}
