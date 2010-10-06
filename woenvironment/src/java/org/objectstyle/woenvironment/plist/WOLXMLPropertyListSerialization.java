package org.objectstyle.woenvironment.plist;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.util.Collection;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class WOLXMLPropertyListSerialization {
	private static final DocumentBuilderFactory _builderFactory;
	
	static { 
		_builderFactory = DocumentBuilderFactory.newInstance();
		_builderFactory.setValidating(false);
		_builderFactory.setNamespaceAware(false);
		_builderFactory.setAttribute("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
	}
	
	public static Object propertyListFromString(String string) throws IOException, PropertyListParserException, SAXException, ParserConfigurationException {
		return WOLXMLPropertyListSerialization.propertyListFromString(string, new SimpleParserDataStructureFactory());
	}

	public static Object propertyListFromString(String string, ParserDataStructureFactory factory) throws PropertyListParserException, SAXException, IOException, ParserConfigurationException {
		return parse(_builderFactory.newDocumentBuilder().parse(new InputSource(new StringReader(string))), factory);
	}

	public static Object propertyListWithPathURL(URL url, ParserDataStructureFactory factory) throws IOException, PropertyListParserException, SAXException, ParserConfigurationException {
		InputStream is = url.openStream();
		try {
			return parse(_builderFactory.newDocumentBuilder().parse(is), factory);
		}
		finally {
			is.close();
		}
	}

	public static Object propertyListWithContentsOfInputStream(InputStream inputStream, ParserDataStructureFactory factory) throws IOException, PropertyListParserException, SAXException, ParserConfigurationException {
		return parse(_builderFactory.newDocumentBuilder().parse(inputStream), factory);
	}

	public static Object propertyListWithContentsOfFile(String path, ParserDataStructureFactory factory) throws IOException, PropertyListParserException, SAXException, ParserConfigurationException {
		return parse(_builderFactory.newDocumentBuilder().parse(new File(path)), factory);
	}

	public static Object propertyListWithContentsOfFile(File file, ParserDataStructureFactory factory) throws IOException, PropertyListParserException, SAXException, ParserConfigurationException {
		return parse(_builderFactory.newDocumentBuilder().parse(file), factory);
	}

	protected static Object parse(Document document, ParserDataStructureFactory factory) {
		document.normalize();
		Element plistElement = document.getDocumentElement();
		if ("plist".equalsIgnoreCase(plistElement.getNodeName())) {
			NodeList plistChildren = plistElement.getChildNodes();
			for (int i = 0; i < plistChildren.getLength(); i++) {
				Node plistChild = plistChildren.item(i);
				if (plistChild instanceof Element) {
					return parseValue((Element) plistChild, "", factory);
				}
			}
		}
		return null;
	}

	protected static Map<Object, Object> parseDict(Element dictElement, String keyPath, ParserDataStructureFactory factory) {
		Map<Object, Object> result = factory.createMap(keyPath);
		NodeList dictNodes = dictElement.getChildNodes();
		String key = null;
		for (int i = 0; i < dictNodes.getLength(); i++) {
			Node node = dictNodes.item(i);
			if (node instanceof Element) {
				String nodeName = node.getNodeName();
				if ("key".equalsIgnoreCase(nodeName)) {
					key = node.getTextContent();
				}
				else {
					String newKeyPath = keyPath.length() == 0 ? key : (keyPath + "." + key);
					Object value = parseValue((Element) node, newKeyPath, factory);
					result.put(key, value);
				}
			}
		}
		return result;
	}

	protected static Collection<Object> parseArray(Element arrayElement, String keyPath, ParserDataStructureFactory factory) {
		Collection<Object> result = factory.createCollection(keyPath);
		NodeList arrayNodes = arrayElement.getChildNodes();
		for (int i = 0; i < arrayNodes.getLength(); i++) {
			Node node = arrayNodes.item(i);
			if (node instanceof Element) {
				Object value = parseValue((Element) node, keyPath, factory);
				result.add(value);
			}
		}
		return result;
	}

	protected static Object parseValue(Element element, String keyPath, ParserDataStructureFactory factory) {
		Object value;
		String nodeName = element.getNodeName();
		if ("string".equalsIgnoreCase(nodeName)) {
			value = element.getTextContent();
		}
		else if ("true".equalsIgnoreCase(nodeName)) {
			value = Boolean.TRUE;
		}
		else if ("false".equalsIgnoreCase(nodeName)) {
			value = Boolean.FALSE;
		}
		else if ("array".equalsIgnoreCase(nodeName)) {
			value = parseArray(element, keyPath, factory);
		}
		else if ("dict".equalsIgnoreCase(nodeName)) {
			value = parseDict(element, keyPath, factory);
		}
		else {
			value = null;
		}
		return value;
	}
}
