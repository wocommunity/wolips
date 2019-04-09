package ch.rucotec.wolips.eomodeler.editors.diagram;

import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.objectstyle.wolips.baseforplugins.util.ComparisonUtils;
import org.objectstyle.wolips.eomodeler.core.model.EOEntity;
import org.objectstyle.wolips.eomodeler.core.model.EOModel;
import org.objectstyle.wolips.eomodeler.utils.UglyFocusHackWorkaroundListener;

import ch.rucotec.wolips.eomodeler.DiagramTab;
import ch.rucotec.wolips.eomodeler.core.model.AbstractDiagram;
import ch.rucotec.wolips.eomodeler.core.model.EOERDiagram;

/**
 * This class describes how the PropetiesView in Eclipse.
 * If a {@link EOERDiagram} is selected in the Outline then the PropetiesView will show this.
 * the settings are made in the {@code plugin.xml}
 * 
 * @author Savas Celik
 *
 */
public class EOERDiagramBasicEditorSection extends AbstractPropertySection {

	//---------------------------------------------------------------------------
	// ### Variables and Constants
	//---------------------------------------------------------------------------
	
	private static final String GROUP_NAME = "Entites";

	private AbstractDiagram<?> myDiagram;
	
	private EOModel myCurrentModel;

	private Text myNameText;

	private DataBindingContext myBindingContext;

	private Composite parent;

	private Group grpcheckBoxes;
	
	private Map<EOEntity, Button> checkBoxes;

	//---------------------------------------------------------------------------
	// ### Construction
	//---------------------------------------------------------------------------

	//---------------------------------------------------------------------------
	// ### Methods
	//---------------------------------------------------------------------------

