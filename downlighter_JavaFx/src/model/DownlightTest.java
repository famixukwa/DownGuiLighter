package model;

import java.io.File;

public class DownlightTest {
	public static void main(String[] args) {
		File ebookFile=new File("epubs/Beichte eines Morders, erzahlt in einer Na - Joseph Roth.epub");
		File htmlFile=new File("epubs/Beichte eines Mörders, erzählt in einer Nacht (Vollständige Ausgabe)- Geschichte eines Doppelmordes im Ersten Weltkrieg (Kriminalroman) (German Edition) - Notebook.html");
		InputHandler.setEbookFile(ebookFile);;
		InputHandler.setHighlights(htmlFile);;
		BookProcess processedBook=new BookProcess();
		processedBook.start();

	}
}
