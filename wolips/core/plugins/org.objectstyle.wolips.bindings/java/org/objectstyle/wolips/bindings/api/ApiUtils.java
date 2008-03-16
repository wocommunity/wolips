package org.objectstyle.wolips.bindings.api;

import java.io.File;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IOpenable;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.SearchPattern;
import org.objectstyle.wolips.bindings.Activator;
import org.objectstyle.wolips.bindings.utils.BindingReflectionUtils;
import org.objectstyle.wolips.bindings.wod.BindingValueKey;
import org.objectstyle.wolips.bindings.wod.TypeCache;
import org.objectstyle.wolips.core.resources.internal.types.project.ProjectAdapter;
import org.objectstyle.wolips.core.resources.types.TypeNameCollector;
import org.objectstyle.wolips.core.resources.types.project.IProjectAdapter;
import org.objectstyle.wolips.locate.LocatePlugin;
import org.objectstyle.wolips.locate.result.LocalizedComponentsLocateResult;
import org.osgi.framework.Bundle;

public class ApiUtils {
  private static ApiModel _globalApiModel;

  public static boolean isActionBinding(IApiBinding binding) {
    String defaults = binding.getDefaults();
    boolean isActionBinding = false;
    if (IApiBinding.ACTIONS_DEFAULT.equals(defaults)) {
      isActionBinding = true;
    }
    else if (defaults == null) {
      String bindingName = binding.getName();
      isActionBinding = "action".equals(bindingName);
    }
    return isActionBinding;
  }

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
    }
    else {
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
          try {
            if (elementType.getFullyQualifiedName().startsWith("com.webobjects.appserver._private.")) {
              if (_globalApiModel == null) {
                Bundle bundle = Activator.getDefault().getBundle();
                URL woDefinitionsURL = bundle.getEntry("/WebObjectDefinitions.xml");
                if (woDefinitionsURL != null) {
                  apiModel = new ApiModel(woDefinitionsURL);
                }
                _globalApiModel = apiModel;
              }
              else {
                apiModel = _globalApiModel;
              }
            }
            else {
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
                  if (fileExists) {
                    apiModel = new ApiModel(apiFile);
                  }
                }
              }
              else if (typeContainer instanceof ICompilationUnit) {
                // ICompilationUnit cu = (ICompilationUnit)
                // typeContainer;
                // IResource resource = cu.getCorrespondingResource();
                // String name = resource.getName();
                LocalizedComponentsLocateResult componentsLocateResults = LocatePlugin.getDefault().getLocalizedComponentsLocateResult(elementType.getJavaProject().getProject(), elementType.getElementName());
                IFile apiFile = componentsLocateResults.getDotApi();
                if (apiFile != null && apiFile.exists()) {
                  apiModel = new ApiModel(apiFile);
                }
              }
            }

            if (apiModel != null) {
              Wo[] wos = apiModel.getWODefinitions().getWos();
              if (wos.length == 0) {
                // leave it alone
              }
              else if (wos.length == 1) {
                wo = wos[0];
              }
              else {
                for (int i = 0; wo == null && i < wos.length; i++) {
                  if (elementType.getElementName().equals(wos[i].getClassName())) {
                    wo = wos[i];
                  }
                }
              }
            }
          }
          catch (Throwable t) {
            wo = null;
            Activator.getDefault().log("Failed to parse API for " + elementType.getElementName() + ".", t);
          }

          if (wo == null) {
            cache.setApiMissingForElementType(true, elementType);
          }
          else {
            cache.setApiForType(wo, elementType);
          }
        }
      }
    }

    return wo;
  }

  public static String[] getValidValues(String partialValue, IJavaProject javaProject, IType componentType, IType apiType, String bindingName, TypeCache typeCache) throws JavaModelException, ApiModelException {
    String[] validValues = null;
    Wo wo = ApiUtils.findApiModelWo(apiType, typeCache.getApiCache(javaProject));
    if (wo != null) {
      Binding matchingBinding = wo.getBinding(bindingName);
      if (matchingBinding != null) {
        validValues = matchingBinding.getValidValues(partialValue, javaProject, componentType, typeCache);
      }
    }
    return validValues;
  }

  public static String[] getValidValues(IApiBinding binding, String partialValue, IJavaProject javaProject, IType componentType, TypeCache typeCache) throws JavaModelException {
    Set<String> validValues = new HashSet<String>();
    int selectedDefaults = binding.getSelectedDefaults();
    String defaultsName = IApiBinding.ALL_DEFAULTS[selectedDefaults];
    if ("Boolean".equals(defaultsName)) {
      validValues.add("true");
      validValues.add("false");
    }
    else if ("YES/NO".equals(defaultsName)) {
      validValues.add("yes");
      validValues.add("no");
    }
    else if ("Date Format Strings".equals(defaultsName)) {
      validValues.add("\"%m/%d/%y\"");
      validValues.add("\"%B %d, %Y\"");
      validValues.add("\"%b %d, %Y\"");
      validValues.add("\"%A, %B %d, %Y\"");
      validValues.add("\"%A, %b %d, %Y\"");
      validValues.add("\"%d.%m.%y\"");
      validValues.add("\"%d %B %y\"");
      validValues.add("\"%d %b %y\"");
      validValues.add("\"%A %d %B %Y\"");
      validValues.add("\"%A %d %b %Y\"");
      validValues.add("\"%x\"");
      validValues.add("\"%H:%M:%S\"");
      validValues.add("\"%I:%M:%S %p\"");
      validValues.add("\"%H:%M\"");
      validValues.add("\"%I:%M %p\"");
      validValues.add("\"%X\"");
    }
    else if ("Number Format Strings".equals(defaultsName)) {
      validValues.add("\"0\"");
      validValues.add("\"0.00\"");
      validValues.add("\"0.##\"");
      validValues.add("\"#,##0\"");
      validValues.add("\"_,__0\"");
      validValues.add("\"#,##0.00\"");
      validValues.add("\"$#,##0\"");
      validValues.add("\"$#,##0.00\"");
      validValues.add("\"$#,##0.##\"");
    }
    else if ("MIME Types".equals(defaultsName)) {
      validValues.add("\"image/gif\"");
      validValues.add("\"image/jpeg\"");
      validValues.add("\"image/png\"");
    }
    else if ("Direct Actions".equals(defaultsName)) {
      TypeNameCollector typeNameCollector = new TypeNameCollector("com.webobjects.appserver.WODirectAction", javaProject, false);
      BindingReflectionUtils.findMatchingElementClassNames("", SearchPattern.R_PREFIX_MATCH, typeNameCollector, new NullProgressMonitor());
      for (IType type : typeNameCollector.types()) {
        IMethod[] methods = type.getMethods();
        for (IMethod method : methods) {
          String name = method.getElementName();
          if (name.endsWith("Action") && method.getParameterNames().length == 0) {
            validValues.add("\"" + name.substring(0, name.length() - "Action".length()) + "\"");
          }
        }
      }
    }
    else if ("Direct Action Classes".equals(defaultsName)) {
      TypeNameCollector typeNameCollector = new TypeNameCollector("com.webobjects.appserver.WODirectAction", javaProject, false);
      BindingReflectionUtils.findMatchingElementClassNames(partialValue, SearchPattern.R_PREFIX_MATCH, typeNameCollector, new NullProgressMonitor());
      for (String typeName : typeNameCollector.getTypeNames()) {
        int dotIndex = typeName.lastIndexOf('.');
        if (dotIndex != -1) {
          typeName = typeName.substring(dotIndex + 1);
        }
        validValues.add("\"" + typeName + "\"");
      }
    }
    else if ("Page Names".equals(defaultsName)) {
      TypeNameCollector typeNameCollector = new TypeNameCollector(javaProject, false);
      BindingReflectionUtils.findMatchingElementClassNames(partialValue, SearchPattern.R_PREFIX_MATCH, typeNameCollector, new NullProgressMonitor());
      for (String typeName : typeNameCollector.getTypeNames()) {
        int dotIndex = typeName.lastIndexOf('.');
        if (dotIndex != -1) {
          typeName = typeName.substring(dotIndex + 1);
        }
        validValues.add("\"" + typeName + "\"");
      }
    }
    else if ("Frameworks".equals(defaultsName)) {
      validValues.add("\"app\"");
      ProjectAdapter projectAdapter = (ProjectAdapter) javaProject.getProject().getAdapter(IProjectAdapter.class);
      if (projectAdapter != null) {
        for (String frameworkName : projectAdapter.getFrameworkNames()) {
          if (frameworkName.endsWith(".framework")) {
            validValues.add("\"" + frameworkName.substring(0, frameworkName.length() - ".framework".length()) + "\"");
          }
        }
      }
    }
    else if ("Resources".equals(defaultsName)) {
      ProjectAdapter projectAdapter = (ProjectAdapter) javaProject.getProject().getAdapter(IProjectAdapter.class);
      if (projectAdapter != null) {
        IFolder folder = projectAdapter.getBuildAdapter().getProductAdapter().getContentsAdapter().getWebServerResourcesAdapter().getUnderlyingFolder();
        try {
          ApiUtils.acceptResources(folder, "", validValues);
        }
        catch (CoreException e) {
          e.printStackTrace();
        }
      }
    }
    else if ("Actions".equals(defaultsName)) {
      List<BindingValueKey> bindingKeysList = BindingReflectionUtils.getBindingKeys(javaProject, componentType, "", false, BindingReflectionUtils.VOID_ONLY, false, typeCache);
      for (BindingValueKey key : bindingKeysList) {
        validValues.add(key.getBindingName());
      }
    }
    String[] validValueStrings = validValues.toArray(new String[validValues.size()]);
    return validValueStrings;
  }

  protected static void acceptResources(IResource resource, String basePath, Set<String> paths) throws CoreException {
    if (resource instanceof IFolder) {
      IResource[] members = ((IFolder) resource).members();
      for (IResource childResource : members) {
        if (childResource instanceof IFolder) {
          ApiUtils.acceptResources(childResource, basePath + "/" + childResource.getName() + "/", paths);
        }
        else {
          ApiUtils.acceptResources(childResource, basePath, paths);
        }
      }
    }
    else if (resource instanceof IFile) {
      paths.add("\"" + basePath + resource.getName() + "\"");
    }
  }
}
