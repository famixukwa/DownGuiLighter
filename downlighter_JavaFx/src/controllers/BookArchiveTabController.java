package controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import application.Main;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.EBook;
import model.Highlight;
import model.RetrievePersistanceService;
import model.RetrievePersistanceService.Mode;
import model.SavePersistanceService;

public class BookArchiveTabController {

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private TableView<EBook> bookArchiveTable;

	@FXML
	private TableColumn<EBook, String> bookTitleColumn;

	@FXML
	private TableColumn<EBook, String> highlightsColumn;

	@SuppressWarnings("unchecked")
	@FXML
	public void trigguerPopup(int selectedBook) {
		RetrievePersistanceService task = new RetrievePersistanceService(Mode.EBOOK, selectedBook);
		Thread th = new Thread(task);
		th.setDaemon(true);
		th.start();
		task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent t) {
				ObservableList<Highlight> highlightsFound=task.getHighlightObservableList();
				PopupWindowView(highlightsFound);
				
			}
		});
	}


	@FXML
	void PopupWindowView(ObservableList<Highlight>highlightsFound) {
		Stage primaryStage = new Stage();
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("/views/PopupWindow.fxml"));
		Parent root = null;
		try {
			root = loader.load();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		PopupWindowController controller = loader.getController();
		controller.addHighlightToList(highlightsFound);
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("/application/application.css").toExternalForm());
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	@FXML
	public void setBooksColumn(ObservableList<EBook> observableList) {
		ObservableList<EBook> data=observableList;
		bookTitleColumn.setCellValueFactory(new PropertyValueFactory<EBook,String>("bookTitle"));
		highlightsColumn.setCellValueFactory(new PropertyValueFactory<EBook,String>("numberHighlightsFound"));
		bookArchiveTable.setItems(data);
		bookArchiveTable.getColumns().addAll(bookTitleColumn,highlightsColumn);
	}


	@FXML
	void initialize() {
		assert bookArchiveTable != null : "fx:id=\"bookArchiveTable\" was not injected: check your FXML file 'BookArchiveTab.fxml'.";

		RetrievePersistanceService task = new RetrievePersistanceService(Mode.BEAN);
		Thread th = new Thread(task);
		th.setDaemon(true);
		th.start();
		task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent t) {
				ObservableList<EBook> observableList=task.getEbookObservableList();
				ObservableList<EBook> data=observableList;
				bookTitleColumn.setCellValueFactory(new PropertyValueFactory<EBook,String>("bookTitle"));
				highlightsColumn.setCellValueFactory(new PropertyValueFactory<EBook,String>("numberHighlightsFound"));
				bookArchiveTable.setItems(data);
			}
		});

		bookArchiveTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			if (newSelection != null) {
				int selectedBook=bookArchiveTable.getSelectionModel().selectedItemProperty().getValue().getEbookId();
				System.out.println(selectedBook);
				trigguerPopup(selectedBook);
			}
		});
		//bookArchiveTable.getColumns().addAll(bookTitleColumn,highlightsColumn);
		assert bookTitleColumn != null : "fx:id=\"bookTitleColumn\" was not injected: check your FXML file 'BookArchiveTab.fxml'.";
		assert highlightsColumn != null : "fx:id=\"highlightsColumn\" was not injected: check your FXML file 'BookArchiveTab.fxml'.";


	}
}


