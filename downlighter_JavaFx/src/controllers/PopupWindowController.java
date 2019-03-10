package controllers;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import model.EBook;
import model.Highlight;
import model.ModelInterface;

public class PopupWindowController {
	@FXML
	private ImageView cover;
	@FXML
	private Label title;

	@FXML
	private Label author;

	@FXML
	private Label publisher;
	@FXML
	private TextArea description;

	@FXML
	private AnchorPane splitPaneAnchorPane;
	@FXML
	private SplitPane splitPane;
	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private WebView bookView;

	@FXML
	private ListView<Highlight> listOfhighlights;

	@FXML
	public void addMetadata(EBook eBook) {
		if (eBook.getCoverPath()!=null) {
			Image image = null;
			try {
				image = new Image(new FileInputStream(eBook.getCoverPath()));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			cover.setImage(image);
			cover.setFitWidth(100);
			cover.setPreserveRatio(true);
			cover.setSmooth(true);
		}
		if (eBook.getAuthor()!=null) {
			author.setText(eBook.getAuthor());
		}
		if (eBook.getAuthor()!=null) {
			author.setText(eBook.getAuthor());
		}
		if (eBook.getBookTitleP()!=null) {
			title.setText(eBook.getBookTitleP());
		}
		if (eBook.getPublisher()!=null) {
			publisher.setText(eBook.getPublisher());
		}
		if (eBook.getDescription()!=null) {
			description.setText(eBook.getDescription());
		}
	}
	public void initialize() {
		assert bookView != null : "fx:id=\"bookView\" was not injected: check your FXML file 'PopupWindow.fxml'.";
		assert listOfhighlights != null : "fx:id=\"listOfhighlights\" was not injected: check your FXML file 'PopupWindow.fxml'.";
		//splitPaneAnchorPane.prefHeightProperty().bind(splitPaneAnchorPane.heightProperty());

	}

	/**
	 * 
	 * populates the highlight list and creates a listener to show the book on the left webviewer
	 * @param eBook takes the processed ebook as parameter
	 */
	@FXML
	public void addHighlightToList(ObservableList<Highlight>highlightsFound) {
		ObservableList<Highlight> data=highlightsFound;
		System.out.println("numero de highlights en popup"+data.size());
		listOfhighlights.setItems(data);
		listOfhighlights.setCellFactory(listView ->{
			ListCell <Highlight> cell= new ListCell<Highlight>() {
				//	private Text text; 
				@Override
				public void updateItem (Highlight item, boolean empty) {
					super.updateItem(item, empty);
					if (item!=null) {
						Label listContent = new Label();
						setGraphic(listContent);
						listContent.setText(item.getHighligghtText());
						listContent.setWrapText(true);
						listContent.prefWidthProperty().bind(listOfhighlights.widthProperty().divide(1.1));
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
