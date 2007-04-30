package tk.eclipse.plugin.jspeditor.editors;

import jp.aonir.fuzzyxml.FuzzyXMLDocument;
import jp.aonir.fuzzyxml.FuzzyXMLElement;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;

import tk.eclipse.plugin.htmleditor.IHyperlinkProvider;
import tk.eclipse.plugin.htmleditor.editors.HTMLHyperlinkInfo;

public class JSPHyperlinkProvider implements IHyperlinkProvider {

	public HTMLHyperlinkInfo getHyperlinkInfo(IFile file, FuzzyXMLDocument doc,
			FuzzyXMLElement element, String attrName, String attrValue,int offset) {
		
		if(element.getName().equals("jsp:include") && attrName.equals("page")){
			IPath path = file.getParent().getProjectRelativePath();
			IResource resource = file.getProject().findMember(path.append(attrValue));
			if(resource!=null && resource.exists() && resource instanceof IFile){
				HTMLHyperlinkInfo info = new HTMLHyperlinkInfo();
				info.setObject(resource);
				info.setOffset(0);
				info.setLength(attrValue.length());
				return info;
			}
		}
		
		return null;
	}

}