	/**
	 * This creates the form in the Properties Tab
	 */
	@Override
	public void createControls(Composite _parent, TabbedPropertySheetPage _tabbedPropertySheetPage) {
		super.createControls(_parent, _tabbedPropertySheetPage);
		parent = _parent;
		_parent.setLayout(new GridLayout(3, false));

		getWidgetFactory().createCLabel(_parent, "Diagram Name", SWT.NONE);
		myNameText = new Text(_parent, SWT.BORDER);
		myNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 0));
		UglyFocusHackWorkaroundListener.addListener(myNameText);
		selectAllAndDeselectAllButton();
		initGroupForEntityCheckBoxes(_parent);
		checkBoxes = new TreeMap<EOEntity, Button>(new Comparator<EOEntity>() {

			public int compare(EOEntity o1, EOEntity o2) {
				return o1.getExternalName().compareToIgnoreCase(o2.getExternalName());
			}
		});
	}
	
	/**
	 * Creates a SelectAll and DeselectALL Button.
	 */
	private void selectAllAndDeselectAllButton() {
		GridData gd_btnNewButton = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnNewButton.widthHint = 100;
		
		Button selectAll = new Button(parent, SWT.NONE);
		selectAll.setLayoutData(gd_btnNewButton);
		selectAll.setText("Select All");
		selectAll.setToolTipText("Selects all Tables");
		selectAll.addListener(SWT.MouseDown, new Listener() {
			
			public void handleEvent(Event event) {
				for (final EOEntity entity : myCurrentModel.getEntities()) {
					if (!entity.isInherited() && !myDiagram.getEntities().contains(entity)) {
						myDiagram.addEntityToDiagram(entity, entity.getChildrenEntities());
					}
					if (checkBoxes.get(entity) != null) {
						checkBoxes.get(entity).setSelection(true);
					}
				}
				DiagramTab.getInstance().setSelectedDiagram(myDiagram);
				myDiagram._getModelParent().setModelDirty(true);
			}
		});
		
		Button deselectAll = new Button(parent, SWT.NONE);
		deselectAll.setLayoutData(gd_btnNewButton);
		deselectAll.setText("Deselect All");
		deselectAll.setToolTipText("Deselects all Tables");
		deselectAll.addListener(SWT.MouseDown, new Listener() {
			
			public void handleEvent(Event event) {
				for (final EOEntity entity : myCurrentModel.getEntities()) {
					if (!entity.isInherited() && myDiagram.getEntities().contains(entity)) {
						myDiagram.removeEntityFromDiagram(entity, entity.getChildrenEntities());
					}
					if (checkBoxes.get(entity) != null) {
						checkBoxes.get(entity).setSelection(false);
					}
				}
				DiagramTab.getInstance().setSelectedDiagram(myDiagram);
				myDiagram._getModelParent().setModelDirty(true);
			}
		});
	}

	/**
	 * Initializes the Group-Widget and gives it a GridLayout.
	 * 
	 * @param _parent
	 */
	private void initGroupForEntityCheckBoxes(Composite _parent) {
		grpcheckBoxes = new Group(_parent, SWT.NONE);
		grpcheckBoxes.setLayout(new GridLayout(3, false));
		GridData gd_grpLol = new GridData(SWT.FILL, SWT.CENTER, false, false, 3, 1);
		grpcheckBoxes.setLayoutData(gd_grpLol);
		grpcheckBoxes.setText(GROUP_NAME);
	}
	
	/**
	 * Adds a ToolTip for every checkbox
	 * 
	 * @see MyCheckBoxToolTip
	 */
	private void grpCheckBoxesToolTip() {
		for (final EOEntity entity : myCurrentModel.getEntities()) {
			if (entity.isInherited()) {
				continue;
			}
			Set<EOEntity> childrens = entity.getChildrenEntities(); 
			Button checkboxParent = checkBoxes.get(entity);
			MyCheckBoxToolTip dtooltip = new MyCheckBoxToolTip(checkboxParent, myDiagram);
			dtooltip.setParentEntity(entity);
			dtooltip.setChildrenEntities(childrens);

			if (!childrens.isEmpty()) {
				checkboxParent.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT));
			}
		}
	}

	/**
	 * Changes the parent of every checkbox (from Composite to a Group)
	 */
	private void sortButtons() {
		/* if you wonder why there is no sorting done
		 * its because the checkBoxes is already sorted (its a TreeMap)
		 * we just give them their new parent.
		 */
		for (Button checkbox : checkBoxes.values()) {
			if (checkbox.getParent() == parent) {
				checkbox.setParent(grpcheckBoxes);
			}
		}
	}

	/**
	 * Creates checkboxes for every entity in the EOModel.
	 */
	private void createEntityCheckBoxes() {
		if (myCurrentModel == null) {
			myCurrentModel = myDiagram._getModelParent().getModel();
		} else if (myCurrentModel != myDiagram._getModelParent().getModel()) {
			// die alten Checkboxen werden geloescht.
			grpcheckBoxes.dispose();
			initGroupForEntityCheckBoxes(parent);
			checkBoxes.clear();
			myCurrentModel = myDiagram._getModelParent().getModel();
		}

		// fuer jedes entity im EOModel wird ein Checkbox mit einem listener hinzugefuegt.

		for (final EOEntity entity : myCurrentModel.getEntities()) {

			Button entityCheckBox = null;
			
			if (entity.isInherited()) {
				continue;
			}


			if (checkBoxes.get(entity) != null) {
				entityCheckBox = checkBoxes.get(entity);
			}

			if (entityCheckBox == null) {
				entityCheckBox = new Button(parent, SWT.CHECK);
				entityCheckBox.setText(entity.getExternalName());
				checkBoxes.put(entity, entityCheckBox);
			}

			if (myDiagram.getEntities().contains(entity)) {
				entityCheckBox.setSelection(true);
			} else {
				entityCheckBox.setSelection(false);
			}
			entityCheckBox.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent event) {
					Button btn = (Button) event.getSource();
					if (btn.getSelection()) {
						myDiagram.addEntityToDiagram(entity, entity.getChildrenEntities());
					} else if (!btn.getSelection()) {
						myDiagram.removeEntityFromDiagram(entity, entity.getChildrenEntities());
					}
					DiagramTab.getInstance().setSelectedDiagram(myDiagram);
					myDiagram._getModelParent().setModelDirty(true);
				}
			});
			
		}
	}

	/**
	 * This sets the Selection and binds the data with the to a Node (Text, Label, Button, ComboViewer etc.), 
	 * through selection we get the data which needs to be filled
	 * in our form (which is in the Properties Tab).
	 */
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		if (ComparisonUtils.equals(selection, getSelection())) {
			return;
		}

		super.setInput(part, selection);
		disposeBindings();

		Object selectedObject = ((IStructuredSelection) selection).getFirstElement();
		myDiagram = (AbstractDiagram) selectedObject;
		if (myDiagram != null) {
			myBindingContext = new DataBindingContext();
			myBindingContext.bindValue(SWTObservables.observeText(myNameText, SWT.Modify), BeansObservables.observeValue(myDiagram, AbstractDiagram.NAME), null, null);
			createEntityCheckBoxes();
			sortButtons();
			grpCheckBoxesToolTip();
		}
	}

	/**
	 * Disposes the bindings which were created in the setInput() method
	 */
	protected void disposeBindings() {
		if (myBindingContext != null) {
			myBindingContext.dispose();
		}
	}

	@Override
	public void dispose() {
		super.dispose();
		disposeBindings();
	}
}
