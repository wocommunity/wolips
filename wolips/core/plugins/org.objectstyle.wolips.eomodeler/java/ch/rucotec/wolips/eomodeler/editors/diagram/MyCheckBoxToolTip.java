package ch.rucotec.wolips.eomodeler.editors.diagram;

import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;
import org.objectstyle.wolips.eomodeler.Activator;
import org.objectstyle.wolips.eomodeler.core.model.EOEntity;
import org.objectstyle.wolips.eomodeler.core.model.EOModel;
import org.objectstyle.wolips.eomodeler.editors.EOModelEditor;

import ch.rucotec.wolips.eomodeler.core.model.AbstractDiagram;

/**
 * This class extends {@link CustomToolTip} and provides a custom made ToolTip for a 
 * {@link Button} (Can be customized for every {@link Widget}). This ToolTip shows its data in a {@link Tree}.
 * 
 * @author Savas Celik
 *
 */
public class MyCheckBoxToolTip extends CustomToolTip {
	
	//---------------------------------------------------------------------------
	// ### Variables and Constants
	//---------------------------------------------------------------------------
	
	private Tree tree;
	
	private AbstractDiagram<?> myDiagram;
	
	private EOModelEditor myCurrentModelEditor;
	
	private EOEntity parentEntity;
	
	private Set<EOEntity> childrenEntities;
	
	private Control control;
	
	private int disposeDelay;

	//---------------------------------------------------------------------------
	// ### Construction
	//---------------------------------------------------------------------------
	
	/**
	 * Takes the {@link Button} to add the ToolTip on and the {@link AbstractDiagram}
	 * to be able to set the Selection in the {@link EOModelEditor}.
	 * 
	 * @param control
	 * @param myDiagram
	 */
	public MyCheckBoxToolTip(Button control, AbstractDiagram myDiagram) {
		super(control);
		this.control = control;
		this.myDiagram = myDiagram;
		setShift(new Point ( 10, 5 ));
		setPopupDelay(200);
		setHideOnMouseDown(false);
		disposeDelay = 300;
	}
	
	//---------------------------------------------------------------------------
	// ### Methods
	//---------------------------------------------------------------------------
	
	/**
	 * Checks whether or not the cursor is in the ToolTip window.
	 * 
	 * @param tip
	 * @return true if it's in the ToolTip window, else false.
	 */
	protected boolean isCursorInToolTip(Shell tip) {
		boolean cursorInToolTip = false;
		if (tip != null && !tip.isDisposed()) {
			Rectangle rect = tip.getBounds();
			cursorInToolTip = rect.contains(tip.getDisplay().getCursorLocation());
		}
		return cursorInToolTip;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * This method is called to dispose the ToolTip window.
	 * I Override it so the disposing has a delay on it.
	 * (That gives the user time to hover in to the ToolTip window)
	 * 
	 * @see ch.rucotec.wolips.eomodeler.editors.diagram.CustomToolTip#toolTipHide(org.eclipse.swt.widgets.Shell, org.eclipse.swt.widgets.Event)
	 */
	@Override
	protected void toolTipHide(Shell tip, Event event) {
		if (!control.isDisposed()) {
			control.getDisplay().timerExec(disposeDelay, () -> {
				if (!control.isDisposed() && !isCursorInToolTip(tip)) {
					super.toolTipHide(tip, event);
				}
			});
		} else {
			tip.dispose();
		}
	}
	
	@Override
	protected Composite createToolTipContentArea(Event event, Composite parent) {
		tree = new Tree(parent, SWT.NONE);
		// Making the Entity shown in the TreeView as a ToolTip Doubleclick able.
		tree.addListener(SWT.MouseDoubleClick, new Listener() { 
			@Override
			public void handleEvent(Event event) {
				if (tree.getSelection().length > 0) { // This checks whether or not the User selected something. 
					EOModel model = myDiagram._getModelParent().getModel();
					EOEntity selectedEntity = myDiagram._getModelParent().getModel().getEntityNamed(tree.getSelection()[0].getText(0));
					if (selectedEntity == null) {
						for (EOEntity entity : model.getEntities()) {
							if (entity.getName().equals(tree.getSelection()[0].getText(0))) { // Since the Entity names are unique we check if we can find a Entity with the name of our selected item in the TreeView.
								selectedEntity = entity;
								break;
							}
						}
					}
					if (myCurrentModelEditor == null) {
						myCurrentModelEditor = (EOModelEditor)myDiagram.getEOModelEditor();
					}
					myCurrentModelEditor.setSelectedEntity(selectedEntity);
					myCurrentModelEditor.setActivePage(1);
				}
				parent.dispose();
			}
		});
		
		TreeItem parentItem = new TreeItem(tree, SWT.NONE);
		
		if (parentEntity != null) {
			parentItem.setImage(Activator.getDefault().getImageRegistry().get(Activator.EOENTITY_ICON));
			parentItem.setText(parentEntity.getName());
		}
		
		if (parentEntity != null && childrenEntities != null) {
			for (EOEntity childrenEntity : childrenEntities) {
				TreeItem childrenItem = new TreeItem(parentItem, SWT.CHECK);
				childrenItem.setImage(Activator.getDefault().getImageRegistry().get(Activator.EORELATIONSHIP_ICON));
				childrenItem.setText(childrenEntity.getName());
			}
		}
		parentItem.setExpanded(true);
		
		return tree;
	}

	//---------------------------------------------------------------------------
	// ### Basic Accessors
	//---------------------------------------------------------------------------
	
	public EOEntity getParentEntity() {
		return parentEntity;
	}

	public void setParentEntity(EOEntity parentEntity) {
		this.parentEntity = parentEntity;
	}

	public Set<EOEntity> getChildrenEntities() {
		return childrenEntities;
	}

	public void setChildrenEntities(Set<EOEntity> childrenEntities) {
		this.childrenEntities = childrenEntities;
	}
}
