package model;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class PathHandler {
	
	
	private Path containerPath;
	private Path opfPath;
	private Path pathToEpub=Paths.get(InputHandler.getEbookFile().toString()+"/");
	private String fileName=pathToEpub.getFileName().toString();
	private String filenameWithNoExtension=fileName.replaceAll("\\.epub", "");
	private Path archivePath=Paths.get("files archive/");
	private Path bookPath=Paths.get(archivePath.toString(),filenameWithNoExtension);
	private Path epubFiles=Paths.get(bookPath.toString(),"epub files");
	private Path extractedBook=Paths.get(bookPath.toString(),"extracted files");
	private ArrayList<Path>folderArray=new ArrayList<Path>();
	private Path HtmlWithHighlights;
	private Path pathOfhtmlfiles;
	private Path epubfileWHighlights;
	private boolean isepubfileWHighlightsCreated=false;
	
	
	
	public PathHandler() {
		super();
		this.containerPath=Paths.get(extractedBook.toString(),"META-INF/container.xml");
		setFolderArray();
	}
	private void setFolderArray() {
		folderArray.add(archivePath);
		folderArray.add(bookPath);
		folderArray.add(epubFiles);
		folderArray.add(extractedBook);
	}
	
	public Path getPathToEpub() {
		return pathToEpub;
	}
	
	public Path getContainerPath() {
		return containerPath;
	}
	public String getFileName() {
		return fileName;
	}
	public String getFilenameWithNoExtension() {
		return filenameWithNoExtension;
	}
	public void setOpfPath() {
		XmlExtractor xmlExtractor= new XmlExtractor(containerPath.toString());
		opfPath=Paths.get(extractedBook.toString(),xmlExtractor.getRootFile("rootfile", "full-path").toString());
	}

	public Path getArchivePath() {
		return archivePath;
	}
	public Path getBookPath() {
		return bookPath;
	}
	public Path getOpfPath() {
		return opfPath;
	}

	public Path getEpubFiles() {
		return epubFiles;
	}

	public Path getExtractedBook() {
		return extractedBook;
	}
	public ArrayList<Path> getFolderArray() {
		return folderArray;
	}
	public void setPathToEpub(Path pathToEpub) {
		this.pathToEpub = pathToEpub;
	}
	public void setPathOfhtmlfiles() {
		XmlExtractor xmlExtractor=new XmlExtractor(opfPath.toString());
		ArrayList<Path> pathsOfTheText=xmlExtractor.getHmlFilesPath();
		Path pathOfFirstText=pathsOfTheText.get(1);
		pathOfhtmlfiles=pathOfFirstText.getParent();
	}
	public Path getHtmlWithHighlights() {
		return HtmlWithHighlights;
	}
	public void setHtmlWithHighlights() {
		HtmlWithHighlights=Paths.get(extractedBook.toString(),pathOfhtmlfiles.toString(), "highlight_index.html");
	}
	public Path getPathOfhtmlfiles() {
		return pathOfhtmlfiles;
	}
	public Path getEpubfileWHighlights() {
		return epubfileWHighlights;
	}
	public void setEpubfileWHighlights(Path epubfileWHighlights) {
		this.epubfileWHighlights = epubfileWHighlights;
	}
	public boolean isIsepubfileWHighlightsCreated() {
		return isepubfileWHighlightsCreated;
	}
	public void setIsepubfileWHighlightsCreated(boolean isepubfileWHighlightsCreated) {
		this.isepubfileWHighlightsCreated = isepubfileWHighlightsCreated;
	}
	
	
	
}
