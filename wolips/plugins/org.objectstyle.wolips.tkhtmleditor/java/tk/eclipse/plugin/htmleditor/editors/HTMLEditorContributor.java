package tk.eclipse.plugin.htmleditor.editors;

import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.part.MultiPageEditorActionBarContributor;

public class HTMLEditorContributor extends MultiPageEditorActionBarContributor {
	
	protected HTMLSourceEditorContributer contributer = new HTMLSourceEditorContributer();
	
	public HTMLEditorContributor() {
		super();
		init();
	}
	
	protected void init(){
		contributer.addActionId(HTMLSourceEditor.ACTION_ESCAPE_HTML);
		contributer.addActionId(HTMLSourceEditor.ACTION_COMMENT);
	}
	
	@Override
  public void init(IActionBars bars, IWorkbenchPage page) {
		super.init(bars, page);
		contributer.init(bars,page);
	}
	
	@Override
  public void setActivePage(IEditorPart activeEditor) {
		if(activeEditor instanceof HTMLSourceEditor){
		} else {
		}
	}
	
	@Override
  public void setActiveEditor(IEditorPart part) {
		if(part instanceof HTMLEditor){
			part = ((HTMLEditor)part).getPaletteTarget();
			contributer.setActiveEditor(part);
		}
		super.setActiveEditor(part);
	}
	
	@Override
  public void dispose(){
		contributer.dispose();
		super.dispose();
	}
}
