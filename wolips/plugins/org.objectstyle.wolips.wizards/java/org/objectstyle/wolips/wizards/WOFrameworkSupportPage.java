package org.objectstyle.wolips.wizards;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.dialogs.WizardNewLinkPage;

/*
 * TODO: We could dynamically scope out the imported eomodels and figure out if the jndi adaptors
 */
public class WOFrameworkSupportPage extends WizardNewLinkPage {
	public static final String JNDI_STRING = Messages.getString("WOFrameworkSupportPage.checkbox.jndi.label");
	public static final String J2EE_STRING = Messages.getString("WOFrameworkSupportPage.checkbox.j2ee.label");
	public static final String JNDI_OPTIONS_STRING = Messages.getString("WOFrameworkSupportPage.options.jndi.text");
	public static final String J2EE_OPTIONS_STRING = Messages.getString("WOFrameworkSupportPage.options.j2ee.text");

	Button jndiSupportButton;
	Button j2eeDeploymentButton;

	static boolean jndiAdaptorEnabled = false;
	static boolean j2eeDeploymentEnabled = false;

	static CheckBoxListener  checkboxListener;


	public WOFrameworkSupportPage(String pageName, int type) {
		super(pageName, type);
		checkboxListener = new CheckBoxListener();
		jndiAdaptorEnabled = false;
		j2eeDeploymentEnabled = false;
	}


	public boolean getJNDISupport () {
		return jndiAdaptorEnabled;
	}

	public boolean getJ2EESupport() {
		return j2eeDeploymentEnabled;
	}

	public void createControl(Composite parent) {
		Font font = parent.getFont();
		initializeDialogUnits(parent);

		// top level group
		Composite topLevel = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		topLevel.setLayout(layout);
		topLevel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL));
		topLevel.setFont(font);

        Group jndiGroup = new Group(topLevel, SWT.SHADOW_IN);
        GridLayout clientlayout = new GridLayout();
        jndiGroup.setLayout(clientlayout);
        jndiGroup.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL
                | GridData.GRAB_HORIZONTAL));
        jndiGroup.setText(JNDI_OPTIONS_STRING);
        jndiGroup.setFont(topLevel.getFont());

		jndiSupportButton = new Button(jndiGroup, SWT.CHECK);
		jndiSupportButton.setText(JNDI_STRING);
		jndiSupportButton.setSelection(false);
		jndiSupportButton.addSelectionListener(checkboxListener);


        Group j2eeGroup = new Group(topLevel, SWT.SHADOW_IN);
        GridLayout serverlayout = new GridLayout();
        j2eeGroup.setLayout(serverlayout);
        j2eeGroup.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL
                | GridData.GRAB_HORIZONTAL));
        j2eeGroup.setText(J2EE_OPTIONS_STRING);
        j2eeGroup.setFont(topLevel.getFont());

		j2eeDeploymentButton = new Button(j2eeGroup, SWT.CHECK);
		j2eeDeploymentButton.setText(J2EE_STRING);
		j2eeDeploymentButton.setSelection(false);
		j2eeDeploymentButton.addSelectionListener(checkboxListener);

		setErrorMessage(null);
		setMessage(null);
		setControl(topLevel);
	}

	public boolean handleUpdatedSelection(SelectionEvent e) {
		Button s = (Button)e.getSource();
		if (s.equals(jndiSupportButton)) {
			jndiAdaptorEnabled = s.getSelection();
		} else if (s.equals(j2eeDeploymentButton)){
			j2eeDeploymentEnabled = s.getSelection();
		}
		return true;
	}

	class CheckBoxListener extends SelectionAdapter {
		public void widgetSelected(SelectionEvent e) {
			handleUpdatedSelection(e);
		}

		public void widgetDefaultSelected(SelectionEvent e) {
			handleUpdatedSelection(e);
		}
	}
}
