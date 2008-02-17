package org.objectstyle.wolips.wooeditor.databinding.observable;

import org.eclipse.core.databinding.observable.value.AbstractObservableValue;
import org.eclipse.core.databinding.observable.value.ValueDiff;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.objectstyle.wolips.wooeditor.widgets.RadioGroup;

public class RadioGroupObservableValue extends AbstractObservableValue {
	private RadioGroup myGroup;
	private Object mySelection;

	private SelectionListener selectionListener = new SelectionListener() {
		public void widgetDefaultSelected(final SelectionEvent e) {
			widgetSelected(e);
		}

		public void widgetSelected(final SelectionEvent e) {
			final Object newSelection = myGroup.getSelection();
			fireValueChange(new ValueDiff() {
				@Override
        public Object getNewValue() {
					return newSelection;
				}

				@Override
        public Object getOldValue() {
					return mySelection;
				}
			});
			mySelection = newSelection;
		}

	};

	public RadioGroupObservableValue(final RadioGroup group) {
		this.myGroup = group;
		group.addSelectionListener(selectionListener);
	}

	@Override
  public synchronized void dispose() {
		myGroup.removeSelectionListener(selectionListener);
	}

	@Override
  protected void doSetValue(final Object value) {
		myGroup.setSelection(value);
		mySelection = value;
	}

	@Override
  protected Object doGetValue() {
		return myGroup.getSelection();
	}

	public Object getValueType() {
		return Object.class;
	}
}
