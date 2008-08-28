package org.objectstyle.wolips.eomodeler.utils;

import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class BooleanUpdateValueStrategy extends UpdateValueStrategy {
	public BooleanUpdateValueStrategy() {
		super();
	}

	public BooleanUpdateValueStrategy(int updatePolicy) {
		super(updatePolicy);
	}

	public BooleanUpdateValueStrategy(boolean provideDefaults, int updatePolicy) {
		super(provideDefaults, updatePolicy);
	}
	
	@Override
	public Object convert(Object value) {
		Object finalValue = value;
		if (value == null) {
			finalValue = Boolean.FALSE;
		}
		return super.convert(finalValue);
	}

	@Override
	protected IStatus doSet(IObservableValue observableValue, Object value) {
		Object finalValue = value;
		if (value == null) {
			finalValue = Boolean.FALSE;
		}
		return super.doSet(observableValue, finalValue);
	}

	@Override
	public IStatus validateAfterGet(Object value) {
		IStatus status = super.validateAfterGet(value);
		if (value == null) {
			status = Status.OK_STATUS;
		}
		return status;
	}
}
