package org.objectstyle.wolips.wizards;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.dialogs.WizardNewLinkPage;
import org.objectstyle.wolips.wizards.WizardsPlugin.WO_VERSION;

/*
 * Uses the WizardNewLinkPage for basic support plus easy to override
 */
/**
 * Configure WebObjects Direct To Web project configuration options
 * @author dlee
 */
public class D2WApplicationConfigurationPage extends WizardNewLinkPage {
	private Image _d2wbasicimage;
	private Image _d2wneuimage;
	private Image _d2wwolimage;
	private Image _d2wwonderimage;

	private D2WLook _currentLook;
	private WO_VERSION _currentWOVersion;

	//buttons
	Button basicButton = null;
	Button wolButton = null;
	Button neuButton = null;
	Button wonderButton = null;

	Button[] buttons = new Button[] {basicButton, wolButton, neuButton, wonderButton};

	//WO version combo selector
	Combo woVersionList = null;

	//Button text strings
	/**
	 * Set to message key D2WApplicationConfigurationPage.lnf.basic.message
	 */
	public static final String BASIC_BUTTON_STRING = Messages.getString("D2WApplicationConfigurationPage.lnf.basic.message");
	/**
	 * Set to message key D2WApplicationConfigurationPage.lnf.neu.message
	 */
	public static final String NEU_BUTTON_STRING = Messages.getString("D2WApplicationConfigurationPage.lnf.neu.message");
	/**
	 * Set to message key D2WApplicationConfigurationPage.lnf.wol.message
	 */
	public static final String WOL_BUTTON_STRING = Messages.getString("D2WApplicationConfigurationPage.lnf.wol.message");
	/**
	 * Set to message key D2WApplicationConfigurationPage.lnf.classic.message
	 */
	public static final String CLASSIC_BUTTON_STRING = Messages.getString("D2WApplicationConfigurationPage.lnf.classic.message");
	/**
	 * Set to message key D2WApplicationConfigurationPage.lnf.wonder.message
	 */
	public static final String PROJECT_WONDER_STRING = Messages.getString("D2WApplicationConfigurationPage.lnf.wonder.message");

	/**
	 * Listens for button events
	 */
	protected static D2WSelectionListener _d2wListener;

	//Types
	/**
	 * Wraps bindings of d2w template version to template folder used by org.objectstyle.wolips.templateengine
	 */
	public enum D2WLook {
		/**
		 *
		 */
		BASIC54("d2w_bas_application_54"),
		/**
		 *
		 */
		WEBOBJECTS54("d2w_wol_application_54"),
		/**
		 *
		 */
		NEUTRAL54("d2w_neu_application_54"),
		/**
		 *
		 */
		WONDER("wonderd2wapplication"),
		/**
		 *
		 */
		BASIC52("d2w_bas_application_52"),
		/**
		 *
		 */
		WEBOBJECTS52("d2w_wol_application_52"),
		/**
		 *
		 */
		NEUTRAL52("d2w_neu_application_52");

		private String _templateName;

		D2WLook(String templatePath) {
			_templateName = templatePath;
		}

		/**
		 * Template name is the directory in the templates folder of org.objectstyle.wolips.templateengine
		 * @return template folder name
		 * @see org.objectstyle.wolips.templateengine
		 */
		String getTemplatePath() {
			return _templateName;
		}

	}

	/**
	 * Default constructor
	 * @param pageName
	 * @param type
	 */
	public D2WApplicationConfigurationPage(String pageName, int type) {
		super(pageName, type);

		_d2wbasicimage = WizardsPlugin.D2W_BASIC_ICON().createImage();
		_d2wneuimage = WizardsPlugin.D2W_NEU_ICON().createImage();
		_d2wwolimage = WizardsPlugin.D2W_WOL_ICON().createImage();
		_d2wwonderimage = WizardsPlugin.D2W_WONDER_ICON().createImage();

		_d2wListener = new D2WSelectionListener();
	}

	/**
	 * Current selected look.  Basic is default.
	 * @return
	 */
	public D2WLook currentLook () {
		return _currentLook;
	}

	/* (non-Javadoc)
	 * Method declared on IDialogPage.
	 */
	@Override
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

