package org.objectstyle.wolips.launching.ui;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.objectstyle.wolips.launching.LaunchingMessages;
import org.objectstyle.wolips.launching.LaunchingPlugin;
import org.objectstyle.wolips.launching.delegates.WOJavaLocalApplicationLaunchConfigurationDelegate;

public class BrowserTab extends AbstractWOArgumentsTab {

	private Button openInBrowserCheckbox;

	private Button webServerConnect;

	protected static final String EMPTY_STRING = ""; //$NON-NLS-1$

	/**
	 * @see ILaunchConfigurationTab#createControl(Composite)
	 */
	public void createControl(Composite parentComposite) {

		Composite parent = new Composite(parentComposite, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.numColumns = 2;
		parent.setLayout(layout);
		GridData data = new GridData();
		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.FILL;
		parent.setLayoutData(data);
		
		Composite buttons = new Composite(parent, SWT.NULL);
		buttons.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
		layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		buttons.setLayout(layout);

		
		this.openInBrowserCheckbox = new Button(buttons, SWT.CHECK);
		this.openInBrowserCheckbox.setText("Use Eclipse Browser");
		data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.heightHint = 20;
		data.heightHint = Math.max(data.heightHint, this.openInBrowserCheckbox
				.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).y);

		// Dialog.convertVerticalDLUsToPixels(new FontMetrics(),
		// IDialogConstants.BUTTON_HEIGHT);
		data.widthHint = 100;
		// Dialog.convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH);
		data.widthHint = Math.max(data.widthHint, this.openInBrowserCheckbox
				.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x);
		this.openInBrowserCheckbox.setLayoutData(data);
		this.openInBrowserCheckbox.setEnabled(true);

		this.openInBrowserCheckbox.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				//setDirty(true);
				updateLaunchConfigurationDialog();
			}
		});

		this.webServerConnect = new Button(buttons, SWT.CHECK);
		this.webServerConnect.setText("WebServerConnect");
		data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.heightHint = 20;
		data.heightHint = Math.max(data.heightHint, this.webServerConnect
				.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).y);

		// Dialog.convertVerticalDLUsToPixels(new FontMetrics(),
		// IDialogConstants.BUTTON_HEIGHT);
		data.widthHint = 100;
		// Dialog.convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH);
		data.widthHint = Math.max(data.widthHint, this.webServerConnect.computeSize(
				SWT.DEFAULT, SWT.DEFAULT, true).x);
		this.webServerConnect.setLayoutData(data);
		this.webServerConnect.setEnabled(true);

		this.webServerConnect.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				//setDirty(true);
				updateLaunchConfigurationDialog();
			}
		});

		Dialog.applyDialogFont(parent);
		this.setControl(parent);
	}


	/**
	 * @see ILaunchConfigurationTab#isValid(ILaunchConfiguration)
	 */
	public boolean isValid(ILaunchConfiguration config) {
		return true;
	}

	/**
	 * Defaults are empty.
	 * 
	 * @see ILaunchConfigurationTab#setDefaults(ILaunchConfigurationWorkingCopy)
	 */
	public void setDefaults(ILaunchConfigurationWorkingCopy config) {
		config
				.setAttribute(
						WOJavaLocalApplicationLaunchConfigurationDelegate.ATTR_WOLIPS_LAUNCH_OPEN_IN_BROWSER,
						"true");
		config
				.setAttribute(
						WOJavaLocalApplicationLaunchConfigurationDelegate.ATTR_WOLIPS_LAUNCH_WEBSERVER_CONNECT,
						"false");
	}

	/**
	 * @see ILaunchConfigurationTab#initializeFrom(ILaunchConfiguration)
	 */
	public void initializeFrom(ILaunchConfiguration configuration) {
		try {
			if (configuration
					.getAttribute(
							WOJavaLocalApplicationLaunchConfigurationDelegate.ATTR_WOLIPS_LAUNCH_OPEN_IN_BROWSER,
							"true").equals("true")) {
				openInBrowserCheckbox.setSelection(true);
			} else {
				openInBrowserCheckbox.setSelection(false);
			}
			if (configuration
					.getAttribute(
							WOJavaLocalApplicationLaunchConfigurationDelegate.ATTR_WOLIPS_LAUNCH_WEBSERVER_CONNECT,
							"false").equals("true")) {
				webServerConnect.setSelection(true);
			} else {
				webServerConnect.setSelection(false);
			}
		} catch (CoreException e) {
			setErrorMessage(LaunchingMessages
					.getString("WOArgumentsTab.Exception_occurred_reading_configuration___15") + e.getStatus().getMessage()); //$NON-NLS-1$
			LaunchingPlugin.getDefault().log(e);
		}
	}

	/**
	 * @see ILaunchConfigurationTab#performApply(ILaunchConfigurationWorkingCopy)
	 */
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		if (openInBrowserCheckbox.getSelection()) {
			configuration
					.setAttribute(
							WOJavaLocalApplicationLaunchConfigurationDelegate.ATTR_WOLIPS_LAUNCH_OPEN_IN_BROWSER,
							"true");
		} else {
			configuration
					.setAttribute(
							WOJavaLocalApplicationLaunchConfigurationDelegate.ATTR_WOLIPS_LAUNCH_OPEN_IN_BROWSER,
							"false");
		}
		if (webServerConnect.getSelection()) {
			configuration
					.setAttribute(
							WOJavaLocalApplicationLaunchConfigurationDelegate.ATTR_WOLIPS_LAUNCH_WEBSERVER_CONNECT,
							"true");
		} else {
			configuration
					.setAttribute(
							WOJavaLocalApplicationLaunchConfigurationDelegate.ATTR_WOLIPS_LAUNCH_WEBSERVER_CONNECT,
							"false");
		}
	}

	/**
	 * @see ILaunchConfigurationTab#getName()
	 */
	public String getName() {
		return LaunchingMessages.getString("BrowserTab.Name"); //$NON-NLS-1$
	}

	/**
	 * @see ILaunchConfigurationTab#getImage()
	 */
	public Image getImage() {
		return LaunchingPlugin.getImageDescriptor("icons/launching/browser-tab.gif").createImage();
	}

	protected void updateLaunchConfigurationDialog() {
		super.updateLaunchConfigurationDialog();
		this.getControl().update();
	}

}
