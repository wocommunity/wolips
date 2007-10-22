/**
 * 
 */
package org.objectstyle.wolips.wodclipse.action;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.objectstyle.wolips.wodclipse.core.completion.WodCompletionProposal;
import org.objectstyle.wolips.wodclipse.core.completion.WodCompletionUtils;

public class ComponentLiveSearch implements ModifyListener, SelectionListener {
	private IJavaProject _project;

	private IProgressMonitor _progressMonitor;

	private String _lastSearch;

	private Object _completionLock = new Object();

	public ComponentLiveSearch(IJavaProject project, IProgressMonitor progressMonitor) {
		_project = project;
		_progressMonitor = progressMonitor;
	}

	public void attachTo(Combo combo) {
		if (combo != null) {
			combo.addModifyListener(this);
			combo.addSelectionListener(this);
		}
	}

	public void detachFrom(Combo combo) {
		if (combo != null) {
			combo.removeModifyListener(this);
			combo.removeSelectionListener(this);
		}
	}

	public void widgetDefaultSelected(SelectionEvent e) {
		widgetSelected(e);
	}

	public void widgetSelected(SelectionEvent e) {
		Combo componentNameCombo = (Combo) e.getSource();
		if (componentNameCombo.getItemCount() > 0) {
			componentNameCombo.select(0);
		}
	}

	public void modifyText(ModifyEvent e) {
		final Combo componentNameCombo = (Combo) e.getSource();
		Point selection = componentNameCombo.getSelection();
		final String partialName;
		if (selection != null && selection.x != selection.y) {
			partialName = componentNameCombo.getText().substring(0, selection.x);
		} else {
			partialName = componentNameCombo.getText();
		}
		if (_lastSearch == null || !_lastSearch.equals(partialName)) {
			_lastSearch = partialName;
			_progressMonitor.setCanceled(true);

			WorkspaceJob job = new WorkspaceJob("Searching for components ...") {
				public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
					synchronized (_completionLock) {
						_progressMonitor.setCanceled(false);
						try {
							final Set<WodCompletionProposal> proposals = new HashSet<WodCompletionProposal>();
							WodCompletionUtils.fillInElementTypeCompletionProposals(_project, partialName, 0, partialName.length(), proposals, false, _progressMonitor);
							if (!_progressMonitor.isCanceled()) {
								Display.getDefault().asyncExec(new Runnable() {
									public void run() {
										if (!componentNameCombo.isDisposed()) {
											componentNameCombo.remove(0, componentNameCombo.getItemCount() - 1);
											boolean exactMatch = false;
											for (WodCompletionProposal elementName : proposals) {
												String displayString = elementName.getDisplayString();
												if (displayString != null && displayString.equals(_lastSearch)) {
													exactMatch = true;
												}
												componentNameCombo.add(displayString);
											}
											try {
												Method setListVisible = componentNameCombo.getClass().getDeclaredMethod("setListVisible", boolean.class);
												setListVisible.setAccessible(true);
												if (!(exactMatch && componentNameCombo.getItemCount() == 1)) {
													setListVisible.invoke(componentNameCombo, true);
												} else {
													setListVisible.invoke(componentNameCombo, false);
												}
											} catch (Throwable ex) {
												ex.printStackTrace();
											}
										}
									}
								});
							}
						} catch (OperationCanceledException t) {
							// ignore
						} catch (Throwable t) {
							t.printStackTrace();
						}
						return Status.OK_STATUS;
					}
				}
			};
			job.schedule();
		}
	}
}