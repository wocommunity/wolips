package ch.rucotec.wolips.eomodeler.editors.diagram;

import java.util.Set;

import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.objectstyle.wolips.eomodeler.Activator;
import org.objectstyle.wolips.eomodeler.core.model.EOEntity;
import org.objectstyle.wolips.eomodeler.core.model.EOModel;
import org.objectstyle.wolips.eomodeler.editors.EOModelEditor;

import ch.rucotec.wolips.eomodeler.core.model.AbstractDiagram;

/**
 * This class extends the {@link ToolTip} and provides a custom made ToolTip for your 
 * {@link Button}. This ToolTip shows the its data in a {@link Tree}.
 * 
 * @author celik
 *
 */
public class MyCheckBoxToolTip extends ToolTip {
	
	//---------------------------------------------------------------------------
	// ### Variables and Constants
	//---------------------------------------------------------------------------
	
	private Tree tree;
	
	private AbstractDiagram<?> myDiagram;
	
	private EOModelEditor myCurrentModelEditor;
	
	private EOEntity parentEntity;
	
	private Set<EOEntity> childrenEntities;

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
		this.myDiagram = myDiagram;
		setShift(new Point ( 1, 0 ));
		setPopupDelay(200);
		setHideOnMouseDown(false);
	}
	
	//---------------------------------------------------------------------------
	// ### Methods
	//---------------------------------------------------------------------------
	
	@Override
	protected Composite createToolTipContentArea(Event event, Composite parent) {
		tree = new Tree(parent, SWT.NONE);
		tree.addMouseListener(new MouseListener() {
			
			public void mouseUp(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			public void mouseDown(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			public void mouseDoubleClick(MouseEvent e) {
				if (tree.getSelection().length > 0) {
					EOModel model = myDiagram._getModelParent().getModel();
					EOEntity selectedEntity = myDiagram._getModelParent().getModel().getEntityNamed(tree.getSelection()[0].getText(0));
					if (selectedEntity == null) {
						for (EOEntity entity : model.getEntities()) {
							if (entity.getName().equals(tree.getSelection()[0].getText(0))) {
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
