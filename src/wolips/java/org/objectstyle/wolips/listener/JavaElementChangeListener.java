package org.objectstyle.wolips.listener;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.ElementChangedEvent;
import org.eclipse.jdt.core.IElementChangedListener;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaElementDelta;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.objectstyle.wolips.IWOLipsPluginConstants;
import org.objectstyle.wolips.WOLipsPlugin;
import org.objectstyle.wolips.project.PBProjectUpdater;
import org.objectstyle.woproject.pb.PBProject;

import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;

/**
 * Tracking changes in classpath and synchronizes webobjects project file
 */
public class JavaElementChangeListener implements IElementChangedListener {

	/**
	 * Constructor for JavaElementChangeListener.
	 */
	public JavaElementChangeListener() {
		super();
	}

	/**
	 * Catches changed webobjects nature projects and updates frameworks in webobjects
	 * project file.
	 * <br>
	 * @see org.eclipse.jdt.core.IElementChangedListener#elementChanged(ElementChangedEvent)
	 */
	public void elementChanged(ElementChangedEvent event) {

		NSMutableDictionary addedFrameworksProjectDict =
			new NSMutableDictionary();
		NSMutableDictionary removedFrameworksProjectDict =
			new NSMutableDictionary();

		if (event.getDelta().getElement().getElementType()
			== IJavaElement.JAVA_MODEL) {
			IJavaElementDelta elementDeltaToExamine = event.getDelta();
			// java model has changed - get affected webobjects projects
			for (int i = 0;
				i < elementDeltaToExamine.getChangedChildren().length;
				i++) {
				// examine changed children if they are webobjects projects
				if (elementDeltaToExamine
					.getChangedChildren()[i]
					.getElement()
					.getElementType()
					== IJavaElement.JAVA_PROJECT) {
					IProject projectToExamine =
						((IJavaProject) elementDeltaToExamine
							.getChangedChildren()[i]
							.getElement())
							.getProject();
					try {
						if (projectToExamine
							.hasNature(
								IWOLipsPluginConstants.WO_APPLICATION_NATURE)
							|| projectToExamine.hasNature(
								IWOLipsPluginConstants.WO_FRAMEWORK_NATURE)) {

							addedFrameworksProjectDict.setObjectForKey(
								new NSMutableArray(),
								projectToExamine);

							removedFrameworksProjectDict.setObjectForKey(
								new NSMutableArray(),
								projectToExamine);

							// webobjects project changed 
							ArrayList foundElements = new ArrayList();

							// search deltas for classpath changes
							searchDeltas(
								elementDeltaToExamine
									.getChangedChildren()[i]
									.getChangedChildren(),
								IJavaElementDelta.F_ADDED_TO_CLASSPATH,
								foundElements);

							IPackageFragmentRoot currentPackageFragmentRoot;

							for (int j = 0; j < foundElements.size(); j++) {
								currentPackageFragmentRoot =
									(IPackageFragmentRoot) foundElements.get(j);

								NSMutableArray addedFrameworks =
									(
										NSMutableArray) addedFrameworksProjectDict
											.objectForKey(
										projectToExamine);

								addedFrameworks.addObject(
									currentPackageFragmentRoot
										.getRawClasspathEntry()
										.getPath());

								/*
								WOProjectFileUpdater.addFrameworkToPBFile(
									currentPackageFragmentRoot
										.getRawClasspathEntry()
										.getPath(),
									WOProjectFileUpdater
										.projectFileFromParentResource(
										projectToExamine),
									null);
									*/

							}

							foundElements = new ArrayList();

							// search deltas for classpath changes
							searchDeltas(
								elementDeltaToExamine
									.getChangedChildren()[i]
									.getChangedChildren(),
								IJavaElementDelta.F_REMOVED_FROM_CLASSPATH,
								foundElements);

							for (int j = 0; j < foundElements.size(); j++) {
								currentPackageFragmentRoot =
									(IPackageFragmentRoot) foundElements.get(j);

								NSMutableArray removedFrameworks =
									(
										NSMutableArray) removedFrameworksProjectDict
											.objectForKey(
										projectToExamine);

								removedFrameworks.addObject(
									new Path(
										currentPackageFragmentRoot
											.getElementName()));

								/*
								WOProjectFileUpdater
								.removeFrameworkFromPBFile(
								new Path(
								currentPackageFragmentRoot
									.getElementName()),
								WOProjectFileUpdater
								.projectFileFromParentResource(
								projectToExamine),
								null);
								*/

							}
						}
					} catch (CoreException e) {
						WOLipsPlugin.log(e);
					}
				}
			}
		}

		// update project files
		updateProjects(
			addedFrameworksProjectDict,
			removedFrameworksProjectDict);
	}

	/**
	 * Method searchDeltas. Recursive search in java element deltas for changed elements
	 * matching the change flag to search for. The results are stored in foundElements.
	 * <br><br>
	 * @param deltasToExamine
	 * @param changeFlagToSearch
	 * @param foundElements
	 * @return boolean
	 */
	private boolean searchDeltas(
		IJavaElementDelta[] deltasToExamine,
		int changeFlagToSearch,
		ArrayList foundElements) {

		for (int i = 0; i < deltasToExamine.length; i++) {
			if (deltasToExamine[i].getFlags()
				== IJavaElementDelta.F_CHILDREN) {
				// further examination needed
				while (searchDeltas(deltasToExamine[i].getChangedChildren(),
					changeFlagToSearch,
					foundElements));
			} else if (deltasToExamine[i].getFlags() == changeFlagToSearch) {
				// element found 
				foundElements.add(deltasToExamine[i].getElement());
			}
		}
		return false;
	}

	private void updateProjects(
		NSDictionary addedFrameworksProjectDict,
		NSDictionary removedFrameworksProjectDict) {

		IProject currentProject;
		PBProject currentPBProject;
		List frameworks;
		NSArray changedFrameworks;

		for (int i = 0;
			i < addedFrameworksProjectDict.allKeys().count();
			i++) {

			currentProject =
				(IProject) addedFrameworksProjectDict.allKeys().objectAtIndex(
					i);
			changedFrameworks =
				(NSArray) addedFrameworksProjectDict.objectForKey(
					currentProject);

			if (changedFrameworks.count() > 0) {
				PBProjectUpdater projectUpdater =
					new PBProjectUpdater(currentProject);
				projectUpdater.addFrameworks(changedFrameworks);
			}
		}

		for (int i = 0;
			i < removedFrameworksProjectDict.allKeys().count();
			i++) {

			currentProject =
				(IProject) removedFrameworksProjectDict
					.allKeys()
					.objectAtIndex(
					i);

			changedFrameworks =
				(NSArray) removedFrameworksProjectDict.objectForKey(
					currentProject);

			if (changedFrameworks.count() > 0) {
				PBProjectUpdater projectUpdater =
					new PBProjectUpdater(currentProject);
				projectUpdater.removeFrameworks(changedFrameworks);
			}

		}
	}

}
