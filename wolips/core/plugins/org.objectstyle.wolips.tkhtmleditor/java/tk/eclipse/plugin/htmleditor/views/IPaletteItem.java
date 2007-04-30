package tk.eclipse.plugin.htmleditor.views;

import org.eclipse.jface.resource.ImageDescriptor;

import tk.eclipse.plugin.htmleditor.editors.HTMLSourceEditor;

public interface IPaletteItem {
	
	public String getLabel();
	
	public ImageDescriptor getImageDescriptor();
	
	public void execute(HTMLSourceEditor editor);
	
}
