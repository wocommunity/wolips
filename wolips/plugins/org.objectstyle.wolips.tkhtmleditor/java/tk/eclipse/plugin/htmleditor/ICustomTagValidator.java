package tk.eclipse.plugin.htmleditor;

import java.util.Map;

import jp.aonir.fuzzyxml.FuzzyXMLElement;
import tk.eclipse.plugin.jspeditor.editors.IJSPValidationMarkerCreator;
import tk.eclipse.plugin.jspeditor.editors.JSPInfo;

/**
 * An interface to convert taglibs for HTML preview.
 * 
 * @author Naoki Takezoe
 */
public interface ICustomTagValidator {
	
	public void validate(IJSPValidationMarkerCreator creator, Map attrs,FuzzyXMLElement element,JSPInfo info);
	
}