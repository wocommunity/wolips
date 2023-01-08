package org.objectstyle.wolips.wooeditor.widgets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;

public class RadioGroup {
	private ListenerList selectionEventListeners = new ListenerList();
	private List<Button> group = new ArrayList<Button>();
	private Button selected;

	private SelectionListener changeListener = new SelectionListener() {

		public void widgetDefaultSelected(final SelectionEvent e) {
			setSelection(((Button) e.getSource()).getText());
		}

		public void widgetSelected(final SelectionEvent e) {
			Button b = (Button) e.getSource();
			if (b.getSelection()) {
				setSelection(b.getText());
			}
		}

	};

	public RadioGroup() {
	}

	public boolean add(final Button button) {
		button.addSelectionListener(changeListener);
		return group.add(button);
	}

	public void add(final int index, final Button button) {
		button.addSelectionListener(changeListener);
		group.add(index, button);
	}

	public boolean addAll(final Collection<Button> collection) {
		for (Button button : collection) {
			button.addSelectionListener(changeListener);
		}
		return group.addAll(collection);
	}

	public boolean addAll(final int index,
			final Collection<Button> collection) {
		for (Button button : collection) {
			button.addSelectionListener(changeListener);
		}
		return group.addAll(index, collection);
	}

	public void clear() {
		for (Button button : group) {
			button.removeSelectionListener(changeListener);
		}
		group.clear();
	}

	public boolean contains(final Object value) {
		return group.contains(value);
	}

	public boolean containsAll(final Collection<Button> collection) {
		return group.containsAll(collection);
	}

	public Object get(final int index) {
		return group.get(index);
	}

	public int indexOf(final Object value) {
		return group.indexOf(value);
	}

	public boolean isEmpty() {
		return group.isEmpty();
	}

	public Iterator<Button> iterator() {
		return group.iterator();
	}

	public int lastIndexOf(final Object value) {
		return group.lastIndexOf(value);
	}

	public ListIterator<Button> listIterator() {
		return group.listIterator();
	}

	public ListIterator<Button> listIterator(final int arg0) {
		return group.listIterator(arg0);
	}

	public boolean remove(final Button button) {
		button.removeSelectionListener(changeListener);
		return group.remove(button);
	}

	public Object remove(final int index) {
		group.get(index).removeSelectionListener(changeListener);
		return group.remove(index);
	}

	public boolean removeAll(final Collection<Button> arg0) {
		for (Button button : group) {
			button.removeSelectionListener(changeListener);
		}
		return group.removeAll(arg0);
	}

	public int size() {
		return group.size();
	}

	public List<Button> subList(final int arg0, final int arg1) {
		return group.subList(arg0, arg1);
	}

	public Object[] toArray() {
		return group.toArray();
	}

	public Object[] toArray(final Object[] arg0) {
		return group.toArray(arg0);
	}

	public void removeSelectionListener(
			final SelectionListener selectionListener) {
		selectionEventListeners.remove(selectionListener);
	}

	public void addSelectionListener(
			final SelectionListener selectionListener) {
		selectionEventListeners.add(selectionListener);
	}

	public void setSelection(final Object value) {
		if (group.size() < 1) {
			return;
		}
		for (Button i : group) {
			if (i.getText().equals(value)) {
				selected = i;
			} else {
				i.setSelection(false);
			}
		}
		if (selected != null) {
			selected.setSelection(true);
			Event e = new Event();
			e.type = SWT.Selection;
			e.item = selected;
			e.widget = selected;
			e.text = selected.getText();
			fireSelectionEvent(new SelectionEvent(e));
		}
	}

	private void fireSelectionEvent(final SelectionEvent e) {
		if (e.item != null && e.item.isDisposed()) {
			return;
		}
		Object[] l = selectionEventListeners.getListeners();
		for (int i = 0; i < l.length; i++) {
			((SelectionListener) l[i]).widgetSelected(e);
		}
	}

	public Object getSelection() {
		if (group.size() < 1) {
			return null;
		}
		return selected.getText();
	}

}
