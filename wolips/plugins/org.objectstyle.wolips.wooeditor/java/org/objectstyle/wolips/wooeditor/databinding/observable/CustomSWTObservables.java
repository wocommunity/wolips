package org.objectstyle.wolips.wooeditor.databinding.observable;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.objectstyle.wolips.wooeditor.widgets.RadioGroup;

public class CustomSWTObservables {
	
	public static ComboObservableValue observeSelection(Control control) {
		if (control instanceof Combo) {
			return new ComboObservableValue((Combo) control,
					SWTProperties.SELECTION);
		}

		throw new IllegalArgumentException(
				"Widget [" + control.getClass().getName() + "] is not supported."); //$NON-NLS-1$//$NON-NLS-2$
	}
	
	public static ComboObservableValue observeText(Control control) {
		if (control instanceof Combo) {
			return new ComboObservableValue((Combo) control, SWTProperties.TEXT);
		} 
		
		throw new IllegalArgumentException(
				"Widget [" + control.getClass().getName() + "] is not supported."); //$NON-NLS-1$//$NON-NLS-2$
	}

	public static IObservableValue<Object> observeSelection(RadioGroup group) {
		return new RadioGroupObservableValue(group);
	}
}
