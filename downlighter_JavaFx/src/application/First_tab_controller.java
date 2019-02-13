package application;


import java.io.File;
import java.util.Observable;
import java.util.Observer;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.StringPropertyBase;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
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
	BookProcess processedBook = processedBook=new BookProcess();
	private static StringProperty messages2=new SimpleStringProperty("begin");


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
	private TextField messagesWindow;


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

			//	label.textProperty().bind(connectorTask.messageProperty());		
			//	updateMessagesWindow (processedBook);
			processedBook.start();
		}

	}

	@FXML
	static void createMessages (String s) {
		messages2.set(s);
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
		processedBook.messagesProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldvalue, String newValue ) {
				System.out.println("received text");
				Task task = new Task<Void>() {
					@Override public void run() {
						final int max = 1000000;
						for (int i=1; i<=max; i++) {
							updateProgress(i, max);
						}
						return;
					}
					@Override
					protected Void call() throws Exception {
						// TODO Auto-generated method stub
						return null;
					}
				};
				
				//messagesWindow.textProperty().bind(task.);
				new Thread(task).start();
			}
		});	



	}
	@FXML
	void updateMessagesWindow (BookProcess processedBook) {
		MessagesObserver mess= new MessagesObserver(processedBook);
		processedBook.addObserver(mess);
		while (pressed=true) {
			messagesWindow.appendText(mess.getMessage());
		}
	}

	public class ConnectorTask extends Task<Boolean> {
		String newValue;

		public ConnectorTask(String newValue) {
			this.newValue=newValue;
		}


		@Override
		protected Boolean call() throws Exception {
			// ... do whatever you need here
			messagesWindow.appendText(newValue);
			// then you call this method to update the TextProperty from the Label that was bound.

			return Boolean.TRUE;
		}


	}

}
