package model;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

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
	private ArrayList<InformedFile> copyOfHtmlFiles;
	private int numberHighlightsFound=0;
	boolean isBeingSentenceSearch;

	public SearchReplaceEngine(EBook eBook, PathHandler pathHandler) {
		super();
		this.eBook = eBook;
		this.pathHandler = pathHandler;
		listExtractedFiles();
		createHighlights(highlightSnippets);
		copyOfHtmlFiles=new ArrayList<InformedFile>(htmlfiles);
	}


	/**
	 * makes a list of the extracted files
	 */
	private void listExtractedFiles() {	
		XmlExtractor xmlExtractor= new XmlExtractor(pathHandler.getOpfPath().toString());
		ArrayList<Path> filesPath=xmlExtractor.getHmlFilesPath();
		for (int i = 0; i < filesPath.size(); i++) {
			Path fullPath=Paths.get(pathHandler.getExtractedBook().toString(), filesPath.get(i).toString());
			InformedFile file= new InformedFile(fullPath.toString());
			file.setOrderIndex(i);
			htmlfiles.add(file);
		}
	}
	/**
	 * creates a list of highlight objects from the texts extracted from the highlights file
	 *@param highlightSnippets fragments of texts extracted from the highlights file
	 */
	public void createHighlights(List<Element> highlightSnippets) {
		addMessagesToDisplay("Begining process\n");
		for (int i = 0; i < highlightSnippets.size(); i++) {
			String highligghtText=highlightSnippets.get(i).text();
			highlight=new Highlight(highligghtText);
			highlightList.add(highlight);
		}
		addMessagesToDisplay("Highlights extracted! \n");
		addMessagesToDisplay("Number of highlights in the notebook file  "+highlightSnippets.size()+"\n");
	}
	/**
	 * extracts the html document of the file
	 * @param file it takes the informed file that contains the information of his order in the book
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
	 * @param ebook the book being processed
	 */
	public void searchReplaceHighlights(EBook eBook) {
		boolean isBeingSentenceSearch=false;
		//sends a list of the extracted files to ebook
		for (int i = 0; i < highlightList.size(); i++) {
			//set progress
			Double d =0.7/highlightList.size();
			ModelConnector.setProgress(i*d+0.2);
			//Document htmlDoc=extractDocumentFromFile(htmlFile);
			boolean isHighlightFound=searchReplaceInHtml(eBook,highlightList.get(i),copyOfHtmlFiles,isBeingSentenceSearch);
			if (isHighlightFound) {
				addMessagesToDisplay("highlight "+i+" found"+"\n");
			}
			if(!isBeingSentenceSearch&!isHighlightFound) {
				addMessagesToDisplay("highlight "+i+" not found!! :("+"\n");
				System.out.println("regex de highlight no encontrado: "+highlightList.get(i).getSearchable());
			}
		}

	}

	/**
	 * Method that Search the searcheable text in the html text and replaces it with the same text surrounded with 
	 * an underlined tag.
	 *@param ebook the book being processed
	 *@param highlight the highlight instance with all his methods and information
	 *@param copyOfHtmlFiles a list of the informed files in which to search the highlits
	 *@param isBeingSentenceSearch boolean that indicates that the highlight is being serch in highlight mode to avoid false negative findings
	 */
	private boolean searchReplaceInHtml(EBook eBook,Highlight highlight,ArrayList<InformedFile> copyOfHtmlFiles, boolean isBeingSentenceSearch) {
		//gets the list of files
		boolean isHighlightFound=false;

		for (int i = 0; i < copyOfHtmlFiles.size(); i++) {
			InformedFile file=copyOfHtmlFiles.get(i);
			Document htmlDoc=extractDocumentFromFile(file);
			String searcheable=highlight.getSearchable();
			//search the highlights text in the book
			//System.out.println("looking on file: "+file.getOrderIndex());
			Elements founds = htmlDoc.select(searcheable);
			if (founds.size()!=0) {
				isHighlightFound=true;
				Element found= founds.first();
				//counts the highlights
				numberHighlightsFound++;
				//informs what is found 
				informResults (found,file, highlight,copyOfHtmlFiles);
				//replaces the text in the book with the bookmarked and highlighted text
				textReplace(found, highlight);
				//saves the modified file
				saveTheHtmlOfBook(htmlDoc,file,eBook);
				//	optimizer(highlight,htmlfiles);
				optimize(highlight,file);
				break;
			}

		}
		if (!isHighlightFound&highlight.getSentences().size()>0) {
			isBeingSentenceSearch=true;
			System.out.println("test sentence begin");
			SentenceHighlight sentenceHighlight= new SentenceHighlight(highlight.getHighligghtText(),highlight.getHighlightFileIndex());
			isHighlightFound=sentenceSearchReplace(eBook,sentenceHighlight);
		}

		return isHighlightFound;
	}
	/**
	 * 
	 *Optimizes the search algorithm deleting the files on the list that have been already explored
	 *@param highlight highlight being search
	 *@param informed file with the index in order in the book
	 */
	private void optimize(Highlight highlight,InformedFile file) {
		int value=highlight.getHighlightFileIndex()-copyOfHtmlFiles.get(0).getOrderIndex();
		if (value>0) {
			for (int i = 0; i < value; i++) {
				copyOfHtmlFiles.remove(0);
			}
		}

	}
	/**
	 * Helper method for searchReplaceInHtml that searches in sentence mode for the case the highlight contains sentences from two different paragraphs
	 * @param ebook the book being processed
	 * @param sentenceHighlight the special highlight used in sentence search
	 */
	private boolean sentenceSearchReplace(EBook eBook,SentenceHighlight sentenceHighlight ) {
		System.out.println("entering sentence mode");
		Boolean isHighlightFound=false;
		if (sentenceHighlight.getSentences().size()>1) {
			int foundedSentences=0;
			for (int i = 0; i < sentenceHighlight.getSentences().size(); i++) {
				System.out.println("number of sentences:  "+sentenceHighlight.getSentences().size());
				Sentence sentence=sentenceHighlight.getSentences().get(i);
				boolean isHighlightFoundInsentenceSearchReplaceInHtml=sentenceSearchReplaceInHtml(eBook,sentenceHighlight,sentence);
				if (isHighlightFoundInsentenceSearchReplaceInHtml) {
					isHighlightFound=true;
				}
				System.out.println("encontrado highlight en sentenceSearchReplaceInHtml: "+isHighlightFound);
				//				if (isSentenceFound) {
				//					foundedSentences++;
				//					System.out.println("found sentences:  "+foundedSentences);
				//					if (foundedSentences==sentenceHighlight.getSentences().size()) {
				//						isHighlightFound=true;
				//					}
				//				}
			}
		}
		else {
			isHighlightFound=false;
		}
		return isHighlightFound;

	}
	/**
	 * Helper method for sentenSearchReplacer that searches in sentence mode on the files for the case the highlight contains sentences from two different paragraphs
	 * @param ebook the book being processed
	 * @param sentenceHighlight the special highlight used in sentence search
	 * @param sentence sentences created by splitting the highlight text with "." to be able to search for highlights that are distributed in different paragraphs
	 */
	private boolean sentenceSearchReplaceInHtml(EBook eBook,SentenceHighlight sentenceHighlight, Sentence sentence) {
		//gets the list of files
		boolean isSentenceFound=false;
		for (int j = 0; j < copyOfHtmlFiles.size(); j++) {
			InformedFile file=copyOfHtmlFiles.get(j);
			//System.out.println("looking on file: "+file.getOrderIndex());
			Document htmlDoc=extractDocumentFromFile(file);
			String searcheable=sentence.getSearchable();
			//System.out.println("file:   "+file.getOrderIndex());
			//search the highlights text in the book
			Elements founds = htmlDoc.select(searcheable);
			if (founds.size()!=0) {
				Element found= founds.first();
				if (sentence.getHighligghtText().equals(sentenceHighlight.getFirstSentence().getHighligghtText())) {
					isSentenceFound=true;
					//creates normal highlight from sentenceHighlight
					Highlight persistenceHighlight=new Highlight(sentenceHighlight.getHighligghtText());
					//counts the highlights
					numberHighlightsFound++;
					//informs what is found 
					informResults (found,file, persistenceHighlight,copyOfHtmlFiles);
				}
				//replaces the text in the book with the bookmarked and highlighted text
				textReplace(found, sentence);
				//saves the modified file
				saveTheHtmlOfBook(htmlDoc,file,eBook);
				optimize(sentenceHighlight, file);
				break;
			}
		}

		return isSentenceFound;
	}
	/**
	 * Helper method for searchReplaceInBook that takes the found element and the i integer for the for loop
	 *replaces the text with the text modified with highlight tags.
	 *@param found are the paragraphs that in the book HTML file includes
	 *the searched text 
	 *@param highlight highlight being search
	 */

	private void textReplace(Element found,Highlight highlight) {
		String textHighlighted=highlight.getHighlightedText();
		String textToModify=found.html();
		String textOfTheHighlight=highlight.getHighligghtText();
		Pattern SPECIAL_REGEX_CHARS = Pattern.compile("[\\{\\}\\(\\)\\[\\]\\?\\+\\*\\^$\\|\\\\\\-]");
		boolean isContains = textToModify.contains(textOfTheHighlight);
		if (!isContains) {
			
			String[] wordsInText = textOfTheHighlight.split(" ");
			int lengthOfWords=wordsInText.length;
			if (lengthOfWords>3) {
				System.out.println("lengthOfWords: "+lengthOfWords);
				String beginOfRegex=wordsInText[0]+" "+wordsInText[1];
				String beginOfRegexCleaned=SPECIAL_REGEX_CHARS.matcher(beginOfRegex).replaceAll("\\\\$0");
				String endOfRegex=wordsInText[lengthOfWords-2]+" "+wordsInText[lengthOfWords-1];
				String endOfRegexCleaned=SPECIAL_REGEX_CHARS.matcher(endOfRegex).replaceAll("\\\\$0");
				String regex=beginOfRegexCleaned+".*?"+endOfRegexCleaned;
				System.out.println(regex);
				String modifiedText=textToModify.replaceAll(regex, textHighlighted);
				isContains = !textToModify.equals(modifiedText);
				if (modifiedText!=null) {
					found.html(modifiedText);
				}
			}
			if (!isContains) {
				if (lengthOfWords>1) {
					System.out.println("lengthOfWords: "+lengthOfWords);
					String beginOfRegex=wordsInText[0];
					beginOfRegex=beginOfRegex.replaceAll("[,;:\\)\\(]", "");
					String beginOfRegexCleaned=SPECIAL_REGEX_CHARS.matcher(beginOfRegex).replaceAll("\\\\$0");
					String endOfRegex=wordsInText[lengthOfWords-1];
					endOfRegex=endOfRegex.replaceAll("[,;:\\)\\(]", "");
					String endOfRegexCleaned=SPECIAL_REGEX_CHARS.matcher(endOfRegex).replaceAll("\\\\$0");
					String regex=beginOfRegexCleaned+".*?"+endOfRegexCleaned;
					System.out.println(regex);
					String modifiedText=textToModify.replaceAll(regex, textHighlighted);
					if (modifiedText!=null) {
						found.html(modifiedText);
					}
				}
			}
		}
		else {
			String modifiedText=textToModify.replace(highlight.getHighligghtText(), textHighlighted);
			found.html(modifiedText);
		}
	}
	/**
	 * 
	 *informs ebook and highlight of the information found on the search
	 *@param found are the paragraphs that in the book HTML file includes
	 *@param file informed file with the index in order in the book
	 *@param highlight highlight being search
	 *@param htmlFiles a list of the informed files in which to search the highlits
	 */
	public void informResults (Element found,InformedFile file, Highlight highlight,  ArrayList<InformedFile> htmlfiles) {
		//saves the file where the highlight was found
		highlight.setContainerFile(file);
		//produces the links
		highlight.constructHighlightLink();
		//adds highlight to observable for the gui:
		ModelConnector.getHighlightsFound().add(highlight);
		//saves the paragraph where the highlight was found
		highlight.setHighlightLocationInHtml(found.elementSiblingIndex());
		//saves the index file where it was found
		highlight.setHighlightFileIndex(file.getOrderIndex());
		//saves the highlights in book
		saveHighlightInEBook(highlight);
		ModelConnector.setNumberHighlightsFound(numberHighlightsFound);
	}
	/**
	 * saves the highlights in the book
	 *@param highlight highlight being search
	 */
	private void saveHighlightInEBook (Highlight highlight) {
		eBook.setHighlightsFound(highlight);
		highlight.eBook=eBook;
	}
	/**
	 * add messages to display in the gui
	 * @param s string to be displayed
	 */
	void addMessagesToDisplay(String s) {
		ModelConnector.setMessages(s);
	}
	/**
	 * Saves the book calling the OutputHandler
	 *@param htmlDokument jsoup dokument that contains the modified text to be saved in the html file of the book
	 *@param file informed file with the index in order in the book
	 *@param ebook the book being processed
	 *
	 */
	public void saveTheHtmlOfBook(Document htmlDokument, File file,EBook eBook) {
		OutputHandler outputHandler= new OutputHandler(eBook,file);
		outputHandler.saveHtml(htmlDokument);
	}

}
