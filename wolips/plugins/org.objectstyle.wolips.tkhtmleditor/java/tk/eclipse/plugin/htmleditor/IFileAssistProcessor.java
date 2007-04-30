package tk.eclipse.plugin.htmleditor;

import org.eclipse.core.resources.IFile;

import tk.eclipse.plugin.htmleditor.assist.AssistInfo;

public interface IFileAssistProcessor {
	
	public void reload(IFile file);
	
	public AssistInfo[] getAssistInfo(String value);
	
}
