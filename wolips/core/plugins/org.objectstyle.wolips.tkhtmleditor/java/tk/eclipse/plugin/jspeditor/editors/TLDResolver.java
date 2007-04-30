package tk.eclipse.plugin.jspeditor.editors;

import java.io.IOException;
import java.io.InputStream;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class TLDResolver implements EntityResolver {

	public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
		if(systemId!=null && systemId.equals("http://java.sun.com/dtd/web-jsptaglibrary_1_1.dtd")){
			InputStream in = getClass().getResourceAsStream("/DTD/web-jsptaglibrary_1_1.dtd");
			return new InputSource(in);
		}
		if(systemId!=null && systemId.equals("http://java.sun.com/j2ee/dtds/web-jsptaglibrary_1_1.dtd")){
			InputStream in = getClass().getResourceAsStream("/DTD/web-jsptaglibrary_1_1.dtd");
			return new InputSource(in);
		}
		if(systemId!=null && systemId.equals("http://java.sun.com/dtd/web-jsptaglibrary_1_2.dtd")){
			InputStream in = getClass().getResourceAsStream("/DTD/web-jsptaglibrary_1_2.dtd");
			return new InputSource(in);
		}
		return null;
	}

}
