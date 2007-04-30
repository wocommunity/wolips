package tk.eclipse.plugin.xmleditor.editors;

import java.io.File;

import com.thaiopensource.relaxng.edit.SchemaCollection;
import com.thaiopensource.relaxng.input.InputFormat;
import com.thaiopensource.relaxng.input.dtd.DtdInputFormat;
import com.thaiopensource.relaxng.input.xml.XmlInputFormat;
import com.thaiopensource.relaxng.output.LocalOutputDirectory;
import com.thaiopensource.relaxng.output.OutputDirectory;
import com.thaiopensource.relaxng.output.OutputFormat;
import com.thaiopensource.relaxng.output.dtd.DtdOutputFormat;
import com.thaiopensource.relaxng.output.xsd.XsdOutputFormat;
import com.thaiopensource.util.UriOrFile;
import com.thaiopensource.xml.sax.ErrorHandlerImpl;

/**
 * Schema Generator using Trang.
 * <p>
 * It can generate DTD and XML Schema from XML files.
 * 
 * @author Naoki Takezoe
 */
public class SchemaGenerator {
	
	/**
	 * Generates XSD from a DTD file.
	 * 
	 * @param input DTD file (input)
	 * @param output XSD file (output)
	 * @throws Exception
	 */
	public static void generateXSDFromDTD(File input, File output) throws Exception {
		ErrorHandlerImpl eh = new ErrorHandlerImpl();
		OutputFormat of = new XsdOutputFormat();
		InputFormat inFormat = new DtdInputFormat();
		SchemaCollection sc = inFormat.load(
				UriOrFile.toUri(input.getAbsolutePath()), new String[0], "xsd", eh);
		
		OutputDirectory od = new LocalOutputDirectory(
				sc.getMainUri(),output,"xml","utf-8",80,4);
		
		of.output(sc, od, new String[0], "xml", eh);
	}
	
	/**
	 * Generates DTD from a XML file.
	 * 
	 * @param input XML file (input)
	 * @param output DTD file (output)
	 * @throws Exception
	 */
	public static void generateDTDFromXML(File input,File output) throws Exception {
		ErrorHandlerImpl eh = new ErrorHandlerImpl();
		OutputFormat of = new DtdOutputFormat();
		InputFormat inFormat = new XmlInputFormat();
		SchemaCollection sc = inFormat.load(
				UriOrFile.toUri(input.getAbsolutePath()), new String[0], "dtd", eh);
		
		OutputDirectory od = new LocalOutputDirectory(
				sc.getMainUri(),output,"xml","utf-8",80,4);
		
		of.output(sc, od, new String[0], "xml", eh);
	}
	
	/**
	 * Generats XML Schema from a XML file.
	 * 
	 * @param input XML file (input)
	 * @param output XSD file (output)
	 * @throws Exception
	 */
	public static void generateXSDFromXML(File input,File output) throws Exception {
		ErrorHandlerImpl eh = new ErrorHandlerImpl();
		OutputFormat of = new XsdOutputFormat();
		InputFormat inFormat = new XmlInputFormat();
		SchemaCollection sc = inFormat.load(
				UriOrFile.toUri(input.getAbsolutePath()), new String[0], "xsd", eh);
		
		OutputDirectory od = new LocalOutputDirectory(
				sc.getMainUri(),output,"xml","utf-8",80,4);
		
		of.output(sc, od, new String[0], "xml", eh);
	}
}
