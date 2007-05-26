package tk.eclipse.plugin.htmleditor.editors;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.IPageBookViewPage;
import org.eclipse.ui.part.Page;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import tk.eclipse.plugin.htmleditor.HTMLPlugin;

/**
 * <code>IContentOutlinePage</code> implementation for the multi page editor.
 * 
 * @author Naoki Takezoe
 */
public class MultiPageEditorOutlinePage extends Page implements IContentOutlinePage {
	
	private IContentOutlinePage activePage;
	private Composite control;
	
	/**
	 * Set the active editor.
	 * This outline page shows the outline which is provided by given editor.
	 * 
	 * @param editor the active editor
	 */
	public void setActiveEditor(IEditorPart editor){
		if(activePage!=null){
			activePage.getControl().dispose();
		}
		activePage = (IContentOutlinePage)editor.getAdapter(IContentOutlinePage.class);
		if(control!=null){
			if(activePage!=null){
				initActivePage();
				activePage.createControl(control);
				getSite().getActionBars().updateActionBars();
				control.layout();
			}
		}
	}
	
	@Override
  public void createControl(Composite parent) {
		control = new Composite(parent, SWT.NULL);
		control.setLayout(new FillLayout());
		if(activePage!=null){
			initActivePage();
			activePage.createControl(control);
		}
	}
	
	/**
	 * Initializes the active outline page.
	 */
	private void initActivePage(){
		getSite().getActionBars().getToolBarManager().removeAll();
		getSite().getActionBars().getMenuManager().removeAll();
		
		if(activePage instanceof IPageBookViewPage){
			IPageBookViewPage pageBook = (IPageBookViewPage)activePage;
			if(pageBook.getSite()==null){
				try {
					pageBook.init(getSite());
				} catch(PartInitException ex){
					HTMLPlugin.logException(ex);
				}
			}
		}
	}

	@Override
  public Control getControl() {
		if(activePage!=null){
			activePage.getControl();
		}
		return control;
	}

	@Override
  public void setFocus() {
		if(activePage!=null){
			activePage.setFocus();
		}
	}

	public void addSelectionChangedListener(ISelectionChangedListener listener) {
//		if(activePage!=null){
//			activePage.addSelectionChangedListener(listener);
//		}
	}

	public ISelection getSelection() {
		if(activePage!=null){
			return activePage.getSelection();
		}
		return null;
	}

	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
//		if(activePage!=null){
//			activePage.removeSelectionChangedListener(listener);
//		}
	}

	public void setSelection(ISelection selection) {
		if(activePage!=null){
			activePage.setSelection(selection);
		}
	}
	
}
