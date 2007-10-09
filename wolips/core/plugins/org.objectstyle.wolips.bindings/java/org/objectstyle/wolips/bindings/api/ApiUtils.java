package org.objectstyle.wolips.bindings.api;

import java.io.File;
import java.net.URL;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IOpenable;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.objectstyle.wolips.bindings.Activator;
import org.objectstyle.wolips.bindings.utils.BindingReflectionUtils;
import org.objectstyle.wolips.bindings.wod.BindingValueKey;
import org.objectstyle.wolips.bindings.wod.TypeCache;
import org.objectstyle.wolips.locate.LocatePlugin;
import org.objectstyle.wolips.locate.result.LocalizedComponentsLocateResult;
import org.osgi.framework.Bundle;

public class ApiUtils {
	public static int getSelectedDefaults(IApiBinding binding) {
		String defaults = binding.getDefaults();
		if (defaults == null) {
			return 0;
		}
		for (int i = 0; i < IApiBinding.ALL_DEFAULTS.length; i++) {
			String string = IApiBinding.ALL_DEFAULTS[i];
			if (string.equals(defaults)) {
				return i;
			}
		}
		return 0;
	}

	public static Wo findApiModelWo(IType elementType, ApiCache cache) throws ApiModelException {
		Wo wo;
		if (elementType == null) {
			wo = null;
		} else {
			wo = cache.getApiForType(elementType);

			if (wo != null) {
				ApiModel model = wo.getModel();
				if (model.parseIfNecessary()) {
					cache.clearApiForElementType(elementType);
					wo = null;
				}
			}

			if (wo == null) {
				Boolean apiMissing = cache.apiMissingForElementType(elementType);
				if (apiMissing == null || !apiMissing.booleanValue()) {
					ApiModel apiModel = null;
					IOpenable typeContainer = elementType.getOpenable();
					if (typeContainer instanceof IClassFile) {
						IClassFile classFile = (IClassFile) typeContainer;
						IJavaElement parent = classFile.getParent();
						if (parent instanceof IPackageFragment) {
							IPackageFragment parentPackage = (IPackageFragment) parent;
							IPath packagePath = parentPackage.getPath();
							IPath apiPath = packagePath.removeLastSegments(2).append(elementType.getElementName()).addFileExtension("api");
							File apiFile = apiPath.toFile();
							boolean fileExists = apiFile.exists();
							if (!fileExists && parentPackage.getElementName().startsWith("com.webobjects")) {
								Bundle bundle = Activator.getDefault().getBundle();
								URL woDefinitionsURL = bundle.getEntry("/WebObjectDefinitions.xml");
								if (woDefinitionsURL != null) {
									apiModel = new ApiModel(woDefinitionsURL);
								}
							} else if (fileExists) {
								apiModel = new ApiModel(apiFile);
							}
						}
					} else if (typeContainer instanceof ICompilationUnit) {
						// ICompilationUnit cu = (ICompilationUnit)
						// typeContainer;
						// IResource resource = cu.getCorrespondingResource();
						// String name = resource.getName();
						try {
							LocalizedComponentsLocateResult componentsLocateResults = LocatePlugin.getDefault().getLocalizedComponentsLocateResult(elementType.getJavaProject().getProject(), elementType.getElementName());
							IFile apiFile = componentsLocateResults.getDotApi();
							if (apiFile != null && apiFile.exists()) {
								apiModel = new ApiModel(apiFile);
							}
						} catch (Exception e) {
							throw new ApiModelException("Failed to locate API file for " + elementType.getElementName() + ".");
						}
					}

					if (apiModel != null) {
						Wo[] wos = apiModel.getWODefinitions().getWos();
						if (wos.length == 0) {
							// leave it alone
						} else if (wos.length == 1) {
							wo = wos[0];
						} else {
							for (int i = 0; wo == null && i < wos.length; i++) {
								if (elementType.getElementName().equals(wos[i].getClassName())) {
									wo = wos[i];
								}
							}
						}
					}

					if (wo == null) {
						cache.setApiMissingForElementType(true, elementType);
					} else {
						cache.setApiForType(wo, elementType);
					}
				}
			}
		}

		return wo;
	}

	public static String[] getValidValues(IJavaProject javaProject, IType componentType, IType apiType, String bindingName, TypeCache typeCache) throws JavaModelException, ApiModelException {
		String[] validValues = null;
		Wo wo = ApiUtils.findApiModelWo(apiType, typeCache.getApiCache());
		if (wo != null) {
			Binding matchingBinding = wo.getBinding(bindingName);
			if (matchingBinding != null) {
				validValues = matchingBinding.getValidValues(javaProject, componentType, typeCache);
			}
		}
		return validValues;
	}

	public static String[] getValidValues(IApiBinding binding, IJavaProject javaProject, IType componentType, TypeCache typeCache) throws JavaModelException {
		String[] validValues = null;
		int selectedDefaults = binding.getSelectedDefaults();
		String defaultsName = IApiBinding.ALL_DEFAULTS[selectedDefaults];
		if ("Boolean".equals(defaultsName)) {
			validValues = new String[] { "true", "false" };
		} else if ("YES/NO".equals(defaultsName)) {
			validValues = new String[] { "true", "false" };
		} else if ("Date Format Strings".equals(defaultsName)) {
			validValues = new String[] { "\"%m/%d/%y\"", "\"%B %d, %Y\"", "\"%b %d, %Y\"", "\"%A, %B %d, %Y\"", "\"%A, %b %d, %Y\"", "\"%d.%m.%y\"", "\"%d %B %y\"", "\"%d %b %y\"", "\"%A %d %B %Y\"", "\"%A %d %b %Y\"", "\"%x\"", "\"%H:%M:%S\"", "\"%I:%M:%S %p\"", "\"%H:%M\"", "\"%I:%M %p\"", "\"%X\"" };
		} else if ("Number Format Strings".equals(defaultsName)) {
			validValues = new String[] { "\"0\"", "\"0.00\"", "\"0.##\"", "\"#,##0\"", "\"_,__0\"", "\"#,##0.00\"", "\"$#,##0\"", "\"$#,##0.00\"", "\"$#,##0.##\"" };
		} else if ("MIME Types".equals(defaultsName)) {
			validValues = new String[] { "\"image/gif\"", "\"image/jpeg\"", "\"image/png\"" };
		} else if ("Direct Actions".equals(defaultsName)) {
			// do nothing for now
		} else if ("Direct Action Classes".equals(defaultsName)) {
			// do nothing for now
		} else if ("Page Names".equals(defaultsName)) {
			// do nothing for now
		} else if ("Frameworks".equals(defaultsName)) {
			// do nothing for now
		} else if ("Resources".equals(defaultsName)) {
			// do nothing for now
		} else if ("Actions".equals(defaultsName)) {
			List<BindingValueKey> bindingKeysList = BindingReflectionUtils.getBindingKeys(javaProject, componentType, "", false, BindingReflectionUtils.VOID_ONLY, typeCache);
			validValues = new String[bindingKeysList.size()];
			for (int i = 0; i < validValues.length; i++) {
				validValues[i] = bindingKeysList.get(i).getBindingName();
			}
			// do nothing for now
		}
		return validValues;
	}
}
