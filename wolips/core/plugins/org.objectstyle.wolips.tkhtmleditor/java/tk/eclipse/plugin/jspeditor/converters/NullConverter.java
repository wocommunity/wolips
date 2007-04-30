package tk.eclipse.plugin.jspeditor.converters;

import java.util.Map;

import jp.aonir.fuzzyxml.FuzzyXMLNode;
import tk.eclipse.plugin.jspeditor.editors.JSPInfo;

public class NullConverter extends AbstractCustomTagConverter {
	public String process(Map attrs, FuzzyXMLNode[] children, JSPInfo info) {
		return evalBody(children,info);
	}
}
