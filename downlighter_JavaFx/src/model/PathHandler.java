package model;

import java.nio.file.Path;
import java.nio.file.Paths;

public class PathHandler {
	private Path archivePath=Paths.get("files archive/");
	private Path basedirectory= Paths.get("test/files archive/");
	private Path containerPath;
	private Path bookPath;
	private Path opfPath;
	private Path pathToEpub=Paths.get(InputHandler.getEbookFile().toString()+"/");
	private String fileName=pathToEpub.getFileName().toString();
	private String filenameWithNoExtension=fileName.replaceAll("\\.epub", "")+"/";
	private Path folderOfTheBook=Paths.get(archivePath.toString(),filenameWithNoExtension.toString());
	public PathHandler() {
		super();
		this.containerPath=Paths.get(archivePath.toString(),filenameWithNoExtension,"META-INF/container.xml");
		this.bookPath=Paths.get(archivePath.toString(),filenameWithNoExtension);
	}
	
	public Path getPathToEpub() {
		return pathToEpub;
	}
	public Path getBasedirectory() {
		return basedirectory;
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
		opfPath=Paths.get(bookPath.toString(),xmlExtractor.getRootFile("rootfile", "full-path").toString());
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
}
