package model;

import java.nio.file.Path;
import java.nio.file.Paths;

public class PathHandler {
	private Path archivePath=Paths.get("test/files archive/");
	private String filenameWithNoExtension;
	private Path containerPath;
	private Path bookPath;
	private Path opfPath;


	public PathHandler(String filenameWithNoExtension) {
		super();
		this.filenameWithNoExtension = filenameWithNoExtension;
		this.containerPath=Paths.get(archivePath.toString(),filenameWithNoExtension,"META-INF/container.xml");
		this.bookPath=Paths.get(archivePath.toString(),filenameWithNoExtension);
	}
	public void setOpfPath() {
		XmlExtractor xmlExtractor= new XmlExtractor(containerPath.toString());
		opfPath=Paths.get(bookPath.toString(),xmlExtractor.getRootFile("rootfile", "full-path").toString());
	}

	public Path getArchivePath() {
		return archivePath;
	}
	public Path getBookPath() {
		setOpfPath();
		return bookPath;
	}
	public Path getOpfPath() {
		return opfPath;
	}
}
