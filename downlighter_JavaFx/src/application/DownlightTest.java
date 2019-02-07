package application;

import java.io.File;

public class DownlightTest {
public static void main(String[] args) {
	File ebookFile=new File("epubs/part0002_split_000.html");
	File htmlFile=new File("epubs/Die Kapuzinergruft (German Edition) - Notebook.html");
	InputHandler.ebookFile=ebookFile;
	InputHandler.highlights=htmlFile;
	BookProcess processedBook=new BookProcess();
	processedBook.searchReplaceInBook();
	
}
}
