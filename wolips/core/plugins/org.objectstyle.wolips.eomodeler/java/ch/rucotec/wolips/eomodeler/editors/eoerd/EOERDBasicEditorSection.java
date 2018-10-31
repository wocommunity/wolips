package ch.rucotec.wolips.eomodeler.editors.eoerd;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.objectstyle.wolips.baseforplugins.util.ComparisonUtils;
import org.objectstyle.wolips.eomodeler.Messages;
import org.objectstyle.wolips.eomodeler.core.model.EOEntity;
import org.objectstyle.wolips.eomodeler.core.model.EOStoredProcedure;
import org.objectstyle.wolips.eomodeler.utils.FormUtils;
import org.objectstyle.wolips.eomodeler.utils.UglyFocusHackWorkaroundListener;

import ch.rucotec.wolips.eomodeler.core.model.EOERDiagramGroup;

public class EOERDBasicEditorSection extends AbstractPropertySection {
	
	//---------------------------------------------------------------------------
	// ### Variables and Constants
	//---------------------------------------------------------------------------
	
	
	private EOERDiagramGroup myERD;
	
	private Text myNameText;
	
	private DataBindingContext myBindingContext;
	
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
		Composite form = getWidgetFactory().createFlatFormComposite(_parent);
		FormLayout formLayout = new FormLayout();
		form.setLayout(formLayout);

		Composite topForm = FormUtils.createForm(getWidgetFactory(), form);

		getWidgetFactory().createCLabel(topForm, "Diagram Name", SWT.NONE);
		myNameText = new Text(topForm, SWT.BORDER);
		GridData nameFieldLayoutData = new GridData(GridData.FILL_HORIZONTAL);
		myNameText.setLayoutData(nameFieldLayoutData);
		UglyFocusHackWorkaroundListener.addListener(myNameText);
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
		myERD = (EOERDiagramGroup) selectedObject;
		if (myERD != null) {
			myBindingContext = new DataBindingContext();
			myBindingContext.bindValue(SWTObservables.observeText(myNameText, SWT.Modify), BeansObservables.observeValue(myERD, EOERDiagramGroup.NAME), null, null);
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
	
	//---------------------------------------------------------------------------
	// ### Custom Accessors
	//---------------------------------------------------------------------------
	
	//---------------------------------------------------------------------------
	// ### Basic Accessors
	//---------------------------------------------------------------------------
}
