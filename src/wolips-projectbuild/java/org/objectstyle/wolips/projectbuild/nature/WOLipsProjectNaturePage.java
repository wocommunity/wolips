package org.objectstyle.wolips.projectbuild.nature;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.dialogs.PropertyPage;
import org.objectstyle.wolips.projectbuild.WOProjectBuildConstants;


public class WOLipsProjectNaturePage 
  extends PropertyPage 
  implements IAdaptable, WOProjectBuildConstants 
{

	private static final String PATH_TITLE = "Path:";

	private static final String WO_NATURE_TITLE = "Is a WebObjects Project";

	private static final String WO_IS_FRAMEWORK_TITLE = "Is a Framework";
	private static final int TEXT_FIELD_WIDTH = 50;
	/**
	 * Constructor for WOLipsProjectNaturePage.
	 */
	public WOLipsProjectNaturePage() {
		super();
	}

  private void _addFirstSection(Composite parent) {
    Composite group = new Composite(parent,SWT.NONE);
    GridLayout layout = new GridLayout();
    layout.numColumns = 3;
    group.setLayout(layout);
    group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

    _woNatureCheck = new Button(group, SWT.CHECK | SWT.LEFT);
    _woNatureCheck.setText(WO_NATURE_TITLE);
    _woNatureCheck.setEnabled(true);

    try {   
      _woNatureCheck.setSelection(_getProject().hasNature(NATURE_ID));
    } catch (CoreException up) {
      //WOSupportPlugin.log(ex.getMessage());  
      up.printStackTrace ();
    }
  }

	private void _addSeparator(Composite parent) {
		Label separator = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		separator.setLayoutData(gridData);
	}

	private void _addSecondSection(Composite parent) {
		Composite group = _createDefaultComposite(parent);

	    // project kind field (is framework?)
	    _woIsFrameworkCheck = new Button(group, SWT.CHECK | SWT.LEFT);
	    _woIsFrameworkCheck.setText(WO_IS_FRAMEWORK_TITLE);
	    _woIsFrameworkCheck.setEnabled(true);
	
		try {
      		_woIsFrameworkCheck.setSelection(_woNature().isFramework());
		} catch (Exception e) {
	      	_woIsFrameworkCheck.setSelection(false);
	    }
	}

	/**
	 * @see PreferencePage#createContents(Composite)
	 */
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		composite.setLayout(layout);
		GridData data = new GridData(GridData.FILL);
		data.grabExcessHorizontalSpace = true;
		composite.setLayoutData(data);

		_addFirstSection(composite);
		_addSeparator(composite);
		_addSecondSection(composite);
		return composite;
	}

	private Composite _createDefaultComposite(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);

		GridData data = new GridData();
		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.FILL;
		composite.setLayoutData(data);

		return composite;
	}

	protected void performDefaults() {
	}
	
	public boolean performOk() {
		// store the value in the owner text field
		try {
		    if(_woNatureCheck.getSelection()) {   
		    _woNature(true).setIsFramework(_woIsFrameworkCheck.getSelection());
				WOIncrementalBuildNature.s_addToProject(_getProject());
		    } else {
				WOIncrementalBuildNature.s_removeFromProject(_getProject());
		    }
	    } catch (CoreException up) {
		    up.printStackTrace();
		      
			return false;
		}

		return true;
	}

	public WOIncrementalBuildNature _woNature() throws CoreException {
	    return _woNature(false);
	}

	public WOIncrementalBuildNature _woNature(boolean force) throws CoreException {
		IProject project = (IProject)(this.getElement().getAdapter(IProject.class));
		WOIncrementalBuildNature won = WOIncrementalBuildNature.s_getNature(project);
	    if ((null == won) && force) {
			won = new WOIncrementalBuildNature ();
			won.setProject(project);
	    }
	    return won;
	}

  public IJavaProject _getJavaProject() throws CoreException {
    IProject project = (IProject)(this.getElement().getAdapter(IProject.class));
    return (IJavaProject)(project.getNature(JavaCore.NATURE_ID));
  }
  
  public IProject _getProject() throws CoreException {
    IProject project = (IProject)(this.getElement().getAdapter(IProject.class));
    return (project);
  }
  
  public Object getAdapter (Class theClass) {
    return (Platform.getAdapterManager().getAdapter(this, theClass));
  }


  private Button _woNatureCheck;
  private Button _woIsFrameworkCheck;
}