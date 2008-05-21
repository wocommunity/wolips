package org.objectstyle.wolips.eomodeler.editors.qualifier;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.objectstyle.wolips.eomodeler.core.model.qualifier.EOAggregateQualifier;
import org.objectstyle.wolips.eomodeler.core.model.qualifier.EOQualifier;

public class EOQualifierEditor extends Composite implements IQualifierTypeEditorListener {
	private ComboViewer _typeCombo;

	private Composite _typeEditorContainer;

	private Button _removeButton;

	private Button _addButton;

	private IQualifierType _qualifierType;

	private AbstractQualifierTypeEditor _typeEditor;

	private boolean _settingQualifier;

	private IQualifierType[] _qualifierTypes = new IQualifierType[] { new ExpressionQualifierType(), new SelectKeyQualifierType(), new KeyValueQualifierType(), new AndQualifierType(), new OrQualifierType(), new NotQualifierType() };

	private IQualifierEditorListener _listener;

	public EOQualifierEditor(Composite parent, int style) {
		super(parent, style);

		GridLayout layout = new GridLayout(4, false);
		layout.marginLeft = 0;
		layout.marginRight = 0;
		layout.marginWidth = 0;
		layout.marginTop = 0;
		layout.marginBottom = 0;
		layout.marginHeight = 0;
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		setLayout(layout);

		_typeCombo = new ComboViewer(this, SWT.READ_ONLY);
		_typeCombo.add(_qualifierTypes);
		_typeCombo.addSelectionChangedListener(new TypeSelectionHandler());
		_typeCombo.getCombo().setLayoutData(new GridData());

		_typeEditorContainer = new Composite(this, SWT.NONE);
		_typeEditorContainer.setLayout(new FillLayout(SWT.HORIZONTAL));
		_typeEditorContainer.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	public void setQualifierEditorListener(IQualifierEditorListener listener) {
		_listener = listener;
	}

	public IQualifierEditorListener getQualifierEditorListener() {
		return _listener;
	}

	protected void createButtons() {
		_removeButton = new Button(this, SWT.PUSH);
		_removeButton.setText("-");
		_removeButton.setLayoutData(new GridData());
		_removeButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}

			public void widgetSelected(SelectionEvent e) {
				IQualifierEditorListener listener = getQualifierEditorListener();
				if (listener != null) {
					listener.qualifierRemoved(EOQualifierEditor.this);
				}
			}
		});

		_addButton = new Button(this, SWT.PUSH);
		_addButton.setText("+");
		_addButton.setLayoutData(new GridData());
		_addButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}

			public void widgetSelected(SelectionEvent e) {
				IQualifierEditorListener listener = getQualifierEditorListener();
				if (listener != null) {
					listener.qualifierAddedBelow(EOQualifierEditor.this);
				}
			}
		});
	}

	public EOQualifier getQualifier() {
		return (_typeEditor == null) ? null : _typeEditor.getQualifier();
	}

	public void setQualifier(EOQualifier qualifier) {
		IQualifierType matchingQualifierType = null;
		for (IQualifierType qualifierType : _qualifierTypes) {
			if (!(qualifierType instanceof ExpressionQualifierType) && qualifierType.isTypeFor(qualifier)) {
				matchingQualifierType = qualifierType;
			}
		}
		if (matchingQualifierType == null) {
			for (IQualifierType qualifierType : _qualifierTypes) {
				if (qualifierType instanceof ExpressionQualifierType) {
					matchingQualifierType = qualifierType;
				}
			}
		}

		_settingQualifier = true;
		try {
			_typeCombo.setSelection(new StructuredSelection(matchingQualifierType));
			_typeEditor.setQualifier(qualifier);
			refreshQualifierTypes();
		} finally {
			_settingQualifier = false;
		}
	}

	protected void typeChanged() {
		IStructuredSelection selection = (IStructuredSelection) _typeCombo.getSelection();
		IQualifierType selectedQualifierType = (IQualifierType) selection.getFirstElement();

		if (selectedQualifierType != null && _qualifierType != selectedQualifierType) {
			EOQualifier previousQualifier = getQualifier();
			if (_typeEditor != null) {
				_typeEditor.dispose();
				_typeEditor = null;
			}
			_qualifierType = selectedQualifierType;
			if (_qualifierType != null) {
				_typeEditor = _qualifierType.createEditor(_typeEditorContainer);
				_typeEditor.setQualifierTypeEditorListener(this);
				// _typeEditor.setLayoutData(new
				// GridData(GridData.FILL_HORIZONTAL));
			}
			if (!_settingQualifier) {
				_typeEditor.setQualifier(previousQualifier);
				refreshQualifierTypes();
			}
		}
		// layout(true, true);
		getParent().layout(true, true);
	}

	protected void refreshQualifierTypes() {
		// IStructuredSelection selection = (IStructuredSelection)
		// _typeCombo.getSelection();
		// IQualifierType selectedQualifierType = (IQualifierType)
		// selection.getFirstElement();
		EOQualifier qualifier = _typeEditor.getQualifier();
		for (IQualifierType qualifierType : _qualifierTypes) {
			qualifierType.setQualifier(qualifier);
			_typeCombo.refresh(qualifierType);
		}

		boolean buttonsVisible = !(qualifier instanceof EOAggregateQualifier);
		if (!buttonsVisible) {
			if (_addButton != null) {
				_addButton.dispose();
				_addButton = null;
			}
			if (_removeButton != null) {
				_removeButton.dispose();
				_removeButton = null;
			}
			((GridLayout) getLayout()).numColumns = 2;
		} else {
			((GridLayout) getLayout()).numColumns = 4;
			createButtons();
		}
		layout();
	}

	public void qualifierTypeChanged(AbstractQualifierTypeEditor editor) {
		setQualifier(getQualifier());
	}

	protected class TypeSelectionHandler implements ISelectionChangedListener {
		public void selectionChanged(SelectionChangedEvent event) {
			EOQualifierEditor.this.typeChanged();
		}
	}
}
