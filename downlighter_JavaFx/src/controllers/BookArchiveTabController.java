package controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.EBook;
import model.Highlight;
import model.RetrievePersistanceService;
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
		RetrievePersistanceService task = new RetrievePersistanceService();
		Thread th = new Thread(task);
		th.setDaemon(true);
		th.start();
		ObservableList<EBook> observableList=task.getEbookObservableList();
		ObservableList<EBook> data=observableList;
		bookTitleColumn.setCellValueFactory(new PropertyValueFactory<EBook,String>("bookTitle"));
		highlightsColumn.setCellValueFactory(new PropertyValueFactory<EBook,String>("numberHighlightsFound"));
		bookArchiveTable.setItems(data);
		bookArchiveTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
		    if (newSelection != null) {
		       System.out.println(bookArchiveTable.getSelectionModel().selectedItemProperty().getValue().getEbookId()+" selected");
		    }
		});
		//bookArchiveTable.getColumns().addAll(bookTitleColumn,highlightsColumn);
		assert bookTitleColumn != null : "fx:id=\"bookTitleColumn\" was not injected: check your FXML file 'BookArchiveTab.fxml'.";
		assert highlightsColumn != null : "fx:id=\"highlightsColumn\" was not injected: check your FXML file 'BookArchiveTab.fxml'.";

		
	}
}


