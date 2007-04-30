package tk.eclipse.plugin.jspeditor.editors;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tk.eclipse.plugin.htmleditor.HTMLUtil;
import tk.eclipse.plugin.htmleditor.assist.AttributeInfo;
import tk.eclipse.plugin.htmleditor.assist.TagInfo;

/**
 * @author Naoki Takezoe
 */
public class TagFileParser {
	
	private static final Pattern ATTRIBUTE = Pattern.compile("<%@\\s*attribute\\s+(.+?)%>",Pattern.DOTALL);
	private static final Pattern NAME      = Pattern.compile("name\\s*=\\s*\"(.+?)\"");
	private static final Pattern REQUIRED  = Pattern.compile("required\\s*=\\s*\"(.+?)\"");
	
	public static TagInfo parseTagFile(String prefix, String tagName, InputStream in) throws Exception {
		
		TagInfo tag = new TagInfo(prefix + ":" + tagName, true);
		
		byte[] buf = HTMLUtil.readStream(in);
		Matcher matcher = ATTRIBUTE.matcher(new String(buf));
		while (matcher.find()) {
			String content = matcher.group(1);
			String name = getAttribute(content, NAME);
			boolean required = getBooleanValue(getAttribute(content, REQUIRED));
			
			tag.addAttributeInfo(new AttributeInfo(name, true, AttributeInfo.NONE, required));
		}
		
		return tag;
	}
	
	// TODO Is this method required...?
	public static TagInfo parseTagFile(String prefix, File file) throws Exception {
		String fileName = file.getName();
		return parseTagFile(prefix, 
				fileName.substring(0, fileName.lastIndexOf('.')), new FileInputStream(file));
	}
	
	private static boolean getBooleanValue(String value){
		if(value.equals("true")){
			return true;
		}
		return false;
	}
	
	private static String getAttribute(String source,Pattern pattern){
		Matcher matcher = pattern.matcher(source);
		if(matcher.find()){
			return matcher.group(1);
		}
		return null;
	}
}
