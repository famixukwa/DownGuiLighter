package controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
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
import model.ModelConnector;
/*
 * controller of the processing tab
 */
public class BookProcessingTabController {
	Window controllerStage;
	boolean pressed=false;
	private File highlightFile;
	private File ebookFile;
	private File epubFile;
	private  BookProcess bookProcess;

	public void setStage(Stage primaryStage) {
		Stage controllerStage=primaryStage;
	}
	public void setBindings() {
		String value=ebookSelected.toString();
		ebookSelected.setText(value);
	}
	@FXML
	private Button saveFile;
	@FXML
	private ProgressBar progresBar;
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
	/*
	 * chooses the ebook file to be processed by the app
	 */
	@FXML
	void chooseFileEbook(ActionEvent event) {
		FileChooser fileChooser = new FileChooser();
		FileChooser.ExtensionFilter epubExtFilter = new FileChooser.ExtensionFilter("EPUB files (*.epub)", "*.epub");
		fileChooser.getExtensionFilters().add(epubExtFilter);
		ebookFile = fileChooser.showOpenDialog(controllerStage);
		InputHandler.setEbookFile(ebookFile);
		if (ebookFile!=null) {
			String value=ebookFile.getAbsolutePath();
			ebookSelected.setText(value);
		}

	}
	/*
	 * method to choose the file with the highlights to be mapped
	 */
	@FXML
	void chooseFileHighlights(ActionEvent event) {
		FileChooser fileChooser = new FileChooser();
		FileChooser.ExtensionFilter htmlExtFilter = new FileChooser.ExtensionFilter("HTML files (*.html)", "*.html");
		fileChooser.getExtensionFilters().add(htmlExtFilter);
		highlightFile = fileChooser.showOpenDialog(controllerStage);
		InputHandler.setHighlights(highlightFile);
		if (highlightFile!=null) {
			String value=highlightFile.getAbsolutePath();
			highlightsSelected.setText(value);
		}
	}
	/*
	 * method that opens a dialog to choose a path to save the resulting ebook file with a list of links that points to the highlights found in the book
	 */
	@FXML
	void saveEpubFile(ActionEvent event) {
		if (bookProcess!=null&&bookProcess.getPathHandler().isIsepubfileWHighlightsCreated()) {
			FileChooser fileChooser = new FileChooser();
			FileChooser.ExtensionFilter epubFilter = new FileChooser.ExtensionFilter("Epub files (*.epub)", "*.epub");
			fileChooser.getExtensionFilters().add(epubFilter);
			fileChooser.setInitialFileName(bookProcess.getPathHandler().getFileName());
			epubFile = fileChooser.showSaveDialog(controllerStage);
			if (epubFile!=null) {
				String value=epubFile.getAbsolutePath();
				try {
					System.out.println(bookProcess.getPathHandler().getEpubfileWHighlights().toString());
					if (epubFile.exists()) {
						epubFile.delete();
					}
					Files.copy(bookProcess.getPathHandler().getEpubfileWHighlights(), epubFile.toPath());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		/*
		 * method that begins the process of matching the highlights in the book
		 */
	}
	@FXML
	void matchHighlights() {
		if (highlightFile!=null&ebookFile!=null) {
			pressed=true;
			BookProcess processedBook=new BookProcess();
			ModelConnector.messagesProperty().addListener((ObservableValue<? extends String> observable, String oldvalue, String newValue )-> {
				messagesWindow.appendText(newValue);
			});
			
			ModelConnector.coverPathProperty().addListener((ObservableValue<? extends String> observable, String oldvalue, String newValue )-> {
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
			ModelConnector.authorProperty().addListener((ObservableValue<? extends String> observable, String oldvalue, String newValue )-> {
				Platform.runLater(
						() -> {
							authorLegend.setText("Author:");
							author.setText(newValue);
						}
						);

			});
			ModelConnector.bookTitlePProperty().addListener((ObservableValue<? extends String> observable, String oldvalue, String newValue )-> {
				Platform.runLater(
						() -> {
							titleLegend.setText("Book Title:");
							title.setText(newValue);
						}
						);

			});
			ModelConnector.publisherProperty().addListener((ObservableValue<? extends String> observable, String oldvalue, String newValue )-> {
				Platform.runLater(
						() -> {
							publisherLegend.setText("Publisher:");
							publisher.setText(newValue);
						}
						);
			});
			processedBook.start();
			this.bookProcess=processedBook;
		}

	}

	/*
	 * method that produces a listener that sets the progress of the progress bar
	 */
	@FXML
	void initialize() {
		assert borderpane != null : "fx:id=\"borderpane\" was not injected: check your FXML file 'First_tab.fxml'.";
		assert base_tab_1 != null : "fx:id=\"base_tab_1\" was not injected: check your FXML file 'First_tab.fxml'.";
		assert choose_ebook != null : "fx:id=\"choose_ebook\" was not injected: check your FXML file 'First_tab.fxml'.";
		assert ebookSelected != null : "fx:id=\"ebookSelected\" was not injected: check your FXML file 'First_tab.fxml'.";
		assert highlightsSelected != null : "fx:id=\"highlightsSelected\" was not injected: check your FXML file 'First_tab.fxml'.";
		assert Choose_highlight_HTML != null : "fx:id=\"Choose_highlight_HTML\" was not injected: check your FXML file 'First_tab.fxml'.";
		assert messagesWindow != null : "fx:id=\"messagesWindow\" was not injected: check your FXML file 'First_tab.fxml'.";
		ModelConnector.progressProperty().addListener((ObservableValue<? extends Number> observable, Number oldvalue, Number newValue )-> {
			Platform.runLater(
					() -> {
						progresBar.setProgress(ModelConnector.getProgress());
					}
					);

		});
	}

}
