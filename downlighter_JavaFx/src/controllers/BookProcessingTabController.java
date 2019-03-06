package controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import model.BookProcess;
import model.InputHandler;
import model.ModelInterface;

public class BookProcessingTabController {
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
	private Label titleLegend;
	@FXML
	private Label authorLegend;
	@FXML
	private Label publisherLegend;
	@FXML
	private Label title;
	@FXML
	private Label author;
	@FXML
	private Label publisher;
	@FXML
	private ImageView cover;
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
		if (highlightFile!=null&ebookFile!=null) {
			pressed=true;
			BookProcess processedBook=new BookProcess();
			processedBook.messagesProperty().addListener((ObservableValue<? extends String> observable, String oldvalue, String newValue )-> {
				messagesWindow.appendText(newValue);
			});
			processedBook.coverPathProperty().addListener((ObservableValue<? extends String> observable, String oldvalue, String newValue )-> {
				Image image = null;
				try {
					image = new Image(new FileInputStream(newValue));
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				cover.setImage(image);
				cover.setFitWidth(100);
				cover.setPreserveRatio(true);
				cover.setSmooth(true);
			});
			ModelInterface.authorProperty().addListener((ObservableValue<? extends String> observable, String oldvalue, String newValue )-> {
				Platform.runLater(
						() -> {
							authorLegend.setText("Author:");
							author.setText(newValue);
						}
						);

			});
			ModelInterface.bookTitlePProperty().addListener((ObservableValue<? extends String> observable, String oldvalue, String newValue )-> {
				Platform.runLater(
						() -> {
							titleLegend.setText("Book Title:");
							title.setText(newValue);
						}
						);

			});
			ModelInterface.publisherProperty().addListener((ObservableValue<? extends String> observable, String oldvalue, String newValue )-> {
				Platform.runLater(
						() -> {
							publisherLegend.setText("Publisher:");
							publisher.setText(newValue);
						}
						);

			});
			processedBook.start();



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
