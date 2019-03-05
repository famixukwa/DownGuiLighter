package controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import model.EBook;
import model.ModelInterface;
import model.RetrievePersistanceService;
import model.RetrievePersistanceService.Mode;

public class BookArchiveTabController {

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private AnchorPane anchorPane;

	@FXML
	private TableView<EBook> bookArchiveTable;

	@FXML
	private TableColumn<EBook, String> bookTitleColumn;

	@FXML
	private TableColumn<EBook, String> highlightsColumn;

	@FXML
	public void trigguerPopup(int selectedBook) {
		RetrievePersistanceService task = new RetrievePersistanceService(Mode.EBOOK, selectedBook);
		task.start();
	}

	@FXML
	void initialize() {
		assert bookArchiveTable != null : "fx:id=\"bookArchiveTable\" was not injected: check your FXML file 'BookArchiveTab.fxml'.";
		RetrievePersistanceService task = new RetrievePersistanceService(Mode.BEAN);
		task.start();
		ObservableList<EBook> observableList=ModelInterface.getEbookObservableList();
		ObservableList<EBook> data=observableList;
		bookTitleColumn.setCellValueFactory(new PropertyValueFactory<EBook,String>("bookTitle"));
		highlightsColumn.setCellValueFactory(new PropertyValueFactory<EBook,String>("numberHighlightsFound"));
		bookArchiveTable.setItems(data);
		bookArchiveTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			if (newSelection != null) {
				int selectedBook=bookArchiveTable.getSelectionModel().selectedItemProperty().getValue().getEbookId();
				System.out.println(selectedBook);
				trigguerPopup(selectedBook);
			}
		});
		bookTitleColumn.prefWidthProperty().bind(anchorPane.widthProperty().divide(1.3));
		highlightsColumn.prefWidthProperty().bind(bookTitleColumn.prefWidthProperty().divide(3));
		
		assert bookTitleColumn != null : "fx:id=\"bookTitleColumn\" was not injected: check your FXML file 'BookArchiveTab.fxml'.";
		assert highlightsColumn != null : "fx:id=\"highlightsColumn\" was not injected: check your FXML file 'BookArchiveTab.fxml'.";
	}
}


