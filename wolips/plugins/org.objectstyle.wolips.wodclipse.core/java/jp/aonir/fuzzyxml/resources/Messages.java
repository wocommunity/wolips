package jp.aonir.fuzzyxml.resources;

import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * This is an utility class for accessing property files.
 */
public class Messages {
	
	/** ResourceBundle of message.properties */
	private static ResourceBundle resource = ResourceBundle.getBundle("jp.aonir.fuzzyxml.resources.message");
	
	public static String getMessage(String key){
		return getMessage(key,new String[0]);
	}
	
	public static String getMessage(String key,String param){
		return getMessage(key,new String[]{param});
	}
	
	public static String getMessage(String key,String[] params){
		String message = resource.getString(key);
		return MessageFormat.format(message, (Object[]) params);
	}
}
