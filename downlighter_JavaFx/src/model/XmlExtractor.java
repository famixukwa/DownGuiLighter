package model;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
/**
 * 
 * This class extracts the content of the xml files that have various information like file names or paths
 * @Param the path of the file where the xml is located
 */

public class XmlExtractor {

	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	String pathToXml;
	String baseDirectory;
	File fXmlFile;

	public XmlExtractor(String pathToXml) {
		super();
		this.pathToXml = pathToXml;
		this.fXmlFile= new File(pathToXml);
	}
	public XmlExtractor(String pathToXml, String baseDirectory) {
		super();
		this.pathToXml = pathToXml;
		this.baseDirectory = baseDirectory;
		this.fXmlFile= new File(pathToXml);
	}

	/*
	 * method that gets the path files from content.opf
	 *
	 */

	public  ArrayList<Path> getHmlFilesPath() {
		DocumentBuilder dBuilder;
		Document doc =null;
		ArrayList<Path> filesPath=new ArrayList<Path>();
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(fXmlFile);
		} catch (ParserConfigurationException | SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		doc.getDocumentElement().normalize();
		Node spine = doc.getElementsByTagName("spine").item(0);
		NodeList childrenList = spine.getChildNodes();
		Node manifest= doc.getElementsByTagName("manifest").item(0);
		NodeList manifestList = manifest.getChildNodes();

		for (int i = 0; i < childrenList.getLength(); i++) {
			Node children = childrenList.item(i);	
			if (children.getNodeType() == Node.ELEMENT_NODE) {
				Element sElement = (Element) children;
				for (int j = 0; j < manifestList.getLength(); j++) {
					Node manifestChildren = manifestList.item(j);
					if (manifestChildren.getNodeType() == Node.ELEMENT_NODE) {
						Element mElement = (Element) manifestChildren;
						String s= sElement.getAttribute("idref");
						if (s.equals(mElement.getAttribute("id"))) {
							filesPath.add(Paths.get(mElement.getAttribute("href")));
						}
					}

				}

			}
		}
		return filesPath;

	}

	public  Path getRootFile(String parentNode,String childAttribute) {
		DocumentBuilder dBuilder;
		Document doc =null;
		Path resultPath=null;
		ArrayList<String> filesPath=new ArrayList<String>();
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(fXmlFile);
		} catch (ParserConfigurationException | SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		doc.getDocumentElement().normalize();
		Node rootfile = doc.getElementsByTagName(parentNode).item(0);
		Element element=(Element)rootfile;
		resultPath=Paths.get(element.getAttribute(childAttribute));
		return resultPath;

	}
	
	public  Path getAttributePath(String parentNode,String childAttribute, String idValue) {
		DocumentBuilder dBuilder;
		Document doc =null;
		Path resultPath=null;
		String spathToCover = null;
		ArrayList<String> filesPath=new ArrayList<String>();
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(fXmlFile);
		} catch (ParserConfigurationException | SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		doc.getDocumentElement().normalize();
		Node manifest = doc.getElementsByTagName(parentNode).item(0);
		NodeList manifestList = manifest.getChildNodes();
		for (int i = 0; i < manifestList.getLength(); i++) {
			Node children = manifestList.item(i);
			if (children.getNodeType() == Node.ELEMENT_NODE) {
				Element element=(Element)children;
				String s=element.getAttribute(childAttribute);
				if (s.equals("cover")) {
					resultPath= Paths.get(element.getAttribute("href"));
					System.out.println(resultPath.toString());
				}
			}
		}
		return resultPath;
	}

}
