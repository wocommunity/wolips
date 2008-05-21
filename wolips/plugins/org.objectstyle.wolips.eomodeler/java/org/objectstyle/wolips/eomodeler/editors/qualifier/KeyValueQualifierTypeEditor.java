/**
 * 
 */
package org.objectstyle.wolips.eomodeler.editors.qualifier;

import java.util.List;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.objectstyle.wolips.eomodeler.core.model.EOQualifierFactory;
import org.objectstyle.wolips.eomodeler.core.model.qualifier.EOAggregateQualifier;
import org.objectstyle.wolips.eomodeler.core.model.qualifier.EOKeyComparisonQualifier;
import org.objectstyle.wolips.eomodeler.core.model.qualifier.EOKeyValueQualifier;
import org.objectstyle.wolips.eomodeler.core.model.qualifier.EOQualifier;
import org.objectstyle.wolips.eomodeler.core.model.qualifier.EOTruePredicate;

public class KeyValueQualifierTypeEditor extends AbstractQualifierTypeEditor {
	private String _key;

	private ComboViewer _operatorCombo;

	private Text _valueText;

	public KeyValueQualifierTypeEditor(Composite parent, int style) {
		super(parent, style);
		GridLayout layout = new GridLayout(2, false);
		layout.marginLeft = 0;
		layout.marginRight = 0;
		layout.marginWidth = 0;
		layout.marginTop = 0;
		layout.marginBottom = 0;
		layout.marginHeight = 0;
		layout.horizontalSpacing = 0;
		setLayout(layout);

		_operatorCombo = new ComboViewer(this, SWT.READ_ONLY);
		_operatorCombo.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				String text;
				if (element == null) {
					text = "";
				} else {
					text = ((EOQualifier.Comparison) element).getDisplayName();
				}
				return text;
			}
		});
		_operatorCombo.add(new EOQualifier.Comparison("="));
		_operatorCombo.add(new EOQualifier.Comparison("<>", "!="));
		_operatorCombo.add(new EOQualifier.Comparison("<"));
		_operatorCombo.add(new EOQualifier.Comparison("<="));
		_operatorCombo.add(new EOQualifier.Comparison(">"));
		_operatorCombo.add(new EOQualifier.Comparison(">="));
		_operatorCombo.add(new EOQualifier.Comparison("contains"));
		_operatorCombo.add(new EOQualifier.Comparison("like"));
		_operatorCombo.add(new EOQualifier.Comparison("caseinsensitivelike", "like (any case)"));
		_operatorCombo.getCombo().setLayoutData(new GridData());

		_valueText = new Text(this, SWT.BORDER);
		_valueText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	public void setQualifier(EOQualifier qualifier) {
		EOQualifier finalQualifier = qualifier;
		if (finalQualifier instanceof EOAggregateQualifier) {
			List<EOQualifier> qualifiers = ((EOAggregateQualifier) finalQualifier).getQualifiers();
			if (!qualifiers.isEmpty()) {
				finalQualifier = qualifiers.get(0);
			}
		}

		if (finalQualifier instanceof EOKeyValueQualifier) {
			EOKeyValueQualifier kvQualifier = (EOKeyValueQualifier) finalQualifier;
			_key = kvQualifier.getKey();
			_operatorCombo.setSelection(new StructuredSelection(kvQualifier.getComparison()));
			String valueStr;
			Object value = kvQualifier.getValue();
			if (value instanceof String) {
				String escapedValue = (String) value;
				escapedValue = escapedValue.replaceAll("'", "\\'");
				valueStr = "'" + escapedValue + "'";
			} else if (value instanceof Number) {
				valueStr = value.toString();
			} else {
				valueStr = (value == null) ? "" : value.toString();
			}
			_valueText.setText(valueStr);
		} else if (finalQualifier instanceof EOKeyComparisonQualifier) {
			EOKeyComparisonQualifier kcQualifier = (EOKeyComparisonQualifier) finalQualifier;
			_key = kcQualifier.getLeftKey();
			_operatorCombo.setSelection(new StructuredSelection(kcQualifier.getComparison()));
			String valueStr = (kcQualifier.getRightKey() == null) ? "" : kcQualifier.getRightKey();
			_valueText.setText(valueStr);
		} else {
			_operatorCombo.setSelection(new StructuredSelection(_operatorCombo.getElementAt(0)));
			_valueText.setText("");
		}
	}

	public EOQualifier getQualifier() {
		EOQualifier qualifier;
		if (_key == null) {
			qualifier = null;
		} else {
			EOQualifier.Comparison comparison = (EOQualifier.Comparison) ((IStructuredSelection) _operatorCombo.getSelection()).getFirstElement();
			String value = _valueText.getText();
			qualifier = EOQualifierFactory.fromString(_key + " " + comparison + " " + value);
		}
		if (qualifier == null) {
			qualifier = new EOTruePredicate();
		}
		return qualifier;
	}
}