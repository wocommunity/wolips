package org.objectstyle.wolips.wizards;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.dialogs.WizardNewLinkPage;

/*
 * Uses the WizardNewLinkPage for basic support plus easy to override
 */
public class D2WApplicationConfigurationPage extends WizardNewLinkPage {
	protected Image _d2wbasicimage;
	protected Image _d2wneuimage;
	protected Image _d2wwolimage;
	protected Image _d2wclassicimage;
	protected Image _d2wwonderimage;

	//buttons
	Button basicButton = null;
	Button wolButton =null;
	Button neuButton = null;
	Button classicButton = null;
	Button wonderButton = null;

	//Button text strings
	public static final String BASIC_BUTTON_STRING = Messages.getString("D2WApplicationConfigurationPage.lnf.basic.message");
	public static final String NEU_BUTTON_STRING = Messages.getString("D2WApplicationConfigurationPage.lnf.neu.message");
	public static final String WOL_BUTTON_STRING = Messages.getString("D2WApplicationConfigurationPage.lnf.wol.message");
	public static final String CLASSIC_BUTTON_STRING = Messages.getString("D2WApplicationConfigurationPage.lnf.classic.message");
	public static final String PROJECT_WONDER_STRING = Messages.getString("D2WApplicationConfigurationPage.lnf.wonder.message");

	protected static D2WSelectionListener _d2wListener;

	//Types
	public enum D2WLook {BASIC, WEBOBJECTS, NEUTRAL, CLASSIC, WONDER }

	private D2WLook _currentLook;

	public D2WApplicationConfigurationPage(String pageName, int type) {
		super(pageName, type);

		_d2wbasicimage = WizardsPlugin.D2W_BASIC_ICON().createImage();
		_d2wneuimage = WizardsPlugin.D2W_NEU_ICON().createImage();
		_d2wwolimage = WizardsPlugin.D2W_WOL_ICON().createImage();
		_d2wclassicimage = WizardsPlugin.D2W_CLASSIC_ICON().createImage();
		_d2wwonderimage = WizardsPlugin.D2W_WONDER_ICON().createImage();

		_d2wListener = new D2WSelectionListener();
	}

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
		basicButton.setSelection(true);
		basicButton.addSelectionListener(_d2wListener);

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

		classicButton = new Button(d2wButtonGroup, SWT.RADIO);
		classicButton.setText(CLASSIC_BUTTON_STRING);
		classicButton.setImage(_d2wclassicimage);
		classicButton.setSelection(false);
		classicButton.addSelectionListener(_d2wListener);

		wonderButton = new Button(d2wButtonGroup, SWT.RADIO);
		wonderButton.setText(PROJECT_WONDER_STRING);
		wonderButton.setImage(_d2wwonderimage);
		wonderButton.setSelection(false);
		wonderButton.addSelectionListener(_d2wListener);

		setErrorMessage(null);
		setMessage(null);
		setControl(topLevel);
	}

	//e.item is null so we can't compare against it
	//only non-null value is the button text
	public boolean handleUpdatedSelection(SelectionEvent e) {
		Button s = (Button)e.getSource();
		if (s.equals(basicButton)) {
			_currentLook = D2WLook.BASIC;
		} else if (s.equals(wolButton)){
			_currentLook = D2WLook.WEBOBJECTS;
		} else if (s.equals(neuButton)){
			_currentLook = D2WLook.NEUTRAL;
		} else if (s.equals(classicButton)){
			_currentLook = D2WLook.CLASSIC;
		} else {
			_currentLook = D2WLook.BASIC;
			return false;
		}
		return true;
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
