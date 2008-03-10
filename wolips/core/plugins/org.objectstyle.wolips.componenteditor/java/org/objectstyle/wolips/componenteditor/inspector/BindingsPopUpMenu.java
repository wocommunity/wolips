package org.objectstyle.wolips.componenteditor.inspector;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Decorations;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.objectstyle.wolips.bindings.api.IApiBinding;
import org.objectstyle.wolips.bindings.api.Wo;
import org.objectstyle.wolips.bindings.wod.IWodElement;
import org.objectstyle.wolips.bindings.wod.WodProblem;
import org.objectstyle.wolips.componenteditor.ComponenteditorPlugin;
import org.objectstyle.wolips.wodclipse.core.completion.WodParserCache;
import org.objectstyle.wolips.wodclipse.core.refactoring.RefactoringWodElement;
import org.objectstyle.wolips.wodclipse.core.util.WodModelUtils;

public class BindingsPopUpMenu {
	private Menu _menu;

	private WodParserCache _cache;

	public BindingsPopUpMenu(Decorations parent, WodParserCache cache) {
		_menu = new Menu(parent, SWT.POP_UP);
		_cache = cache;
	}

	public Menu getMenu() {
		return _menu;
	}

	public void dispose() {
		_menu.dispose();
	}

	public boolean showMenuAtLocation(IWodElement wodElement, String droppedKeyPath, Point location) throws Exception {
		boolean showMenu = false;
		Wo api = wodElement.getApi(_cache.getJavaProject(), WodParserCache.getTypeCache());
		if (api != null) {
			IApiBinding[] apiBindings = wodElement.getApiBindings(api);
			if (apiBindings != null && apiBindings.length > 0) {
				Set<IApiBinding> keyBindings = new TreeSet<IApiBinding>();
				Set<IApiBinding> actionBindings = new TreeSet<IApiBinding>();
				for (IApiBinding binding : apiBindings) {
					if (binding.isAction()) {
						actionBindings.add(binding);
						showMenu = true;
					} else {
						keyBindings.add(binding);
						showMenu = true;
					}
				}

				for (MenuItem item : _menu.getItems()) {
					item.dispose();
				}

				List<WodProblem> wodProblems = WodModelUtils.getProblems(wodElement, _cache);

				BindingSelectionListener selectionListener = new BindingSelectionListener(wodElement, droppedKeyPath, _cache);
				for (IApiBinding keyBinding : keyBindings) {
					MenuItem mi = createMenuItem(wodElement, keyBinding, wodProblems);
					mi.addSelectionListener(selectionListener);
				}
				if (!keyBindings.isEmpty() && !actionBindings.isEmpty()) {
					new MenuItem(_menu, SWT.SEPARATOR);
				}
				for (IApiBinding actionBinding : actionBindings) {
					MenuItem mi = createMenuItem(wodElement, actionBinding, wodProblems);
					mi.addSelectionListener(selectionListener);
				}
				
				if (showMenu) {
					_menu.setLocation(location.x, location.y);
					_menu.setVisible(true);
				}
			}
			else {
				showMenu = false;
			}
		}
		
		return showMenu;
	}
	
	protected MenuItem createMenuItem(IWodElement element, IApiBinding binding, List<WodProblem> wodProblems) {
		MenuItem menuItem = new MenuItem(_menu, SWT.NONE);
		menuItem.setData(binding);
		menuItem.setText(binding.getName());
		if (element.getBindingNamed(binding.getName()) != null) {
			menuItem.setImage(ComponenteditorPlugin.getDefault().getImage(ComponenteditorPlugin.CONNECTED_ICON));
		}
		else if (WodModelUtils.hasValidationProblem(binding, wodProblems)) {
			menuItem.setImage(ComponenteditorPlugin.getDefault().getImage(ComponenteditorPlugin.UNCONNECTED_PROBLEM_ICON));
		}
		else {
			menuItem.setImage(ComponenteditorPlugin.getDefault().getImage(ComponenteditorPlugin.UNCONNECTED_ICON));
		}
		return menuItem;
	}

	protected static class BindingSelectionListener implements SelectionListener {
		private IWodElement _wodElement;

		private String _droppedKeyPath;

		private WodParserCache _cache;

		public BindingSelectionListener(IWodElement wodElement, String droppedKeyPath, WodParserCache cache) {
			_wodElement = wodElement;
			_droppedKeyPath = droppedKeyPath;
			_cache = cache;
		}

		public void widgetDefaultSelected(SelectionEvent event) {
			widgetSelected(event);
		}

		public void widgetSelected(SelectionEvent event) {
			MenuItem item = (MenuItem) event.widget;
			item.setImage(ComponenteditorPlugin.getDefault().getImage(ComponenteditorPlugin.CONNECTED_ICON));
			IApiBinding apiBinding = (IApiBinding) item.getData();
			RefactoringWodElement refactoringWodElement = new RefactoringWodElement(_wodElement, _cache);
			try {
				refactoringWodElement.setValueForBinding(_droppedKeyPath, apiBinding.getName());
			} catch (Exception e) {
				e.printStackTrace();
				ComponenteditorPlugin.getDefault().log("Failed to add binding.", e);
			}
		}
	}
}