        Group d2wWOVersionGroup = new Group(topLevel, SWT.SHADOW_IN);
        GridLayout versionLayout = new GridLayout();
        d2wWOVersionGroup.setLayout(versionLayout);
        d2wWOVersionGroup.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL
                | GridData.GRAB_HORIZONTAL));
        d2wWOVersionGroup.setText("WebObjects Version:");
        d2wWOVersionGroup.setFont(topLevel.getFont());

        woVersionList = new Combo(d2wWOVersionGroup, SWT.READ_ONLY);
        for (WizardsPlugin.WO_VERSION version : WizardsPlugin.WO_VERSION.values()) {
        	woVersionList.add(version.getDisplayString());
        }
        woVersionList.select(0);  //assume first index should be most current wo version
        woVersionList.addSelectionListener(new D2WComboSelectionListener());

        Group d2wButtonGroup = new Group(topLevel, SWT.SHADOW_IN);
        GridLayout clientlayout = new GridLayout();
        d2wButtonGroup.setLayout(clientlayout);
        d2wButtonGroup.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL
                | GridData.GRAB_HORIZONTAL));
        d2wButtonGroup.setText("Look And Feel");
        d2wButtonGroup.setFont(topLevel.getFont());

		basicButton = new Button(d2wButtonGroup, SWT.RADIO);
		basicButton.setText(BASIC_BUTTON_STRING);
		basicButton.setImage(_d2wbasicimage);
		basicButton.addSelectionListener(_d2wListener);
		basicButton.setSelection(true);

		wolButton = new Button(d2wButtonGroup, SWT.RADIO);
		wolButton.setText(WOL_BUTTON_STRING);
		wolButton.setImage(_d2wwolimage);
		wolButton.setSelection(false);
		wolButton.addSelectionListener(_d2wListener);

		neuButton = new Button(d2wButtonGroup, SWT.RADIO);
		neuButton.setText(NEU_BUTTON_STRING);
		neuButton.setImage(_d2wneuimage);
		neuButton.setSelection(false);
		neuButton.addSelectionListener(_d2wListener);

		wonderButton = new Button(d2wButtonGroup, SWT.RADIO);
		wonderButton.setText(PROJECT_WONDER_STRING);
		wonderButton.setImage(_d2wwonderimage);
		wonderButton.setSelection(false);
		wonderButton.addSelectionListener(_d2wListener);
		wonderButton.setEnabled(false); //FIXME: have not created wonder template support yet.  Disable by default.

		_currentWOVersion = WO_VERSION.WO_54;
		_currentLook = D2WLook.BASIC54;

		setErrorMessage(null);
		setMessage(null);
		setControl(topLevel);

	}


	/**
	 * Default to WO 5.4 if we can't match
	 * @return
	 */
	WO_VERSION getCurrentVersionSelection() {

        for (WO_VERSION version : WO_VERSION.values()) {
        	if (version.getDisplayString().equals(woVersionList.getText())) {
        		return version;
        	}
        }

		return WO_VERSION.WO_54;
	}

	/**
	 * Return currently selected button
	 */
	Button getCurrentSelectedButton() {

		for (Button currButton : buttons) {
			if (currButton.isEnabled()) {
				return currButton;
			}
		}

		return null;
	}

	/**
	 * Match the D2WLook with the button.
	 * @param button
	 * @return
	 */
	D2WLook lookForButton(Button button) {
		D2WLook look;

		if (button.equals(basicButton) && _currentWOVersion.equals(WO_VERSION.WO_54)) {
			look = D2WLook.BASIC54;
		} else if (button.equals(wolButton) && _currentWOVersion.equals(WO_VERSION.WO_54)){
			look = D2WLook.WEBOBJECTS54;
		} else if (button.equals(neuButton) && _currentWOVersion.equals(WO_VERSION.WO_54)){
			look = D2WLook.NEUTRAL54;
		} else if (button.equals(basicButton) && (_currentWOVersion.equals(WO_VERSION.WO_52) || _currentWOVersion.equals(WO_VERSION.WO_53))) {
			look = D2WLook.BASIC52;
		} else if (button.equals(wolButton) && (_currentWOVersion.equals(WO_VERSION.WO_52) || _currentWOVersion.equals(WO_VERSION.WO_53))) {
			look = D2WLook.WEBOBJECTS52;
		} else if (button.equals(neuButton) && (_currentWOVersion.equals(WO_VERSION.WO_52) || _currentWOVersion.equals(WO_VERSION.WO_53))) {
			look = D2WLook.NEUTRAL52;
		} else {
			System.out.println("Can't match selected look.  Picking default "+D2WLook.BASIC54);
			look = D2WLook.BASIC54;
		}

		return look;
	}

	/**
	 * Handle button events
	 * @param e
	 * @return
	 */
	//e.item is null so we can't compare against it
	//only non-null value is the button text
	public boolean handleUpdatedSelection(SelectionEvent e) {

		Object source = e.getSource();
		if (source instanceof Button) {
			_currentWOVersion = getCurrentVersionSelection();
			_currentLook = lookForButton((Button)source);
		} else if (source instanceof Combo) {
			_currentWOVersion = getCurrentVersionSelection();
			_currentLook = lookForButton(getCurrentSelectedButton());
		}
		return true;
	}

	class D2WComboSelectionListener implements SelectionListener {

		public void widgetDefaultSelected(SelectionEvent e) {
			handleUpdatedSelection(e);
		}

		public void widgetSelected(SelectionEvent e) {
			handleUpdatedSelection(e);
		}

	}

	class D2WSelectionListener extends SelectionAdapter {
		public void widgetSelected(SelectionEvent e) {
			handleUpdatedSelection(e);
		}

		public void widgetDefaultSelected(SelectionEvent e) {
			handleUpdatedSelection(e);
		}
	}

}
