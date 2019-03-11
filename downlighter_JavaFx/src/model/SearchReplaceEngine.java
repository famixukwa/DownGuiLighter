package model;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * this class is in charge of searching the highlights in the book
 * @Param eBook the book being analyzed 
 * @Param pathHandler class in charge of handling the paths of the application
 */
public class SearchReplaceEngine {
	private EBook eBook;
	private Highlight highlight;
	private PathHandler pathHandler;
	private ArrayList<InformedFile> htmlfiles= new ArrayList<InformedFile>();
	private ArrayList<Highlight> highlightList=new ArrayList<>();
	private List<Element> highlightSnippets=InputHandler.getHighlightFileSnippets();
	ArrayList<InformedFile> copyOfHtmlFiles;

	private int numberHighlightsFound=0;

	public SearchReplaceEngine(EBook eBook, PathHandler pathHandler) {
		super();
		this.eBook = eBook;
		this.pathHandler = pathHandler;
		listExtractedFiles();
		createHighlights(highlightSnippets);
		copyOfHtmlFiles=new ArrayList<InformedFile>(htmlfiles);
		searchReplaceHighlights(eBook);
	}


	/**
	 * makes a list of the extracted files
	 */
	private void listExtractedFiles() {	
		XmlExtractor xmlExtractor= new XmlExtractor(pathHandler.getOpfPath().toString());
		ArrayList<Path> filesPath=xmlExtractor.getHmlFilesPath();
		for (int i = 0; i < filesPath.size(); i++) {
			Path fullPath=Paths.get(pathHandler.getBookPath().toString(), filesPath.get(i).toString());
			InformedFile file= new InformedFile(fullPath.toString());
			file.setOrderIndex(i);
			htmlfiles.add(file);
		}
	}
	/**
	 * creates a list of highlight objects from the texts extracted from the highlights file
	 *
	 */
	public void createHighlights(List<Element> highlightSnippets) {
		addMessagesToDisplay("Begining process\n");
		for (int i = 0; i < highlightSnippets.size(); i++) {
			String highligghtText=highlightSnippets.get(i).text();
			highlight=new Highlight(highligghtText);
			highlightList.add(highlight);
		}
		addMessagesToDisplay("Highlights extracted! \n");
	}
	/**
	 * extracts the html document of the file
	 */
	private Document extractDocumentFromFile(InformedFile file) {
		InformedFile htmlFile = file;
		Document fileHtmlDocument = null;
		try {
			fileHtmlDocument = Jsoup.parse(htmlFile, "UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fileHtmlDocument;
	}

	/**
	 * uses searchReplaceInHtml in all the book files
	 */
	public void searchReplaceHighlights(EBook eBook) {
		//sends a list of the extracted files to ebook


		for (int i = 0; i < highlightList.size(); i++) {
			//set progress
			Double d =0.7/highlightList.size();
			ModelInterface.setProgress(i*d+0.2);
			//Document htmlDoc=extractDocumentFromFile(htmlFile);
			Boolean highlightFoundBoolean=searchReplaceInHtml(eBook,highlightList.get(i),copyOfHtmlFiles);
			if (highlightFoundBoolean) {
				addMessagesToDisplay("highlight "+i+" found"+"\n");
			}
			else {
				addMessagesToDisplay("highlight "+i+" not found!! :("+"\n");
			}
		}

	}

	/**
	 * Method that Search the searcheable text in the html text and replaces it with the same text surrounded with 
	 * an underlined tag.
	 *
	 */
	private boolean searchReplaceInHtml(EBook eBook,Highlight highlight,ArrayList<InformedFile> copyOfHtmlFiles) {
		//gets the list of files
		boolean highlightFoundBoolean=false;
		for (int i = 0; i < copyOfHtmlFiles.size(); i++) {
			InformedFile file=copyOfHtmlFiles.get(i);
			Document htmlDoc=extractDocumentFromFile(file);
			String searcheable=highlight.getSearchable();
			//search the highlights text in the book
			Elements founds = htmlDoc.select(searcheable);
			if (founds.size()!=0) {
				highlightFoundBoolean=true;
				Element found= founds.first();
				//informs what is found 
				informer (found,file, highlight,copyOfHtmlFiles);
				//counts the highlights
				numberHighlightsFound++;
				ModelInterface.setNumberHighlightsFound(numberHighlightsFound);
				//saved the highlight as past highlight for optimization purposes
				ModelInterface.setPastHighlight(highlight);
				//replaces the text in the book with the bookmarked and highlighted text
				textReplacer(found, highlight);
				//saves the modified file
				saveTheHtmlOfBook(htmlDoc,file,eBook);
				//	optimizer(highlight,htmlfiles);
				optimizer(highlight,file);
				break;
			}

		}
				if (!highlightFoundBoolean&highlight.getSentences().size()>0) {
					System.out.println("test sentence begin");
					SentenceHighlight sentenceHighlight= new SentenceHighlight(highlight.getHighligghtText());
					sentenceSearchReplacer(eBook,sentenceHighlight,highlightFoundBoolean);
				}
		if (!highlightFoundBoolean) {

		}
		return highlightFoundBoolean;
	}
	/**
	 * 
	 *Optimizes the search algorithm deleting the files on the list that have been already explored
	 */
	private void optimizer(Highlight highlight,InformedFile file) {
		int value=highlight.getHighlightFileIndex()-copyOfHtmlFiles.get(0).getOrderIndex();
		if (value>0) {
			for (int i = 0; i < value; i++) {
				copyOfHtmlFiles.remove(0);
			}
		}
			
	}
	/**
	 * Helper method for searchReplaceInHtml that searches in sentence mode for the case the highlight contains sentences from two different paragraphs
	 * 
	 */
	private void sentenceSearchReplacer(EBook eBook,SentenceHighlight sentenceHighlight, Boolean highlightFoundBoolean ) {
		System.out.println("entering sentence mode");
		if (sentenceHighlight.getSentences().size()>1) {
			for (int i = 0; i < sentenceHighlight.getSentences().size(); i++) {
				Sentence sentence=sentenceHighlight.getSentences().get(i);
				highlightFoundBoolean=sentenceSearchReplaceInHtml(eBook,sentenceHighlight,sentence);
			}
		}
		else {
			highlightFoundBoolean=null;
		}

	}
	/**
	 * Helper method for sentenSearchReplacer that searches in sentence mode on the files for the case the highlight contains sentences from two different paragraphs
	 * 
	 */
	private boolean sentenceSearchReplaceInHtml(EBook eBook,SentenceHighlight sentenceHighlight, Sentence sentence) {
		//gets the list of files
		boolean highlightFoundBoolean=false;
		for (int j = 0; j < copyOfHtmlFiles.size(); j++) {
			InformedFile file=copyOfHtmlFiles.get(j);
			Document htmlDoc=extractDocumentFromFile(file);
			String searcheable=sentence.getSearchable();
			//search the highlights text in the book
			Elements founds = htmlDoc.select(searcheable);
			if (founds.size()!=0) {
				highlightFoundBoolean=true;
				Element found= founds.first();
				if (sentence.getHighligghtText().equals(sentenceHighlight.getFirstSentence().getHighligghtText())) {
					//informs what is found 
					informer (found,file, sentenceHighlight,copyOfHtmlFiles);
					//counts the highlights
					numberHighlightsFound++;
					highlightFoundBoolean=true;
					optimizer(sentenceHighlight, file);
				}
				//replaces the text in the book with the bookmarked and highlighted text
				textReplacer(found, sentence);
				//saves the modified file
				saveTheHtmlOfBook(htmlDoc,file,eBook);
			}
		}
		if (!highlightFoundBoolean) {
		}
		//add message to display and passes the number of highlights to eBook

		return highlightFoundBoolean;
	}
	/**
	 * Helper method for searchReplaceInBook that takes the found element and the i integer for the for loop
	 *replaces the text with the text modified with highlight tags.
	 *@param found are the paragraphs that in the book HTML file includes
	 *the searched text 
	 *@param i is the counter for the searchReplaceInBook for loop.
	 */

	private void textReplacer(Element found,Highlight highlight) {
		String textHighlighted=highlight.getHighlightedText();
		String textToModify=found.html();
		String modifiedText=textToModify.replace(highlight.getHighligghtText(), textHighlighted);
		found.html(modifiedText);
	}

	/**
	 * 
	 *Optimizes the search algorithm deleting the files on the list that have been already explored in the case of sentence search
	 */
	private void sentenceOptimizer(Highlight highlight,ArrayList<InformedFile> htmlfiles) {
		int value=highlight.getHighlightFileIndex();
		if (value>1) {
			int readFiles=highlight.getHighlightFileIndex()-1;
			for (int j = 0; j < readFiles; j++) {
				htmlfiles.remove(j);
			}
		}
	}
	/**
	 * 
	 *informs ebook and highlight of the information found on the search
	 */
	public void informer (Element found,InformedFile file, Highlight highlight,  ArrayList<InformedFile> htmlfiles) {
		//saves the file where the highlight was found
		highlight.setContainerFile(file);
		//produces the links
		highlight.constructHighlightLink();
		//adds highlight to observable for the gui:
		ModelInterface.getHighlightsFound().add(highlight);
		//saves the paragraph where the highlight was found
		highlight.setHighlightLocationInHtml(found.elementSiblingIndex());
		//saves the index file where it was found
		highlight.setHighlightFileIndex(file.getOrderIndex());
		//saves the highlights in book
		saveHighlightInEBook(highlight);

	}
	/**
	 * saves the highlights in the book
	 *
	 */
	private void saveHighlightInEBook (Highlight highlight) {
		eBook.setHighlightsFound(highlight);
		highlight.eBook=eBook;
	}
	/**
	 * add messages to display in the gui
	 */
	void addMessagesToDisplay(String s) {
		ModelInterface.setMessages(s);
	}
	/**
	 * Saves the book calling the OutputHandler
	 *
	 */
	public void saveTheHtmlOfBook(Document htmlDokument, File file,EBook eBook) {
		OutputHandler outputHandler= new OutputHandler(eBook,file);
		outputHandler.saveHtml(htmlDokument);
	}

}
