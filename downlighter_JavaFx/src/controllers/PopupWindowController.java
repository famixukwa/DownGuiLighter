package controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;
import javafx.scene.web.WebView;
import model.Highlight;

public class PopupWindowController {


	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private WebView bookView;

	@FXML
	private ListView<Highlight> listOfhighlights;

	@FXML
	void initialize() {
		assert bookView != null : "fx:id=\"bookView\" was not injected: check your FXML file 'PopupWindow.fxml'.";
		assert listOfhighlights != null : "fx:id=\"listOfhighlights\" was not injected: check your FXML file 'PopupWindow.fxml'.";

	}

	/**
	 * 
	 * populates the highlight list and creates a listener to show the book on the left webviewer
	 * @param eBook takes the processed ebook as parameter
	 */
	@FXML
	public void addHighlightToList(ObservableList<Highlight>highlightsFound) {
		ObservableList<Highlight> data=highlightsFound;
		listOfhighlights.setItems(data);
		listOfhighlights.setCellFactory(listView ->{
			ListCell <Highlight> cell= new ListCell<Highlight>() {
				private Text text; 
				@Override
				public void updateItem (Highlight item, boolean empty) {
					super.updateItem(item, empty);
					if (item!=null) {
						text=new Text(item.getCleanHilightText());
						text.setWrappingWidth(listOfhighlights.getWidth()-40);
						setGraphic(text);
					}
				}
			};
			return cell;
		}

				);
		// selection listening
		listOfhighlights.getSelectionModel()
		.selectedItemProperty()
		.addListener((observable, oldValue, newValue) -> {
			if (observable != null && observable.getValue() != null) {
				bookView.getEngine().load(observable.getValue().getHighlightUrl());
				System.out.println(observable.getValue().getHighlightUrl());
			}
		});
	}


}
