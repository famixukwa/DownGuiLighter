package model;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class PathHandler {
	
	
	private Path containerPath;
	private Path opfPath;
	private Path pathToEpub=Paths.get(InputHandler.getEbookFile().toString()+"/");
	private String fileName=pathToEpub.getFileName().toString();
	private String filenameWithNoExtension=fileName.replaceAll("\\.epub", "")+"/";
	private Path archivePath=Paths.get("files archive/");
	private Path bookPath=Paths.get(archivePath.toString(),filenameWithNoExtension);
	private Path epubFiles=Paths.get(bookPath.toString(),"epub files");
	private Path extractedBook=Paths.get(bookPath.toString(),"extracted files");
	private ArrayList<Path>folderArray=new ArrayList<Path>();
	
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
}
